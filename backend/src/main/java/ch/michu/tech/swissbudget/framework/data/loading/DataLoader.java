package ch.michu.tech.swissbudget.framework.data.loading;

import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.utils.DateBuilder;
import ch.michu.tech.swissbudget.framework.utils.EncodingUtil;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

/**
 * Inserts data from a CSV File into tables. CSV File needs to be trusted, inserts are unsafe!
 * <p>
 * <h3>Schema:</h3>
 * <ol>
 *     <li>::table_name -> "::" & "," will be removed, the rest is expected to be the exact table name</li>
 *     <li>comma separated list of column names -> expected to be column names, will be replicated exactly into insert statement</li>
 *     <li>
 *         comma separated list of values -> values in same order as columns, an empty string will be parsed to null, everything else is
 *         treated as a string - postgres takes care of the type parsing
 *     </li>
 * </ol>
 * <p>
 * <h3>value helper functions</h3>
 * if you wrap a value in {{}}, then the parser expects this to be a supported function. every function needs to have () otherwise it is
 * not a valid function. (case-sensitive!)
 * <ul>
 *     <li>hashWithSalt("stringToHash") -> hashes the value and caches the salt for the line or uses cached salt if exists</li>
 *     <li>salt() -> generates a salt or takes the cached salt for line</li>
 *     <li>genGuid() -> generates a guid that is not cached</li>
 *     <li>genGuid("idAlias") -> generate a guid and store in instance</li>
 *     <li>useGuid("idAlias") -> use a generated guid by alias (if not exists -> generate guid)</li>
 *     <li>
 *         date("shortcut")
 *         these date functions can be chained using a "+" {{date(1&&day)->date(lastofmonth)}}
 *         dates always start form today
 *         converts into a date, suppoerted shortcuts are (case is ignored):
 *         <ul>
 *             <li>"lastofmonth"</li>
 *             <li>"firstofmonth"</li>
 *             <li>"lastofyear"</li>
 *             <li>"firstofyear"</li>
 *         </ul>
 *     </li>
 *     <li>
 *         date("amount"&&"type") -> converts into a date, amount is a positiv or negativ long,
 *         these date functions can be chained using a "+" {{date(lastofyear)->date(2&&month)}}
 *         dates always start form today
 *         suppoerted types are (case is ignored):
 *         <ul>
 *             <li>"day"</li>
 *             <li>"week"</li>
 *             <li>"month"</li>
 *             <li>"year"</li>
 *         </ul>
 *     </li>
 * </ul>
 */
@Dependent
public class DataLoader {

    private static final Logger LOGGER = Logger.getLogger(DataLoader.class.getSimpleName());
    private static final String TABLE_START = "::";
    private static final String COMMA = ",";
    private static final String AND = "&&";
    private static final String ARROW = "->";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE;

    private final DataProvider data;
    /**
     * ID alias with value cache, but remember this class is not application scoped
     */
    @Getter
    private final Map<String, String> uuidCache = new HashMap<>();
    private SQLInsertStringBuilder builder;

    @Inject
    public DataLoader(DataProvider data) {
        this.data = data;
        LOGGER.log(Level.INFO, "constructed", new Object[]{}); // TODO remove, but test if really created on every use
    }

    /**
     * @param path CSV file to read
     * @return a queue with the statements
     */
    public Queue<String> load(Path path, Map<String, String> variables) {
        long startTime = Instant.now().toEpochMilli();
        if (variables == null) {
            variables = Map.of();
        }

        LOGGER.log(Level.INFO, "loading data from: {0}", new Object[]{path.toAbsolutePath()});

        File filePath = path.toFile();
        if (!filePath.exists() || !filePath.isFile() || !filePath.getName().endsWith(".csv")) {
            throw new DataLoaderException("given path is not a CSV file");
        }

        try {
            Queue<String> statements = buildStatements(Files.readAllLines(path, StandardCharsets.UTF_8), variables);
            long duration = Instant.now().toEpochMilli() - startTime;
            LOGGER.log(Level.INFO, "loaded data into SQL. took:{0}ms", new Object[]{duration});
            return statements;
        } catch (IOException e) {
            throw new DataLoaderException("could not read data loader file: " + e.getMessage());
        }
    }

    protected Queue<String> buildStatements(List<String> lines, Map<String, String> variables) {
        Queue<String> statements = new LinkedBlockingQueue<>();

        boolean previousWasTableName = false;
        for (String line : lines) {
            if (line.startsWith(TABLE_START)) {
                handleNewTable(line, statements);
                previousWasTableName = true;
            } else if (previousWasTableName) {
                handleColumnsLine(line);
                previousWasTableName = false;
            } else if (!line.replace(COMMA, "").isBlank()) {
                handleValuesLine(line, variables);
            }
        }

        completeTable(statements); // the last table is still in the builder.
        return statements;
    }

    public void store(Queue<String> statements) {
        long startTime = Instant.now().toEpochMilli();

        LOGGER.log(Level.INFO, "executing {0} import statements", new Object[]{statements});

        data.getContext().transaction(ctx -> statements.forEach(statement -> {
            LOGGER.log(Level.FINE, "executing: {0}", new Object[]{statement});
            //noinspection SqlSourceToSinkFlow (i know /:)
            ctx.dsl().execute(statement);
        }));

        long duration = Instant.now().toEpochMilli() - startTime;
        LOGGER.log(Level.INFO, "executed statements in {0}ms", new Object[]{duration});
    }

    protected void handleNewTable(String line, Queue<String> statements) {
        String tableName = line.replace(TABLE_START, "").replace(COMMA, "");

        if (builder == null) {
            builder = new SQLInsertStringBuilder();
        } else {
            completeTable(statements); // need to complete previous table
        }

        LOGGER.log(Level.FINE, "starting parsing of table: {0}", new Object[]{tableName});
        builder.insertInto(tableName);
    }

    protected void completeTable(Queue<String> statements) {
        String builtSql = builder.complete();
        statements.add(builtSql);
        LOGGER.log(Level.FINE, "parsed table: {0} - {1}", new Object[]{builder.getTableName(), builtSql});
    }

    protected void handleColumnsLine(String line) {
        String[] columns = line.split(COMMA);
        for (String column : columns) {
            if (!column.isBlank()) {
                builder.addColumn(column);
            }
        }
    }

    @SuppressWarnings("java:S3776") // sorry for the complexity, but this needs to be mapped somewhere
    protected void handleValuesLine(String line, Map<String, String> variables) {
        LOGGER.log(Level.FINE, "handling values line: {0}", new Object[]{line});
        int columnCount = builder.getColumnsCount();
        String[] values = line.split(COMMA);
        List<String> valueLine = new ArrayList<>();

        String salt = null;
        for (int i = 0; i < values.length; i++) {
            if (i > columnCount) {
                break;
            }

            String value = values[i];
            if (value.isBlank()) {
                value = null;
            } else if (value.startsWith("{{") && value.endsWith("}}")) {
                String[] params = parseFunctionParams(value);

                if (value.contains("hashWithSalt") && params.length > 0) {
                    if (salt == null) {
                        salt = EncodingUtil.generateSalt();
                    }

                    value = EncodingUtil.hashString(params[0], salt);
                } else if (value.contains("salt")) {
                    if (salt == null) {
                        salt = EncodingUtil.generateSalt();
                    }
                    value = salt;
                } else if (value.contains("date")) {
                    if (value.contains(ARROW)) {
                        value = date(value.split(ARROW));
                    } else {
                        value = date(value);
                    }
                } else if (value.contains("genGuid")) {
                    value = genGuid(params);
                } else if (value.contains("useGuid") && params.length > 0) {
                    value = useGuid(params[0]);
                } else if (value.contains("useVar") && params.length > 0) {
                    if (!variables.containsKey(params[0])) {
                        throw new DataLoaderException("variable %s not provided".formatted(params[0]));
                    }
                    value = variables.get(params[0]);
                }
            }

            valueLine.add(value);
        }

        LOGGER.log(Level.FINE, "done with line: {0}", new Object[]{valueLine});
        builder.addValueLine(valueLine);
    }

    protected String useGuid(String alias) {
        uuidCache.putIfAbsent(alias, UUID.randomUUID().toString());
        return uuidCache.get(alias);
    }

    protected String genGuid(String[] params) {
        if (params.length > 0 && !params[0].isBlank()) {
            String value = uuidCache.getOrDefault(params[0], UUID.randomUUID().toString());
            uuidCache.put(params[0], value);
            return value;
        }

        return UUID.randomUUID().toString();
    }

    protected String date(String... dateCalls) {
        final DateBuilder dateBuilder = DateBuilder.today();

        for (String dateCall : dateCalls) {
            String[] params = parseFunctionParams(dateCall);
            if (params.length == 1) {
                String dateShortcutParam = params[0];

                switch (dateShortcutParam.toLowerCase()) {
                    case "lastofmonth" -> dateBuilder.lastDayOfMonth();
                    case "firstofmonth" -> dateBuilder.firstDayOfMonth();
                    case "lastofyear" -> dateBuilder.lastDayOfYear();
                    case "firstofyear" -> dateBuilder.firstDayOfYear();
                }
            } else if (params.length > 1) {
                int amount;
                try {
                    amount = Integer.parseInt(params[0]);
                } catch (NumberFormatException e) {
                    amount = 0;
                }
                String dateType = params[1];

                switch (dateType.toLowerCase()) {
                    case "week" -> dateBuilder.addWeeks(amount);
                    case "month" -> dateBuilder.addMonths(amount);
                    case "year" -> dateBuilder.addYears(amount);
                    default -> dateBuilder.addDays(amount);
                }
            }
        }

        return dateBuilder.formatted(FORMATTER);
    }

    protected String[] parseFunctionParams(String colVal) {
        return colVal.substring(colVal.indexOf("(") + 1, colVal.indexOf(")")).split(AND);
    }
}

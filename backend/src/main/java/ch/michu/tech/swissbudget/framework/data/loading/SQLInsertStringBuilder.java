package ch.michu.tech.swissbudget.framework.data.loading;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;

public class SQLInsertStringBuilder {

    private static final Logger LOGGER = Logger.getLogger(SQLInsertStringBuilder.class.getSimpleName());

    private static final String START = "INSERT INTO ";
    private static final String VALUES = "VALUES";
    private static final String WHITE_SPACE = " ";
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";
    private static final String OPEN_BRACKET = "(";
    private static final String CLOSE_BRACKET = ")";
    private static final String CHICKEN_FOOT = "'";

    private boolean startedColumns = false;
    private boolean startedValues = false;

    @Getter
    private int columnsCount = 0;

    @Getter
    private String tableName;

    private StringBuilder builder = new StringBuilder(START);

    public SQLInsertStringBuilder insertInto(String table) {
        columnsCount = 0;
        startedColumns = false;
        startedValues = false;
        tableName = table;
        this.builder = new StringBuilder(START);
        this.builder.append(table);

        return this;
    }

    public SQLInsertStringBuilder addColumn(String column) {
        if (startedValues) {
            throw new SQLInsertBuilderException("inserter has state that does not allow new column");
        }

        if (!startedColumns) {
            builder.append(WHITE_SPACE).append(OPEN_BRACKET);
            startedColumns = true;
            builder.append(column);
        } else {
            builder.append(COMMA).append(column);
        }

        columnsCount++;

        return this;
    }

    public SQLInsertStringBuilder addValueLine(List<String> values) {
        if (!startedColumns) {
            throw new SQLInsertBuilderException("inserter has state that does not allow new column");
        }
        if (values.size() != columnsCount) {
            throw new SQLInsertBuilderException(
                "values don't match with columns: [should: %s - is: %s]".formatted(columnsCount, values.size()));
        }

        if (startedValues) {
            builder.append(COMMA).append(WHITE_SPACE);
        } else {
            builder.append(CLOSE_BRACKET).append(WHITE_SPACE).append(VALUES).append(WHITE_SPACE);
            startedValues = true;
        }
        builder.append(OPEN_BRACKET);
        builder.append(getValueString(values.getFirst()));

        for (int i = 1; i < values.size(); i++) {
            builder.append(COMMA).append(getValueString(values.get(i)));
        }
        builder.append(CLOSE_BRACKET);

        return this;
    }

    private String getValueString(String value) {
        if (value == null || value.equals("null")) {
            return "null";
        }

        // replace: need to escape the CHICKEN_FOOT (:
        return CHICKEN_FOOT + value.replace(CHICKEN_FOOT, CHICKEN_FOOT + CHICKEN_FOOT).trim() + CHICKEN_FOOT;
    }

    public String complete() {
        builder.append(SEMICOLON);
        String sql = builder.toString();
        LOGGER.log(Level.FINE, "built SQL insert string: {0}", new Object[]{sql});

        return sql;
    }
}

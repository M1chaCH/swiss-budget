package ch.michu.tech.swissbudget.test;

import ch.michu.tech.swissbudget.framework.data.loading.DataLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

@ApplicationScoped
public class TestDataManager {

    private static final Logger LOGGER = Logger.getLogger(TestDataManager.class.getSimpleName());

    private final DataLoader dataLoader;
    private final Connection connection;
    @Getter
    private final DSLContext dsl;
    private final String setupScriptPath;

    private final Queue<String> dataStatements;
    private final Map<String, String> idAliasWithUUID;

    private boolean initialized = false;

    @Inject
    public TestDataManager(
        DataLoader dataLoader,
        @ConfigProperty(name = "db.user") String dbUser,
        @ConfigProperty(name = "db.password") String dbPassword,
        @ConfigProperty(name = "db.url") String dbUrl,
        @ConfigProperty(name = "db.setup.script") String setupScriptPath
    ) throws SQLException {
        this.dataLoader = dataLoader;
        this.setupScriptPath = setupScriptPath;

        LOGGER.log(Level.INFO, "connecting test db at: {0}", new Object[]{dbUrl});
        connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        dsl = DSL.using(connection, SQLDialect.H2, new Settings().withRenderSchema(false));
        LOGGER.log(Level.INFO, "successfully connected to Test DB", new Object[]{});

        Path p = Paths.get("src/main/resources/sql/test-data.csv");
        dataStatements = dataLoader.load(p, Map.of());
        idAliasWithUUID = dataLoader.getUuidCache();
    }

    public void initDb() throws IOException, SQLException {
        if (initialized) {
            return;
        }

        Path p = Paths.get(setupScriptPath);
        LOGGER.log(Level.INFO, "setting up DB with script at: {0}", new Object[]{p.toAbsolutePath()});

        String script = Files.readString(p);
        String[] statements = script
            .replaceAll("--.*\\n", "")
            .replace("\n", "")
            .replace("\r", "")
            .split(";");
        Statement statement = connection.createStatement();
        LOGGER.log(Level.INFO, "successfully prepared, running script", new Object[]{});

        for (String sql : statements) {
            LOGGER.log(Level.FINE, "running: {0}", new Object[]{sql});
            statement.execute(sql);
        }
        statement.close();

        dataLoader.store(dataStatements);

        LOGGER.log(Level.INFO, "DB setup completed", new Object[]{});
        initialized = true;
    }

    public String getGeneratedId(String alias) {
        return idAliasWithUUID.getOrDefault(alias, "");
    }

    public void applyTestData() throws SQLException {
        clearAllTables();
        dataLoader.store(dataStatements);
    }

    protected void clearAllTables() throws SQLException {
        LOGGER.log(Level.WARNING, "clearing all content from all tables", new Object[]{});
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SHOW TABLES");

        while (resultSet.next()) {
            clearTable(resultSet.getString(1));
        }

        resultSet.close();
        statement.close();
    }

    protected void clearTable(String name) throws SQLException {
        Statement statement = connection.createStatement();

        // might be insecure, but is testing env, so can't be that bad.
        // want to delete everything
        //noinspection SqlSourceToSinkFlow, SqlWithoutWhere
        statement.executeUpdate("DELETE FROM " + name);
        statement.close();
    }
}

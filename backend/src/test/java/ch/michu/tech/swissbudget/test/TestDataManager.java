package ch.michu.tech.swissbudget.test;

import ch.michu.tech.swissbudget.framework.data.connection.DBConnectionFactory;
import ch.michu.tech.swissbudget.framework.data.loading.DataLoader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;

@ApplicationScoped
public class TestDataManager {

    private static final Logger LOGGER = Logger.getLogger(TestDataManager.class.getSimpleName());

    private final DataLoader dataLoader;
    private final Connection connection;
    @Getter
    private final DSLContext dsl;
    private final String setupScriptPath;

    private final Queue<String> testDataStatements;
    private final Queue<String> demoDataStatements;
    private final Map<String, UUID> idAliasWithUUID;

    private boolean initialized = false;

    @Inject
    public TestDataManager(
        DataLoader dataLoader, DBConnectionFactory connectionFactory,
        @ConfigProperty(name = "db.setup.script") String setupScriptPath
    ) {
        this.dataLoader = dataLoader;
        this.setupScriptPath = setupScriptPath;

        LOGGER.log(Level.INFO, "connecting test db", new Object[]{});
        connection = connectionFactory.createConnection();
        dsl = connectionFactory.createContext(connection);
        LOGGER.log(Level.INFO, "successfully connected to Test DB", new Object[]{});

        Path testDataPath = Paths.get("src/main/resources/sql/test-data.csv");
        testDataStatements = dataLoader.load(testDataPath, Map.of());
        idAliasWithUUID = dataLoader.getUuidCache();
        Path demoUserPath = Paths.get("src/main/resources/sql/demo-user-data.csv");
        demoDataStatements = dataLoader.load(demoUserPath, Map.of("user_id", idAliasWithUUID.get("usr_rt")));
    }

    public void initDbIfRequired() throws IOException, SQLException {
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
        connection.commit();
        statement.close();

        LOGGER.log(Level.INFO, "DB setup completed", new Object[]{});
        initialized = true;
    }

    public UUID getGeneratedId(String alias) {
        return idAliasWithUUID.get(alias);
    }

    public void applyTestData(boolean loadDemoUser) throws SQLException {
        clearAllTables();
        dataLoader.store(dsl, testDataStatements);

        if (loadDemoUser) {
            dataLoader.store(dsl, demoDataStatements);
        }

        dsl.commit().execute();
    }

    protected void clearAllTables() throws SQLException {
        LOGGER.log(Level.WARNING, "clearing all content from all tables", new Object[]{});
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SHOW TABLES");

        while (resultSet.next()) {
            clearTable(resultSet.getString(1));
        }

        connection.commit();
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

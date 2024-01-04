package ch.michu.tech.swissbudget.framework.data.connection;

import ch.michu.tech.swissbudget.framework.error.exception.UnexpectedServerException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jooq.DSLContext;

public abstract class DBConnectionFactory {

    protected final Logger LOGGER = Logger.getLogger(getClass().getSimpleName());
    protected final String user;
    protected final String password;
    protected final String url;
    protected final int maxDbPoolSize;
    private final HikariConfig dataConfig;
    private final HikariDataSource dataSource;

    protected DBConnectionFactory(String user, String password, String url, int maxDbPoolSize) {
        this.user = user;
        this.password = password;
        this.url = url;
        this.maxDbPoolSize = maxDbPoolSize;

        // only run this once in the entire application.
        // otherwise multiple can pools exist
        dataConfig = createHikariConfig();
        dataSource = new HikariDataSource(dataConfig);
    }

    protected abstract HikariConfig createHikariConfig();

    public DSLContext createContext() {
        Connection connection = createConnection();
        return createContext(connection);
    }

    public Connection createConnection() {
        try {
            Connection connection = dataSource.getConnection();
            LOGGER.log(Level.FINE, "successfully created new DB connection", new Object[]{});
            return connection;
        } catch (SQLException e) {
            throw new UnexpectedServerException("db connection failed", e);
        }
    }

    public abstract DSLContext createContext(Connection connection);
}

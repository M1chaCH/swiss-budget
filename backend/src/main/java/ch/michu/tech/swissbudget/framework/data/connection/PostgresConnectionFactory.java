package ch.michu.tech.swissbudget.framework.data.connection;

import com.zaxxer.hikari.HikariConfig;
import java.sql.Connection;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

public class PostgresConnectionFactory extends DBConnectionFactory {

    public static final SQLDialect DIALECT = SQLDialect.POSTGRES;

    public PostgresConnectionFactory(String dbUser, String dbPassword, String dbUrl, int dbMaxSize) {
        super(dbUser, dbPassword, dbUrl, dbMaxSize);
    }

    @Override
    protected HikariConfig createHikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setAutoCommit(false);
        config.setTransactionIsolation("TRANSACTION_READ_COMMITTED"); // only read transactions that are already committed
        config.setIdleTimeout(10 * 60 * 1000L); // allow connections to be idle for 10 Minutes (long running request, f.e. mail loading)
        config.setMaximumPoolSize(maxDbPoolSize);
        config.setPoolName("Hikari x Postgres");
        config.addDataSourceProperty("cachePrepStmts", "false");

        return config;
    }

    @Override
    public DSLContext createContext(Connection connection) {
        return DSL.using(connection, PostgresConnectionFactory.DIALECT);
    }
}

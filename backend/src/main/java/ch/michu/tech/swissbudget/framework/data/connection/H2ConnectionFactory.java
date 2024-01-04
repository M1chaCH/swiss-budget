package ch.michu.tech.swissbudget.framework.data.connection;

import com.zaxxer.hikari.HikariConfig;
import java.sql.Connection;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

public class H2ConnectionFactory extends DBConnectionFactory {

    public static final SQLDialect DIALECT = SQLDialect.H2;

    public H2ConnectionFactory(String dbUser, String dbPassword, String dbUrl, int dbMaxSize) {
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
        config.setIdleTimeout(60 * 1000L); // allow connections to be idle for 1 Minute
        config.setMaximumPoolSize(maxDbPoolSize);
        config.setPoolName("Hikari x H2");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return config;
    }

    @Override
    public DSLContext createContext(Connection connection) {
        return DSL.using(
            connection,
            H2ConnectionFactory.DIALECT,
            new Settings().withRenderSchema(false) // h2 cant handle a rendered schema
        );
    }
}

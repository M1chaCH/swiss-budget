package ch.michu.tech.swissbudget.framework.data.connection;

import com.zaxxer.hikari.HikariConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@ApplicationScoped
public class PostgresConnectionFactory extends DBConnectionFactory {

    public static final SQLDialect DIALECT = SQLDialect.POSTGRES;
    private final int maxPoolSize;

    @Inject
    public PostgresConnectionFactory(
        @ConfigProperty(name = "db.user") String dbUser,
        @ConfigProperty(name = "db.password") String dbPassword,
        @ConfigProperty(name = "db.url") String dbUrl,
        @ConfigProperty(name = "db.max.pool.size", defaultValue = "10") int dbMaxSize
    ) {
        super(dbUser, dbPassword, dbUrl);
        this.maxPoolSize = dbMaxSize;
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
        config.setMaximumPoolSize(maxPoolSize);
        config.setPoolName("Hikari x Postgres");
        config.addDataSourceProperty("cachePrepStmts", "false");

        return config;
    }

    @Override
    public DSLContext createContext(Connection connection) {
        return DSL.using(connection, PostgresConnectionFactory.DIALECT);
    }
}

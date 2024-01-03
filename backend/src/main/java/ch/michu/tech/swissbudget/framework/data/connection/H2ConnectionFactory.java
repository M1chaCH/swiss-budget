package ch.michu.tech.swissbudget.framework.data.connection;

import com.zaxxer.hikari.HikariConfig;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

@ApplicationScoped
public class H2ConnectionFactory extends DBConnectionFactory {

    public static final SQLDialect DIALECT = SQLDialect.H2;
    private final int maxPoolSize;

    @Inject
    public H2ConnectionFactory(
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
        config.setIdleTimeout(60 * 1000L); // allow connections to be idle for 1 Minute
        config.setMaximumPoolSize(maxPoolSize);
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

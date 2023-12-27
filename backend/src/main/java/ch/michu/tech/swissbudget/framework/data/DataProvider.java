package ch.michu.tech.swissbudget.framework.data;

import ch.michu.tech.swissbudget.framework.event.EventHandlerPriority;
import ch.michu.tech.swissbudget.framework.event.HandlerPriority;
import ch.michu.tech.swissbudget.framework.event.OnAppStartupListener;
import ch.michu.tech.swissbudget.framework.mail.MailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.SelectLimitStep;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;

// TODO upgrade to connection pool (keep same connection per request) (either use plain hickari connection pool lib or use helidon integration)
// TODO use transaction per request (rollback if any error)

/**
 * simple provider for a global db connections via jOOQ.
 */
@ApplicationScoped
public class DataProvider implements OnAppStartupListener {

    private static final Logger LOGGER = Logger.getLogger(DataProvider.class.getSimpleName());

    private final String dbUser;
    private final String dbPassword;
    private final String dbUrl;
    @Getter
    @Setter
    protected int pageSize;
    private Connection dbConnection;
    private DSLContext dbContext;

    @Inject
    public DataProvider(
        @ConfigProperty(name = "db.user") String dbUser,
        @ConfigProperty(name = "db.password") String dbPassword,
        @ConfigProperty(name = "db.url") String dbUrl,
        @ConfigProperty(name = "db.limit.page.size", defaultValue = "100") int pageSize, MailSender sender
    ) {
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
        this.pageSize = pageSize;
    }

    @Override
    @EventHandlerPriority(HandlerPriority.FIRST)
    public void onAppStartup() {
        try {
            LOGGER.log(Level.INFO, "initializing database connection at: {0}", new Object[]{dbUrl});
            final SQLDialect dialect = dbUrl.contains("h2") ? SQLDialect.H2 : SQLDialect.POSTGRES;
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            dbContext = DSL.using(dbConnection, dialect, new Settings().withRenderSchema(false)); // h2 cant handle a rendered schema
            LOGGER.log(Level.INFO, "successfully initialized DB context");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "connection to db failed", e);
            System.exit(66);
        }
    }

    public DSLContext getContext() {
        return dbContext;
    }

    public <T extends Record> Result<T> fetchWithLimit(SelectLimitStep<T> step, int page) {
        if (page > 1) {
            return step.limit((page - 1) * pageSize, pageSize).fetch();
        } else {
            return step.limit(pageSize).fetch();
        }
    }

    public Connection getConnection() {
        return dbConnection;
    }
}

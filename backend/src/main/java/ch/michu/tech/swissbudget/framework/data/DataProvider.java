package ch.michu.tech.swissbudget.framework.data;

import ch.michu.tech.swissbudget.framework.data.connection.DBConnectionFactory;
import ch.michu.tech.swissbudget.framework.event.EventHandlerPriority;
import ch.michu.tech.swissbudget.framework.event.HandlerPriority;
import ch.michu.tech.swissbudget.framework.event.OnAppStartupListener;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectLimitStep;

/**
 * some data utils
 */
@ApplicationScoped
public class DataProvider implements OnAppStartupListener {

    private static final Logger LOGGER = Logger.getLogger(DataProvider.class.getSimpleName());

    private final String dbUser;
    private final String dbUrl;
    private final DBConnectionFactory connectionFactory;

    @Inject
    public DataProvider(
        @ConfigProperty(name = "db.url") String dbUrl,
        @ConfigProperty(name = "db.user") String dbUser, DBConnectionFactory connectionFactory
    ) {
        this.dbUser = dbUser;
        this.dbUrl = dbUrl;
        this.connectionFactory = connectionFactory;
    }

    public static <T extends Record> Result<T> fetchWithLimit(SelectLimitStep<T> step, int page, int pageSize) {
        if (page > 1) {
            return step.limit((page - 1) * pageSize, pageSize).fetch();
        } else {
            return step.limit(pageSize).fetch();
        }
    }

    @Override
    @EventHandlerPriority(HandlerPriority.FIRST)
    public void onAppStartup() {
        try {
            LOGGER.log(Level.INFO, "testing DB connection at {0} as {1}", new Object[]{dbUrl, dbUser});
            Connection connection = connectionFactory.createConnection();
            connectionFactory.createContext(connection);
            connection.close();
            LOGGER.log(Level.INFO, "successfully initialized DB context");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "connection to db failed", e);
            System.exit(66);
        }
    }
}

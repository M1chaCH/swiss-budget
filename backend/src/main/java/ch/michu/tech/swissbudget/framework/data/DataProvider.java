package ch.michu.tech.swissbudget.framework.data;

import ch.michu.tech.swissbudget.framework.event.EventHandlerPriority;
import ch.michu.tech.swissbudget.framework.event.HandlerPriority;
import ch.michu.tech.swissbudget.framework.event.OnAppStartupListener;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@ApplicationScoped
public class DataProvider implements OnAppStartupListener {

    private static final Logger LOGGER = Logger.getLogger(DataProvider.class.getSimpleName());

    private final String dbUser;
    private final String dbPassword;
    private final String dbUrl;

    private Connection dbConnection;
    private DSLContext dbContext;

    @Inject
    public DataProvider(@ConfigProperty(name = "db.user") String dbUser,
        @ConfigProperty(name = "db.password") String dbPassword,
        @ConfigProperty(name = "db.url") String dbUrl) {
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbUrl = dbUrl;
    }

    @Override
    @EventHandlerPriority(HandlerPriority.FIRST)
    public void onAppStartup() {
        try {
            LOGGER.log(Level.INFO, "initializing database connection at: {0}", new Object[]{dbUrl});
            dbConnection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            dbContext = DSL.using(dbConnection, SQLDialect.POSTGRES);
            LOGGER.log(Level.INFO, "successfully initialized DB context");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "connection to db failed", e);
            System.exit(66);
        }
    }

    public DSLContext getContext() {
        return dbContext;
    }

    public Connection getConnection() {
        return dbConnection;
    }
}

package ch.michu.tech.swissbudget.framework.data.connection;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class DBConnectionFactoryProducer {

    private final String url;
    private final String user;
    private final String password;
    private final int maxPoolSize;

    private DBConnectionFactory currentFactory;

    @Inject
    public DBConnectionFactoryProducer(
        @ConfigProperty(name = "db.url") String url,
        @ConfigProperty(name = "db.user") String user,
        @ConfigProperty(name = "db.password") String password,
        @ConfigProperty(name = "db.max.pool.size", defaultValue = "10") int maxPoolSize
    ) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxPoolSize = maxPoolSize;
    }

    @Produces
    public DBConnectionFactory produceDbConnectionFactory(InjectionPoint injectionPoint) {
        if (currentFactory != null) {
            return currentFactory;
        }

        if (url.contains("h2")) {
            currentFactory = new H2ConnectionFactory(user, password, url, maxPoolSize);
        } else {
            currentFactory = new PostgresConnectionFactory(user, password, url, maxPoolSize);
        }

        return currentFactory;
    }
}

package ch.michu.tech.swissbudget.framework.data.connection;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Dependent
public class DBConnectionFactoryProducer {

    private final String url;
    private final String user;
    private final String password;
    private final int maxPoolSize;

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
        if (url.contains("h2")) {
            return new H2ConnectionFactory(user, password, url, maxPoolSize);
        }

        return new PostgresConnectionFactory(user, password, url, maxPoolSize);
    }
}

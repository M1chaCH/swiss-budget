package ch.michu.tech.swissbudget.framework.data;

import ch.michu.tech.swissbudget.framework.data.connection.DBConnectionFactory;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.sql.Connection;
import org.jooq.DSLContext;

@Provider
@RequestScoped
@Priority(Priorities.USER)
public class RequestConnectionAppender implements ContainerRequestFilter {

    public static final String DB_CONNECTION_PROP = "db-con";

    private final RequestSupport support;
    private final DBConnectionFactory connectionFactory;

    @Inject
    public RequestConnectionAppender(RequestSupport support, DBConnectionFactory connectionFactory) {
        this.support = support;
        this.connectionFactory = connectionFactory;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        support.logFine(this, "creating request DB connection");
        Connection requestConnection = connectionFactory.createConnection();
        DSLContext ctx = connectionFactory.createContext(requestConnection);

        support.setCtx(ctx);
        support.storeProperty(DB_CONNECTION_PROP, requestConnection);

        ctx.startTransaction().execute();
        support.logFine(this, "acquired db connection for request & started transaction");
    }
}

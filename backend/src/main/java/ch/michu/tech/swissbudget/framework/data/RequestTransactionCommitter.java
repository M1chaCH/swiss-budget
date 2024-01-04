package ch.michu.tech.swissbudget.framework.data;

import ch.michu.tech.swissbudget.framework.error.exception.UnexpectedServerException;
import jakarta.annotation.Priority;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;

@Provider
@RequestScoped
@Priority(Priorities.USER)
public class RequestTransactionCommitter implements ContainerResponseFilter {

    /**
     * if true is stored in the request store, then the RequestTransactionCommitter will commit the request transaction even if the request
     * failed.
     */
    public static final String FORCE_COMMIT_PROP = "force-db-transaction-commit";
    private final RequestSupport support;

    @Inject
    public RequestTransactionCommitter(RequestSupport support) {
        this.support = support;
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        DSLContext ctx = support.db();
        try {
            boolean forceCommit = support.loadProperty(FORCE_COMMIT_PROP, Boolean.class).orElse(false);
            if (forceCommit || responseContext.getStatus() < 400) {
                support.logFine(this, "committing request transaction");
                ctx.commit().execute();
            } else {
                support.logFine(this, "detected errors in request, rollback DB");
                ctx.rollback().execute();
            }

            support.loadProperty(RequestConnectionAppender.DB_CONNECTION_PROP, Connection.class).ifPresent(c -> {
                try {
                    c.close();
                } catch (SQLException e) {
                    throw new UnexpectedServerException("could not close request connection", e);
                }
            });

            support.logFine(this, "cleaned request db connection");
        } catch (DataAccessException | NullPointerException e) {
            support.logWarning(this, "could not cleanup request db connection: %s - %s", null, e.getClass().getSimpleName(),
                               e.getMessage());
        }
    }
}

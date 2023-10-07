package ch.michu.tech.swissbudget.app.exception;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;
import org.jooq.exception.DataAccessException;

public class UnexpectedDbException extends AppException {

    public UnexpectedDbException(DataAccessException root) {
        super(true, "failed to access db: " + root.getMessage(), Status.INTERNAL_SERVER_ERROR, root,
            null);
        toggleReportToAdmin();
    }
}

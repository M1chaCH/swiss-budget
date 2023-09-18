package ch.michu.tech.swissbudget.framework.error.exception.mail;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;
import java.util.Map;
import javax.mail.MessagingException;

public class MailConnectionException extends AppException {

    public MailConnectionException(String mail, String host, MessagingException root) {
        super(false, String.format("could not connect %s to %s: %s", mail, host, root.getMessage()),
            Status.UNAUTHORIZED, root,
            Map.of(
                "mail", mail,
                "host", host,
                "message", root.getMessage()
            ));
    }
}

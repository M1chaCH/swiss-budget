package ch.michu.tech.swissbudget.framework.error.exception.mail;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;
import java.util.Map;
import lombok.Getter;

@Getter
public class MailSendException extends AppException {

    private final String messageId;

    public MailSendException(String internalMessageId, Exception root) {
        super(true, String.format("failed to send mail id:%s: %s - %s", internalMessageId,
            root.getClass().getSimpleName(),
            root.getMessage()), Status.BAD_REQUEST, root, Map.of());
        this.messageId = internalMessageId;
    }
}

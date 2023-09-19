package ch.michu.tech.swissbudget.framework.error.exception.mail;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;

@Getter
public class MailTemplateNotFoundException extends AppException {

    private final String templateName;
    private final IOException exception;

    public MailTemplateNotFoundException(String templateName, IOException e) {
        super(true, String.format("could not load mail template: %s", templateName),
            Status.INTERNAL_SERVER_ERROR, e, Map.of());
        this.templateName = templateName;
        this.exception = e;
    }
}

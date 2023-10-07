package ch.michu.tech.swissbudget.framework.error;

import ch.michu.tech.swissbudget.framework.mail.MailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class ErrorReporter {

    protected static final Logger LOGGER = Logger.getLogger(ErrorReporter.class.getSimpleName());

    protected final MailSender mailSender;
    protected final boolean production;

    protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
        "dd MMM. yyyy zzz hh:mm:ss");

    @Inject
    public ErrorReporter(MailSender mailSender,
        @ConfigProperty(name = "ch.michu.tech.production", defaultValue = "true") boolean production) {
        this.mailSender = mailSender;
        this.production = production;
    }

    public void reportError(Throwable e) {
        if (production) {
            try {
                LOGGER.log(Level.FINE, "notifying admin about exception: {0}",
                    new Object[]{e.getClass().getSimpleName()});

                String subject = String.format("SwissBudget Exception: %s",
                    e.getClass().getSimpleName());
                MimeBodyPart body = new MimeBodyPart();
                body.setContent(buildMessage(e), MailSender.HTML_MESSAGE_TYPE);
                mailSender.asyncSendMessageToAdmin(subject, new MimeMultipart(body));
            } catch (Exception exception) {
                LOGGER.log(Level.SEVERE, "failed to report error to admin", exception);
            }
        } else {
            LOGGER.log(Level.WARNING, "WOULD REPORT ERROR (in production)", e);
        }
    }

    protected String buildMessage(Throwable e) {
        ZonedDateTime timestamp = ZonedDateTime.now();
        String message = """
            <h3>Hi mate</h3>
            <p>SwissBudget threw an exception which needed to be reported.</p>
            <h4>Exception: </h4>
            <ul>
                <li>Exception-Class: %s</li>
                <li>Message: %s</li>
                <li>Timetamp: %s</li>
            </ul>
            <p>Stack:<p/>
            <pre>%s</pre>
                        
            <p>Consider taking action upon this.</p>
            <p>Best Regards - michu de dev 🙋‍♂️</p>
            """;

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        e.printStackTrace(printStream);
        printStream.flush();
        String stack = outputStream.toString();

        return String.format(message,
            e.getClass().getName(),
            e.getMessage(),
            formatter.format(timestamp),
            stack
        );
    }
}

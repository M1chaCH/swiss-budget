package ch.michu.tech.swissbudget.app.service.mail;

import ch.michu.tech.swissbudget.framework.mail.MailReader;
import ch.michu.tech.swissbudget.framework.mail.TemplatedMailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Store;

@ApplicationScoped
public class MailService {

    private static final Logger LOGGER = Logger.getLogger(MailService.class.getSimpleName());

    private final MailReader mailReader;
    private final TemplatedMailSender templatedMailSender;

    @Inject
    public MailService(MailReader mailReader, TemplatedMailSender templatedMailSender) {
        this.mailReader = mailReader;
        this.templatedMailSender = templatedMailSender;
    }

    public void testMailConnection(String mail, String password) {
        Store connectedStore = mailReader.openConnection(mail, password);
        try {
            connectedStore.close();
        } catch (MessagingException e) {
            LOGGER.log(Level.FINE, "could not close connection store after testing connection", e);
        }
    }

    public void sendUserMessageToAdmin(String sourceAddress, String subject, String message) {
        subject = "User message - " + subject;
        templatedMailSender.sendMailToAdmin(subject, MailTemplateNames.USER_MESSAGE, Map.of(
            "subject", subject,
            "sender", sourceAddress,
            "message", message
        ));
        LOGGER.log(Level.INFO, "successfully sent user message to admin from {0} with subject {1}",
            new Object[]{sourceAddress, subject});
    }
}

package ch.michu.tech.swissbudget.app.service.mail;

import ch.michu.tech.swissbudget.framework.mail.MailReader;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Store;

@ApplicationScoped
public class MailService {

    private static final Logger LOGGER = Logger.getLogger(MailService.class.getSimpleName());

    private final MailReader mailReader;

    @Inject
    public MailService(MailReader mailReader) {
        this.mailReader = mailReader;
    }

    public void testMailConnection(String mail, String password) {
        Store connectedStore = mailReader.openConnection(mail, password);
        try {
            connectedStore.close();
        } catch (MessagingException e) {
            LOGGER.log(Level.FINE, "could not close connection store after testing connection", e);
        }
    }
}

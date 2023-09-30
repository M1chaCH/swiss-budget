package ch.michu.tech.swissbudget.app.service.mail;

import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.mail.MailConnectionException;
import ch.michu.tech.swissbudget.framework.mail.MailReader;
import ch.michu.tech.swissbudget.framework.mail.TemplatedMailSender;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Map;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

@ApplicationScoped
public class MailService {

    private final MailReader mailReader;
    private final TemplatedMailSender templatedMailSender;
    private final Provider<RequestSupport> supportProvider;

    @Inject
    public MailService(MailReader mailReader, TemplatedMailSender templatedMailSender,
        Provider<RequestSupport> supportProvider) {
        this.mailReader = mailReader;
        this.templatedMailSender = templatedMailSender;
        this.supportProvider = supportProvider;
    }

    public void testMailConnection(String mail, String password) {
        Store connectedStore = mailReader.openConnection(mail, password);
        try {
            connectedStore.close();
        } catch (MessagingException e) {
            supportProvider.get()
                .logFine(this, "could not close connection store after testing connection", e);
        }
    }

    public void createFolder(String mail, String password, String folderName) {
        Store store = mailReader.openConnection(mail, password);
        try {
            Folder folder = store.getFolder(folderName);
            if (!folder.exists()) {
                folder.create(Folder.HOLDS_MESSAGES);
                folder.setSubscribed(true);
            }

            store.close();
        } catch (MessagingException e) {
            throw new MailConnectionException(mail, "undefined", e);
        }
    }

    public void sendUserMessageToAdmin(String sourceAddress, String subject, String message) {
        subject = "User message - " + subject;
        templatedMailSender.sendMailToAdmin(subject, MailTemplateNames.USER_MESSAGE, Map.of(
            "subject", subject,
            "sender", sourceAddress,
            "message", message
        ));
        supportProvider.get()
            .logInfo(this, "successfully sent user message to admin from %s with subject %s",
                sourceAddress, subject);
    }
}

package ch.michu.tech.swissbudget.test;

import ch.michu.tech.swissbudget.framework.error.exception.mail.MailSendException;
import ch.michu.tech.swissbudget.framework.mail.MailSender;
import jakarta.enterprise.inject.Specializes;
import jakarta.inject.Inject;
import java.util.Stack;
import java.util.logging.Level;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;
import lombok.Getter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@Getter
@Specializes
public class TestMailSender extends MailSender {

    private final Stack<SentMessage> messages = new Stack<>();

    @Inject
    public TestMailSender(
        @ConfigProperty(name = "ch.michu.tech.mail.admin.receiver") String adminReceiver,
        @ConfigProperty(name = "ch.michu.tech.mail.sender.address") String senderAddress,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.user") String smtpUser,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.password") String smtpPassword,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.port") String smtpPort,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.server") String smtpServer
    ) throws AddressException {
        super(adminReceiver, senderAddress, smtpUser, smtpPassword, smtpPort, smtpServer);
    }

    @Override
    protected Session createSession() {
        LOGGER.log(Level.INFO, "created mock mail session", new Object[]{});
        return null;
    }

    @Override
    public void sendMessage(String internalMessageId, InternetAddress recipient, String subject, MimeMultipart content)
        throws MailSendException {
        LOGGER.log(Level.INFO, "mock: sent message: {0}-{1}", new Object[]{recipient.getAddress(), subject});
        messages.addElement(new SentMessage(
            internalMessageId,
            recipient,
            subject,
            content
        ));
    }

    public void clearSentMails() {
        this.messages.clear();
    }

    public record SentMessage(
        String internalMessageId,
        InternetAddress recipient,
        String subject,
        MimeMultipart content
    ) {

    }
}

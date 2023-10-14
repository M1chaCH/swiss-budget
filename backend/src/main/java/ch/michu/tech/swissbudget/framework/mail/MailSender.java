package ch.michu.tech.swissbudget.framework.mail;

import ch.michu.tech.swissbudget.framework.error.exception.mail.MailSendException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MailSender {

    public static final String HTML_MESSAGE_TYPE = "text/html; charset=utf-8";
    private static final Logger LOGGER = Logger.getLogger(MailSender.class.getSimpleName());

    private final InternetAddress adminReceiver;
    private final InternetAddress senderAddress;
    private final String smtpUser;
    private final String smtpPassword;
    private final String smtpPort;
    private final String smtpServer;

    private final Session session;
    private final AtomicInteger mailIdCounter = new AtomicInteger(0);
    private final ExecutorService sendExecutor = Executors.newSingleThreadExecutor();

    @Inject
    public MailSender(
        @ConfigProperty(name = "ch.michu.tech.mail.admin.receiver") String adminReceiver,
        @ConfigProperty(name = "ch.michu.tech.mail.sender.address") String senderAddress,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.user") String smtpUser,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.password") String smtpPassword,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.port") String smtpPort,
        @ConfigProperty(name = "ch.michu.tech.mail.smtp.server") String smtpServer
    ) throws AddressException {
        this.adminReceiver = new InternetAddress(adminReceiver);
        this.senderAddress = new InternetAddress(senderAddress);
        this.smtpUser = smtpUser;
        this.smtpPassword = smtpPassword;
        this.smtpPort = smtpPort;
        this.smtpServer = smtpServer;

        session = createSession();
    }

    /**
     * sends a mail to the configured admin mail
     *
     * @param subject the subject of the mail
     * @param content the content of the mail
     * @return true: the mail was sent successfully
     */
    public Future<Boolean> asyncSendMessageToAdmin(String subject, MimeMultipart content) {
        return sendExecutor.submit(() -> {
            try {
                sendMessageToAdmin(subject, content);
                return true;
            } catch (Exception e) {
                return false;
            }
        });
    }

    /**
     * sends mail to the configured admin mail
     *
     * @param subject the subject of the mail
     * @param content the content of the mail
     * @throws MailSendException if a messaging exception was thrown in the process
     */
    public void sendMessageToAdmin(String subject, MimeMultipart content) throws MailSendException {
        sendMessage(getNextId(), adminReceiver, subject, content);
    }

    public void sendMessage(String internalMessageId, InternetAddress recipient, String subject, MimeMultipart content)
        throws MailSendException {
        if (subject == null) {
            subject = "SwissBudget message";
        }
        LOGGER.log(Level.FINE, "sending mail id:{0} to {1} with subject: {2}",
            new Object[]{internalMessageId, recipient.getAddress(), subject});

        try {
            Message message = new MimeMessage(session);
            message.setFrom(senderAddress);
            message.setRecipient(RecipientType.TO, recipient);
            message.setSubject(subject);
            message.setContent(content);

            Transport.send(message);
            LOGGER.log(Level.FINE, "successfully sent mail id:{0}",
                new Object[]{internalMessageId});
        } catch (MessagingException e) {
            throw new MailSendException(internalMessageId, e);
        }
    }

    public String getNextId() {
        return "mail-id-" + mailIdCounter.incrementAndGet();
    }

    protected Session createSession() {
        LOGGER.log(Level.INFO, "creating mail sender session, will send from: {0}:{1} - {2}",
            new Object[]{smtpServer, smtpPort, senderAddress});

        Properties smtpProps = new Properties();
        smtpProps.put("mail.smtp.auth", true);
        smtpProps.put("mail.smtp.starttls.enable", "true");
        smtpProps.put("mail.smtp.host", smtpServer);
        smtpProps.put("mail.smtp.port", smtpPort);
        smtpProps.put("mail.smtp.ssl.trust", smtpServer);

        Session createdSession = Session.getInstance(smtpProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpUser, smtpPassword);
            }
        });

        LOGGER.log(Level.INFO, "successfully created mail session");
        return createdSession;
    }

    InternetAddress getAdminReceiver() {
        return adminReceiver;
    }
}

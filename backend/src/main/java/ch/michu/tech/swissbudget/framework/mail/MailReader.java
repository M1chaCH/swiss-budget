package ch.michu.tech.swissbudget.framework.mail;

import ch.michu.tech.swissbudget.framework.error.exception.mail.MailConnectionException;
import ch.michu.tech.swissbudget.framework.error.exception.mail.MailProviderNotSupportedException;
import ch.michu.tech.swissbudget.framework.event.EventHandlerPriority;
import ch.michu.tech.swissbudget.framework.event.HandlerPriority;
import ch.michu.tech.swissbudget.framework.event.OnAppStartupListener;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

@ApplicationScoped
public class MailReader implements OnAppStartupListener {

    private static final Logger LOGGER = Logger.getLogger(MailReader.class.getSimpleName());
    private final Properties mailProperties = new Properties();

    @Override
    @EventHandlerPriority(HandlerPriority.NOT_APPLICABLE)
    public void onAppStartup() {
        LOGGER.log(Level.INFO, "initializing mail reader", new Object[]{});

        String pathToMailProps = Objects.requireNonNull(
                getClass()
                    .getClassLoader()
                    .getResource("mail.properties"))
            .getPath();
        LOGGER.log(Level.INFO, "loading mail properties from {0} ",
            new Object[]{pathToMailProps});

        try (FileInputStream propertyInput = new FileInputStream(pathToMailProps)) {
            mailProperties.load(propertyInput);
            LOGGER.log(Level.INFO, "successfully loaded mail properties", new Object[]{});
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "could not load mail properties", e);
            System.exit(99);
        }
    }

    public Store openConnection(String mail, String password)
        throws IllegalArgumentException, MailProviderNotSupportedException {
        String mailProvider;
        try {
            mailProvider = mail.split("@")[1].split("\\.")[0].toLowerCase(Locale.ROOT);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(mail + " is not a valid mail address");
        }

        String imapServerUrl = mailProperties.getProperty(
            String.format("mail.reader.%s.url", mailProvider));
        if (imapServerUrl == null) {
            throw new MailProviderNotSupportedException(mailProvider);
        }
        int imapServerPort = Integer.parseInt(
            mailProperties.getProperty(String.format("mail.reader.%s.port", mailProvider), "993"));

        Properties storeProperties = new Properties();
        storeProperties.put("mail.imap.host", imapServerUrl);
        storeProperties.put("mail.imap.port", imapServerPort);
        storeProperties.put("mail.imap.ssl.enable", "true");

        Session mailSession = Session.getInstance(storeProperties);
        try {
            Store mailStore = mailSession.getStore("imaps");
            mailStore.connect(imapServerUrl, imapServerPort, mail, password);
            return mailStore;
        } catch (MessagingException e) {
            throw new MailConnectionException(mail, imapServerUrl, e);
        }
    }
}

package ch.michu.tech.swissbudget.framework.mail;

import ch.michu.tech.swissbudget.framework.error.exception.mail.MailConnectionException;
import ch.michu.tech.swissbudget.framework.error.exception.mail.MailProviderNotSupportedException;
import ch.michu.tech.swissbudget.framework.event.EventHandlerPriority;
import ch.michu.tech.swissbudget.framework.event.HandlerPriority;
import ch.michu.tech.swissbudget.framework.event.OnAppStartupListener;
import jakarta.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;

@ApplicationScoped
public class MailReader implements OnAppStartupListener {

    private static final Logger LOGGER = Logger.getLogger(MailReader.class.getSimpleName());
    private final Properties mailProperties = new Properties();

    @Override
    @EventHandlerPriority(HandlerPriority.NOT_APPLICABLE)
    public void onAppStartup() {
        LOGGER.log(Level.INFO, "initializing mail reader", new Object[]{});

        // read mail properties
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

    /**
     * connect to an SMTP account
     *
     * @param mail     the mail of the account
     * @param password the password to the account
     * @return the store with an open connection <b>NEEDS TO BE CLOSED LATER</b>
     * @throws IllegalArgumentException          if the mail is not in a valid format
     * @throws MailProviderNotSupportedException if the server has no configure SMTP host for the given provider.
     */
    public Store openConnection(String mail, String password) throws IllegalArgumentException, MailProviderNotSupportedException {
        String mailProvider;
        try {
            mailProvider = mail.split("@")[1].split("\\.")[0].toLowerCase(Locale.ROOT);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException(mail + " is not a valid mail address");
        }

        String imapServerUrl = mailProperties.getProperty(String.format("mail.reader.%s.url", mailProvider));
        if (imapServerUrl == null) {
            throw new MailProviderNotSupportedException(mailProvider);
        }
        int imapServerPort = Integer.parseInt(mailProperties.getProperty(String.format("mail.reader.%s.port", mailProvider), "993"));

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

    /**
     * @param store      an open SMTP Store
     * @param folderName the name of the folder to find the messages in
     * @param since      from when to start the search (GT)
     * @return all messages since a given date
     * @throws MessagingException if one of the steps fails (store closed, folder not found, ...)
     */
    public Message[] findMessages(Store store, String folderName, LocalDateTime since) throws MessagingException {
        final SearchTerm dateFilter = new ReceivedDateTerm(ComparisonTerm.GT, Date.from(since.atZone(ZoneId.systemDefault()).toInstant()));

        LOGGER.log(Level.FINE, "searching mails in folder {0}", new Object[]{folderName});

        Folder folder = store.getFolder(folderName);
        folder.open(Folder.READ_ONLY);

        Message[] messages = folder.search(dateFilter);
        LOGGER.log(Level.FINE, "found {0} messages in {1} since {2}", new Object[]{messages.length, folderName, since});
        return messages;
    }
}

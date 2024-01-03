package ch.michu.tech.swissbudget.app.transaction.mail;

import ch.michu.tech.swissbudget.app.exception.InvalidTransactionMailFormatException;
import ch.michu.tech.swissbudget.app.transaction.SupportedBank;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

/**
 * this class is responsible for the parsing of an email
 */
public abstract class MailContentHandler {

    /**
     * parses the content of an email into a TransactionMailRecord (changes are not stored in the DB)
     *
     * @param entity  the entity to fill with values
     * @param message the original message from the IMAP server
     * @throws InvalidTransactionMailFormatException if there was any error while parsing the message, can't really be handled
     */
    public void parseMail(TransactionMailRecord entity, Message message) {
        try {
            entity.setMessageNumber(message.getMessageNumber());
            entity.setId(UUID.randomUUID());

            Address[] froms = message.getFrom();
            entity.setFromMail(froms[0].toString());
            entity.setToMail(
                Arrays.stream(message.getRecipients(RecipientType.TO)).map(Address::toString).collect(Collectors.joining(",")));

            entity.setReceivedDate(message.getReceivedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
            entity.setSubject(message.getSubject());

            MimeMultipart content = (MimeMultipart) message.getContent();
            entity.setRawMessage(content.getBodyPart(0).getContent().toString());
            entity.setBank(getSupportedBank().getKey());
        } catch (MessagingException | IndexOutOfBoundsException | NullPointerException | ClassCastException e) {
            throw new InvalidTransactionMailFormatException(getSupportedBank().name(), "mail has invalid attribute");
        } catch (IOException e) {
            throw new InvalidTransactionMailFormatException(getSupportedBank().name(), "failed to read body");
        }
    }

    /**
     * check if the mail is actually a transaction mail from the current bank
     *
     * @param mail the TransactionMailRecord to analyze
     * @return true: the mail can be parsed into a TransactionRecord
     */
    public abstract boolean validateFromBank(TransactionMailRecord mail);

    /**
     * parses the content of the given mail into a TransactionRecord <br> changes are not stored in the DB
     *
     * @param transaction the record to modify
     * @param mail        the mail to analyze
     */
    public abstract void parseTransaction(TransactionRecord transaction, TransactionMailRecord mail);

    public abstract SupportedBank getSupportedBank();
}

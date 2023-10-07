package ch.michu.tech.swissbudget.app.transaction.mail;

import ch.michu.tech.swissbudget.app.exception.InvalidTransactionMailFromatException;
import ch.michu.tech.swissbudget.app.transaction.SupportedBank;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import java.io.IOException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;

public abstract class MailContentHandler {

    public void parseMail(TransactionMailRecord entity, Message message) {
        try {
            entity.setMessageNumber(message.getMessageNumber());
            entity.setId(entity.getUserId() + "-" + entity.getMessageNumber()); // userId & messageId should form a unique primary key

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
            throw new InvalidTransactionMailFromatException(getSupportedBank().name(), "mail has invalid attribute");
        } catch (IOException e) {
            throw new InvalidTransactionMailFromatException(getSupportedBank().name(), "failed to read body");
        }
    }

    public abstract boolean validateFromBank(TransactionMailRecord mail);

    public abstract void parseTransaction(TransactionRecord record, TransactionMailRecord mail);

    public abstract SupportedBank getSupportedBank();
}

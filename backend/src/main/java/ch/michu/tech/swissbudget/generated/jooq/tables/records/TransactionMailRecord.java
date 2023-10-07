/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables.records;


import ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMail;
import java.time.LocalDateTime;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record10;
import org.jooq.Row10;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class TransactionMailRecord extends UpdatableRecordImpl<TransactionMailRecord> implements
    Record10<String, Integer, String, String, LocalDateTime, String, String, String, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a detached TransactionMailRecord
     */
    public TransactionMailRecord() {
        super(TransactionMail.TRANSACTION_MAIL);
    }

    /**
     * Create a detached, initialised TransactionMailRecord
     */
    public TransactionMailRecord(String id, Integer messageNumber, String fromMail, String toMail, LocalDateTime receivedDate,
        String subject, String rawMessage, String transactionId, String userId, String bank) {
        super(TransactionMail.TRANSACTION_MAIL);

        setId(id);
        setMessageNumber(messageNumber);
        setFromMail(fromMail);
        setToMail(toMail);
        setReceivedDate(receivedDate);
        setSubject(subject);
        setRawMessage(rawMessage);
        setTransactionId(transactionId);
        setUserId(userId);
        setBank(bank);
    }

    /**
     * Getter for <code>public.transaction_mail.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Getter for <code>public.transaction_mail.message_number</code>.
     */
    public Integer getMessageNumber() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.transaction_mail.from_mail</code>.
     */
    public void setFromMail(String value) {
        set(2, value);
    }

    /**
     * Setter for <code>public.transaction_mail.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Setter for <code>public.transaction_mail.to_mail</code>.
     */
    public void setToMail(String value) {
        set(3, value);
    }

    /**
     * Setter for <code>public.transaction_mail.message_number</code>.
     */
    public void setMessageNumber(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.transaction_mail.from_mail</code>.
     */
    public String getFromMail() {
        return (String) get(2);
    }

    /**
     * Getter for <code>public.transaction_mail.to_mail</code>.
     */
    public String getToMail() {
        return (String) get(3);
    }

    /**
     * Getter for <code>public.transaction_mail.received_date</code>.
     */
    public LocalDateTime getReceivedDate() {
        return (LocalDateTime) get(4);
    }

    /**
     * Getter for <code>public.transaction_mail.subject</code>.
     */
    public String getSubject() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.transaction_mail.received_date</code>.
     */
    public void setReceivedDate(LocalDateTime value) {
        set(4, value);
    }

    /**
     * Setter for <code>public.transaction_mail.subject</code>.
     */
    public void setSubject(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>public.transaction_mail.raw_message</code>.
     */
    public String getRawMessage() {
        return (String) get(6);
    }

    /**
     * Setter for <code>public.transaction_mail.raw_message</code>.
     */
    public void setRawMessage(String value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.transaction_mail.transaction_id</code>.
     */
    public String getTransactionId() {
        return (String) get(7);
    }

    /**
     * Getter for <code>public.transaction_mail.user_id</code>.
     */
    public String getUserId() {
        return (String) get(8);
    }

    /**
     * Setter for <code>public.transaction_mail.transaction_id</code>.
     */
    public void setTransactionId(String value) {
        set(7, value);
    }

    /**
     * Setter for <code>public.transaction_mail.user_id</code>.
     */
    public void setUserId(String value) {
        set(8, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>public.transaction_mail.bank</code>.
     */
    public String getBank() {
        return (String) get(9);
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>public.transaction_mail.bank</code>.
     */
    public void setBank(String value) {
        set(9, value);
    }

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    @Override
    public Row10<String, Integer, String, String, LocalDateTime, String, String, String, String, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    @Override
    public Field<Integer> field2() {
        return TransactionMail.TRANSACTION_MAIL.MESSAGE_NUMBER;
    }

    @Override
    public Field<String> field3() {
        return TransactionMail.TRANSACTION_MAIL.FROM_MAIL;
    }

    @Override
    public Field<String> field4() {
        return TransactionMail.TRANSACTION_MAIL.TO_MAIL;
    }

    @Override
    public Row10<String, Integer, String, String, LocalDateTime, String, String, String, String, String> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<String> field6() {
        return TransactionMail.TRANSACTION_MAIL.SUBJECT;
    }

    @Override
    public Field<String> field7() {
        return TransactionMail.TRANSACTION_MAIL.RAW_MESSAGE;
    }

    @Override
    public Field<String> field1() {
        return TransactionMail.TRANSACTION_MAIL.ID;
    }

    @Override
    public Field<LocalDateTime> field5() {
        return TransactionMail.TRANSACTION_MAIL.RECEIVED_DATE;
    }

    @Override
    public Field<String> field8() {
        return TransactionMail.TRANSACTION_MAIL.TRANSACTION_ID;
    }

    @Override
    public Field<String> field9() {
        return TransactionMail.TRANSACTION_MAIL.USER_ID;
    }

    @Override
    public Integer component2() {
        return getMessageNumber();
    }

    @Override
    public String component3() {
        return getFromMail();
    }

    @Override
    public String component4() {
        return getToMail();
    }

    @Override
    public Field<String> field10() {
        return TransactionMail.TRANSACTION_MAIL.BANK;
    }

    @Override
    public String component6() {
        return getSubject();
    }

    @Override
    public String component7() {
        return getRawMessage();
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public LocalDateTime component5() {
        return getReceivedDate();
    }

    @Override
    public String component8() {
        return getTransactionId();
    }

    @Override
    public String component9() {
        return getUserId();
    }

    @Override
    public Integer value2() {
        return getMessageNumber();
    }

    @Override
    public String value3() {
        return getFromMail();
    }

    @Override
    public String value4() {
        return getToMail();
    }

    @Override
    public String component10() {
        return getBank();
    }

    @Override
    public String value6() {
        return getSubject();
    }

    @Override
    public String value7() {
        return getRawMessage();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public LocalDateTime value5() {
        return getReceivedDate();
    }

    @Override
    public String value8() {
        return getTransactionId();
    }

    @Override
    public String value9() {
        return getUserId();
    }

    @Override
    public TransactionMailRecord value2(Integer value) {
        setMessageNumber(value);
        return this;
    }

    @Override
    public TransactionMailRecord value3(String value) {
        setFromMail(value);
        return this;
    }

    @Override
    public TransactionMailRecord value4(String value) {
        setToMail(value);
        return this;
    }

    @Override
    public String value10() {
        return getBank();
    }

    @Override
    public TransactionMailRecord value6(String value) {
        setSubject(value);
        return this;
    }

    @Override
    public TransactionMailRecord value7(String value) {
        setRawMessage(value);
        return this;
    }

    @Override
    public TransactionMailRecord value1(String value) {
        setId(value);
        return this;
    }

    @Override
    public TransactionMailRecord value9(String value) {
        setUserId(value);
        return this;
    }

    @Override
    public TransactionMailRecord value5(LocalDateTime value) {
        setReceivedDate(value);
        return this;
    }

    @Override
    public TransactionMailRecord value8(String value) {
        setTransactionId(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    @Override
    public TransactionMailRecord value10(String value) {
        setBank(value);
        return this;
    }

    @Override
    public TransactionMailRecord values(String value1, Integer value2, String value3, String value4, LocalDateTime value5, String value6,
        String value7, String value8, String value9, String value10) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        return this;
    }
}

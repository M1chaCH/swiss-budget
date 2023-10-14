/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables.records;


import ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMetaData;
import java.time.LocalDateTime;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class TransactionMetaDataRecord extends UpdatableRecordImpl<TransactionMetaDataRecord> implements
    Record5<String, String, LocalDateTime, LocalDateTime, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.transaction_meta_data.user_id</code>.
     */
    public void setUserId(String value) {
        set(0, value);
    }

    /**
     * Create a detached TransactionMetaDataRecord
     */
    public TransactionMetaDataRecord() {
        super(TransactionMetaData.TRANSACTION_META_DATA);
    }

    /**
     * Setter for <code>public.transaction_meta_data.bank</code>.
     */
    public void setBank(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.transaction_meta_data.bank</code>.
     */
    public String getBank() {
        return (String) get(1);
    }

    /**
     * Create a detached, initialised TransactionMetaDataRecord
     */
    public TransactionMetaDataRecord(String userId, String bank, LocalDateTime lastImportCheck, LocalDateTime lastImportedTransaction,
        String transactionsFolder) {
        super(TransactionMetaData.TRANSACTION_META_DATA);

        setUserId(userId);
        setBank(bank);
        setLastImportCheck(lastImportCheck);
        setLastImportedTransaction(lastImportedTransaction);
        setTransactionsFolder(transactionsFolder);
    }

    /**
     * Getter for <code>public.transaction_meta_data.user_id</code>.
     */
    public String getUserId() {
        return (String) get(0);
    }

    /**
     * Getter for <code>public.transaction_meta_data.last_import_check</code>.
     */
    public LocalDateTime getLastImportCheck() {
        return (LocalDateTime) get(2);
    }

    /**
     * Setter for <code>public.transaction_meta_data.last_import_check</code>.
     */
    public void setLastImportCheck(LocalDateTime value) {
        set(2, value);
    }

    /**
     * Getter for
     * <code>public.transaction_meta_data.last_imported_transaction</code>.
     */
    public LocalDateTime getLastImportedTransaction() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for
     * <code>public.transaction_meta_data.last_imported_transaction</code>.
     */
    public void setLastImportedTransaction(LocalDateTime value) {
        set(3, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>public.transaction_meta_data.transactions_folder</code>.
     */
    public String getTransactionsFolder() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.transaction_meta_data.transactions_folder</code>.
     */
    public void setTransactionsFolder(String value) {
        set(4, value);
    }

    @Override
    public Field<String> field1() {
        return TransactionMetaData.TRANSACTION_META_DATA.USER_ID;
    }

    @Override
    public Field<String> field2() {
        return TransactionMetaData.TRANSACTION_META_DATA.BANK;
    }

    @Override
    public Row5<String, String, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<String, String, LocalDateTime, LocalDateTime, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<LocalDateTime> field3() {
        return TransactionMetaData.TRANSACTION_META_DATA.LAST_IMPORT_CHECK;
    }

    @Override
    public String component1() {
        return getUserId();
    }

    @Override
    public String component2() {
        return getBank();
    }

    @Override
    public Field<LocalDateTime> field4() {
        return TransactionMetaData.TRANSACTION_META_DATA.LAST_IMPORTED_TRANSACTION;
    }

    @Override
    public Field<String> field5() {
        return TransactionMetaData.TRANSACTION_META_DATA.TRANSACTIONS_FOLDER;
    }

    @Override
    public LocalDateTime component3() {
        return getLastImportCheck();
    }

    @Override
    public String value1() {
        return getUserId();
    }

    @Override
    public String value2() {
        return getBank();
    }

    @Override
    public LocalDateTime component4() {
        return getLastImportedTransaction();
    }

    @Override
    public String component5() {
        return getTransactionsFolder();
    }

    @Override
    public LocalDateTime value3() {
        return getLastImportCheck();
    }

    @Override
    public TransactionMetaDataRecord value1(String value) {
        setUserId(value);
        return this;
    }

    @Override
    public TransactionMetaDataRecord value2(String value) {
        setBank(value);
        return this;
    }

    @Override
    public LocalDateTime value4() {
        return getLastImportedTransaction();
    }

    @Override
    public String value5() {
        return getTransactionsFolder();
    }

    @Override
    public TransactionMetaDataRecord value3(LocalDateTime value) {
        setLastImportCheck(value);
        return this;
    }

    @Override
    public TransactionMetaDataRecord value4(LocalDateTime value) {
        setLastImportedTransaction(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    @Override
    public TransactionMetaDataRecord value5(String value) {
        setTransactionsFolder(value);
        return this;
    }

    @Override
    public TransactionMetaDataRecord values(String value1, String value2, LocalDateTime value3, LocalDateTime value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }
}

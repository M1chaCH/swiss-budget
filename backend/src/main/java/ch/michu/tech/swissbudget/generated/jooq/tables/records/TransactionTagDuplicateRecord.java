/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables.records;


import ch.michu.tech.swissbudget.generated.jooq.tables.TransactionTagDuplicate;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record4;
import org.jooq.Row4;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class TransactionTagDuplicateRecord extends UpdatableRecordImpl<TransactionTagDuplicateRecord> implements
    Record4<Integer, String, Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.transaction_tag_duplicate.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Create a detached TransactionTagDuplicateRecord
     */
    public TransactionTagDuplicateRecord() {
        super(TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE);
    }

    /**
     * Setter for <code>public.transaction_tag_duplicate.transaction_id</code>.
     */
    public void setTransactionId(String value) {
        set(1, value);
    }

    /**
     * Create a detached, initialised TransactionTagDuplicateRecord
     */
    public TransactionTagDuplicateRecord(Integer id, String transactionId, Integer tagId, Integer matchingKeywordId) {
        super(TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE);

        setId(id);
        setTransactionId(transactionId);
        setTagId(tagId);
        setMatchingKeywordId(matchingKeywordId);
    }

    /**
     * Setter for <code>public.transaction_tag_duplicate.tag_id</code>.
     */
    public void setTagId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.transaction_tag_duplicate.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Getter for <code>public.transaction_tag_duplicate.transaction_id</code>.
     */
    public String getTransactionId() {
        return (String) get(1);
    }

    /**
     * Getter for
     * <code>public.transaction_tag_duplicate.matching_keyword_id</code>.
     */
    public Integer getMatchingKeywordId() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record4 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, Integer, Integer> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    @Override
    public Row4<Integer, String, Integer, Integer> valuesRow() {
        return (Row4) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE.ID;
    }

    @Override
    public Field<String> field2() {
        return TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID;
    }

    @Override
    public Field<Integer> field3() {
        return TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE.TAG_ID;
    }

    @Override
    public Field<Integer> field4() {
        return TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE.MATCHING_KEYWORD_ID;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getTransactionId();
    }

    @Override
    public Integer component3() {
        return getTagId();
    }

    @Override
    public Integer component4() {
        return getMatchingKeywordId();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getTransactionId();
    }

    @Override
    public Integer value3() {
        return getTagId();
    }

    @Override
    public Integer value4() {
        return getMatchingKeywordId();
    }

    @Override
    public TransactionTagDuplicateRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public TransactionTagDuplicateRecord value2(String value) {
        setTransactionId(value);
        return this;
    }

    @Override
    public TransactionTagDuplicateRecord value3(Integer value) {
        setTagId(value);
        return this;
    }

    @Override
    public TransactionTagDuplicateRecord value4(Integer value) {
        setMatchingKeywordId(value);
        return this;
    }

    @Override
    public TransactionTagDuplicateRecord values(Integer value1, String value2, Integer value3, Integer value4) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>public.transaction_tag_duplicate.tag_id</code>.
     */
    public Integer getTagId() {
        return (Integer) get(2);
    }

    /**
     * Setter for
     * <code>public.transaction_tag_duplicate.matching_keyword_id</code>.
     */
    public void setMatchingKeywordId(Integer value) {
        set(3, value);
    }
}
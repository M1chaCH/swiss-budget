/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables;


import ch.michu.tech.swissbudget.generated.jooq.Keys;
import ch.michu.tech.swissbudget.generated.jooq.Public;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import org.jooq.Check;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function12;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row12;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.Internal;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class Transaction extends TableImpl<TransactionRecord> {

    /**
     * The reference instance of <code>public.transaction</code>
     */
    public static final Transaction TRANSACTION = new Transaction();
    private static final long serialVersionUID = 1L;
    /**
     * The column <code>public.transaction.id</code>.
     */
    public final TableField<TransactionRecord, UUID> ID = createField(DSL.name("id"), SQLDataType.UUID.nullable(false), this, "");
    /**
     * The column <code>public.transaction.expense</code>.
     */
    public final TableField<TransactionRecord, Boolean> EXPENSE = createField(DSL.name("expense"), SQLDataType.BOOLEAN.nullable(false),
        this, "");
    /**
     * The column <code>public.transaction.transaction_date</code>.
     */
    public final TableField<TransactionRecord, LocalDate> TRANSACTION_DATE = createField(DSL.name("transaction_date"),
        SQLDataType.LOCALDATE.nullable(false), this, "");
    /**
     * The column <code>public.transaction.bankaccount</code>.
     */
    public final TableField<TransactionRecord, String> BANKACCOUNT = createField(DSL.name("bankaccount"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");
    /**
     * The column <code>public.transaction.amount</code>.
     */
    public final TableField<TransactionRecord, Double> AMOUNT = createField(DSL.name("amount"), SQLDataType.DOUBLE.nullable(false), this,
        "");
    /**
     * The column <code>public.transaction.receiver</code>.
     */
    public final TableField<TransactionRecord, String> RECEIVER = createField(DSL.name("receiver"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");
    /**
     * The column <code>public.transaction.tag_id</code>.
     */
    public final TableField<TransactionRecord, UUID> TAG_ID = createField(DSL.name("tag_id"), SQLDataType.UUID, this, "");
    /**
     * The column <code>public.transaction.matching_keyword_id</code>.
     */
    public final TableField<TransactionRecord, UUID> MATCHING_KEYWORD_ID = createField(DSL.name("matching_keyword_id"), SQLDataType.UUID,
        this, "");
    /**
     * The column <code>public.transaction.need_user_attention</code>.
     */
    public final TableField<TransactionRecord, Boolean> NEED_USER_ATTENTION = createField(DSL.name("need_user_attention"),
        SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field(DSL.raw("true"), SQLDataType.BOOLEAN)), this, "");
    /**
     * The column <code>public.transaction.alias</code>.
     */
    public final TableField<TransactionRecord, String> ALIAS = createField(DSL.name("alias"), SQLDataType.VARCHAR(250), this, "");
    /**
     * The column <code>public.transaction.note</code>.
     */
    public final TableField<TransactionRecord, String> NOTE = createField(DSL.name("note"), SQLDataType.VARCHAR(250), this, "");
    /**
     * The column <code>public.transaction.user_id</code>.
     */
    public final TableField<TransactionRecord, UUID> USER_ID = createField(DSL.name("user_id"), SQLDataType.UUID.nullable(false), this, "");
    private transient Tag _tag;
    private transient Keyword _keyword;
    private transient RegisteredUser _registeredUser;

    private Transaction(Name alias, Table<TransactionRecord> aliased) {
        this(alias, aliased, null);
    }

    private Transaction(Name alias, Table<TransactionRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.transaction</code> table reference
     */
    public Transaction(String alias) {
        this(DSL.name(alias), TRANSACTION);
    }

    /**
     * Create an aliased <code>public.transaction</code> table reference
     */
    public Transaction(Name alias) {
        this(alias, TRANSACTION);
    }

    /**
     * Create a <code>public.transaction</code> table reference
     */
    public Transaction() {
        this(DSL.name("transaction"), null);
    }

    public <O extends Record> Transaction(Table<O> child, ForeignKey<O, TransactionRecord> key) {
        super(child, key, TRANSACTION);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TransactionRecord> getRecordType() {
        return TransactionRecord.class;
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<TransactionRecord> getPrimaryKey() {
        return Keys.TRANSACTION_PKEY;
    }

    @Override
    public List<ForeignKey<TransactionRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TRANSACTION__TRANSACTION_TAG_ID_FKEY, Keys.TRANSACTION__TRANSACTION_MATCHING_KEYWORD_ID_FKEY,
            Keys.TRANSACTION__TRANSACTION_USER_ID_FKEY);
    }

    /**
     * Get the implicit join path to the <code>public.tag</code> table.
     */
    public Tag tag() {
        if (_tag == null) {
            _tag = new Tag(this, Keys.TRANSACTION__TRANSACTION_TAG_ID_FKEY);
        }

        return _tag;
    }

    /**
     * Get the implicit join path to the <code>public.keyword</code> table.
     */
    public Keyword keyword() {
        if (_keyword == null) {
            _keyword = new Keyword(this, Keys.TRANSACTION__TRANSACTION_MATCHING_KEYWORD_ID_FKEY);
        }

        return _keyword;
    }

    /**
     * Get the implicit join path to the <code>public.registered_user</code> table.
     */
    public RegisteredUser registeredUser() {
        if (_registeredUser == null) {
            _registeredUser = new RegisteredUser(this, Keys.TRANSACTION__TRANSACTION_USER_ID_FKEY);
        }

        return _registeredUser;
    }

    @Override
    public List<Check<TransactionRecord>> getChecks() {
        return Arrays.asList(
            Internal.createCheck(this, DSL.name("transaction_amount_check"), "((amount > (0)::double precision))", true)
        );
    }

    @Override
    public Transaction as(String alias) {
        return new Transaction(DSL.name(alias), this);
    }

    @Override
    public Transaction as(Name alias) {
        return new Transaction(alias, this);
    }

    @Override
    public Transaction as(Table<?> alias) {
        return new Transaction(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Transaction rename(String name) {
        return new Transaction(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Transaction rename(Name name) {
        return new Transaction(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Transaction rename(Table<?> name) {
        return new Transaction(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row12 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row12<UUID, Boolean, LocalDate, String, Double, String, UUID, UUID, Boolean, String, String, UUID> fieldsRow() {
        return (Row12) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(
        Function12<? super UUID, ? super Boolean, ? super LocalDate, ? super String, ? super Double, ? super String, ? super UUID, ? super UUID, ? super Boolean, ? super String, ? super String, ? super UUID, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType,
        Function12<? super UUID, ? super Boolean, ? super LocalDate, ? super String, ? super Double, ? super String, ? super UUID, ? super UUID, ? super Boolean, ? super String, ? super String, ? super UUID, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}

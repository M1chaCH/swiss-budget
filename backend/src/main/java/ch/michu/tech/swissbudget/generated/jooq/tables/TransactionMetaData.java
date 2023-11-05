/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables;


import ch.michu.tech.swissbudget.generated.jooq.Keys;
import ch.michu.tech.swissbudget.generated.jooq.Public;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMetaDataRecord;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function5;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row5;
import org.jooq.Schema;
import org.jooq.SelectField;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class TransactionMetaData extends TableImpl<TransactionMetaDataRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.transaction_meta_data</code>
     */
    public static final TransactionMetaData TRANSACTION_META_DATA = new TransactionMetaData();
    private transient RegisteredUser _registeredUser;

    /**
     * The column <code>public.transaction_meta_data.user_id</code>.
     */
    public final TableField<TransactionMetaDataRecord, String> USER_ID = createField(DSL.name("user_id"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");

    /**
     * The column <code>public.transaction_meta_data.bank</code>.
     */
    public final TableField<TransactionMetaDataRecord, String> BANK = createField(DSL.name("bank"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");

    /**
     * The column <code>public.transaction_meta_data.last_import_check</code>.
     */
    public final TableField<TransactionMetaDataRecord, LocalDateTime> LAST_IMPORT_CHECK = createField(DSL.name("last_import_check"),
        SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column
     * <code>public.transaction_meta_data.last_imported_transaction</code>.
     */
    public final TableField<TransactionMetaDataRecord, LocalDateTime> LAST_IMPORTED_TRANSACTION = createField(
        DSL.name("last_imported_transaction"), SQLDataType.LOCALDATETIME(6), this, "");

    /**
     * The column <code>public.transaction_meta_data.transactions_folder</code>.
     */
    public final TableField<TransactionMetaDataRecord, String> TRANSACTIONS_FOLDER = createField(DSL.name("transactions_folder"),
        SQLDataType.VARCHAR(250).nullable(false).defaultValue(DSL.field("'INBOX'::character varying", SQLDataType.VARCHAR)), this, "");

    private TransactionMetaData(Name alias, Table<TransactionMetaDataRecord> aliased) {
        this(alias, aliased, null);
    }

    private TransactionMetaData(Name alias, Table<TransactionMetaDataRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.transaction_meta_data</code> table
     * reference
     */
    public TransactionMetaData(String alias) {
        this(DSL.name(alias), TRANSACTION_META_DATA);
    }

    /**
     * Create an aliased <code>public.transaction_meta_data</code> table
     * reference
     */
    public TransactionMetaData(Name alias) {
        this(alias, TRANSACTION_META_DATA);
    }

    /**
     * Create a <code>public.transaction_meta_data</code> table reference
     */
    public TransactionMetaData() {
        this(DSL.name("transaction_meta_data"), null);
    }

    public <O extends Record> TransactionMetaData(Table<O> child, ForeignKey<O, TransactionMetaDataRecord> key) {
        super(child, key, TRANSACTION_META_DATA);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<TransactionMetaDataRecord> getPrimaryKey() {
        return Keys.TRANSACTION_META_DATA_PKEY;
    }

    @Override
    public List<ForeignKey<TransactionMetaDataRecord, ?>> getReferences() {
        return Arrays.asList(Keys.TRANSACTION_META_DATA__TRANSACTION_META_DATA_USER_ID_FKEY);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<TransactionMetaDataRecord> getRecordType() {
        return TransactionMetaDataRecord.class;
    }

    /**
     * Get the implicit join path to the <code>public.registered_user</code>
     * table.
     */
    public RegisteredUser registeredUser() {
        if (_registeredUser == null)
            _registeredUser = new RegisteredUser(this, Keys.TRANSACTION_META_DATA__TRANSACTION_META_DATA_USER_ID_FKEY);

        return _registeredUser;
    }

    @Override
    public TransactionMetaData as(String alias) {
        return new TransactionMetaData(DSL.name(alias), this);
    }

    @Override
    public TransactionMetaData as(Name alias) {
        return new TransactionMetaData(alias, this);
    }

    @Override
    public TransactionMetaData as(Table<?> alias) {
        return new TransactionMetaData(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionMetaData rename(String name) {
        return new TransactionMetaData(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionMetaData rename(Name name) {
        return new TransactionMetaData(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public TransactionMetaData rename(Table<?> name) {
        return new TransactionMetaData(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, String, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(
        Function5<? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType,
        Function5<? super String, ? super String, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}

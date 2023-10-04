/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables;


import ch.michu.tech.swissbudget.generated.jooq.Keys;
import ch.michu.tech.swissbudget.generated.jooq.Public;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.MfaCodeRecord;
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
public class MfaCode extends TableImpl<MfaCodeRecord> {

    /**
     * The reference instance of <code>public.mfa_code</code>
     */
    public static final MfaCode MFA_CODE = new MfaCode();
    private static final long serialVersionUID = 1L;
    /**
     * The column <code>public.mfa_code.id</code>.
     */
    public final TableField<MfaCodeRecord, String> ID = createField(DSL.name("id"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");
    /**
     * The column <code>public.mfa_code.code</code>.
     */
    public final TableField<MfaCodeRecord, Integer> CODE = createField(DSL.name("code"),
        SQLDataType.INTEGER, this, "");
    /**
     * The column <code>public.mfa_code.user_id</code>.
     */
    public final TableField<MfaCodeRecord, String> USER_ID = createField(DSL.name("user_id"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");
    /**
     * The column <code>public.mfa_code.expires_at</code>.
     */
    public final TableField<MfaCodeRecord, LocalDateTime> EXPIRES_AT = createField(
        DSL.name("expires_at"), SQLDataType.LOCALDATETIME(6).nullable(false), this, "");
    /**
     * The column <code>public.mfa_code.user_agent</code>.
     */
    public final TableField<MfaCodeRecord, String> USER_AGENT = createField(DSL.name("user_agent"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");
    private transient RegisteredUser _registeredUser;

    private MfaCode(Name alias, Table<MfaCodeRecord> aliased) {
        this(alias, aliased, null);
    }

    private MfaCode(Name alias, Table<MfaCodeRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.mfa_code</code> table reference
     */
    public MfaCode(String alias) {
        this(DSL.name(alias), MFA_CODE);
    }

    /**
     * Create an aliased <code>public.mfa_code</code> table reference
     */
    public MfaCode(Name alias) {
        this(alias, MFA_CODE);
    }

    /**
     * Create a <code>public.mfa_code</code> table reference
     */
    public MfaCode() {
        this(DSL.name("mfa_code"), null);
    }

    public <O extends Record> MfaCode(Table<O> child, ForeignKey<O, MfaCodeRecord> key) {
        super(child, key, MFA_CODE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<MfaCodeRecord> getPrimaryKey() {
        return Keys.MFA_CODE_PKEY;
    }

    @Override
    public List<UniqueKey<MfaCodeRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.MFA_CODE_CODE_KEY);
    }

    @Override
    public List<ForeignKey<MfaCodeRecord, ?>> getReferences() {
        return Arrays.asList(Keys.MFA_CODE__MFA_CODE_USER_ID_FKEY);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MfaCodeRecord> getRecordType() {
        return MfaCodeRecord.class;
    }

    /**
     * Get the implicit join path to the <code>public.registered_user</code> table.
     */
    public RegisteredUser registeredUser() {
        if (_registeredUser == null) {
            _registeredUser = new RegisteredUser(this, Keys.MFA_CODE__MFA_CODE_USER_ID_FKEY);
        }

        return _registeredUser;
    }

    @Override
    public MfaCode as(String alias) {
        return new MfaCode(DSL.name(alias), this);
    }

    @Override
    public MfaCode as(Name alias) {
        return new MfaCode(alias, this);
    }

    @Override
    public MfaCode as(Table<?> alias) {
        return new MfaCode(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public MfaCode rename(String name) {
        return new MfaCode(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MfaCode rename(Name name) {
        return new MfaCode(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public MfaCode rename(Table<?> name) {
        return new MfaCode(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row5 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row5<String, Integer, String, LocalDateTime, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(
        Function5<? super String, ? super Integer, ? super String, ? super LocalDateTime, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType,
        Function5<? super String, ? super Integer, ? super String, ? super LocalDateTime, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}
/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables;


import ch.michu.tech.swissbudget.generated.jooq.Keys;
import ch.michu.tech.swissbudget.generated.jooq.Public;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function10;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row10;
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
public class RegisteredUser extends TableImpl<RegisteredUserRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.registered_user</code>
     */
    public static final RegisteredUser REGISTERED_USER = new RegisteredUser();
    /**
     * The column <code>public.registered_user.id</code>.
     */
    public final TableField<RegisteredUserRecord, String> ID = createField(DSL.name("id"), SQLDataType.VARCHAR(250).nullable(false), this,
        "");
    /**
     * The column <code>public.registered_user.mail</code>.
     */
    public final TableField<RegisteredUserRecord, String> MAIL = createField(DSL.name("mail"), SQLDataType.VARCHAR(250).nullable(false),
        this, "");
    /**
     * The column <code>public.registered_user.password</code>.
     */
    public final TableField<RegisteredUserRecord, String> PASSWORD = createField(DSL.name("password"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");
    /**
     * The column <code>public.registered_user.salt</code>.
     */
    public final TableField<RegisteredUserRecord, String> SALT = createField(DSL.name("salt"), SQLDataType.VARCHAR(20).nullable(false),
        this, "");
    /**
     * The column <code>public.registered_user.mail_password</code>.
     */
    public final TableField<RegisteredUserRecord, String> MAIL_PASSWORD = createField(DSL.name("mail_password"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");
    /**
     * The column <code>public.registered_user.username</code>.
     */
    public final TableField<RegisteredUserRecord, String> USERNAME = createField(DSL.name("username"), SQLDataType.VARCHAR(250), this, "");
    /**
     * The column <code>public.registered_user.disabled</code>.
     */
    public final TableField<RegisteredUserRecord, Boolean> DISABLED = createField(DSL.name("disabled"),
        SQLDataType.BOOLEAN.nullable(false).defaultValue(DSL.field("false", SQLDataType.BOOLEAN)), this, "");
    /**
     * The column <code>public.registered_user.created_at</code>.
     */
    public final TableField<RegisteredUserRecord, LocalDateTime> CREATED_AT = createField(DSL.name("created_at"),
        SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");
    /**
     * The column <code>public.registered_user.last_login</code>.
     */
    public final TableField<RegisteredUserRecord, LocalDateTime> LAST_LOGIN = createField(DSL.name("last_login"),
        SQLDataType.LOCALDATETIME(6).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "");
    /**
     * The column <code>public.registered_user.current_session</code>.
     */
    public final TableField<RegisteredUserRecord, String> CURRENT_SESSION = createField(DSL.name("current_session"),
        SQLDataType.VARCHAR(250), this, "");

    public <O extends Record> RegisteredUser(Table<O> child, ForeignKey<O, RegisteredUserRecord> key) {
        super(child, key, REGISTERED_USER);
    }

    private RegisteredUser(Name alias, Table<RegisteredUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private RegisteredUser(Name alias, Table<RegisteredUserRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.registered_user</code> table reference
     */
    public RegisteredUser(String alias) {
        this(DSL.name(alias), REGISTERED_USER);
    }

    /**
     * Create an aliased <code>public.registered_user</code> table reference
     */
    public RegisteredUser(Name alias) {
        this(alias, REGISTERED_USER);
    }

    /**
     * Create a <code>public.registered_user</code> table reference
     */
    public RegisteredUser() {
        this(DSL.name("registered_user"), null);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<RegisteredUserRecord> getRecordType() {
        return RegisteredUserRecord.class;
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public UniqueKey<RegisteredUserRecord> getPrimaryKey() {
        return Keys.REGISTERED_USER_PKEY;
    }

    @Override
    public List<UniqueKey<RegisteredUserRecord>> getUniqueKeys() {
        return Arrays.asList(Keys.REGISTERED_USER_MAIL_KEY);
    }

    @Override
    public RegisteredUser as(String alias) {
        return new RegisteredUser(DSL.name(alias), this);
    }

    @Override
    public RegisteredUser as(Name alias) {
        return new RegisteredUser(alias, this);
    }

    @Override
    public RegisteredUser as(Table<?> alias) {
        return new RegisteredUser(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public RegisteredUser rename(String name) {
        return new RegisteredUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public RegisteredUser rename(Name name) {
        return new RegisteredUser(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public RegisteredUser rename(Table<?> name) {
        return new RegisteredUser(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row10 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(
        Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Boolean, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType,
        Function10<? super String, ? super String, ? super String, ? super String, ? super String, ? super String, ? super Boolean, ? super LocalDateTime, ? super LocalDateTime, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}

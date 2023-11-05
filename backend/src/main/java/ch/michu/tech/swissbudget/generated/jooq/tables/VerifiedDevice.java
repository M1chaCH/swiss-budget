/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables;


import ch.michu.tech.swissbudget.generated.jooq.Keys;
import ch.michu.tech.swissbudget.generated.jooq.Public;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.VerifiedDeviceRecord;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function3;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row3;
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
public class VerifiedDevice extends TableImpl<VerifiedDeviceRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.verified_device</code>
     */
    public static final VerifiedDevice VERIFIED_DEVICE = new VerifiedDevice();
    private transient RegisteredUser _registeredUser;

    /**
     * The column <code>public.verified_device.id</code>.
     */
    public final TableField<VerifiedDeviceRecord, Integer> ID = createField(DSL.name("id"),
        SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.verified_device.user_id</code>.
     */
    public final TableField<VerifiedDeviceRecord, String> USER_ID = createField(DSL.name("user_id"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");

    /**
     * The column <code>public.verified_device.user_agent</code>.
     */
    public final TableField<VerifiedDeviceRecord, String> USER_AGENT = createField(DSL.name("user_agent"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");

    private VerifiedDevice(Name alias, Table<VerifiedDeviceRecord> aliased) {
        this(alias, aliased, null);
    }

    private VerifiedDevice(Name alias, Table<VerifiedDeviceRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.verified_device</code> table reference
     */
    public VerifiedDevice(String alias) {
        this(DSL.name(alias), VERIFIED_DEVICE);
    }

    /**
     * Create an aliased <code>public.verified_device</code> table reference
     */
    public VerifiedDevice(Name alias) {
        this(alias, VERIFIED_DEVICE);
    }

    /**
     * Create a <code>public.verified_device</code> table reference
     */
    public VerifiedDevice() {
        this(DSL.name("verified_device"), null);
    }

    public <O extends Record> VerifiedDevice(Table<O> child, ForeignKey<O, VerifiedDeviceRecord> key) {
        super(child, key, VERIFIED_DEVICE);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<VerifiedDeviceRecord, Integer> getIdentity() {
        return (Identity<VerifiedDeviceRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<VerifiedDeviceRecord> getPrimaryKey() {
        return Keys.VERIFIED_DEVICE_PKEY;
    }

    @Override
    public List<ForeignKey<VerifiedDeviceRecord, ?>> getReferences() {
        return Arrays.asList(Keys.VERIFIED_DEVICE__VERIFIED_DEVICE_USER_ID_FKEY);
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<VerifiedDeviceRecord> getRecordType() {
        return VerifiedDeviceRecord.class;
    }

    /**
     * Get the implicit join path to the <code>public.registered_user</code>
     * table.
     */
    public RegisteredUser registeredUser() {
        if (_registeredUser == null)
            _registeredUser = new RegisteredUser(this, Keys.VERIFIED_DEVICE__VERIFIED_DEVICE_USER_ID_FKEY);

        return _registeredUser;
    }

    @Override
    public VerifiedDevice as(String alias) {
        return new VerifiedDevice(DSL.name(alias), this);
    }

    @Override
    public VerifiedDevice as(Name alias) {
        return new VerifiedDevice(alias, this);
    }

    @Override
    public VerifiedDevice as(Table<?> alias) {
        return new VerifiedDevice(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public VerifiedDevice rename(String name) {
        return new VerifiedDevice(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public VerifiedDevice rename(Name name) {
        return new VerifiedDevice(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public VerifiedDevice rename(Table<?> name) {
        return new VerifiedDevice(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(Function3<? super Integer, ? super String, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class,
     * Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType, Function3<? super Integer, ? super String, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}

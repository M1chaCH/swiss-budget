/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables.records;


import ch.michu.tech.swissbudget.generated.jooq.tables.VerifiedDevice;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class VerifiedDeviceRecord extends UpdatableRecordImpl<VerifiedDeviceRecord> implements
    Record3<Integer, String, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a detached VerifiedDeviceRecord
     */
    public VerifiedDeviceRecord() {
        super(VerifiedDevice.VERIFIED_DEVICE);
    }

    /**
     * Create a detached, initialised VerifiedDeviceRecord
     */
    public VerifiedDeviceRecord(Integer id, String userId, String userAgent) {
        super(VerifiedDevice.VERIFIED_DEVICE);

        setId(id);
        setUserId(userId);
        setUserAgent(userAgent);
    }

    /**
     * Getter for <code>public.verified_device.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.verified_device.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.verified_device.user_id</code>.
     */
    public String getUserId() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.verified_device.user_id</code>.
     */
    public void setUserId(String value) {
        set(1, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>public.verified_device.user_agent</code>.
     */
    public String getUserAgent() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>public.verified_device.user_agent</code>.
     */
    public void setUserAgent(String value) {
        set(2, value);
    }

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    @Override
    public Row3<Integer, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return VerifiedDevice.VERIFIED_DEVICE.ID;
    }

    @Override
    public Field<String> field2() {
        return VerifiedDevice.VERIFIED_DEVICE.USER_ID;
    }

    @Override
    public Field<String> field3() {
        return VerifiedDevice.VERIFIED_DEVICE.USER_AGENT;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getUserId();
    }

    @Override
    public String component3() {
        return getUserAgent();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getUserId();
    }

    @Override
    public String value3() {
        return getUserAgent();
    }

    @Override
    public VerifiedDeviceRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public VerifiedDeviceRecord value2(String value) {
        setUserId(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    @Override
    public VerifiedDeviceRecord value3(String value) {
        setUserAgent(value);
        return this;
    }

    @Override
    public VerifiedDeviceRecord values(Integer value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }
}
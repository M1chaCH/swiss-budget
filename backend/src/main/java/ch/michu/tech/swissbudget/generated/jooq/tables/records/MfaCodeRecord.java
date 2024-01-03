/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables.records;


import ch.michu.tech.swissbudget.generated.jooq.tables.MfaCode;
import java.time.LocalDateTime;
import java.util.UUID;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes", "this-escape"})
public class MfaCodeRecord extends UpdatableRecordImpl<MfaCodeRecord> implements
    Record6<UUID, Integer, UUID, LocalDateTime, Integer, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a detached MfaCodeRecord
     */
    public MfaCodeRecord() {
        super(MfaCode.MFA_CODE);
    }

    /**
     * Create a detached, initialised MfaCodeRecord
     */
    public MfaCodeRecord(UUID id, Integer code, UUID userId, LocalDateTime expiresAt, Integer tries, String userAgent) {
        super(MfaCode.MFA_CODE);

        setId(id);
        setCode(code);
        setUserId(userId);
        setExpiresAt(expiresAt);
        setTries(tries);
        setUserAgent(userAgent);
        resetChangedOnNotNull();
    }

    /**
     * Getter for <code>public.mfa_code.id</code>.
     */
    public UUID getId() {
        return (UUID) get(0);
    }

    /**
     * Setter for <code>public.mfa_code.id</code>.
     */
    public void setId(UUID value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.mfa_code.code</code>.
     */
    public Integer getCode() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>public.mfa_code.code</code>.
     */
    public void setCode(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.mfa_code.user_id</code>.
     */
    public UUID getUserId() {
        return (UUID) get(2);
    }

    /**
     * Setter for <code>public.mfa_code.user_id</code>.
     */
    public void setUserId(UUID value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.mfa_code.expires_at</code>.
     */
    public LocalDateTime getExpiresAt() {
        return (LocalDateTime) get(3);
    }

    /**
     * Setter for <code>public.mfa_code.expires_at</code>.
     */
    public void setExpiresAt(LocalDateTime value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.mfa_code.tries</code>.
     */
    public Integer getTries() {
        return (Integer) get(4);
    }

    /**
     * Setter for <code>public.mfa_code.tries</code>.
     */
    public void setTries(Integer value) {
        set(4, value);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * Getter for <code>public.mfa_code.user_agent</code>.
     */
    public String getUserAgent() {
        return (String) get(5);
    }

    // -------------------------------------------------------------------------
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * Setter for <code>public.mfa_code.user_agent</code>.
     */
    public void setUserAgent(String value) {
        set(5, value);
    }

    @Override
    public Record1<UUID> key() {
        return (Record1) super.key();
    }

    @Override
    public Row6<UUID, Integer, UUID, LocalDateTime, Integer, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    @Override
    public Row6<UUID, Integer, UUID, LocalDateTime, Integer, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    @Override
    public Field<UUID> field1() {
        return MfaCode.MFA_CODE.ID;
    }

    @Override
    public Field<Integer> field2() {
        return MfaCode.MFA_CODE.CODE;
    }

    @Override
    public Field<UUID> field3() {
        return MfaCode.MFA_CODE.USER_ID;
    }

    @Override
    public Field<LocalDateTime> field4() {
        return MfaCode.MFA_CODE.EXPIRES_AT;
    }

    @Override
    public Field<Integer> field5() {
        return MfaCode.MFA_CODE.TRIES;
    }

    @Override
    public Field<String> field6() {
        return MfaCode.MFA_CODE.USER_AGENT;
    }

    @Override
    public UUID component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getCode();
    }

    @Override
    public UUID component3() {
        return getUserId();
    }

    @Override
    public LocalDateTime component4() {
        return getExpiresAt();
    }

    @Override
    public Integer component5() {
        return getTries();
    }

    @Override
    public String component6() {
        return getUserAgent();
    }

    @Override
    public UUID value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getCode();
    }

    @Override
    public UUID value3() {
        return getUserId();
    }

    @Override
    public LocalDateTime value4() {
        return getExpiresAt();
    }

    @Override
    public Integer value5() {
        return getTries();
    }

    @Override
    public String value6() {
        return getUserAgent();
    }

    @Override
    public MfaCodeRecord value1(UUID value) {
        setId(value);
        return this;
    }

    @Override
    public MfaCodeRecord value2(Integer value) {
        setCode(value);
        return this;
    }

    @Override
    public MfaCodeRecord value3(UUID value) {
        setUserId(value);
        return this;
    }

    @Override
    public MfaCodeRecord value4(LocalDateTime value) {
        setExpiresAt(value);
        return this;
    }

    @Override
    public MfaCodeRecord value5(Integer value) {
        setTries(value);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    @Override
    public MfaCodeRecord value6(String value) {
        setUserAgent(value);
        return this;
    }

    @Override
    public MfaCodeRecord values(UUID value1, Integer value2, UUID value3, LocalDateTime value4, Integer value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }
}

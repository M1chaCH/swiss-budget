/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables.records;


import ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser;
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
public class RegisteredUserRecord extends UpdatableRecordImpl<RegisteredUserRecord> implements
    Record10<String, String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Create a detached RegisteredUserRecord
     */
    public RegisteredUserRecord() {
        super(RegisteredUser.REGISTERED_USER);
    }

    /**
     * Getter for <code>public.registered_user.id</code>.
     */
    public String getId() {
        return (String) get(0);
    }

    /**
     * Setter for <code>public.registered_user.mail</code>.
     */
    public void setMail(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.registered_user.mail</code>.
     */
    public String getMail() {
        return (String) get(1);
    }

    /**
     * Create a detached, initialised RegisteredUserRecord
     */
    public RegisteredUserRecord(String id, String mail, String password, String salt, String mailPassword, String username,
        Boolean disabled, LocalDateTime createdAt, LocalDateTime lastLogin, String currentSession) {
        super(RegisteredUser.REGISTERED_USER);

        setId(id);
        setMail(mail);
        setPassword(password);
        setSalt(salt);
        setMailPassword(mailPassword);
        setUsername(username);
        setDisabled(disabled);
        setCreatedAt(createdAt);
        setLastLogin(lastLogin);
        setCurrentSession(currentSession);
    }

    /**
     * Setter for <code>public.registered_user.id</code>.
     */
    public void setId(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.registered_user.password</code>.
     */
    public String getPassword() {
        return (String) get(2);
    }

    /**
     * Getter for <code>public.registered_user.salt</code>.
     */
    public String getSalt() {
        return (String) get(3);
    }

    /**
     * Setter for <code>public.registered_user.mail_password</code>.
     */
    public void setMailPassword(String value) {
        set(4, value);
    }

    /**
     * Setter for <code>public.registered_user.password</code>.
     */
    public void setPassword(String value) {
        set(2, value);
    }

    /**
     * Setter for <code>public.registered_user.username</code>.
     */
    public void setUsername(String value) {
        set(5, value);
    }

    /**
     * Setter for <code>public.registered_user.salt</code>.
     */
    public void setSalt(String value) {
        set(3, value);
    }

    /**
     * Setter for <code>public.registered_user.disabled</code>.
     */
    public void setDisabled(Boolean value) {
        set(6, value);
    }

    /**
     * Getter for <code>public.registered_user.mail_password</code>.
     */
    public String getMailPassword() {
        return (String) get(4);
    }

    /**
     * Setter for <code>public.registered_user.created_at</code>.
     */
    public void setCreatedAt(LocalDateTime value) {
        set(7, value);
    }

    /**
     * Getter for <code>public.registered_user.created_at</code>.
     */
    public LocalDateTime getCreatedAt() {
        return (LocalDateTime) get(7);
    }

    /**
     * Setter for <code>public.registered_user.last_login</code>.
     */
    public void setLastLogin(LocalDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>public.registered_user.username</code>.
     */
    public String getUsername() {
        return (String) get(5);
    }

    /**
     * Getter for <code>public.registered_user.disabled</code>.
     */
    public Boolean getDisabled() {
        return (Boolean) get(6);
    }

    /**
     * Getter for <code>public.registered_user.last_login</code>.
     */
    public LocalDateTime getLastLogin() {
        return (LocalDateTime) get(8);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<String> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record10 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row10<String, String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime, String> fieldsRow() {
        return (Row10) super.fieldsRow();
    }

    /**
     * Getter for <code>public.registered_user.current_session</code>.
     */
    public String getCurrentSession() {
        return (String) get(9);
    }

    /**
     * Setter for <code>public.registered_user.current_session</code>.
     */
    public void setCurrentSession(String value) {
        set(9, value);
    }

    @Override
    public Field<String> field2() {
        return RegisteredUser.REGISTERED_USER.MAIL;
    }

    @Override
    public Field<String> field3() {
        return RegisteredUser.REGISTERED_USER.PASSWORD;
    }

    @Override
    public Field<String> field4() {
        return RegisteredUser.REGISTERED_USER.SALT;
    }

    @Override
    public Field<String> field5() {
        return RegisteredUser.REGISTERED_USER.MAIL_PASSWORD;
    }

    @Override
    public Field<String> field6() {
        return RegisteredUser.REGISTERED_USER.USERNAME;
    }

    @Override
    public Field<Boolean> field7() {
        return RegisteredUser.REGISTERED_USER.DISABLED;
    }

    @Override
    public Field<LocalDateTime> field8() {
        return RegisteredUser.REGISTERED_USER.CREATED_AT;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return RegisteredUser.REGISTERED_USER.LAST_LOGIN;
    }

    @Override
    public Row10<String, String, String, String, String, String, Boolean, LocalDateTime, LocalDateTime, String> valuesRow() {
        return (Row10) super.valuesRow();
    }

    @Override
    public Field<String> field1() {
        return RegisteredUser.REGISTERED_USER.ID;
    }

    @Override
    public String component2() {
        return getMail();
    }

    @Override
    public String component3() {
        return getPassword();
    }

    @Override
    public String component4() {
        return getSalt();
    }

    @Override
    public String component5() {
        return getMailPassword();
    }

    @Override
    public String component6() {
        return getUsername();
    }

    @Override
    public Boolean component7() {
        return getDisabled();
    }

    @Override
    public LocalDateTime component8() {
        return getCreatedAt();
    }

    @Override
    public LocalDateTime component9() {
        return getLastLogin();
    }

    @Override
    public Field<String> field10() {
        return RegisteredUser.REGISTERED_USER.CURRENT_SESSION;
    }

    @Override
    public String component1() {
        return getId();
    }

    @Override
    public String value2() {
        return getMail();
    }

    @Override
    public String value3() {
        return getPassword();
    }

    @Override
    public String value4() {
        return getSalt();
    }

    @Override
    public String value5() {
        return getMailPassword();
    }

    @Override
    public String value6() {
        return getUsername();
    }

    @Override
    public Boolean value7() {
        return getDisabled();
    }

    @Override
    public LocalDateTime value8() {
        return getCreatedAt();
    }

    @Override
    public LocalDateTime value9() {
        return getLastLogin();
    }

    @Override
    public String component10() {
        return getCurrentSession();
    }

    @Override
    public String value1() {
        return getId();
    }

    @Override
    public RegisteredUserRecord value2(String value) {
        setMail(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value3(String value) {
        setPassword(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value4(String value) {
        setSalt(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value5(String value) {
        setMailPassword(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value6(String value) {
        setUsername(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value7(Boolean value) {
        setDisabled(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value8(LocalDateTime value) {
        setCreatedAt(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value9(LocalDateTime value) {
        setLastLogin(value);
        return this;
    }

    @Override
    public RegisteredUserRecord value10(String value) {
        setCurrentSession(value);
        return this;
    }

    @Override
    public RegisteredUserRecord values(String value1, String value2, String value3, String value4, String value5, String value6,
        Boolean value7, LocalDateTime value8, LocalDateTime value9, String value10) {
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

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    @Override
    public String value10() {
        return getCurrentSession();
    }

    @Override
    public RegisteredUserRecord value1(String value) {
        setId(value);
        return this;
    }
}

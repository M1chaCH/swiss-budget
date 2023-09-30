/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq.tables;


import ch.michu.tech.swissbudget.generated.jooq.Keys;
import ch.michu.tech.swissbudget.generated.jooq.Public;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Function4;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Records;
import org.jooq.Row4;
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
public class Keyword extends TableImpl<KeywordRecord> {

    /**
     * The reference instance of <code>public.keyword</code>
     */
    public static final Keyword KEYWORD = new Keyword();
    private static final long serialVersionUID = 1L;
    /**
     * The column <code>public.keyword.user_id</code>.
     */
    public final TableField<KeywordRecord, String> USER_ID = createField(DSL.name("user_id"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");

    /**
     * The column <code>public.keyword.id</code>.
     */
    public final TableField<KeywordRecord, Integer> ID = createField(DSL.name("id"),
        SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.keyword.keyword</code>.
     */
    public final TableField<KeywordRecord, String> KEYWORD_ = createField(DSL.name("keyword"),
        SQLDataType.VARCHAR(250).nullable(false), this, "");

    /**
     * The column <code>public.keyword.tag_id</code>.
     */
    public final TableField<KeywordRecord, Integer> TAG_ID = createField(DSL.name("tag_id"),
        SQLDataType.INTEGER, this, "");
    private transient Tag _tag;
    private transient RegisteredUser _registeredUser;

    private Keyword(Name alias, Table<KeywordRecord> aliased) {
        this(alias, aliased, null);
    }

    private Keyword(Name alias, Table<KeywordRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>public.keyword</code> table reference
     */
    public Keyword(String alias) {
        this(DSL.name(alias), KEYWORD);
    }

    /**
     * Create an aliased <code>public.keyword</code> table reference
     */
    public Keyword(Name alias) {
        this(alias, KEYWORD);
    }

    /**
     * Create a <code>public.keyword</code> table reference
     */
    public Keyword() {
        this(DSL.name("keyword"), null);
    }

    public <O extends Record> Keyword(Table<O> child, ForeignKey<O, KeywordRecord> key) {
        super(child, key, KEYWORD);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<KeywordRecord, Integer> getIdentity() {
        return (Identity<KeywordRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<KeywordRecord> getPrimaryKey() {
        return Keys.KEYWORD_PKEY;
    }

    /**
     * The class holding records for this type
     */
    @Override
    public Class<KeywordRecord> getRecordType() {
        return KeywordRecord.class;
    }

    @Override
    public List<ForeignKey<KeywordRecord, ?>> getReferences() {
        return Arrays.asList(Keys.KEYWORD__KEYWORD_TAG_ID_FKEY, Keys.KEYWORD__KEYWORD_USER_ID_FKEY);
    }

    /**
     * Get the implicit join path to the <code>public.tag</code> table.
     */
    public Tag tag() {
        if (_tag == null) {
            _tag = new Tag(this, Keys.KEYWORD__KEYWORD_TAG_ID_FKEY);
        }

        return _tag;
    }

    /**
     * Get the implicit join path to the <code>public.registered_user</code> table.
     */
    public RegisteredUser registeredUser() {
        if (_registeredUser == null) {
            _registeredUser = new RegisteredUser(this, Keys.KEYWORD__KEYWORD_USER_ID_FKEY);
        }

        return _registeredUser;
    }

    @Override
    public Keyword as(String alias) {
        return new Keyword(DSL.name(alias), this);
    }

    @Override
    public Keyword as(Name alias) {
        return new Keyword(alias, this);
    }

    @Override
    public Keyword as(Table<?> alias) {
        return new Keyword(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Keyword rename(String name) {
        return new Keyword(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Keyword rename(Name name) {
        return new Keyword(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Keyword rename(Table<?> name) {
        return new Keyword(name.getQualifiedName(), null);
    }

    // -------------------------------------------------------------------------
    // Row4 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row4<Integer, String, Integer, String> fieldsRow() {
        return (Row4) super.fieldsRow();
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Function)}.
     */
    public <U> SelectField<U> mapping(
        Function4<? super Integer, ? super String, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(Records.mapping(from));
    }

    /**
     * Convenience mapping calling {@link SelectField#convertFrom(Class, Function)}.
     */
    public <U> SelectField<U> mapping(Class<U> toType,
        Function4<? super Integer, ? super String, ? super Integer, ? super String, ? extends U> from) {
        return convertFrom(toType, Records.mapping(from));
    }
}

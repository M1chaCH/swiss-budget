/*
 * This file is generated by jOOQ.
 */
package ch.michu.tech.swissbudget.generated.jooq;


import ch.michu.tech.swissbudget.generated.jooq.tables.Keyword;
import ch.michu.tech.swissbudget.generated.jooq.tables.MfaCode;
import ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser;
import ch.michu.tech.swissbudget.generated.jooq.tables.Tag;
import ch.michu.tech.swissbudget.generated.jooq.tables.Transaction;
import ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMail;
import ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMetaData;
import ch.michu.tech.swissbudget.generated.jooq.tables.TransactionTagDuplicate;
import ch.michu.tech.swissbudget.generated.jooq.tables.VerifiedDevice;
import java.util.Arrays;
import java.util.List;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Public extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public</code>
     */
    public static final Public PUBLIC = new Public();

    /**
     * The table <code>public.keyword</code>.
     */
    public final Keyword KEYWORD = Keyword.KEYWORD;

    /**
     * The table <code>public.mfa_code</code>.
     */
    public final MfaCode MFA_CODE = MfaCode.MFA_CODE;

    /**
     * The table <code>public.registered_user</code>.
     */
    public final RegisteredUser REGISTERED_USER = RegisteredUser.REGISTERED_USER;

    /**
     * The table <code>public.tag</code>.
     */
    public final Tag TAG = Tag.TAG;

    /**
     * The table <code>public.transaction</code>.
     */
    public final Transaction TRANSACTION = Transaction.TRANSACTION;

    /**
     * The table <code>public.transaction_mail</code>.
     */
    public final TransactionMail TRANSACTION_MAIL = TransactionMail.TRANSACTION_MAIL;

    /**
     * The table <code>public.transaction_meta_data</code>.
     */
    public final TransactionMetaData TRANSACTION_META_DATA = TransactionMetaData.TRANSACTION_META_DATA;

    /**
     * The table <code>public.transaction_tag_duplicate</code>.
     */
    public final TransactionTagDuplicate TRANSACTION_TAG_DUPLICATE = TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE;

    /**
     * The table <code>public.verified_device</code>.
     */
    public final VerifiedDevice VERIFIED_DEVICE = VerifiedDevice.VERIFIED_DEVICE;

    /**
     * No further instances allowed
     */
    private Public() {
        super("public", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.asList(
            Keyword.KEYWORD,
            MfaCode.MFA_CODE,
            RegisteredUser.REGISTERED_USER,
            Tag.TAG,
            Transaction.TRANSACTION,
            TransactionMail.TRANSACTION_MAIL,
            TransactionMetaData.TRANSACTION_META_DATA,
            TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE,
            VerifiedDevice.VERIFIED_DEVICE
        );
    }
}

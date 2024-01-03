package ch.michu.tech.swissbudget.app.provider;

import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMail.TRANSACTION_MAIL;

import ch.michu.tech.swissbudget.framework.data.BaseRecordProvider;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;

@ApplicationScoped
public class TransactionMailProvider implements BaseRecordProvider<TransactionMailRecord, UUID> {

    protected final DataProvider data;
    protected final DSLContext db;

    @Inject
    public TransactionMailProvider(DataProvider data) {
        this.data = data;
        this.db = data.getContext();
    }

    @Override
    public TransactionMailRecord newRecord() {
        return db.newRecord(TRANSACTION_MAIL);
    }

    @Override
    public TransactionMailRecord fromRecord(Record result) {
        TransactionMailRecord mail = newRecord();

        mail.setId(result.getValue(TRANSACTION_MAIL.ID));
        mail.setMessageNumber(result.getValue(TRANSACTION_MAIL.MESSAGE_NUMBER));
        mail.setFromMail(result.getValue(TRANSACTION_MAIL.FROM_MAIL));
        mail.setToMail(result.getValue(TRANSACTION_MAIL.TO_MAIL));
        mail.setReceivedDate(result.getValue(TRANSACTION_MAIL.RECEIVED_DATE));
        mail.setSubject(result.getValue(TRANSACTION_MAIL.SUBJECT));
        mail.setRawMessage(result.getValue(TRANSACTION_MAIL.RAW_MESSAGE));
        mail.setTransactionId(result.getValue(TRANSACTION_MAIL.TRANSACTION_ID));
        mail.setUserId(result.getValue(TRANSACTION_MAIL.USER_ID));
        mail.setBank(result.getValue(TRANSACTION_MAIL.BANK));

        return mail;
    }

    @Override
    @LoggedStatement
    public boolean fetchExists(UUID userId, UUID recordId) {
        Condition userCondition = TRANSACTION_MAIL.USER_ID.eq(userId);
        Condition transactionMailCondition = TRANSACTION_MAIL.ID.eq(recordId);

        return db.fetchExists(TRANSACTION_MAIL, userCondition, transactionMailCondition);
    }
}

package ch.michu.tech.swissbudget.app.provider;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser.REGISTERED_USER;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMail.TRANSACTION_MAIL;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMetaData.TRANSACTION_META_DATA;

import ch.michu.tech.swissbudget.app.dto.KeywordDto;
import ch.michu.tech.swissbudget.app.dto.TagDto;
import ch.michu.tech.swissbudget.app.dto.TransactionDto;
import ch.michu.tech.swissbudget.app.entity.CompleteTransactionEntity;
import ch.michu.tech.swissbudget.app.exception.UnexpectedDbException;
import ch.michu.tech.swissbudget.app.transaction.TransactionImporter;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import ch.michu.tech.swissbudget.framework.error.exception.UnexpectedServerException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectLimitStep;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings("unused")
@ApplicationScoped
public class TransactionProvider {

    protected final DataProvider data;
    protected final DSLContext db;

    @Inject
    public TransactionProvider(DataProvider data) {
        this.data = data;
        this.db = data.getContext();
    }

    public TransactionRecord newTransaction() {
        return db.newRecord(TRANSACTION);
    }

    public TransactionMailRecord newTransactionMail() {
        return db.newRecord(TRANSACTION_MAIL);
    }

    @LoggedStatement
    public Optional<TransactionRecord> selectTransaction(String userId, String transactionId) {
        return db.fetch(TRANSACTION,
                TRANSACTION.USER_ID.eq(userId)
                    .and(TRANSACTION.ID.eq(transactionId)))
            .stream().findAny();
    }

    @LoggedStatement
    public void insertCompleteTransactions(List<CompleteTransactionEntity> transactions) {
        List<Query> queries = new ArrayList<>();

        for (CompleteTransactionEntity entity : transactions) {
            queries.add(db.insertInto(TRANSACTION, TRANSACTION.fields()).values(entity.getTransaction()));
            queries.add(db.insertInto(TRANSACTION_MAIL, TRANSACTION_MAIL.fields()).values(entity.getMail()));
        }

        CompletionStage<int[]> completion = db.batch(queries).executeAsync();
        completion.exceptionally(e -> {
            if (e instanceof DataAccessException dataAccessException) {
                throw new UnexpectedDbException(dataAccessException);
            }
            throw new UnexpectedServerException("failed to insert mail transactions", new Exception(e));
        });
    }

    @LoggedStatement
    public void insertTransactions(List<TransactionRecord> transactions) {
        db.transaction(c -> transactions.forEach(UpdatableRecordImpl::store));
    }

    @LoggedStatement
    public void updateLastImport(String id, LocalDateTime lastImportedTransaction) {
        CompletionStage<Integer> completion = db
            .update(TRANSACTION_META_DATA)
            .set(TRANSACTION_META_DATA.LAST_IMPORTED_TRANSACTION, lastImportedTransaction)
            .set(TRANSACTION_META_DATA.LAST_IMPORT_CHECK, LocalDateTime.now())
            .where(TRANSACTION_META_DATA.USER_ID.eq(id))
            .executeAsync();

        completion.exceptionally(e -> {
            if (e instanceof DataAccessException dataAccessException) {
                throw new UnexpectedDbException(dataAccessException);
            }
            throw new UnexpectedServerException("failed to update last import for userid:" + id, new Exception(e));
        });
    }

    @LoggedStatement
    public List<TransactionDto> selectTransactionsByUserIdAsDto(String userId) {
        return db
            .selectFrom(TRANSACTION)
            .where(TRANSACTION.USER_ID.eq(userId))
            .orderBy(TRANSACTION.TRANSACTION_DATE.desc())
            .fetch().map(TransactionDto::new);
    }

    @LoggedStatement
    public List<TransactionDto> selectTransactionsWithDependenciesByUserIdAsDto(String userId) {
        Result<?> result = db
            .select(TRANSACTION.asterisk(), TAG.asterisk(), KEYWORD.asterisk())
            .from(TRANSACTION)
            .leftJoin(TAG)
            .on(TRANSACTION.TAG_ID.eq(TAG.ID))
            .leftJoin(KEYWORD)
            .on(TRANSACTION.MATCHING_KEYWORD_ID.eq(KEYWORD.ID))
            .where(TRANSACTION.USER_ID.eq(userId))
            .orderBy(TRANSACTION.TRANSACTION_DATE.desc())
            .fetch();

        return parseDeepResultToDto(result);
    }

    @LoggedStatement
    public List<TransactionDto> selectTransactionsWithDependenciesWithFilterWithPageAsDto(
        String userId,
        String query,
        int[] tagIds,
        LocalDate from,
        LocalDate to,
        int page
    ) {
        LocalDate now = LocalDate.now();
        SelectConditionStep<?> conditions = db
            .select(TRANSACTION.asterisk(), TAG.asterisk(), KEYWORD.asterisk())
            .from(TRANSACTION)
            .leftJoin(TAG)
            .on(TRANSACTION.TAG_ID.eq(TAG.ID))
            .leftJoin(KEYWORD)
            .on(TRANSACTION.MATCHING_KEYWORD_ID.eq(KEYWORD.ID))
            .where(TRANSACTION.USER_ID.eq(userId));

        if (query != null && !query.isBlank()) {
            if (!query.startsWith("%")) {
                query = "%" + query;
            }
            if (!query.endsWith("%")) {
                query += "%";
            }

            conditions = conditions
                .and(TRANSACTION.ALIAS.likeIgnoreCase(query)
                    .or(TRANSACTION.NOTE.likeIgnoreCase(query)
                        .or(TRANSACTION.RECEIVER.likeIgnoreCase(query))));
        }

        if (tagIds != null && tagIds.length > 0) {
            Condition tagsCondition = TRANSACTION.TAG_ID.eq(tagIds[0]);
            for (int i = 1; i < tagIds.length; i++) {
                tagsCondition = tagsCondition.or(TRANSACTION.TAG_ID.eq(tagIds[i]));
            }
            conditions = conditions.and(tagsCondition);
        }

        if (from != null && from.isBefore(now)) {
            conditions = conditions.and(TRANSACTION.TRANSACTION_DATE.ge(from));
        }

        if (to != null && to.isBefore(now)) {
            conditions = conditions.and(TRANSACTION.TRANSACTION_DATE.le(to));
        }

        SelectLimitStep<?> limitStep = conditions.orderBy(TRANSACTION.TRANSACTION_DATE.desc());
        Result<?> result = data.fetchWithLimit(limitStep, page);
        return parseDeepResultToDto(result);
    }

    private List<TransactionDto> parseDeepResultToDto(Result<?> result) {
        final List<TransactionDto> dtos = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            int matchingKeywordId = 0;
            KeywordDto matchingKeyword = null;
            if (result.get(i).get(TRANSACTION.MATCHING_KEYWORD_ID) != null) {
                matchingKeywordId = result.getValue(i, TRANSACTION.MATCHING_KEYWORD_ID);
                matchingKeyword = new KeywordDto(
                    result.getValue(i, KEYWORD.ID),
                    result.getValue(i, KEYWORD.KEYWORD_),
                    result.getValue(i, KEYWORD.TAG_ID)
                );
            }

            int tagId = 0;
            TagDto tag = null;
            if (result.get(i).get(TRANSACTION.TAG_ID) != null) {
                tagId = result.getValue(i, TRANSACTION.TAG_ID);
                tag = new TagDto(
                    result.getValue(i, TAG.ID),
                    result.getValue(i, TAG.ICON),
                    result.getValue(i, TAG.COLOR),
                    result.getValue(i, TAG.NAME),
                    result.getValue(i, TAG.DEFAULT_TAG),
                    List.of()
                );
            }

            dtos.add(new TransactionDto(
                result.getValue(i, TRANSACTION.ID),
                result.getValue(i, TRANSACTION.EXPENSE),
                result.getValue(i, TRANSACTION.AMOUNT),
                result.getValue(i, TRANSACTION.TRANSACTION_DATE),
                result.getValue(i, TRANSACTION.BANKACCOUNT),
                result.getValue(i, TRANSACTION.RECEIVER),
                tagId,
                tag,
                matchingKeywordId,
                matchingKeyword,
                result.getValue(i, TRANSACTION.ALIAS),
                result.getValue(i, TRANSACTION.NOTE)
            ));
        }

        return dtos;
    }

    @LoggedStatement
    public ImportDbData selectImportDataByUserId(String userId) {
        Result<?> result = db
            .select(REGISTERED_USER.MAIL, REGISTERED_USER.MAIL_PASSWORD, REGISTERED_USER.ID,
                TRANSACTION_META_DATA.LAST_IMPORTED_TRANSACTION, TRANSACTION_META_DATA.LAST_IMPORT_CHECK,
                TRANSACTION_META_DATA.TRANSACTIONS_FOLDER, TRANSACTION_META_DATA.BANK)
            .from(REGISTERED_USER)
            .join(TRANSACTION_META_DATA)
            .on(TRANSACTION_META_DATA.USER_ID.eq(REGISTERED_USER.ID))
            .where(REGISTERED_USER.ID.eq(userId))
            .fetch();

        if (result.size() != 1) {
            throw new ResourceNotFoundException("user", userId);
        }

        return resultToRecord(result, 0);
    }

    @LoggedStatement
    public List<ImportDbData> selectImportData() {
        Result<?> result = db
            .select(REGISTERED_USER.MAIL, REGISTERED_USER.MAIL_PASSWORD, REGISTERED_USER.ID,
                TRANSACTION_META_DATA.LAST_IMPORTED_TRANSACTION, TRANSACTION_META_DATA.LAST_IMPORT_CHECK,
                TRANSACTION_META_DATA.TRANSACTIONS_FOLDER, TRANSACTION_META_DATA.BANK)
            .from(REGISTERED_USER)
            .join(TRANSACTION_META_DATA)
            .on(TRANSACTION_META_DATA.USER_ID.eq(REGISTERED_USER.ID))
            .fetch();

        List<ImportDbData> importedDbData = new ArrayList<>();
        for (int i = 0; i < result.size(); i++) {
            importedDbData.add(resultToRecord(result, i));
        }

        return importedDbData;
    }

    @LoggedStatement
    public void updateTransaction(String transactionId, int tagId, String alias, String note) {
        db.update(TRANSACTION)
            .set(TRANSACTION.TAG_ID, tagId)
            .set(TRANSACTION.ALIAS, alias)
            .set(TRANSACTION.NOTE, note)
            .where(TRANSACTION.ID.eq(transactionId))
            .execute();
    }

    @LoggedStatement
    public void updateTransaction(TransactionRecord transaction) {
        if (transaction.getTagId() == 0) {
            transaction.store(TRANSACTION.ALIAS, TRANSACTION.NOTE);
        } else if (transaction.getMatchingKeywordId() == 0) {
            transaction.store(TRANSACTION.TAG_ID, TRANSACTION.ALIAS, TRANSACTION.NOTE);
        } else {
            transaction.store(TRANSACTION.TAG_ID, TRANSACTION.MATCHING_KEYWORD_ID, TRANSACTION.ALIAS, TRANSACTION.NOTE);
        }
    }

    protected ImportDbData resultToRecord(Result<?> result, int index) {
        LocalDateTime lastImportedTransaction = result.getValue(index, TRANSACTION_META_DATA.LAST_IMPORTED_TRANSACTION);
        lastImportedTransaction = lastImportedTransaction == null ?
            LocalDateTime.now().minusYears(TransactionImporter.MAX_IMPORT_SINCE_YEARS) :
            lastImportedTransaction;

        LocalDateTime lastImportCheck = result.getValue(index, TRANSACTION_META_DATA.LAST_IMPORT_CHECK);
        lastImportCheck = lastImportCheck == null ?
            LocalDateTime.now().minusYears(TransactionImporter.MAX_IMPORT_SINCE_YEARS) :
            lastImportCheck;

        return new ImportDbData(
            result.getValue(index, REGISTERED_USER.ID),
            result.getValue(index, REGISTERED_USER.MAIL),
            result.getValue(index, REGISTERED_USER.MAIL_PASSWORD),
            lastImportedTransaction,
            lastImportCheck,
            result.getValue(index, TRANSACTION_META_DATA.TRANSACTIONS_FOLDER),
            result.getValue(index, TRANSACTION_META_DATA.BANK)
        );
    }

    /**
     * a "struct" like record (:, used to select the required data from the db
     *
     * @param id                      will be filled with the user id
     * @param mail                    mail
     * @param password                password to the mail account
     * @param lastImportedTransaction the time when the last imported message was received
     * @param lastImportCheck         the last time when the messages where checked
     * @param folder                  the folder for the import
     * @param bank                    the SupportedBank key
     */
    public record ImportDbData(
        String id,
        String mail,
        String password,
        LocalDateTime lastImportedTransaction,
        LocalDateTime lastImportCheck,
        String folder,
        String bank) {

    }
}

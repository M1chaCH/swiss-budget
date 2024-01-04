package ch.michu.tech.swissbudget.app.provider;

import static ch.michu.tech.swissbudget.framework.utils.DateBuilder.localDateNow;
import static ch.michu.tech.swissbudget.framework.utils.DateBuilder.localDateTimeNow;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser.REGISTERED_USER;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMail.TRANSACTION_MAIL;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMetaData.TRANSACTION_META_DATA;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE;
import static org.jooq.impl.DSL.count;

import ch.michu.tech.swissbudget.app.dto.keyword.KeywordDto;
import ch.michu.tech.swissbudget.app.dto.tag.TagDto;
import ch.michu.tech.swissbudget.app.dto.transaction.TransactionDto;
import ch.michu.tech.swissbudget.app.dto.transaction.TransactionTagDuplicateDto;
import ch.michu.tech.swissbudget.app.entity.CompleteTransactionEntity;
import ch.michu.tech.swissbudget.app.entity.TransactionTagDuplicateEntity;
import ch.michu.tech.swissbudget.app.transaction.TransactionImporter;
import ch.michu.tech.swissbudget.framework.data.BaseRecordProvider;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import ch.michu.tech.swissbudget.framework.error.exception.ResourceNotFoundException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMailRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.SelectLimitStep;
import org.jooq.impl.UpdatableRecordImpl;

// TODO cache for improved read times
// TODO write tests with demo data
@ApplicationScoped
public class TransactionProvider implements BaseRecordProvider<TransactionRecord, UUID> {

    protected static final String DUPLICATES_COUNT_COLUMN = "duplicates";

    protected final TransactionMailProvider transactionMailProvider;
    protected final TagProvider tagProvider;
    protected final KeywordProvider keywordProvider;
    protected final TransactionTagDuplicateProvider transactionTagDuplicateProvider;
    protected final int pageSize;

    @Inject
    public TransactionProvider(
        @ConfigProperty(name = "db.limit.page.size", defaultValue = "100") int pageSize,
        TransactionMailProvider transactionMailProvider,
        TagProvider tagProvider,
        KeywordProvider keywordProvider,
        TransactionTagDuplicateProvider transactionTagDuplicateProvider
    ) {
        this.transactionMailProvider = transactionMailProvider;
        this.tagProvider = tagProvider;
        this.keywordProvider = keywordProvider;
        this.transactionTagDuplicateProvider = transactionTagDuplicateProvider;
        this.pageSize = pageSize;
    }

    @LoggedStatement
    public Optional<TransactionRecord> selectTransaction(DSLContext db, UUID userId, UUID transactionId) {
        TransactionRecord transaction = db.fetchOne(TRANSACTION, TRANSACTION.USER_ID.eq(userId).and(TRANSACTION.ID.eq(transactionId)));

        if (transaction == null) {
            return Optional.empty();
        }
        return Optional.of(transaction);
    }

    @LoggedStatement
    public CompleteTransactionEntity selectCompleteTransaction(DSLContext db, UUID userId, UUID transactionId) {
        Record result = db
                            .select(TRANSACTION_MAIL.asterisk(),
                                    TRANSACTION.asterisk(),
                                    TAG.asterisk(),
                                    KEYWORD.asterisk(),
                                    count(TRANSACTION_TAG_DUPLICATE.ID).as(DUPLICATES_COUNT_COLUMN))
                            .from(TRANSACTION)
                            .leftJoin(TRANSACTION_MAIL).on(TRANSACTION_MAIL.TRANSACTION_ID.eq(TRANSACTION.ID))
                            .leftJoin(TAG).on(TRANSACTION.TAG_ID.eq(TAG.ID))
                            .leftJoin(KEYWORD).on(TRANSACTION.MATCHING_KEYWORD_ID.eq(KEYWORD.ID))
                            .leftJoin(TRANSACTION_TAG_DUPLICATE).on(TRANSACTION.ID.eq(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID))
                            .where(TRANSACTION.USER_ID.eq(userId)).and(TRANSACTION.ID.eq(transactionId))
                            .groupBy(TRANSACTION.ID, TAG.ID, KEYWORD.ID, TRANSACTION_MAIL.ID)
                            .fetchOne();

        if (result == null) {
            throw new ResourceNotFoundException("transaction", transactionId.toString());
        }

        TransactionMailRecord transactionMail = transactionMailProvider.fromRecord(db, result);
        TransactionRecord transaction = fromRecord(db, result);

        TagRecord tag = null;
        if (result.get(TRANSACTION.TAG_ID) != null) {
            tag = tagProvider.fromRecord(db, result);
        }

        KeywordRecord matchingKeyword = null;
        if (result.get(TRANSACTION.MATCHING_KEYWORD_ID) != null) {
            matchingKeyword = keywordProvider.fromRecord(db, result);
        }

        int duplicatedCount = result.get(DUPLICATES_COUNT_COLUMN, Integer.class);
        List<TransactionTagDuplicateEntity> duplicates = new ArrayList<>(duplicatedCount);
        if (duplicatedCount > 0) {
            duplicates.addAll(transactionTagDuplicateProvider.selectTagDuplicatesForTransaction(db, transactionId,
                                                                                                transactionTagDuplicateProvider.getRecordToEntityMapper(
                                                                                                    db)));
        }

        return new CompleteTransactionEntity(transactionMail, transaction, tag, matchingKeyword, duplicates);
    }

    @Override
    public TransactionRecord newRecord(DSLContext db) {
        return db.newRecord(TRANSACTION);
    }

    @Override
    public TransactionRecord fromRecord(DSLContext db, Record result) {
        TransactionRecord transaction = newRecord(db);

        transaction.setId(result.getValue(TRANSACTION.ID));
        transaction.setExpense(result.getValue(TRANSACTION.EXPENSE));
        transaction.setTransactionDate(result.getValue(TRANSACTION.TRANSACTION_DATE));
        transaction.setBankaccount(result.getValue(TRANSACTION.BANKACCOUNT));
        transaction.setAmount(result.getValue(TRANSACTION.AMOUNT));
        transaction.setReceiver(result.getValue(TRANSACTION.RECEIVER));
        transaction.setTagId(result.getValue(TRANSACTION.TAG_ID));
        transaction.setMatchingKeywordId(result.getValue(TRANSACTION.MATCHING_KEYWORD_ID));
        transaction.setNeedUserAttention(result.getValue(TRANSACTION.NEED_USER_ATTENTION));
        transaction.setAlias(result.getValue(TRANSACTION.ALIAS));
        transaction.setNote(result.getValue(TRANSACTION.NOTE));
        transaction.setUserId(result.getValue(TRANSACTION.USER_ID));

        return transaction;
    }

    @Override
    @LoggedStatement
    public boolean fetchExists(DSLContext db, UUID userId, UUID transactionId) {
        Condition userCondition = TRANSACTION.USER_ID.eq(userId);
        Condition transactionCondition = TRANSACTION.ID.eq(transactionId);

        return db.fetchExists(TRANSACTION, userCondition, transactionCondition);
    }

    @LoggedStatement
    public List<TransactionIdWithTagDuplicateCount> selectTransactionIdsByMatchingKeyword(DSLContext db, UUID userId, String keyword) {
        keyword = "%" + keyword + "%";

        return db
                   .select(TRANSACTION.ID, TRANSACTION.MATCHING_KEYWORD_ID, count(TRANSACTION_TAG_DUPLICATE.ID).as(DUPLICATES_COUNT_COLUMN))
                   .from(TRANSACTION)
                   .leftJoin(TRANSACTION_TAG_DUPLICATE).on(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID.eq(TRANSACTION.ID))
                   .where(TRANSACTION.USER_ID.eq(userId).and(TRANSACTION.RECEIVER.likeIgnoreCase(keyword)))
                   .groupBy(TRANSACTION.ID, TRANSACTION.MATCHING_KEYWORD_ID)
                   .fetch()
                   .map(result -> {
                       boolean alreadyHasTagMapped = false;
                       if (result.get(TRANSACTION.MATCHING_KEYWORD_ID) != null) {
                           alreadyHasTagMapped = result.get(TRANSACTION.MATCHING_KEYWORD_ID) != null;
                       }

                       return new TransactionIdWithTagDuplicateCount(
                           result.getValue(TRANSACTION.ID),
                           result.getValue(DUPLICATES_COUNT_COLUMN, Integer.class),
                           alreadyHasTagMapped
                       );
                   });
    }

    @LoggedStatement
    public List<TransactionDto> selectTransactionsWithDependenciesWithFilterWithPageAsDto(
        DSLContext db,
        UUID userId,
        String query,
        UUID[] tagIds,
        LocalDate from,
        LocalDate to,
        boolean needAttention,
        int page
    ) {
        LocalDate tomorrow = localDateNow().plusDays(1);
        SelectConditionStep<?> conditions = db
                                                .select(TRANSACTION.asterisk(), TAG.asterisk(), KEYWORD.asterisk(),
                                                        count(TRANSACTION_TAG_DUPLICATE.ID).as(DUPLICATES_COUNT_COLUMN))
                                                .from(TRANSACTION)
                                                .leftJoin(TAG)
                                                .on(TRANSACTION.TAG_ID.eq(TAG.ID))
                                                .leftJoin(KEYWORD)
                                                .on(TRANSACTION.MATCHING_KEYWORD_ID.eq(KEYWORD.ID))
                                                .leftJoin(TRANSACTION_TAG_DUPLICATE)
                                                .on(TRANSACTION.ID.eq(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID))
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

        if (needAttention) {
            conditions = conditions.and(TRANSACTION.NEED_USER_ATTENTION.eq(true));
        }

        if (tagIds != null && tagIds.length > 0) {
            Condition tagsCondition = TRANSACTION.TAG_ID.eq(tagIds[0]);
            for (int i = 1; i < tagIds.length; i++) {
                tagsCondition = tagsCondition.or(TRANSACTION.TAG_ID.eq(tagIds[i]));
            }
            conditions = conditions.and(tagsCondition);
        }

        if (from != null && from.isBefore(tomorrow)) {
            conditions = conditions.and(TRANSACTION.TRANSACTION_DATE.ge(from));
        }

        if (to != null && to.isBefore(tomorrow)) {
            conditions = conditions.and(TRANSACTION.TRANSACTION_DATE.le(to));
        }

        SelectLimitStep<?> limitStep = conditions
                                           .groupBy(TRANSACTION.ID, TAG.ID, KEYWORD.ID)
                                           .orderBy(TRANSACTION.TRANSACTION_DATE.desc());

        Result<?> result = DataProvider.fetchWithLimit(limitStep, page, pageSize);
        return parseDeepResultToDtos(db, result);
    }

    protected List<TransactionDto> parseDeepResultToDtos(DSLContext db, Result<?> result) {
        final List<TransactionDto> dtos = new ArrayList<>();
        for (Record currentResult : result) {
            KeywordDto matchingKeyword = null;
            if (currentResult.get(TRANSACTION.MATCHING_KEYWORD_ID) != null) {
                matchingKeyword = keywordProvider.asDto(currentResult);
            }

            TagDto tag = null;
            if (currentResult.get(TRANSACTION.TAG_ID) != null) {
                tag = tagProvider.asDto(currentResult, List.of());
            }

            int duplicatesCount = currentResult.get(DUPLICATES_COUNT_COLUMN, int.class);
            final List<TransactionTagDuplicateDto> duplicatedTags = new ArrayList<>(duplicatesCount);
            if (duplicatesCount > 0) {
                duplicatedTags.addAll(
                    transactionTagDuplicateProvider.selectTagDuplicatesForTransaction(db, currentResult.getValue(TRANSACTION.ID),
                                                                                      transactionTagDuplicateProvider.getRecordToDtoMapper()));
            }

            dtos.add(asDto(currentResult, tag, matchingKeyword, duplicatedTags));
        }

        return dtos;
    }

    public TransactionDto asDto(Record result, TagDto tag, KeywordDto matchingKeyword, List<TransactionTagDuplicateDto> duplicatedTags) {
        UUID tagId = tag == null ? null : tag.getId();
        UUID matchingKeywordId = matchingKeyword == null ? null : matchingKeyword.getId();

        return new TransactionDto(
            result.getValue(TRANSACTION.ID),
            result.getValue(TRANSACTION.EXPENSE),
            result.getValue(TRANSACTION.AMOUNT),
            result.getValue(TRANSACTION.TRANSACTION_DATE),
            result.getValue(TRANSACTION.BANKACCOUNT),
            result.getValue(TRANSACTION.RECEIVER),
            tagId,
            tag,
            matchingKeywordId,
            matchingKeyword,
            result.getValue(TRANSACTION.ALIAS),
            result.getValue(TRANSACTION.NOTE),
            result.getValue(TRANSACTION.NEED_USER_ATTENTION),
            duplicatedTags
        );
    }

    @LoggedStatement
    public ImportDbData selectImportDataByUserId(DSLContext db, UUID userId) {
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

        return parseResultToImportData(result, 0);
    }

    protected ImportDbData parseResultToImportData(Result<?> result, int index) {
        LocalDateTime lastImportedTransaction = result.getValue(index, TRANSACTION_META_DATA.LAST_IMPORTED_TRANSACTION);
        lastImportedTransaction = lastImportedTransaction == null ?
                                  localDateTimeNow().minusYears(TransactionImporter.MAX_IMPORT_SINCE_YEARS) :
                                  lastImportedTransaction;

        LocalDateTime lastImportCheck = result.getValue(index, TRANSACTION_META_DATA.LAST_IMPORT_CHECK);
        lastImportCheck = lastImportCheck == null ?
                          localDateTimeNow().minusYears(TransactionImporter.MAX_IMPORT_SINCE_YEARS) :
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

    @LoggedStatement
    public List<ImportDbData> selectImportData(DSLContext db) {
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
            importedDbData.add(parseResultToImportData(result, i));
        }

        return importedDbData;
    }

    @LoggedStatement
    public void insertCompleteTransactions(DSLContext db, List<CompleteTransactionEntity> transactions) {
        List<Query> queries = new ArrayList<>();

        for (CompleteTransactionEntity entity : transactions) {
            queries.add(db.insertInto(TRANSACTION, TRANSACTION.fields()).values(entity.getTransaction()));
            queries.add(db.insertInto(TRANSACTION_MAIL, TRANSACTION_MAIL.fields()).values(entity.getMail()));

            for (TransactionTagDuplicateEntity otherMatch : entity.getTagDuplicates()) {
                queries.add(
                    db.insertInto(TRANSACTION_TAG_DUPLICATE,
                                  TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID, TRANSACTION_TAG_DUPLICATE.TAG_ID,
                                  TRANSACTION_TAG_DUPLICATE.MATCHING_KEYWORD_ID)
                      .values(otherMatch.getTransactionId(), otherMatch.getTag().getId(), otherMatch.getMatchingKeyword().getId())
                );
            }
        }

        db.batch(queries).execute();
    }

    @LoggedStatement
    public void insertTransactions(DSLContext db, List<TransactionRecord> transactions) {
        db.batched(c -> transactions.forEach(UpdatableRecordImpl::store));
    }

    @LoggedStatement
    public void updateTransactionUserInput(DSLContext db, TransactionRecord transaction) {
        if (transaction.get(TRANSACTION.TAG_ID) == null) {
            transaction.store(TRANSACTION.ALIAS, TRANSACTION.NOTE);
        } else if (transaction.get(TRANSACTION.MATCHING_KEYWORD_ID) == null) {
            transaction.store(TRANSACTION.TAG_ID, TRANSACTION.ALIAS, TRANSACTION.NOTE);
        } else {
            transaction.store(TRANSACTION.TAG_ID, TRANSACTION.MATCHING_KEYWORD_ID, TRANSACTION.ALIAS, TRANSACTION.NOTE);
        }
    }

    @LoggedStatement
    public void updateTransactionNeedsUserAttention(DSLContext db, UUID transactionId, boolean needAttention) {
        db
            .update(TRANSACTION)
            .set(TRANSACTION.NEED_USER_ATTENTION, needAttention)
            .where(TRANSACTION.ID.eq(transactionId))
            .execute();
    }

    @LoggedStatement
    public void updateTransactionsByTagWithDefaultTag(DSLContext db, UUID oldTagId, UUID defaultTagId) {
        db
            .update(TRANSACTION)
            .set(TRANSACTION.TAG_ID, defaultTagId)
            .set(TRANSACTION.NEED_USER_ATTENTION, true)
            .set(TRANSACTION.MATCHING_KEYWORD_ID, (UUID) null)
            .where(TRANSACTION.TAG_ID.eq(oldTagId))
            .execute();
    }

    /**
     * don't send default tag id, this will break with the needUserAttention field. needUserAttention field is set to false here
     */
    @LoggedStatement
    public void updateTransactionWithTagAndRemoveNeedAttention(DSLContext db, UUID transactionId, UUID tagId) {
        db
            .update(TRANSACTION)
            .set(TRANSACTION.TAG_ID, tagId)
            .set(TRANSACTION.NEED_USER_ATTENTION, false)
            .set(TRANSACTION.MATCHING_KEYWORD_ID, (UUID) null)
            .where(TRANSACTION.ID.eq(transactionId))
            .execute();
    }

    /**
     * don't send default tag id, this will break with the needUserAttention field. needUserAttention field is set to false here
     */
    @LoggedStatement
    public void updateTransactionWithTagAndRemoveNeedAttention(DSLContext db, UUID transactionId, UUID tagId, UUID matchingKeywordId) {
        db
            .update(TRANSACTION)
            .set(TRANSACTION.TAG_ID, tagId)
            .set(TRANSACTION.MATCHING_KEYWORD_ID, matchingKeywordId)
            .set(TRANSACTION.NEED_USER_ATTENTION, false)
            .where(TRANSACTION.ID.eq(transactionId))
            .execute();
    }

    @LoggedStatement
    public void updateLastImport(DSLContext db, UUID id, LocalDateTime lastImportedTransaction) {
        db
            .update(TRANSACTION_META_DATA)
            .set(TRANSACTION_META_DATA.LAST_IMPORTED_TRANSACTION, lastImportedTransaction)
            .set(TRANSACTION_META_DATA.LAST_IMPORT_CHECK, localDateTimeNow())
            .where(TRANSACTION_META_DATA.USER_ID.eq(id))
            .execute();
    }

    @LoggedStatement
    public void deleteAllTagDuplicates(DSLContext db, UUID transactionId) {
        db
            .delete(TRANSACTION_TAG_DUPLICATE)
            .where(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID.eq(transactionId))
            .execute();
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
        UUID id,
        String mail,
        String password,
        LocalDateTime lastImportedTransaction,
        LocalDateTime lastImportCheck,
        String folder,
        String bank
    ) {

    }

    public record TransactionIdWithTagDuplicateCount(
        UUID transactionId,
        int tagDuplicateCount,
        boolean alreadyMappedToAnyTag
    ) {

    }
}

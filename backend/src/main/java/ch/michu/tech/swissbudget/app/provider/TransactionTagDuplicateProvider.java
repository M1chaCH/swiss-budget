package ch.michu.tech.swissbudget.app.provider;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE;

import ch.michu.tech.swissbudget.app.dto.transaction.TransactionTagDuplicateDto;
import ch.michu.tech.swissbudget.app.entity.TransactionTagDuplicateEntity;
import ch.michu.tech.swissbudget.framework.data.BaseRecordProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionTagDuplicateRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.RecordMapper;

@ApplicationScoped
public class TransactionTagDuplicateProvider implements BaseRecordProvider<TransactionTagDuplicateRecord, UUID> {

    protected final TagProvider tagProvider;
    protected final KeywordProvider keywordProvider;

    @Inject
    public TransactionTagDuplicateProvider(TagProvider tagProvider, KeywordProvider keywordProvider) {
        this.tagProvider = tagProvider;
        this.keywordProvider = keywordProvider;
    }

    @Override
    public TransactionTagDuplicateRecord newRecord(DSLContext db) {
        return db.newRecord(TRANSACTION_TAG_DUPLICATE);
    }

    @Override
    public TransactionTagDuplicateRecord fromRecord(DSLContext db, Record result) {
        TransactionTagDuplicateRecord duplicate = newRecord(db);

        duplicate.setId(result.getValue(TRANSACTION_TAG_DUPLICATE.ID));
        duplicate.setTransactionId(result.getValue(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID));
        duplicate.setTagId(result.getValue(TRANSACTION_TAG_DUPLICATE.TAG_ID));
        duplicate.setMatchingKeywordId(result.getValue(TRANSACTION_TAG_DUPLICATE.MATCHING_KEYWORD_ID));

        return duplicate;
    }

    @Override
    @LoggedStatement
    public boolean fetchExists(DSLContext db, UUID userId, UUID recordId) {
        Condition transactionMailCondition = TRANSACTION_TAG_DUPLICATE.ID.eq(recordId);

        return db.fetchExists(TRANSACTION_TAG_DUPLICATE, transactionMailCondition);
    }

    @LoggedStatement
    public <T> List<T> selectTagDuplicatesForTransaction(DSLContext db, UUID transactionId, RecordMapper<Record, T> mapper) {
        return db
            .select(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID, TAG.asterisk(), KEYWORD.asterisk())
            .from(TAG)
            .leftJoin(TRANSACTION_TAG_DUPLICATE)
            .on(TRANSACTION_TAG_DUPLICATE.TAG_ID.eq(TAG.ID))
            .leftJoin(KEYWORD)
            .on(TRANSACTION_TAG_DUPLICATE.MATCHING_KEYWORD_ID.eq(KEYWORD.ID))
            .where(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID.eq(transactionId))
            .fetch()
            .map(mapper);
    }

    @LoggedStatement
    public UUID insertDuplicatedTag(DSLContext db, UUID transactionId, UUID tagId, UUID matchingKeywordId) {
        return db
            .insertInto(TRANSACTION_TAG_DUPLICATE, TRANSACTION_TAG_DUPLICATE.ID, TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID,
                TRANSACTION_TAG_DUPLICATE.TAG_ID,
                TRANSACTION_TAG_DUPLICATE.MATCHING_KEYWORD_ID)
            .values(UUID.randomUUID(), transactionId, tagId, matchingKeywordId)
            .returningResult(TRANSACTION_TAG_DUPLICATE.ID)
            .fetchOne(TRANSACTION_TAG_DUPLICATE.ID);
    }

    public RecordMapper<Record, TransactionTagDuplicateDto> getRecordToDtoMapper() {
        return result -> {
            TransactionTagDuplicateDto dto = new TransactionTagDuplicateDto();
            dto.setTransactionId(result.getValue(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID));
            dto.setTag(tagProvider.asDto(result, List.of()));
            dto.setMatchingKeyword(keywordProvider.asDto(result));

            return dto;
        };
    }

    public RecordMapper<Record, TransactionTagDuplicateEntity> getRecordToEntityMapper(final DSLContext db) {
        return result -> {
            final TagRecord tag = tagProvider.fromRecord(db, result);
            final KeywordRecord keyword = keywordProvider.fromRecord(db, result);

            return new TransactionTagDuplicateEntity(result.getValue(TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID), tag, keyword);
        };
    }
}

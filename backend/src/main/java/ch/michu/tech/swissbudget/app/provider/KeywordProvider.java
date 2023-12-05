package ch.michu.tech.swissbudget.app.provider;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;

import ch.michu.tech.swissbudget.app.dto.keyword.KeywordDto;
import ch.michu.tech.swissbudget.framework.data.BaseRecordProvider;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Query;
import org.jooq.Record;
import org.jooq.Record4;
import org.jooq.exception.TooManyRowsException;

@ApplicationScoped
public class KeywordProvider implements BaseRecordProvider<KeywordRecord, Integer> {

    protected final DataProvider data;
    protected final DSLContext db;

    @Inject
    public KeywordProvider(DataProvider data) {
        this.data = data;
        this.db = data.getContext();
    }

    @Override
    public KeywordRecord newRecord() {
        return db.newRecord(KEYWORD);
    }

    @Override
    public KeywordRecord fromRecord(Record result) {
        KeywordRecord keyword = newRecord();

        keyword.setId(result.getValue(KEYWORD.ID));
        keyword.setKeyword(result.getValue(KEYWORD.KEYWORD_));
        keyword.setTagId(result.getValue(KEYWORD.TAG_ID));
        keyword.setUserId(result.getValue(KEYWORD.USER_ID));

        return keyword;
    }

    @Override
    @LoggedStatement
    public boolean fetchExists(String userId, Integer recordId) {
        Condition userCondition = KEYWORD.USER_ID.eq(userId);
        Condition keywordCondition = KEYWORD.ID.eq(recordId);

        return db.fetchExists(KEYWORD, userCondition, keywordCondition);
    }

    public KeywordDto asDto(Record result) {
        return new KeywordDto(
            result.getValue(KEYWORD.ID),
            result.getValue(KEYWORD.KEYWORD_),
            result.getValue(KEYWORD.TAG_ID)
        );
    }

    @LoggedStatement
    public KeywordWithTagEntity selectByKeywordWithTagName(String userId, String keyword) throws TooManyRowsException {
        keyword = "%" + keyword + "%";

        Condition userCondition = KEYWORD.USER_ID.eq(userId);
        Condition keywordCondition = KEYWORD.KEYWORD_.likeIgnoreCase(keyword);

        Record4<Integer, String, String, String> result = db
            .select(KEYWORD.ID, KEYWORD.KEYWORD_, KEYWORD.USER_ID, TAG.NAME.as("tag"))
            .from(KEYWORD)
            .leftJoin(TAG)
            .on(KEYWORD.TAG_ID.eq(TAG.ID))
            .where(userCondition)
            .and(keywordCondition)
            .fetchOne();

        if (result == null) {
            return null;
        }
        return new KeywordWithTagEntity(
            result.get(KEYWORD.USER_ID),
            result.get(KEYWORD.ID),
            result.get(KEYWORD.KEYWORD_),
            result.get("tag", String.class)
        );
    }

    @LoggedStatement
    public List<KeywordRecord> selectKeywordsByTagId(String userId, int tagId) {
        return db.fetch(KEYWORD, KEYWORD.USER_ID.eq(userId), KEYWORD.TAG_ID.eq(tagId));
    }

    @LoggedStatement
    public int insertKeywordToTag(String userId, int tagId, String keyword) {
        return db
            .insertInto(KEYWORD, KEYWORD.TAG_ID, KEYWORD.KEYWORD_, KEYWORD.USER_ID)
            .values(tagId, keyword, userId)
            .returning(KEYWORD.ID)
            .fetchOne(KEYWORD.ID);
    }

    @LoggedStatement()
    public void insertKeywordsToTag(String userId, int tagId, List<String> keywords) {
        List<Query> insertKeywords = keywords
            .stream()
            .map(keyword -> (Query) db
                .insertInto(KEYWORD, KEYWORD.KEYWORD_, KEYWORD.USER_ID, KEYWORD.TAG_ID)
                .values(keyword, userId, tagId))
            .toList();

        db.batch(insertKeywords).execute();
    }

    @LoggedStatement
    public void deleteKeywordsByIds(String userId, int... keywordIds) {
        if (keywordIds.length == 0) {
            return;
        }

        List<Condition> conditions = new ArrayList<>(keywordIds.length);
        for (int id : keywordIds) {
            conditions.add(KEYWORD.ID.eq(id).and(KEYWORD.USER_ID.eq(userId)));
        }

        db.deleteFrom(KEYWORD)
            .where(conditions)
            .execute();
    }

    public record KeywordWithTagEntity(
        String userId,
        int keywordId,
        String keyword,
        String tagName
    ) {

    }
}

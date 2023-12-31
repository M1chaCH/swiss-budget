package ch.michu.tech.swissbudget.app.provider;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;

import ch.michu.tech.swissbudget.app.dto.keyword.KeywordDto;
import ch.michu.tech.swissbudget.app.dto.tag.TagDto;
import ch.michu.tech.swissbudget.framework.data.BaseRecordProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;

@SuppressWarnings("unused")
@ApplicationScoped
public class TagProvider implements BaseRecordProvider<TagRecord, UUID> {

    public static final String DEFAULT_TAG_COLOR = "#3c3e3c";
    public static final String DEFAULT_TAG_ICON = "question_mark";

    @Override
    public TagRecord newRecord(DSLContext db) {
        return db.newRecord(TAG);
    }

    @Override
    public TagRecord fromRecord(DSLContext db, Record result) {
        TagRecord tag = newRecord(db);

        tag.setId(result.getValue(TAG.ID));
        tag.setIcon(result.getValue(TAG.ICON));
        tag.setColor(result.getValue(TAG.COLOR));
        tag.setName(result.getValue(TAG.NAME));
        tag.setUserId(result.getValue(TAG.USER_ID));
        tag.setDefaultTag(result.getValue(TAG.DEFAULT_TAG));

        return tag;
    }

    @Override
    @LoggedStatement
    public boolean fetchExists(DSLContext db, UUID userId, UUID recordId) {
        Condition userCondition = TAG.USER_ID.eq(userId);
        Condition tagCondition = TAG.ID.eq(recordId);

        return db.fetchExists(TAG, userCondition, tagCondition);
    }

    @LoggedStatement
    public boolean fetchExists(DSLContext db, UUID userId, UUID excludedTagId, String name) {
        return db.fetchExists(TAG, TAG.USER_ID.eq(userId), TAG.NAME.equalIgnoreCase(name), TAG.ID.ne(excludedTagId));
    }

    public TagDto asDto(Record result, List<KeywordDto> keywords) {
        return new TagDto(
            result.getValue(TAG.ID),
            result.getValue(TAG.ICON),
            result.getValue(TAG.COLOR),
            result.getValue(TAG.NAME),
            result.getValue(TAG.DEFAULT_TAG),
            keywords
        );
    }

    @LoggedStatement
    public UUID selectDefaultTagId(DSLContext db, UUID userId) {
        return db.select(TAG.ID).from(TAG).where(TAG.USER_ID.eq(userId)).and(TAG.DEFAULT_TAG.eq(true)).fetchOne(TAG.ID);
    }

    /**
     * selects all tags with their records from the DB <br> NOTE: executes a lot of queries, don't use too regularly
     *
     * @param userId the user to filter by
     * @return a map of Tags with their Keywords
     */
    @LoggedStatement
    public Map<TagRecord, List<KeywordRecord>> selectTagsWithKeywordsByUserId(DSLContext db, UUID userId) {
        Map<TagRecord, List<KeywordRecord>> entities = new HashMap<>();

        List<TagRecord> tags = db.selectFrom(TAG)
            .where(TAG.USER_ID.eq(userId))
            .fetch();

        for (TagRecord tag : tags) {
            List<KeywordRecord> keywords = db
                .selectFrom(KEYWORD)
                .where(KEYWORD.USER_ID.eq(userId)
                    .and(KEYWORD.TAG_ID.eq(tag.getId())))
                .fetch();

            entities.put(tag, keywords);
        }

        return entities;
    }

    @LoggedStatement
    public List<TagDto> selectTagsWithKeywordsByUserIdAsDto(DSLContext db, UUID userId) {
        List<TagDto> tags = db
            .selectFrom(TAG)
            .where(TAG.USER_ID.eq(userId))
            .fetch(TagDto::new);

        tags.forEach(tag -> tag.setKeywords(db
            .selectFrom(KEYWORD)
            .where(KEYWORD.TAG_ID
                .eq(tag.getId())
                .and(KEYWORD.USER_ID.eq(userId)))
            .fetch(KeywordDto::new)));
        return tags;
    }

    @LoggedStatement
    public void insertCompleteTag(DSLContext db, UUID userId, UUID tagId, String name, String color, String icon, List<String> keywords) {
        db.transaction(ctx -> {
            DSLContext dsl = ctx.dsl();

            dsl.insertInto(TAG, TAG.ID, TAG.NAME, TAG.COLOR, TAG.ICON, TAG.USER_ID)
                .values(tagId, name, color, icon, userId)
                .execute();

            for (String keyword : keywords) {
                dsl.insertInto(KEYWORD, KEYWORD.ID, KEYWORD.KEYWORD_, KEYWORD.USER_ID, KEYWORD.TAG_ID)
                    .values(UUID.randomUUID(), keyword, userId, tagId)
                    .execute();
            }
        });
    }

    @LoggedStatement
    public void updateTag(DSLContext db, UUID userId, UUID tagId, String name, String color, String icon) {
        db.update(TAG)
            .set(TAG.ICON, icon)
            .set(TAG.COLOR, color)
            .set(TAG.NAME, name)
            .where(TAG.ID.eq(tagId))
            .and(TAG.USER_ID.eq(userId))
            .execute();
    }

    @LoggedStatement
    public void deleteById(DSLContext db, UUID userId, UUID tagId) {
        db.deleteFrom(TAG).where(TAG.USER_ID.eq(userId)).and(TAG.ID.eq(tagId)).execute();
    }
}

package ch.michu.tech.swissbudget.app.provider;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;

import ch.michu.tech.swissbudget.app.dto.tag.KeywordDto;
import ch.michu.tech.swissbudget.app.dto.tag.TagDto;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jooq.Condition;
import org.jooq.DSLContext;

@SuppressWarnings("unused")
@ApplicationScoped
public class TagProvider {

    protected final DataProvider data;
    protected final DSLContext db;

    @Inject
    public TagProvider(DataProvider data) {
        this.data = data;
        this.db = data.getContext();
    }

    @LoggedStatement
    public boolean fetchExistsTag(String userId, int tagId) {
        Condition userCondition = TAG.USER_ID.eq(userId);
        Condition tagCondition = TAG.ID.eq(tagId);

        return db.fetchExists(TAG, userCondition, tagCondition);
    }

    /**
     * selects all tags with their records from the DB <br> NOTE: executes a lot of queries, don't use too regularly
     *
     * @param userId the user to filter by
     * @return a map of Tags with their Keywords
     */
    @LoggedStatement
    public Map<TagRecord, List<KeywordRecord>> selectTagsWithKeywordsByUserId(String userId) {
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
    public List<TagDto> selectTagsWithKeywordsByUserIdAsDto(String userId) {
        List<TagDto> tags = db
            .selectFrom(TAG)
            .where(TAG.USER_ID.eq(userId))
            .fetch(TagDto::new);

        tags.forEach(tag -> db
            .selectFrom(KEYWORD)
            .where(KEYWORD.TAG_ID
                .eq(tag.getId())
                .and(KEYWORD.USER_ID.eq(userId)))
            .fetch(KeywordDto::new));
        return tags;
    }
}

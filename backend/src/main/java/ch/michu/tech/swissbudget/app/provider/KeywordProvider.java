package ch.michu.tech.swissbudget.app.provider;


import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;

import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.LoggedStatement;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.exception.TooManyRowsException;

@ApplicationScoped
public class KeywordProvider {

    protected final DataProvider data;
    protected final DSLContext db;

    @Inject
    public KeywordProvider(DataProvider data) {
        this.data = data;
        this.db = data.getContext();
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
    public int insertKeywordToTag(String userId, int tagId, String keyword) {
        return db
            .insertInto(KEYWORD, KEYWORD.TAG_ID, KEYWORD.KEYWORD_, KEYWORD.USER_ID)
            .values(tagId, keyword, userId)
            .returning(KEYWORD.ID)
            .fetchOne(KEYWORD.ID);
    }

    public record KeywordWithTagEntity(
        String userId,
        int keywordId,
        String keyword,
        String tagName
    ) {

    }
}

package ch.michu.tech.swissbudget.app.service;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;

import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.ArrayList;
import java.util.List;
import org.jooq.DSLContext;
import org.jooq.Query;

@ApplicationScoped
public class DefaultDataService {

    public static final String DEFAULT_TAG_COLOR = "#3C3E3C";
    public static final String DEFAULT_TAG_ICON = "question_mark";

    private final Provider<RequestSupport> supportProvider;
    private final DataProvider db;

    @Inject
    public DefaultDataService(Provider<RequestSupport> supportProvider, DataProvider db) {
        this.supportProvider = supportProvider;
        this.db = db;
    }

    public void insertDefaultTagsAndKeywords(String userId) {
        RequestSupport support = supportProvider.get();
        support.logInfo(this, "inserting default tags and keywords for %s", userId);

        db.getContext().transaction(c -> {
            DSLContext ctx = c.dsl();
            TagRecord defaultTag = createTag(ctx, DEFAULT_TAG_ICON, DEFAULT_TAG_COLOR, "Other", userId, true);
            TagRecord travelTag = createTag(ctx, "train", DEFAULT_TAG_COLOR, "Travel", userId);
            TagRecord restaurantTag = createTag(ctx, "nightlife", DEFAULT_TAG_COLOR, "Restaurant", userId);
            TagRecord groceriesTag = createTag(ctx, "shopping_cart", DEFAULT_TAG_COLOR, "Groceries", userId);
            TagRecord clothesTag = createTag(ctx, "styler", DEFAULT_TAG_COLOR, "Clothes", userId);
            TagRecord hobbyTag = createTag(ctx, "sports_soccer", DEFAULT_TAG_COLOR, "Hobby", userId);
            TagRecord subscriptionTag = createTag(ctx, "all_inclusive", DEFAULT_TAG_COLOR, "Subscriptions", userId);
            TagRecord healthTag = createTag(ctx, "vital_signs", DEFAULT_TAG_COLOR, "Health", userId);
            TagRecord vacationTag = createTag(ctx, "public", DEFAULT_TAG_COLOR, "Vacation", userId);

            List<Query> queries = new ArrayList<>();
            queries.addAll(prepareKeywords(travelTag.getId(), userId, "sbb", "transport", "gondelbahn", "bahn", "shell", "aral"));
            queries.addAll(
                prepareKeywords(restaurantTag.getId(), userId, "subway", "andy's place", "pizzeria", "restaurant", "mcdonalds",
                    "burger king", "pizza", "café", "cafe", "take away", "diner", "sushi", "gelateria"));
            queries.addAll(
                prepareKeywords(groceriesTag.getId(), userId, "coop", "migros", "landi", "lidl", "migrolino", "supermarkt", "aldi",
                    "denner", "spar ", "bäckerei", "müller"));
            queries.addAll(
                prepareKeywords(clothesTag.getId(), userId, "zalando", "nike", "addidas", "puma", "h&m", "zara", "c&a", "manor", "globus",
                    "vögele", "fashion", "decathlon", "sportx", "gucci", "prada", "louis vitton", "broki", "ochsner"));
            queries.addAll(
                prepareKeywords(hobbyTag.getId(), userId, "digitec", "galaxus", "interdiscount", "media", "decatlon", "bike", "kino",
                    "coiffeur", "amazon", "aliexpress", "apple", "ebay", "museum", "google", "domain", "buch"));
            queries.addAll(
                prepareKeywords(subscriptionTag.getId(), userId, "sky", "netflix", "disney", "swisscom", "sunrise", "salt", "spotify",
                    "prime", "hulu"));
            queries.addAll(
                prepareKeywords(healthTag.getId(), userId, "apotheke", "arzt", "krankenhaus", "medizin", "spital", "drogerie",
                    "pharmacie", "klinik", "physio", "zahn", "auge", "brille", "fielman", "visilab"));

            ctx.batch(queries).execute();
            support.logInfo(this, "successfully inserted tags and keywords for %s", userId);
        });
    }

    protected List<Query> prepareKeywords(int tagId, String userId, String... keywords) {
        DSLContext ctx = db.getContext();
        List<Query> queries = new ArrayList<>();

        for (String keyword : keywords) {
            KeywordRecord keywordRecord = createKeyword(keyword, tagId, userId);
            queries.add(ctx.insertInto(KEYWORD, KEYWORD.KEYWORD_, KEYWORD.TAG_ID, KEYWORD.USER_ID)
                .values(keywordRecord.getKeyword(), keywordRecord.getTagId(), keywordRecord.getUserId()));
        }

        return queries;
    }

    protected TagRecord createTag(DSLContext ctx, String icon, String color, String name, String userId) {
        return createTag(ctx, icon, color, name, userId, false);
    }

    protected TagRecord createTag(DSLContext ctx, String icon, String color, String name, String userId, boolean defaultTag) {
        TagRecord tag = ctx.newRecord(TAG);
        tag.setIcon(icon);
        tag.setColor(color);
        tag.setName(name);
        tag.setUserId(userId);
        tag.setDefaultTag(defaultTag);
        tag.store();
        return tag;
    }

    protected KeywordRecord createKeyword(String keyword, int tagId, String userId) {
        KeywordRecord keywordRecord = db.getContext().newRecord(KEYWORD);
        keywordRecord.setKeyword(keyword);
        keywordRecord.setTagId(tagId);
        keywordRecord.setUserId(userId);
        return keywordRecord;
    }
}

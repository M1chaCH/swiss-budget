package ch.michu.tech.swissbudget.app.endpoint;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionTagDuplicate.TRANSACTION_TAG_DUPLICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.michu.tech.swissbudget.app.dto.tag.AssignTagDto;
import ch.michu.tech.swissbudget.app.dto.tag.ResolveConflictDto;
import ch.michu.tech.swissbudget.app.dto.tag.UpdateTagDto;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.KeywordRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TagRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionTagDuplicateRecord;
import ch.michu.tech.swissbudget.test.AppIntegrationTest;
import ch.michu.tech.swissbudget.test.TestDataManager;
import ch.michu.tech.swissbudget.test.TestHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodName.class)
@SuppressWarnings("resource") // closed by AppIntegrationTest
class TagEndpointTest extends AppIntegrationTest {

    @Inject
    protected TagEndpointTest(TestDataManager data, TestHttpClient client) {
        super("/tag", data, client);
    }

    @Test
    void getTags_happy() {
        String hobbyTagId = data.getGeneratedId("tag_hob").toString();
        Response tagRes = client.createAsRootUser().get();
        assertEquals(Status.OK.getStatusCode(), tagRes.getStatus());

        List<Map<String, Object>> tags = tagRes.readEntity(List.class);
        assertEquals(9, tags.size());

        long hobbyTagCount = tags.stream().filter(t -> t.get("id").equals(hobbyTagId)).count();
        assertEquals(1, hobbyTagCount);
        Optional<Map<String, Object>> hobbyTag = tags.stream().filter(t -> t.get("id").equals(hobbyTagId)).findFirst();
        assertTrue(hobbyTag.isPresent());
        List<Map<String, Object>> keywords = (ArrayList<Map<String, Object>>) hobbyTag.get().get("keywords");
        assertEquals(16, keywords.size());
    }

    @Test
    void postTag_happy() {
        UUID newTagId = UUID.randomUUID();
        UUID userId = data.getGeneratedId("usr_rt");
        UpdateTagDto tagToCreate = new UpdateTagDto(newTagId, "test", "#fff", "test tag", List.of("Online Einkauf"), null);
        Response addResponse = client.createAsRootUser().post(tagToCreate);
        assertEquals(Status.CREATED.getStatusCode(), addResponse.getStatus());

        TagRecord createdTag = data.getDsl().fetchOne(TAG, TAG.ID.eq(newTagId));
        assertNotNull(createdTag);
        assertEquals("test", createdTag.getIcon());
        assertEquals("#fff", createdTag.getColor());
        assertEquals("test tag", createdTag.getName());
        assertEquals(userId, createdTag.getUserId());

        List<KeywordRecord> createdKeywords = data.getDsl().fetch(KEYWORD, KEYWORD.TAG_ID.eq(newTagId));
        assertEquals(1, createdKeywords.size());

        UUID onlineEinkaufTransactionId = data.getGeneratedId("tra_gsg");
        TransactionTagDuplicateRecord duplicate = data.getDsl()
            .fetchOne(TRANSACTION_TAG_DUPLICATE, TRANSACTION_TAG_DUPLICATE.TAG_ID.eq(newTagId),
                TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID.eq(onlineEinkaufTransactionId));
        assertNotNull(duplicate);

        TransactionRecord modifiedTransaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(onlineEinkaufTransactionId));
        assertNotNull(modifiedTransaction);
        assertTrue(modifiedTransaction.getNeedUserAttention());

        KeywordRecord createdKeyword = data.getDsl().fetchOne(KEYWORD, KEYWORD.ID.eq(duplicate.getMatchingKeywordId()));
        assertNotNull(createdKeyword);
        assertEquals("Online Einkauf", createdKeyword.getKeyword());
        assertEquals(newTagId, createdKeyword.getTagId());
    }

    @Test()
    void postTag_validation() {
        UpdateTagDto tagToCreate = new UpdateTagDto(UUID.randomUUID(), "icon", "#fff", "name", List.of("digitec"), new UUID[0]);
        Response tagDuplicateResponse = client.createAsRootUser().post(tagToCreate);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), tagDuplicateResponse.getStatus());

        Response tagNameAlreadyExists = client.createAsRootUser()
            .post(new UpdateTagDto(UUID.randomUUID(), "icon", "color", "Other", List.of(), new UUID[0]));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), tagNameAlreadyExists.getStatus());
    }

    @Test()
    void putTag_happy() {
        UUID tagId = data.getGeneratedId("tag_hob");
        UUID keywordIdToRemove = data.getGeneratedId("kew_gog");
        UpdateTagDto dto = new UpdateTagDto(tagId, "icon", "#fff", "test", List.of("more test", "Online Einkauf", "test"),
            new UUID[]{keywordIdToRemove});

        Response response = client.createAsRootUser().put(dto);
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        assertFalse(data.getDsl().fetchExists(KEYWORD, KEYWORD.ID.eq(keywordIdToRemove)));

        KeywordRecord createdKeyword = data.getDsl().fetchOne(KEYWORD, KEYWORD.KEYWORD_.eq("Online Einkauf"));
        assertNotNull(createdKeyword);
        TransactionRecord transaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(data.getGeneratedId("tra_gsg")));
        assertNotNull(transaction);
        assertEquals(tagId, transaction.getTagId());
        assertTrue(transaction.getNeedUserAttention());
    }

    @Test
    void putTag_invalid() {
        Response notFound = client.createAsRootUser()
            .put(new UpdateTagDto(UUID.randomUUID(), "icon", "color", "name", List.of(), new UUID[0]));
        assertEquals(Status.NOT_FOUND.getStatusCode(), notFound.getStatus());

        UUID tagId = data.getGeneratedId("tag_hob");
        Response notAllowed = client
            .create()
            .withUserAgent("other-agent")
            .loginUser("bak@test.ch")
            .put(new UpdateTagDto(tagId, "icon", "color", "name", List.of(), new UUID[0]));
        assertEquals(Status.NOT_FOUND.getStatusCode(), notAllowed.getStatus());

        Response alreadyExists = client
            .createAsRootUser()
            .put(new UpdateTagDto(tagId, "icon", "color", "name", List.of("netflix"), new UUID[0]));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), alreadyExists.getStatus());

        Response invalidDelete = client
            .createAsRootUser()
            .put(new UpdateTagDto(tagId, "icon", "color", "name", List.of(), new UUID[]{UUID.randomUUID()}));
        assertEquals(Status.NO_CONTENT.getStatusCode(), invalidDelete.getStatus());

        Response tagNameAlreadyExists = client.createAsRootUser()
            .post(new UpdateTagDto(tagId, "icon", "color", "Other", List.of(), new UUID[0]));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), tagNameAlreadyExists.getStatus());
    }

    @Test
    void deleteTag_happy() {
        UUID tagId = data.getGeneratedId("tag_hob");
        Response deleteResponse = client.createAsRootUser().appendPath("/%s".formatted(tagId.toString())).delete();
        assertEquals(Status.NO_CONTENT.getStatusCode(), deleteResponse.getStatus());

        UUID defaultTagId = data.getGeneratedId("tag_def");
        TransactionRecord transactionRecord = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(data.getGeneratedId("tra_gsg")));
        assertNotNull(transactionRecord);
        assertEquals(defaultTagId, transactionRecord.getTagId());
        assertNull(transactionRecord.get(TRANSACTION.MATCHING_KEYWORD_ID));
        assertTrue(transactionRecord.getNeedUserAttention());
    }

    @Test
    void postValidateKeyword() {
        Response newKeywordResponse = client.createAsRootUser().appendPath("/validate_no_keyword").queryParam("keyword", "new keyword")
            .post(null);
        assertEquals(Status.NO_CONTENT.getStatusCode(), newKeywordResponse.getStatus());

        Response existingKeywordResponse = client.createAsRootUser().appendPath("/validate_no_keyword").queryParam("keyword", "ARZT")
            .post(null);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), existingKeywordResponse.getStatus());
    }

    @Test
    void putAssignTag_happyWithKeyword() {
        UUID transactionId = data.getGeneratedId("tra_and");
        UUID tagId = data.getGeneratedId("tag_tra");
        Response withKeywordResponse = client.createAsRootUser().appendPath("/assign_tag")
            .put(new AssignTagDto(transactionId, tagId, "Einkauf TWINT"));
        assertEquals(Status.NO_CONTENT.getStatusCode(), withKeywordResponse.getStatus());

        KeywordRecord createdKeyword = data.getDsl().fetchOne(KEYWORD, KEYWORD.KEYWORD_.eq("Einkauf TWINT"));
        assertNotNull(createdKeyword);

        TransactionRecord transaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(transactionId));
        assertNotNull(transaction);
//        assertEquals(tagId, transaction.getTagId()); // is not changed because the transaction is already assigned to a tag
        assertTrue(transaction.getNeedUserAttention());

        TransactionTagDuplicateRecord duplicate = data.getDsl()
            .fetchOne(TRANSACTION_TAG_DUPLICATE, TRANSACTION_TAG_DUPLICATE.TRANSACTION_ID.eq(transactionId),
                TRANSACTION_TAG_DUPLICATE.TAG_ID.eq(tagId), TRANSACTION_TAG_DUPLICATE.MATCHING_KEYWORD_ID.eq(createdKeyword.getId()));
        assertNotNull(duplicate);
    }

    @Test
    void putAssignTag_happyWithoutKeyword() {
        UUID transactionId = data.getGeneratedId("tra_and");
        UUID tagId = data.getGeneratedId("tag_tra");
        Response response = client.createAsRootUser().appendPath("/assign_tag")
            .put(new AssignTagDto(transactionId, tagId, null));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        TransactionRecord transaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(transactionId));
        assertNotNull(transaction);
        assertEquals(tagId, transaction.getTagId());
        assertNull(transaction.get(TRANSACTION.MATCHING_KEYWORD_ID));
        assertFalse(transaction.getNeedUserAttention());
    }

    @Test
    void putAssignTag_validation() {
        UUID transactionId = data.getGeneratedId("tra_and");
        UUID tagId = data.getGeneratedId("kew_bak_sub");
        Response notAllowed = client.createAsRootUser().appendPath("/assign_tag").put(new AssignTagDto(transactionId, tagId, null));
        assertEquals(Status.NOT_FOUND.getStatusCode(), notAllowed.getStatus());

        Response notAllowed2 = client.create().withUserAgent("other-agent").loginUser("bak@test.ch").appendPath("/assign_tag")
            .put(new AssignTagDto(transactionId, tagId, null));
        assertEquals(Status.NOT_FOUND.getStatusCode(), notAllowed2.getStatus());

        UUID traAndId = data.getGeneratedId("tra_and");
        UUID tagTraId = data.getGeneratedId("tag_tra");
        Response keywordExistsResponse = client.createAsRootUser().appendPath("/assign_tag")
            .put(new AssignTagDto(traAndId, tagTraId, "galaxus"));
        assertEquals(Status.BAD_REQUEST.getStatusCode(), keywordExistsResponse.getStatus());
    }

    @Test
    void putResolveConflict_happy() {
        UUID transactionId = data.getGeneratedId("tra_bak_con");
        UUID cityTransactionId = data.getGeneratedId("tra_bak_oth");
        UUID tagResId = data.getGeneratedId("tag_bak_res");
        UUID pizzaKeywordId = data.getGeneratedId("kew_bak_pz");
        UUID tagHobId = data.getGeneratedId("tag_bak_hob");
        UUID cityKeyword = data.getGeneratedId("kew_bak_cty");
        UUID transactionDuplicateId = data.getGeneratedId("tra_bak_con_dup");

        assertTrue(data.getDsl().fetchExists(TRANSACTION_TAG_DUPLICATE, TRANSACTION_TAG_DUPLICATE.ID.eq(transactionDuplicateId)));

        Response response = client.create().withUserAgent("other-agent").loginUser("bak@test.ch").appendPath("/resolve_conflict")
            .put(new ResolveConflictDto(transactionId, tagResId, pizzaKeywordId, true));
        assertEquals(Status.NO_CONTENT.getStatusCode(), response.getStatus());

        TransactionRecord transaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(transactionId));
        assertNotNull(transaction);
        assertEquals(tagResId, transaction.getTagId());
        assertEquals(pizzaKeywordId, transaction.getMatchingKeywordId());
        assertFalse(transaction.getNeedUserAttention());

        assertFalse(data.getDsl().fetchExists(KEYWORD, KEYWORD.ID.eq(cityKeyword)));
        assertFalse(data.getDsl().fetchExists(TRANSACTION_TAG_DUPLICATE, TRANSACTION_TAG_DUPLICATE.ID.eq(transactionDuplicateId)));

        TransactionRecord cityTransaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(cityTransactionId));
        assertNotNull(cityTransaction);
        assertEquals(tagHobId, cityTransaction.getTagId());
        assertNull(cityTransaction.get(TRANSACTION.MATCHING_KEYWORD_ID));
    }

    @Test
    void putResolveConflict_invalid() {
        UUID transactionId = data.getGeneratedId("tra_bak_con");
        UUID tagResId = data.getGeneratedId("tag_bak_res");
        UUID pizzaKeywordId = data.getGeneratedId("kew_bak_pz");

        Response unauthenticated = client.create().unauthenticated().appendPath("/resolve_conflict")
            .put(new ResolveConflictDto(transactionId, tagResId, pizzaKeywordId, true));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), unauthenticated.getStatus());

        Response notAllowed = client.createAsRootUser().appendPath("/resolve_conflict")
            .put(new ResolveConflictDto(transactionId, tagResId, pizzaKeywordId, true));
        assertEquals(Status.NOT_FOUND.getStatusCode(), notAllowed.getStatus());

        Response invalidTag = client.create().withUserAgent("other-agent").loginUser("bak@test.ch").appendPath("/resolve_conflict")
            .put(new ResolveConflictDto(transactionId, UUID.randomUUID(), pizzaKeywordId, true));
        assertEquals(Status.NOT_FOUND.getStatusCode(), invalidTag.getStatus());

        Response invalidKeyword = client.create().withUserAgent("other-agent").loginUser("bak@test.ch").appendPath("/resolve_conflict")
            .put(new ResolveConflictDto(transactionId, tagResId, UUID.randomUUID(), true));
        assertEquals(Status.NOT_FOUND.getStatusCode(), invalidKeyword.getStatus());
    }
}
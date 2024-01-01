package ch.michu.tech.swissbudget.app.endpoint;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Transaction.TRANSACTION;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import ch.michu.tech.swissbudget.app.dto.transaction.TransactionDto;
import ch.michu.tech.swissbudget.framework.utils.DateBuilder;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionRecord;
import ch.michu.tech.swissbudget.test.AppIntegrationTest;
import ch.michu.tech.swissbudget.test.TestDataManager;
import ch.michu.tech.swissbudget.test.TestHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.LocalDate;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodName.class)
@SuppressWarnings("resource") // closed by AppIntegrationTest
class TransactionEndpointTest extends AppIntegrationTest {

    @Inject
    protected TransactionEndpointTest(TestDataManager data, TestHttpClient client) {
        super("/transaction", data, client);
    }

    @Test
    void getTransactions_happy() {
        Response happyResponse = client.createAsRootUser().get();
        String result = happyResponse.readEntity(String.class);
        String expectedTransactionId = data.getGeneratedId("tra_cpm").toString();
        JSONArray items = new JSONArray(result);
        assertEquals(3, items.length());

        JSONObject first = items.getJSONObject(0);
        assertEquals(expectedTransactionId, first.getString("id"));
        assertEquals(12.5, first.getDouble("amount"));

        Response secondPage = client.createAsRootUser().queryParam("page", "2").get();
        JSONArray secondPageItems = new JSONArray(secondPage.readEntity(String.class));
        assertEquals(3, secondPageItems.length());
        String expectedDate = DateBuilder.today().addMonths(-1).firstDayOfMonth().addDays(26).formatted("yyyy-MM-dd");
        assertEquals(expectedDate, secondPageItems.getJSONObject(0).getString("transactionDate"));

        Response lastPage = client.createAsRootUser().queryParam("page", "4").get();
        JSONArray lastPageItems = new JSONArray(lastPage.readEntity(String.class));
        assertEquals(2, lastPageItems.length()); // in total, there are 11 data rows (11 % 3 = 2)
    }

    @Test
    void putTransaction_happy() {
        UUID transactionId = data.getGeneratedId("tra_gsg");
        UUID tagId = data.getGeneratedId("tag_hea");
        UUID keywordId = data.getGeneratedId("kew_phr");
        TransactionRecord originalTransaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(transactionId));
        assertNotNull(originalTransaction);
        TransactionDto transactionDto = new TransactionDto(transactionId, false, 1, LocalDate.MIN, "test", "dumb receiver");
        transactionDto.setNote("cool note");
        transactionDto.setAlias("test");
        transactionDto.setTagId(tagId);
        transactionDto.setMatchingKeywordId(keywordId);

        Response r = client.createAsRootUser().put(transactionDto);
        assertEquals(Status.NO_CONTENT.getStatusCode(), r.getStatus());

        TransactionRecord changedTransaction = data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(transactionId));
        assertNotNull(changedTransaction);
        assertEquals(originalTransaction.getExpense(), changedTransaction.getExpense());
        assertEquals(originalTransaction.getAmount(), changedTransaction.getAmount());
        assertEquals(originalTransaction.getTransactionDate().format(DateBuilder.DEFAULT_DATE_FORMATTER),
            changedTransaction.getTransactionDate().format(DateBuilder.DEFAULT_DATE_FORMATTER));
        assertEquals("cool note", changedTransaction.getNote());
        assertEquals("test", changedTransaction.getAlias());

        //these should not be changed
        assertEquals(originalTransaction.getTagId(), changedTransaction.getTagId());
        assertEquals(originalTransaction.getMatchingKeywordId(), changedTransaction.getMatchingKeywordId());
        assertEquals(originalTransaction.getBankaccount(), changedTransaction.getBankaccount());
        assertEquals(originalTransaction.getReceiver(), changedTransaction.getReceiver());
    }

    @Test
    void putTransaction_invalid() {
        UUID transactionId = data.getGeneratedId("tra_gsg");
        TransactionDto transaction = new TransactionDto(data.getDsl().fetchOne(TRANSACTION, TRANSACTION.ID.eq(transactionId)));

        Response notAuthenticated = client.create().unauthenticated().put(transaction);
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), notAuthenticated.getStatus());

        Response unauthorized = client.create().withUserAgent("other-agent").loginUser("bak@test.ch").put(transaction);
        assertEquals(Status.NOT_FOUND.getStatusCode(), unauthorized.getStatus());
        
        transaction.setId(UUID.randomUUID());
        Response notFount = client.createAsRootUser().put(transaction);
        assertEquals(Status.NOT_FOUND.getStatusCode(), notFount.getStatus());
    }
}
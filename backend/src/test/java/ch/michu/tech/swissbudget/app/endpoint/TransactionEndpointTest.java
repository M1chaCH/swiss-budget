package ch.michu.tech.swissbudget.app.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.michu.tech.swissbudget.framework.utils.DateBuilder;
import ch.michu.tech.swissbudget.test.AppIntegrationTest;
import ch.michu.tech.swissbudget.test.TestDataManager;
import ch.michu.tech.swissbudget.test.TestHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

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

    @Override
    protected boolean useDemoUser() {
        return true;
    }
}
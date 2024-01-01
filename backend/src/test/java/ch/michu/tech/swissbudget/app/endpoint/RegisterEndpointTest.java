package ch.michu.tech.swissbudget.app.endpoint;

import static ch.michu.tech.swissbudget.generated.jooq.tables.Keyword.KEYWORD;
import static ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser.REGISTERED_USER;
import static ch.michu.tech.swissbudget.generated.jooq.tables.Tag.TAG;
import static ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMetaData.TRANSACTION_META_DATA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.michu.tech.swissbudget.app.dto.RegisterDto;
import ch.michu.tech.swissbudget.app.exception.UserAlreadyExistsException;
import ch.michu.tech.swissbudget.framework.error.exception.AgentNotRegisteredException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMetaDataRecord;
import ch.michu.tech.swissbudget.test.AppIntegrationTest;
import ch.michu.tech.swissbudget.test.TestDataManager;
import ch.michu.tech.swissbudget.test.TestHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.MethodOrderer.MethodName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodName.class)
@SuppressWarnings("resource") // all responses will be closed by TestHttpClient
class RegisterEndpointTest extends AppIntegrationTest {

    private boolean changesData = false;

    @Inject
    public RegisterEndpointTest(TestDataManager data, TestHttpClient client) {
        super("/register", data, client);
    }

    @Override
    protected boolean wasDataModified() {
        return changesData;
    }

    @Override
    protected boolean useDemoUser() {
        return false;
    }

    @Test
    void register_happy() {
        changesData = true;
        final String expectedMail = "test@mail.com";
        RegisterDto registerDto = new RegisterDto("test-folder", "raiffeisen", expectedMail, "test", "mailPass");

        Response r = client.create().unauthenticated().post(registerDto);
        assertEquals(Status.OK.getStatusCode(), r.getStatus());
        String result = r.readEntity(String.class);
        assertTrue(result.contains(AgentNotRegisteredException.class.getSimpleName()));

        RegisteredUserRecord user = data.getDsl().fetchOne(REGISTERED_USER, REGISTERED_USER.MAIL.eq(expectedMail));
        assertNotNull(user);

        TransactionMetaDataRecord metaData = data.getDsl().fetchOne(TRANSACTION_META_DATA, TRANSACTION_META_DATA.USER_ID.eq(user.getId()));
        assertNotNull(metaData);
        assertEquals("raiffeisen", metaData.getBank());
        assertEquals("test-folder", metaData.getTransactionsFolder());

        int tags = data.getDsl().fetchCount(TAG, TAG.USER_ID.eq(user.getId()));
        assertEquals(9, tags);

        int keywords = data.getDsl().fetchCount(KEYWORD, KEYWORD.USER_ID.eq(user.getId()));
        assertEquals(87, keywords);
    }

    @Test
    void register_alreadyExists() {
        changesData = false;
        RegisterDto registerDto = new RegisterDto("test-folder", "raiffeisen", TestHttpClient.ROOT_MAIL, "test", "mailPass");

        Response r = client.create().unauthenticated().post(registerDto);
        assertEquals(Status.BAD_REQUEST.getStatusCode(), r.getStatus());
        String body = r.readEntity(String.class);
        assertTrue(body.contains(UserAlreadyExistsException.class.getSimpleName()));
    }
}
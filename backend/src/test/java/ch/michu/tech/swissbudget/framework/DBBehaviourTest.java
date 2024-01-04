package ch.michu.tech.swissbudget.framework;

import static ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser.REGISTERED_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import ch.michu.tech.swissbudget.app.dto.RegisterDto;
import ch.michu.tech.swissbudget.test.AppIntegrationTest;
import ch.michu.tech.swissbudget.test.TestDataManager;
import ch.michu.tech.swissbudget.test.TestHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

class DBBehaviourTest extends AppIntegrationTest {

    @Inject
    public DBBehaviourTest(TestDataManager data, TestHttpClient client) {
        super("", data, client);
    }

    @Test
    void transactionalTest() {
        final String expectedMail = "new-mail@test.com";
        RegisterDto registerDto = new RegisterDto("test-folder", "unsupported-bank", expectedMail, "test", "mailPass");
        Response r = client.createAsRootUser().targetPath("/register").post(registerDto);
        assertEquals(Status.NOT_FOUND.getStatusCode(), r.getStatus());
        assertFalse(data.getDsl().fetchExists(REGISTERED_USER, REGISTERED_USER.MAIL.eq(expectedMail)));
    }

    @Override
    protected boolean useDemoUser() {
        return false;
    }
}

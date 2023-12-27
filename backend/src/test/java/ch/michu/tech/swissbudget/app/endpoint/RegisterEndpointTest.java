package ch.michu.tech.swissbudget.app.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.michu.tech.swissbudget.app.dto.RegisterDto;
import ch.michu.tech.swissbudget.test.AppIntegrationTest;
import ch.michu.tech.swissbudget.test.TestDataManager;
import ch.michu.tech.swissbudget.test.TestHttpClient;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import org.junit.jupiter.api.Test;

@SuppressWarnings("resource") // will be closed by TestHttpClient
class RegisterEndpointTest extends AppIntegrationTest {

    @Inject
    public RegisterEndpointTest(TestDataManager data, TestHttpClient client) {
        super("/register", data, client);
    }

    @Test
    void register_happy() {
        RegisterDto registerDto = new RegisterDto("test-folder", "raiffeisen", "test@mail.com", "test", "mailPass");

        Response r = client.create().unauthorized().post(registerDto);
        assertEquals(Status.OK.getStatusCode(), r.getStatus());
    }
}
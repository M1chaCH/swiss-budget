package ch.michu.tech.swissbudget.app.endpoint;

import static ch.michu.tech.swissbudget.generated.jooq.tables.MfaCode.MFA_CODE;
import static ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser.REGISTERED_USER;
import static ch.michu.tech.swissbudget.test.AssertUtils.niceAssertInSecond;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.dto.LoginDto;
import ch.michu.tech.swissbudget.app.dto.MfaCodeDto;
import ch.michu.tech.swissbudget.framework.authentication.AuthenticationService;
import ch.michu.tech.swissbudget.framework.dto.MessageDto;
import ch.michu.tech.swissbudget.framework.error.exception.AgentNotRegisteredException;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.MfaCodeRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import ch.michu.tech.swissbudget.test.AppIntegrationTest;
import ch.michu.tech.swissbudget.test.TestDataManager;
import ch.michu.tech.swissbudget.test.TestHttpClient;
import ch.michu.tech.swissbudget.test.TestMailSender;
import io.helidon.http.HeaderNames;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.time.Instant;
import java.time.ZoneId;
import org.json.JSONObject;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
class AuthenticationEndpointTest extends AppIntegrationTest {

    private final TestMailSender mailSender;
    private boolean wroteData = false;

    @Inject
    protected AuthenticationEndpointTest(TestDataManager data, TestHttpClient client, TestMailSender mailSender) {
        super("/auth", data, client);
        this.mailSender = mailSender;
    }

    @Override
    protected boolean wasDataModified() {
        return wroteData;
    }

    @Test
    @Order(0)
    void login_happy() {
        LoginDto dto = new LoginDto(new CredentialDto("user@test.ch", "test"), true);
        String userId = data.getGeneratedId("usr_tst");

        long executedAtSeconds = Instant.now().atZone(ZoneId.of("CET")).toEpochSecond();
        Response response = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .post(Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());

        RegisteredUserRecord user = data.getDsl().fetch(REGISTERED_USER, REGISTERED_USER.ID.eq(userId)).getFirst();
        long actualSeconds = user.getLastLogin().atZone(ZoneId.of("CET")).toEpochSecond();
        niceAssertInSecond(executedAtSeconds, actualSeconds);
        assertNotNull(user.get(REGISTERED_USER.CURRENT_SESSION));
        MessageDto token = response.readEntity(MessageDto.class);

        Response validationResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token.getMessage())
            .get();
        assertEquals(Status.NO_CONTENT.getStatusCode(), validationResponse.getStatus());

        response.close();
        validationResponse.close();
        wroteData = false;
    }

    @Test
    void login_disabledUser() {
        LoginDto dto = new LoginDto(new CredentialDto("locked@test.ch", "test"), true);

        Response response = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .post(Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }

    @Test
    void login_newAgent() {
        mailSender.clearSentMails();
        LoginDto dto = new LoginDto(new CredentialDto("user@test.ch", "test"), true);

        Response response = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "other-agent")
            .post(Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        String res = response.readEntity(String.class);
        assertTrue(res.contains(AgentNotRegisteredException.class.getSimpleName()));

        assertEquals(1, mailSender.getMessages().size());

        response.close();
        wroteData = true;
    }

    @Test
    void login_invalidCredentials() {
        LoginDto userWrongDto = new LoginDto(new CredentialDto("not-a-user", "test"), true);
        LoginDto passwordWrongDto = new LoginDto(new CredentialDto("user@test.ch", "blah"), true);

        Response urResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "other-agent")
            .post(Entity.entity(userWrongDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.FORBIDDEN.getStatusCode(), urResponse.getStatus());

        Response pwResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "other-agent")
            .post(Entity.entity(passwordWrongDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.FORBIDDEN.getStatusCode(), pwResponse.getStatus());

        urResponse.close();
        pwResponse.close();
        wroteData = false;
    }

    @Test
    @Order(1)
    void mfa_happy() {
        mailSender.clearSentMails();
        String expectedUserId = data.getGeneratedId("usr_tst");
        LoginDto loginDto = new LoginDto(new CredentialDto("user@test.ch", "test"), false);
        Response loginResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(loginDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), loginResponse.getStatus());
        String content = loginResponse.readEntity(String.class);
        MfaCodeDto mfaDto = loadMfaCode(content);
        assertEquals(expectedUserId, mfaDto.getUserId());

        Response mfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), mfaResponse.getStatus());
        MessageDto token = mfaResponse.readEntity(MessageDto.class);

        Response validationResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token.getMessage())
            .get();
        assertEquals(Status.NO_CONTENT.getStatusCode(), validationResponse.getStatus());

        Response secondMfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), secondMfaResponse.getStatus());

        assertEquals(1, mailSender.getMessages().size());
        loginResponse.close();
        mfaResponse.close();
        validationResponse.close();
        secondMfaResponse.close();
        wroteData = true;
    }

    @Test
    void mfa_maxTries() {
        LoginDto loginDto = new LoginDto(new CredentialDto("user@test.ch", "test"), false);
        Response loginResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(loginDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), loginResponse.getStatus());
        String content = loginResponse.readEntity(String.class);
        MfaCodeDto mfaDto = loadMfaCode(content);
        int correctCode = mfaDto.getCode();
        mfaDto.setCode(111111);

        Response firstMfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), firstMfaResponse.getStatus());

        Response secondMfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), secondMfaResponse.getStatus());

        mfaDto.setCode(correctCode);
        Response thridMfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), thridMfaResponse.getStatus());

        // test if still works even after deletion
        Response fourthMfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), fourthMfaResponse.getStatus());

        loginResponse.close();
        firstMfaResponse.close();
        secondMfaResponse.close();
        thridMfaResponse.close();
        fourthMfaResponse.close();
        wroteData = true;
    }

    @Test
    void mfa_deviceChanged() {
        String expectedUserId = data.getGeneratedId("usr_tst");
        LoginDto loginDto = new LoginDto(new CredentialDto("user@test.ch", "test"), false);
        Response loginResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(loginDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), loginResponse.getStatus());
        String content = loginResponse.readEntity(String.class);
        MfaCodeDto mfaDto = loadMfaCode(content);
        assertEquals(expectedUserId, mfaDto.getUserId());

        Response mfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "another-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), mfaResponse.getStatus());

        // after device change the process should be deleted, due to security reasons
        Response secondMfaResponse = client.getTarget()
            .path("/auth/mfa")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "new-agent")
            .post(Entity.entity(mfaDto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), secondMfaResponse.getStatus());

        loginResponse.close();
        mfaResponse.close();
        secondMfaResponse.close();
        wroteData = false;
    }

    @Test
    void validation_agentChange() {
        LoginDto dto = new LoginDto(new CredentialDto("user@test.ch", "test"), false);

        Response response = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .post(Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        String token = response.readEntity(MessageDto.class).getMessage();

        Response validationResponse = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "other-agent")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token)
            .get();
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), validationResponse.getStatus());

        response.close();
        validationResponse.close();
        wroteData = false;
    }

    @Test
    void validation_ipCheck() {
        LoginDto dto = new LoginDto(new CredentialDto("user@test.ch", "test"), false);

        Response response = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .header(HeaderNames.X_FORWARDED_FOR.defaultCase(), "123.123.123.123")
            .post(Entity.entity(dto, MediaType.APPLICATION_JSON_TYPE));
        assertEquals(Status.OK.getStatusCode(), response.getStatus());
        String token = response.readEntity(MessageDto.class).getMessage();

        Response validationRealIpInvalid = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .header(AuthenticationService.HEADER_X_REAL_IP.defaultCase(), "1.1.1.1")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token)
            .get();
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), validationRealIpInvalid.getStatus());

        Response validationReadIpValid = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .header(AuthenticationService.HEADER_X_REAL_IP.defaultCase(), "123.123.123.123")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token)
            .get();
        assertEquals(Status.NO_CONTENT.getStatusCode(), validationReadIpValid.getStatus());

        Response validationForwardedForInvalid = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .header(HeaderNames.X_FORWARDED_FOR.defaultCase(), "1.1.1.1")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token)
            .get();
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), validationForwardedForInvalid.getStatus());

        Response validationForwardedForValid = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .header(HeaderNames.X_FORWARDED_FOR.defaultCase(), "123.123.123.123")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token)
            .get();
        assertEquals(Status.NO_CONTENT.getStatusCode(), validationForwardedForValid.getStatus());

        Response validationDefaultInvalid = client.getTarget()
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), "test-agent")
            .header(AuthenticationService.HEADER_AUTH_TOKEN, token)
            .get();
        assertEquals(Status.UNAUTHORIZED.getStatusCode(), validationDefaultInvalid.getStatus());

        response.close();
        validationRealIpInvalid.close();
        validationReadIpValid.close();
        validationForwardedForInvalid.close();
        validationForwardedForValid.close();
        validationDefaultInvalid.close();
        wroteData = false;
    }

    protected MfaCodeDto loadMfaCode(String responseJSON) {
        JSONObject args = new JSONObject(responseJSON).optJSONObject("args");
        assertNotNull(args);
        String processId = args.optString("processId");
        assertNotNull(processId);
        String userId = args.optString("userId");

        MfaCodeRecord mfaCode = data.getDsl().fetch(MFA_CODE, MFA_CODE.USER_ID.eq(userId)).getFirst();
        assertEquals(processId, mfaCode.getId());
        assertEquals("new-agent", mfaCode.getUserAgent());
        int code = mfaCode.getCode();

        return new MfaCodeDto(processId, userId, code);
    }
}
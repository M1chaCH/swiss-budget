package ch.michu.tech.swissbudget.test;

import static org.junit.jupiter.api.Assertions.fail;

import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.dto.LoginDto;
import ch.michu.tech.swissbudget.framework.authentication.AuthenticationService;
import ch.michu.tech.swissbudget.framework.dto.MessageDto;
import io.helidon.http.HeaderNames;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class TestHttpClient {

    public static final String ROOT_MAIL = "root@test.ch";
    public static final String ROOT_PASSWORD = "test";
    public static final String ROOT_AGENT = "root-agent";
    public static final String ROOT_IP = "185.244.115.101";
    private static final Logger LOGGER = Logger.getLogger(TestHttpClient.class.getSimpleName());
    @Getter
    private final WebTarget target;
    private final List<Response> openResponses = new ArrayList<>();
    private final int defaultMaxTestRequestMillis;

    @Setter
    private String defaultPath = "";
    private int activeMaxTestRequestMillis;
    private String rootSessionToken;

    @Inject
    public TestHttpClient(WebTarget target, @ConfigProperty(name = "ch.michu.tech.test.max.request") int defaultMaxTestRequestMillis) {
        this.target = target;
        this.defaultMaxTestRequestMillis = defaultMaxTestRequestMillis;
        this.activeMaxTestRequestMillis = defaultMaxTestRequestMillis;
    }

    public String getDefaultToken() {
        if (rootSessionToken == null) {
            rootSessionToken = postLoginUser(ROOT_MAIL, ROOT_PASSWORD, ROOT_AGENT, ROOT_IP, true);
        }

        return rootSessionToken;
    }

    public String postLoginUser(String mail, String password, String agent, String ip, boolean stay) {
        try (Response response = target
            .path("/auth")
            .request()
            .header(HeaderNames.USER_AGENT.defaultCase(), agent)
            .header(HeaderNames.X_FORWARDED_FOR.defaultCase(), ip)
            .post(Entity.entity(new LoginDto(new CredentialDto(mail, password), stay), MediaType.APPLICATION_JSON_TYPE))) {

            MessageDto result = response.readEntity(MessageDto.class);
            return result.getMessage();
        }
    }

    public void closeResponses() {
        openResponses.forEach(Response::close);
        openResponses.clear();
    }

    public AuthenticationBuilder create() {
        return new AuthenticationBuilder();
    }

    public RequestBuilder createAsRootUser() {
        AuthenticationBuilder opts = new AuthenticationBuilder(ROOT_AGENT, ROOT_IP, getDefaultToken());
        return new RequestBuilder(opts);
    }

    public void setMaxRequestTimeOnce(int millis) {
        this.activeMaxTestRequestMillis = millis;
    }

    public class AuthenticationBuilder {

        protected String userAgent = TestHttpClient.ROOT_AGENT;
        protected String ip = TestHttpClient.ROOT_IP;
        protected String authToken;

        protected AuthenticationBuilder() {
        }

        protected AuthenticationBuilder(String userAgent, String ip, String authToken) {
            this.userAgent = userAgent;
            this.ip = ip;
            this.authToken = authToken;
        }

        public AuthenticationBuilder withUserAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        public AuthenticationBuilder withIp(String ip) {
            this.ip = ip;
            return this;
        }

        public RequestBuilder unauthenticated() {
            this.authToken = "invalid";
            return new RequestBuilder(this);
        }

        public RequestBuilder loginUser(String mail) {
            return loginUser(mail, "test", true);
        }

        public RequestBuilder loginUser(String mail, String password, boolean stay) {
            authToken = postLoginUser(mail, password, userAgent, ip, stay);
            return new RequestBuilder(this);
        }
    }

    public class RequestBuilder {

        protected final Map<String, String> queries = new HashMap<>();
        private final AuthenticationBuilder headers;
        protected String path = defaultPath;

        protected RequestBuilder(AuthenticationBuilder headers) {
            this.headers = headers;
        }

        public RequestBuilder targetPath(String path) {
            this.path = path;
            return this;
        }

        public RequestBuilder appendPath(String path) {
            this.path += path;
            return this;
        }

        public RequestBuilder queryParam(String key, String value) {
            queries.put(key, value);
            return this;
        }

        public Response get() {
            Invocation.Builder builder = buildRequestBase();
            long startMillis = Instant.now().toEpochMilli();
            Response r = builder.get();
            openResponses.add(r);
            handleDuration(startMillis, "GET");
            return r;
        }

        public Response post(Object body) {
            Invocation.Builder builder = buildRequestBase();
            long startMillis = Instant.now().toEpochMilli();
            Response r = builder.post(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
            openResponses.add(r);
            handleDuration(startMillis, "POST");
            return r;
        }

        public Response put(Object body) {
            Invocation.Builder builder = buildRequestBase();
            long startMillis = Instant.now().toEpochMilli();
            Response r = builder.put(Entity.entity(body, MediaType.APPLICATION_JSON_TYPE));
            openResponses.add(r);
            handleDuration(startMillis, "PUT");
            return r;
        }

        public Response delete() {
            Invocation.Builder builder = buildRequestBase();
            long startMillis = Instant.now().toEpochMilli();
            Response r = builder.delete();
            openResponses.add(r);
            handleDuration(startMillis, "DELETE");
            return r;
        }

        protected Invocation.Builder buildRequestBase() {
            WebTarget newTarget = target.path(path);
            for (Entry<String, String> query : queries.entrySet()) {
                newTarget = newTarget.queryParam(query.getKey(), query.getValue());
            }

            return newTarget
                .request()
                .header(HeaderNames.X_FORWARDED_FOR.defaultCase(), headers.ip)
                .header(HeaderNames.USER_AGENT.defaultCase(), headers.userAgent)
                .header(AuthenticationService.HEADER_AUTH_TOKEN, headers.authToken);
        }

        protected void handleDuration(long start, String method) {
            int allowedDuration = activeMaxTestRequestMillis;
            // since fail() throws an exception I can't reset active to default after fail,
            // so it has to be done here, but I still need the actual active value
            activeMaxTestRequestMillis = defaultMaxTestRequestMillis;

            long duration = Instant.now().toEpochMilli() - start;
            LOGGER.log(Level.INFO, "sent test request to {0}:{1}, took {2}ms", new Object[]{method, path, duration});

            if (duration > allowedDuration) {
                fail("long running request to %s:%s, took: %sms, but max is %sms".formatted(method, path, duration,
                    allowedDuration));
            }
        }
    }
}

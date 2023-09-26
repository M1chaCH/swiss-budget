package ch.michu.tech.swissbudget.framework.authentication;

import ch.michu.tech.swissbudget.framework.EncodingUtil;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.error.exception.LoginFailedException;
import ch.michu.tech.swissbudget.framework.error.exception.RemoteAddressNotPresentException;
import ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser;
import ch.michu.tech.swissbudget.generated.jooq.tables.Session;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.SessionRecord;
import io.helidon.webserver.RequestHeaders;
import io.helidon.webserver.ServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.validator.routines.InetAddressValidator;

@ApplicationScoped
public class AuthenticationService {

    public static final String HEADER_X_REAL_IP = "X-Real-IP";
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String INVALID_USER_AGENT = "invalid-user-agent";

    private static final Logger LOGGER = Logger.getLogger(
        AuthenticationService.class.getSimpleName());
    private final DataProvider data;

    private final Map<String, SessionRecord> sessions = new HashMap<>();

    @Inject
    public AuthenticationService(DataProvider data) {
        this.data = data;
    }

    public static String extractRemoteAddress(ServerRequest request) {
        RequestHeaders headers = request.headers();

        Optional<String> xRealIp = headers.first(HEADER_X_REAL_IP);

        if (xRealIp.isPresent() && InetAddressValidator.getInstance().isValid(xRealIp.get())) {
            return xRealIp.get();
        }

        Optional<String> xForwardedFor = headers.first(HEADER_X_FORWARDED_FOR);
        if (xForwardedFor.isPresent() && InetAddressValidator.getInstance()
            .isValid(xForwardedFor.get())) {
            return xForwardedFor.get();
        }

        String remoteAddress = request.remoteAddress();
        if (InetAddressValidator.getInstance().isValid(remoteAddress)) {
            return remoteAddress;
        }

        throw new RemoteAddressNotPresentException(remoteAddress);
    }

    public static String extractUserAgent(ServerRequest request) {
        return request.headers().first(HEADER_USER_AGENT).orElse(INVALID_USER_AGENT);
    }

    public String login(String mail, String password, boolean stay, ServerRequest request) {
        return login(mail, password, stay, AuthenticationService.extractUserAgent(request),
            AuthenticationService.extractRemoteAddress(request));
    }

    public String login(String mail, String password, boolean stay, String userAgent,
        String remoteAddress) {
        RegisteredUserRecord user = data.getContext()
            .fetchOne(RegisteredUser.REGISTERED_USER, RegisteredUser.REGISTERED_USER.MAIL.eq(mail));
        if (user == null) {
            LOGGER.log(Level.FINE, "{0} tried to login, but user does not exist",
                new Object[]{mail});
            throw new LoginFailedException(mail);
        }

        String hashedPassword = EncodingUtil.hashString(password, user.getSalt());
        if (user.getPassword().equals(hashedPassword)) {
            getSessionByUserId(user.getId()).ifPresent(session -> {
                LOGGER.log(Level.INFO, "user {0} is already logged in here {1}->{2}",
                    new Object[]{mail, session.getRemoteAddress(), session.getUserAgent()});
                session.delete();
                sessions.remove(session.getSessionToken());

                // TODO notify overridden logged in user about new login
            });

            SessionRecord session = data.getContext().newRecord(Session.SESSION);
            session.setSessionToken(UUID.randomUUID().toString());
            session.setUserId(user.getId());
            session.setUserAgent(userAgent);
            session.setRemoteAddress(remoteAddress);
            session.setStay(stay);
            session.store();
            sessions.put(session.getSessionToken(), session);

            user.setLastLogin(LocalDateTime.now());
            user.store();

            LOGGER.log(Level.INFO, "created new session for {0}", new Object[]{mail});
            return session.getSessionToken();
        }

        LOGGER.log(Level.INFO, "{0} tried to login with wrong password", new Object[]{mail});
        throw new LoginFailedException(mail);
    }

    protected Optional<SessionRecord> getSessionByUserId(int userId) {
        for (Entry<String, SessionRecord> session : sessions.entrySet()) {
            if (session.getValue().getUserId().equals(userId)) {
                return Optional.of(session.getValue());
            }
        }
        return Optional.empty();
    }
}

package ch.michu.tech.swissbudget.framework.authentication;

import ch.michu.tech.swissbudget.app.dto.MfaCodeDto;
import ch.michu.tech.swissbudget.framework.EncodingUtil;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.AgentNotRegisteredException;
import ch.michu.tech.swissbudget.framework.error.exception.LoginFailedException;
import ch.michu.tech.swissbudget.framework.error.exception.LoginFromNewClientException;
import ch.michu.tech.swissbudget.framework.error.exception.RemoteAddressNotPresentException;
import ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser;
import ch.michu.tech.swissbudget.generated.jooq.tables.VerifiedDevice;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.VerifiedDeviceRecord;
import io.helidon.webserver.RequestHeaders;
import io.helidon.webserver.ServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.validator.routines.InetAddressValidator;

@ApplicationScoped
public class AuthenticationService {

    public static final String HEADER_AUTH_TOKEN = "Auth-Token";
    public static final String HEADER_X_REAL_IP = "X-Real-IP";
    public static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String INVALID_USER_AGENT = "invalid-user-agent";

    private final DataProvider data;
    private final SessionTokenService tokenService;
    private final MfaService mfaService;

    private final Map<String, String> userSessionCache = new HashMap<>();

    private final Provider<RequestSupport> supportProvider;

    @Inject
    public AuthenticationService(DataProvider data, SessionTokenService tokenService,
        MfaService mfaService, Provider<RequestSupport> supportProvider) {
        this.data = data;
        this.tokenService = tokenService;
        this.mfaService = mfaService;
        this.supportProvider = supportProvider;
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

    public String login(String mail, String password, boolean stay) {
        RequestSupport support = supportProvider.get();

        RegisteredUserRecord user = data.getContext()
            .fetchOne(RegisteredUser.REGISTERED_USER, RegisteredUser.REGISTERED_USER.MAIL.eq(mail));

        if (user == null) {
            support.logFine(this, "%s tried to login, but user does not exist", mail);
            throw new LoginFailedException(mail);
        }

        String userAgent = AuthenticationService.extractUserAgent(support.getRequest());

        String hashedPassword = EncodingUtil.hashString(password, user.getSalt());
        if (user.getPassword().equals(hashedPassword)) { // password correct
            if (!isUserAgentVerified(user.getId(), userAgent)) {
                String processId = mfaService.startMfaProcess(user);
                throw new AgentNotRegisteredException(mail, userAgent,
                    new MfaCodeDto(processId, user.getId(), -1));
            }

            return createNewSession(user, stay);
        }

        support.logInfo(this, "%s tried to login with wrong password", mail);
        throw new LoginFailedException(mail);
    }

    public SessionToken validateToken(String jwt) {
        SessionToken token = tokenService.validateJwt(jwt);
        if (isCurrentSession(token.getUserId(), token.getSessionId())) {
            return token;
        }
        throw new LoginFromNewClientException();
    }

    public String validateMfaCode(String userId, String mfaProcessId, int providedCode) {
        mfaService.verifyMfaCode(userId, mfaProcessId, providedCode);

        RegisteredUserRecord user = data.getContext()
            .fetchOne(RegisteredUser.REGISTERED_USER, RegisteredUser.REGISTERED_USER.ID.eq(userId));
        if (user == null) {
            throw new LoginFailedException("not found");
        }
        return createNewSession(user, false);
    }

    protected String createNewSession(RegisteredUserRecord user, boolean stay) {
        SessionToken token = tokenService.newSessionToken(user.getId(), stay);

        user.setCurrentSession(token.getSessionId());
        user.setLastLogin(LocalDateTime.now());
        user.store();
        userSessionCache.put(token.getUserId(), token.getSessionId());

        supportProvider.get().logInfo(this, "created new session for %s", user.getMail());
        return tokenService.buildJwt(token);
    }

    protected boolean isUserAgentVerified(String userId, String userAgent) {
        VerifiedDeviceRecord verifiedDevice = data.getContext()
            .fetchOne(VerifiedDevice.VERIFIED_DEVICE,
                VerifiedDevice.VERIFIED_DEVICE.USER_ID.eq(userId)
                    .and(VerifiedDevice.VERIFIED_DEVICE.USER_AGENT.eq(userAgent)));
        return verifiedDevice != null;
    }

    protected boolean isCurrentSession(String userId, String currentSession) {
        String cachedSessionId = userSessionCache.get(userId);
        if (cachedSessionId != null) {
            return cachedSessionId.equals(currentSession);
        }

        supportProvider.get().logFine(this, "could not find session in cache, checking DB");
        RegisteredUserRecord user = data.getContext().fetchOne(RegisteredUser.REGISTERED_USER,
            RegisteredUser.REGISTERED_USER.ID.eq(userId)
                .and(RegisteredUser.REGISTERED_USER.CURRENT_SESSION.eq(currentSession)));
        if (user != null) {
            userSessionCache.put(userId, currentSession);
            return true;
        }
        return false;
    }
}

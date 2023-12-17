package ch.michu.tech.swissbudget.framework.authentication;

import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.error.exception.InvalidSessionTokenException;
import io.helidon.webserver.http.ServerRequest;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import javax.crypto.spec.SecretKeySpec;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class SessionTokenService {

    protected static final String CLAIM_USER_ID = "user_id";
    protected static final String CLAIM_STAY = "stay";
    protected static final String CLAIM_SESSION_ID = "session_id";
    protected static final SignatureAlgorithm SIGNATURE_ALGORITHM = SignatureAlgorithm.HS512;
    protected static final String ISSUER_DELIMITER = "@@";

    protected final int stayLifetimeDays;
    protected final int lifetimeDays;
    protected final SecretKeySpec tokenKey;

    protected final Provider<RequestSupport> supportProvider;

    @Inject
    public SessionTokenService(
        @ConfigProperty(name = "session.stay.lifetime", defaultValue = "30") int stayLifetimeDays,
        @ConfigProperty(name = "session.lifetime", defaultValue = "1") int lifetimeDays,
        @ConfigProperty(name = "session.key") String key,
        Provider<RequestSupport> supportProvider) {
        this.stayLifetimeDays = stayLifetimeDays;
        this.lifetimeDays = lifetimeDays;
        tokenKey = new SecretKeySpec(Base64.getDecoder().decode(key),
            SIGNATURE_ALGORITHM.getJcaName());
        this.supportProvider = supportProvider;
    }

    public SessionToken newSessionToken(String userId, boolean stay) {
        ServerRequest request = supportProvider.get().getRequest();
        String userAgent = AuthenticationService.extractUserAgent(request);
        String remoteAddress = AuthenticationService.extractRemoteAddress(request);

        return new SessionToken(new Date(), constructExpiration(stay), userId, userAgent,
            remoteAddress, stay, UUID.randomUUID().toString());
    }

    public String buildJwt(SessionToken token) {
        return Jwts.builder()
            .signWith(SIGNATURE_ALGORITHM, tokenKey)
            .setIssuer(constructIssuer(token.getUserAgent(), token.getRemoteAddress()))
            .setIssuedAt(token.getIssuedAt())
            .setExpiration(token.getExpiresAt())
            .claim(CLAIM_USER_ID, token.getUserId())
            .claim(CLAIM_STAY, token.isStay())
            .claim(CLAIM_SESSION_ID, token.getSessionId())
            .compact();
    }

    public SessionToken parseJwt(String jwt) {
        if (jwt == null || jwt.isBlank()) {
            throw new InvalidSessionTokenException();
        }

        SessionToken token = new SessionToken();
        try {
            Claims claims = Jwts.parser().setSigningKey(tokenKey).parseClaimsJws(jwt).getBody();

            String[] split = claims.getIssuer().split("@@");
            token.setUserAgent(split[0]);
            token.setRemoteAddress(split[1]);

            token.setStay(claims.get(CLAIM_STAY, Boolean.class));
            token.setUserId(claims.get(CLAIM_USER_ID, String.class));
            token.setSessionId(claims.get(CLAIM_SESSION_ID, String.class));
            token.setIssuedAt(claims.getIssuedAt());
            token.setExpiresAt(claims.getExpiration());
            return token;
        } catch (JwtException | IndexOutOfBoundsException e) {
            throw new InvalidSessionTokenException();
        }
    }

    public SessionToken validateJwt(String jwt) {
        RequestSupport support = supportProvider.get();
        SessionToken sessionToken = parseJwt(jwt);

        String requiredRemoteAddress = AuthenticationService.extractRemoteAddress(support.getRequest());
        String requiredUserAgent = AuthenticationService.extractUserAgent(support.getRequest());

        if (new Date().after(sessionToken.getExpiresAt())
            || !requiredUserAgent.equals(sessionToken.getUserAgent())
            || !requiredRemoteAddress.equals(sessionToken.getRemoteAddress())) {
            support.logFine(this,
                "got invalid session token, either expired (%s) or user device changed (%s - %s)",
                sessionToken.getExpiresAt(),
                sessionToken.getUserAgent(),
                sessionToken.getRemoteAddress());
            throw new InvalidSessionTokenException();
        }

        return sessionToken;
    }

    public Date constructExpiration(boolean stay) {
        Calendar calendar = Calendar.getInstance();
        int days = stay ? stayLifetimeDays : lifetimeDays;
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar.getTime();
    }

    protected String constructIssuer(String userAgent, String remoteAddress) {
        return userAgent + ISSUER_DELIMITER + remoteAddress;
    }
}

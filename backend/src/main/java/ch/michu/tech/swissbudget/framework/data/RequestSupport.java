package ch.michu.tech.swissbudget.framework.data;

import ch.michu.tech.swissbudget.framework.authentication.SessionToken;
import ch.michu.tech.swissbudget.framework.error.exception.InvalidSessionTokenException;
import io.helidon.webserver.http.ServerRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;
import org.jooq.DSLContext;

@RequestScoped
public class RequestSupport {

    protected static final Logger LOGGER = Logger.getLogger("RequestLogger");
    protected final Map<String, Object> store = new HashMap<>();

    @Getter
    private final ServerRequest request;
    @Setter
    private SessionToken sessionToken;
    @Setter
    private DSLContext ctx;

    @Inject
    public RequestSupport(@Context ServerRequest request) {
        this.request = request;
    }

    public void storeProperty(String key, Object value) {
        store.put(key, value);
    }

    public Object loadProperty(String key) {
        return store.get(key);
    }

    public <T> Optional<T> loadProperty(String key, Class<T> type) {
        Object value = loadProperty(key);
        if (!type.isInstance(value)) {
            return Optional.empty();
        }

        try {
            return Optional.of(type.cast(value));
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }

    public UUID getUserIdOrThrow() {
        if (sessionToken == null) {
            throw new InvalidSessionTokenException();
        }

        return sessionToken.getUserId();
    }

    public DSLContext db() {
        return ctx;
    }

    public void logInfo(Object instance, String formattedMessage, Object... args) {
        String message = prepareLogMessage(instance, formattedMessage, args);
        LOGGER.log(Level.INFO, message);
    }

    public void logFine(Object instance, String formattedMessage, Object... args) {
        String message = prepareLogMessage(instance, formattedMessage, args);
        LOGGER.log(Level.FINE, message);
    }

    public void logWarning(Object instance, String formattedMessage, Exception optionalException,
        Object... args) {
        String message = prepareLogMessage(instance, formattedMessage, args);
        LOGGER.log(Level.WARNING, message, optionalException);
    }

    public void logError(Object instance, String formattedMessage, Exception optionalException,
        Object... args) {
        String message = prepareLogMessage(instance, formattedMessage, args);
        LOGGER.log(Level.SEVERE, message, optionalException);
    }

    protected String prepareLogMessage(Object instance, String formattedMessage, Object... args) {
        if (instance == null) {
            instance = this;
        }

        return String.format("r%s@%s: %s", request.id(),
            instance.getClass().getSimpleName(), formattedMessage.formatted(args));
    }
}

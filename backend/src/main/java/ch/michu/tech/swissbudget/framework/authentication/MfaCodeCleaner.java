package ch.michu.tech.swissbudget.framework.authentication;

import static ch.michu.tech.swissbudget.framework.utils.DateBuilder.localDateTimeNow;
import static ch.michu.tech.swissbudget.generated.jooq.tables.MfaCode.MFA_CODE;

import ch.michu.tech.swissbudget.framework.data.connection.DBConnectionFactory;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.MfaCodeRecord;
import io.helidon.microprofile.scheduling.FixedRate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jooq.DSLContext;
import org.jooq.impl.UpdatableRecordImpl;

@SuppressWarnings("unused") // called by Helidon scheduler
@ApplicationScoped
public class MfaCodeCleaner {

    private static final Logger LOGGER = Logger.getLogger(MfaCodeCleaner.class.getSimpleName());
    private final DBConnectionFactory connectionFactory;

    @Inject
    public MfaCodeCleaner(DBConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    @SuppressWarnings("unused") // called by fixed rate scheduler of helidon
    @FixedRate(initialDelay = 0, value = 12, timeUnit = TimeUnit.HOURS)
    public void wipeExpiredCodes() {
        LOGGER.log(Level.FINE, "checking for expired mfa codes", new Object[]{});
        DSLContext db = connectionFactory.createContext();
        List<MfaCodeRecord> codes = db.selectFrom(MFA_CODE).stream().toList();
        codes.stream().filter(code -> code.getExpiresAt().isBefore(localDateTimeNow()))
            .forEach(UpdatableRecordImpl::delete);
        LOGGER.log(Level.FINE, "check for expired mfa codes completed", new Object[]{});
    }
}

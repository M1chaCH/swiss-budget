package ch.michu.tech.swissbudget.framework.authentication;

import static ch.michu.tech.swissbudget.framework.utils.DateBuilder.localDateTimeNow;

import ch.michu.tech.swissbudget.app.service.mail.MailTemplateNames;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.data.RequestTransactionCommitter;
import ch.michu.tech.swissbudget.framework.error.exception.InvalidMfaCodeException;
import ch.michu.tech.swissbudget.framework.error.exception.mail.MailSendException;
import ch.michu.tech.swissbudget.framework.mail.TemplatedMailSender;
import ch.michu.tech.swissbudget.generated.jooq.tables.MfaCode;
import ch.michu.tech.swissbudget.generated.jooq.tables.VerifiedDevice;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.MfaCodeRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.VerifiedDeviceRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class MfaService {

    private final TemplatedMailSender mailSender;
    private final Provider<RequestSupport> supportProvider;
    private final int mfaCodeLifetime;
    private final int mfaCodeTryLimit;

    private final Random random = new Random();

    @Inject
    public MfaService(
        TemplatedMailSender mailSender, Provider<RequestSupport> supportProvider,
        @ConfigProperty(name = "session.mfa.lifetime", defaultValue = "4") int mfaCodeLifetimeHours,
        @ConfigProperty(name = "session.mfa.tries", defaultValue = "5") int mfaCodeTryLimit
    ) {
        this.mailSender = mailSender;
        this.supportProvider = supportProvider;
        this.mfaCodeLifetime = mfaCodeLifetimeHours;
        this.mfaCodeTryLimit = mfaCodeTryLimit;
    }

    public UUID startMfaProcess(RegisteredUserRecord user) {
        RequestSupport support = supportProvider.get();
        String currentUserAgent = AuthenticationService.extractUserAgent(support.getRequest());
        UUID mfaProcessId = UUID.randomUUID();
        MfaCodeRecord mfaCodeRecord = support.db().newRecord(MfaCode.MFA_CODE);
        mfaCodeRecord.setCode(random.nextInt(100000, 999999));
        mfaCodeRecord.setExpiresAt(localDateTimeNow().plusHours(mfaCodeLifetime));
        mfaCodeRecord.setUserId(user.getId());
        mfaCodeRecord.setId(mfaProcessId);
        mfaCodeRecord.setUserAgent(currentUserAgent);
        mfaCodeRecord.store();
        supportProvider.get().logFine(this, "initialized mfa process for user %s", user.getMail());

        try {
            mailSender.sendMail(new InternetAddress(user.getMail(), false),
                                "SwissBudget verification",
                                MailTemplateNames.MFA_MESSAGE,
                                Map.of("code", Integer.toString(mfaCodeRecord.getCode())));
        } catch (AddressException e) {
            throw new MailSendException("not available", e);
        }

        return mfaProcessId;
    }

    public void verifyMfaCode(UUID userId, UUID mfaProcessId, int providedCode) {
        RequestSupport support = supportProvider.get();
        support.storeProperty(RequestTransactionCommitter.FORCE_COMMIT_PROP, true);
        String currentUserAgent = AuthenticationService.extractUserAgent(support.getRequest());

        MfaCodeRecord mfaCode = support.db()
                                       .fetchOne(MfaCode.MFA_CODE, MfaCode.MFA_CODE.ID.eq(mfaProcessId)
                                                                                      .and(MfaCode.MFA_CODE.USER_ID.eq(userId)));

        if (mfaCode == null) {
            throw new InvalidMfaCodeException();
        }
        if (!mfaCode.getUserAgent().equals(currentUserAgent) || mfaCode.getExpiresAt().isBefore(localDateTimeNow())
            || mfaCode.getTries() >= mfaCodeTryLimit) {
            mfaCode.delete();
            throw new InvalidMfaCodeException();
        }
        if (mfaCode.getCode() != providedCode) {
            mfaCode.setTries(mfaCode.getTries() + 1);
            mfaCode.store();

            throw new InvalidMfaCodeException();
        }

        VerifiedDeviceRecord verifiedDeviceRecord = support.db().newRecord(VerifiedDevice.VERIFIED_DEVICE);
        verifiedDeviceRecord.setId(UUID.randomUUID());
        verifiedDeviceRecord.setUserAgent(currentUserAgent);
        verifiedDeviceRecord.setUserId(userId);
        verifiedDeviceRecord.store();

        support.logInfo(this, "mfa process (%s) completed", mfaProcessId);
        mfaCode.delete();
    }
}

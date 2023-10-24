package ch.michu.tech.swissbudget.framework.authentication;

import ch.michu.tech.swissbudget.app.service.mail.MailTemplateNames;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
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
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jooq.DSLContext;

@ApplicationScoped
public class MfaService {

    private final TemplatedMailSender mailSender;
    private final DataProvider data;
    private final Provider<RequestSupport> supportProvider;
    private final int mfaCodeLifetime;
    private final int mfaCodeTryLimit;

    private final Random random = new Random();

    @Inject
    public MfaService(TemplatedMailSender mailSender, DataProvider data, Provider<RequestSupport> supportProvider,
        @ConfigProperty(name = "session.mfa.lifetime", defaultValue = "4") int mfaCodeLifetimeHours,
        @ConfigProperty(name = "session.mfa.tries", defaultValue = "5") int mfaCodeTryLimit) {
        this.mailSender = mailSender;
        this.data = data;
        this.supportProvider = supportProvider;
        this.mfaCodeLifetime = mfaCodeLifetimeHours;
        this.mfaCodeTryLimit = mfaCodeTryLimit;
    }

    public String startMfaProcess(RegisteredUserRecord user) {
        String currentUserAgent = AuthenticationService.extractUserAgent(
            supportProvider.get().getRequest());
        String mfaProcessId = UUID.randomUUID().toString();
        MfaCodeRecord mfaCodeRecord = data.getContext().newRecord(MfaCode.MFA_CODE);
        mfaCodeRecord.setCode(random.nextInt(100000, 999999));
        mfaCodeRecord.setExpiresAt(LocalDateTime.now().plusHours(mfaCodeLifetime));
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

    public void verifyMfaCode(String userId, String mfaProcessId, int providedCode) {
        RequestSupport support = supportProvider.get();
        String currentUserAgent = AuthenticationService.extractUserAgent(support.getRequest());
        DSLContext dataContext = data.getContext();

        MfaCodeRecord mfaCodeRecord = dataContext
            .fetchOne(MfaCode.MFA_CODE,
                MfaCode.MFA_CODE.ID.eq(mfaProcessId)
                    .and(MfaCode.MFA_CODE.USER_ID.eq(userId)
                        .and(MfaCode.MFA_CODE.CODE.eq(providedCode))));

        if (mfaCodeRecord == null || !mfaCodeRecord.getUserAgent().equals(currentUserAgent)
            || mfaCodeRecord.getTries() >= mfaCodeTryLimit) {
            if (mfaCodeRecord != null) {
                mfaCodeRecord.setTries(mfaCodeRecord.getTries() + 1);
                mfaCodeRecord.store();
            }

            throw new InvalidMfaCodeException();
        }

        VerifiedDeviceRecord verifiedDeviceRecord = dataContext.newRecord(VerifiedDevice.VERIFIED_DEVICE);
        verifiedDeviceRecord.setUserAgent(currentUserAgent);
        verifiedDeviceRecord.setUserId(userId);
        verifiedDeviceRecord.store();

        support.logInfo(this, "mfa process (%s) completed", mfaProcessId);
        mfaCodeRecord.delete();
    }
}

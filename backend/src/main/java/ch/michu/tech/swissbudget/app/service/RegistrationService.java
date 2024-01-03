package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.app.dto.CreateMailFolderDto;
import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.dto.RegisterDemoUserDto;
import ch.michu.tech.swissbudget.app.dto.RegisterDto;
import ch.michu.tech.swissbudget.app.exception.BankNotSupportedException;
import ch.michu.tech.swissbudget.app.exception.UserAlreadyExistsException;
import ch.michu.tech.swissbudget.app.service.mail.MailService;
import ch.michu.tech.swissbudget.app.transaction.SupportedBank;
import ch.michu.tech.swissbudget.framework.authentication.AuthenticationService;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.dto.MessageDto;
import ch.michu.tech.swissbudget.framework.utils.EncodingUtil;
import ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser;
import ch.michu.tech.swissbudget.generated.jooq.tables.TransactionMetaData;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.TransactionMetaDataRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class RegistrationService {

    private final MailService mailService;
    private final DataProvider data;
    private final AuthenticationService authService;
    private final Provider<RequestSupport> supportProvider;
    private final DataLoaderService dataLoaderService;

    @Inject
    public RegistrationService(MailService mailService, DataProvider dataProvider,
        AuthenticationService authService, Provider<RequestSupport> supportProvider, DataLoaderService dataLoaderService) {
        this.mailService = mailService;
        this.data = dataProvider;
        this.authService = authService;
        this.supportProvider = supportProvider;
        this.dataLoaderService = dataLoaderService;
    }

    public void checkMailCredentials(CredentialDto credentials) {
        if (doesUserExist(credentials.getMail())) {
            throw new UserAlreadyExistsException(credentials.getMail());
        }

        mailService.testMailConnection(credentials.getMail(), credentials.getPassword());
    }

    public void createMailFolder(CreateMailFolderDto dto) {
        mailService.createFolder(dto.getCredentials().getMail(), dto.getCredentials().getPassword(),
            dto.getFolderName());
    }

    public List<String> getSupportedBanks() {
        return Arrays.stream(SupportedBank.values()).map(SupportedBank::getKey).toList();
    }

    public MessageDto register(RegisterDto dto) {
        if (doesUserExist(dto.getMail())) {
            throw new UserAlreadyExistsException(dto.getMail());
        }

        String passwordSalt = EncodingUtil.generateSalt();
        String hashedPassword = EncodingUtil.hashString(dto.getPassword(), passwordSalt);

        RegisteredUserRecord user = data.getContext().newRecord(RegisteredUser.REGISTERED_USER);
        user.setId(UUID.randomUUID());
        user.setMail(dto.getMail());
        user.setSalt(passwordSalt);
        user.setPassword(hashedPassword);
        // TODO find better way than storing this in plain text
        user.setMailPassword(dto.getMailPassword());
        user.store();

        SupportedBank bank = SupportedBank.fromKey(dto.getBank())
            .orElseThrow(() -> new BankNotSupportedException(dto.getMail(), dto.getBank()));

        TransactionMetaDataRecord metaData = data.getContext().newRecord(TransactionMetaData.TRANSACTION_META_DATA);
        metaData.setUserId(user.getId());
        metaData.setBank(bank.getKey());
        metaData.setTransactionsFolder(dto.getFolderName()); // TODO validate if folter exists
        metaData.store();

        dataLoaderService.insertUserDefaultData(user.getId());

        supportProvider.get().logInfo("created user %s", dto.getMail());

        String sessionToken = authService.login(dto.getMail(), dto.getPassword(), false);
        return new MessageDto(sessionToken);
    }

    public boolean doesUserExist(String mail) {
        return data.getContext().fetchExists(RegisteredUser.REGISTERED_USER, RegisteredUser.REGISTERED_USER.MAIL.eq(mail));
    }

    public boolean doesUsernameExist(String username) {
        return data.getContext().fetchExists(RegisteredUser.REGISTERED_USER, RegisteredUser.REGISTERED_USER.USERNAME.eq(username));
    }

    public MessageDto createDemoUser(RegisterDemoUserDto dto) {
        if (doesUsernameExist(dto.getUsername())) {
            throw new UserAlreadyExistsException(dto.getUsername());
        }

        RegisteredUserRecord user = data.getContext().newRecord(RegisteredUser.REGISTERED_USER);
        user.setId(UUID.randomUUID());
        user.setDemoUser(true);
        user.setUsername(dto.getUsername());

        String passwordSalt = EncodingUtil.generateSalt();
        String hashedPassword = EncodingUtil.hashString(dto.getPassword(), passwordSalt);

        user.setSalt(passwordSalt);
        user.setPassword(hashedPassword);
        user.store();

        dataLoaderService.insertUserDefaultData(user.getId());

        return new MessageDto();
    }
}

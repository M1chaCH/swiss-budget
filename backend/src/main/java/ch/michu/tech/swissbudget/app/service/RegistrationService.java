package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.app.dto.CreateMailFolderDto;
import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.dto.RegisterDto;
import ch.michu.tech.swissbudget.app.exception.UserAlreadyExistsException;
import ch.michu.tech.swissbudget.app.service.mail.MailService;
import ch.michu.tech.swissbudget.framework.EncodingUtil;
import ch.michu.tech.swissbudget.framework.authentication.AuthenticationService;
import ch.michu.tech.swissbudget.framework.data.DataProvider;
import ch.michu.tech.swissbudget.framework.data.RequestSupport;
import ch.michu.tech.swissbudget.framework.dto.MessageDto;
import ch.michu.tech.swissbudget.generated.jooq.tables.RegisteredUser;
import ch.michu.tech.swissbudget.generated.jooq.tables.records.RegisteredUserRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import java.util.UUID;

@ApplicationScoped
public class RegistrationService {

    private final MailService mailService;
    private final DataProvider data;
    private final AuthenticationService authService;
    private final Provider<RequestSupport> supportProvider;

    @Inject
    public RegistrationService(MailService mailService, DataProvider dataProvider,
        AuthenticationService authService, Provider<RequestSupport> supportProvider) {
        this.mailService = mailService;
        this.data = dataProvider;
        this.authService = authService;
        this.supportProvider = supportProvider;
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

    public MessageDto register(RegisterDto dto) {
        if (doesUserExist(dto.getMail())) {
            throw new UserAlreadyExistsException(dto.getMail());
        }

        String passwordSalt = EncodingUtil.generateSalt();
        String hashedPassword = EncodingUtil.hashString(dto.getPassword(), passwordSalt);

        RegisteredUserRecord user = data.getContext().newRecord(RegisteredUser.REGISTERED_USER);
        user.setId(UUID.randomUUID().toString());
        user.setMail(dto.getMail());
        user.setSalt(passwordSalt);
        user.setPassword(hashedPassword);
        // TODO find better way than storing this in plain text
        user.setMailPassword(dto.getMailPassword());
        user.store();

        supportProvider.get().logInfo("created user %s", dto.getMail());

        String sessionToken = authService.login(dto.getMail(), dto.getPassword(), false);
        return new MessageDto(sessionToken);
    }

    public boolean doesUserExist(String mail) {
        return data.getContext().fetchExists(RegisteredUser.REGISTERED_USER,
            RegisteredUser.REGISTERED_USER.MAIL.eq(mail));
    }
}

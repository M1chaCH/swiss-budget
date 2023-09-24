package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.app.dto.CreateMailFolderDto;
import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.dto.RegisterDto;
import ch.michu.tech.swissbudget.app.service.mail.MailService;
import ch.michu.tech.swissbudget.framework.dto.MessageDto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RegistrationService {

    private final MailService mailService;

    @Inject
    public RegistrationService(MailService mailService) {
        this.mailService = mailService;
    }

    public void checkMailCredentials(CredentialDto credentials) {
        // TODO check if mail is already used
        mailService.testMailConnection(credentials.getMail(), credentials.getPassword());
    }

    public void createMailFolder(CreateMailFolderDto dto) {
        mailService.createFolder(dto.getCredentials().getMail(), dto.getCredentials().getPassword(),
            dto.getFolderName());
    }

    public MessageDto register(RegisterDto dto) {
        // TODO implement
        return new MessageDto("session token");
    }
}

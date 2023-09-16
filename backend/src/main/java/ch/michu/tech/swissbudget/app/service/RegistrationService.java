package ch.michu.tech.swissbudget.app.service;

import ch.michu.tech.swissbudget.app.dto.CredentialDto;
import ch.michu.tech.swissbudget.app.service.mail.MailService;
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
        mailService.testMailConnection(credentials.getMail(), credentials.getPassword());
    }
}

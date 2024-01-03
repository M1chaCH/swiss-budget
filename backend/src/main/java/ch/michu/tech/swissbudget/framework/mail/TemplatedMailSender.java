package ch.michu.tech.swissbudget.framework.mail;

import ch.michu.tech.swissbudget.framework.error.exception.mail.MailSendException;
import ch.michu.tech.swissbudget.framework.error.exception.mail.MailTemplateNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * sends mails where the content is built from a template. the template path can be configured here: ch.michu.tech.mail.templates.dir
 * template name is the file name either with or without .html, but file has to be HTML
 */
@ApplicationScoped
public class TemplatedMailSender {

    private static final Logger LOGGER = Logger.getLogger(
        TemplatedMailSender.class.getSimpleName());

    private final MailSender sender;
    private final Path templatesDir;

    @Inject
    public TemplatedMailSender(@ConfigProperty(name = "ch.michu.tech.mail.templates.dir") String templatesUrl, MailSender sender) {
        this.sender = sender;
        this.templatesDir = Path.of(templatesUrl);
    }

    public void sendMailToAdmin(String subject, String templateName, Map<String, String> content) {
        sendMail(sender.getAdminReceiver(), subject, templateName, content);
    }

    public void sendMail(InternetAddress recipient, String subject, String templateName, Map<String, String> content) {
        String internalMessageId = sender.getNextId();

        try {
            String messageContent = loadTemplatedContent(internalMessageId, templateName, content);
            LOGGER.log(Level.FINE, "sending templated mail to {0}: {1}", new Object[]{recipient.getAddress(), messageContent});
            MimeBodyPart body = new MimeBodyPart();
            body.setContent(messageContent, MailSender.HTML_MESSAGE_TYPE);
            sender.sendMessage(internalMessageId, recipient, subject, new MimeMultipart(body));
        } catch (MessagingException e) {
            throw new MailSendException(internalMessageId, e);
        }
    }

    protected String loadTemplatedContent(String id, String templateName, Map<String, String> values) {
        String template = readTemplate(id, templateName);
        return fillTemplate(id, template, values);
    }

    protected String readTemplate(String id, String templateName) {
        templateName = templateName.endsWith(".html") ? templateName : templateName + ".html";
        Path templatePath = templatesDir.resolve(templateName);
        LOGGER.log(Level.FINE, "mail:{0} - loading template at: {1}",
            new Object[]{id, templatePath.toAbsolutePath()});
        try {
            return Files.readString(templatePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new MailTemplateNotFoundException(templateName, e);
        }
    }

    protected String fillTemplate(String id, String template, Map<String, String> values) {
        LOGGER.log(Level.FINE, "mail:{0} - filling template with values", new Object[]{id});
        String filledTemplate = String.valueOf(template);
        for (Entry<String, String> templateValue : values.entrySet()) {
            LOGGER.log(Level.FINE, "mail:{0} - replacing {1}",
                new Object[]{id, templateValue.getKey()});
            filledTemplate = filledTemplate.replaceAll(
                String.format("\\{\\{%s\\}\\}", templateValue.getKey()), templateValue.getValue());
        }
        LOGGER.log(Level.FINE, "mail:{0} - successfully replaces keys with values in template",
            new Object[]{id});
        return filledTemplate;
    }
}

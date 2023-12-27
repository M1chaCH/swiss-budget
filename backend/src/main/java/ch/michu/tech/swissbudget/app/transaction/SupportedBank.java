package ch.michu.tech.swissbudget.app.transaction;

import ch.michu.tech.swissbudget.app.transaction.mail.MailContentHandler;
import ch.michu.tech.swissbudget.app.transaction.mail.RaiffeisenMessageHandler;
import java.util.Optional;
import lombok.Getter;

@Getter
public enum SupportedBank {
    RAIFFEISEN("raiffeisen", RaiffeisenMessageHandler.class);

    private final String key;
    private final Class<? extends MailContentHandler> handler;

    SupportedBank(String key, Class<? extends MailContentHandler> handler) {
        this.key = key;
        this.handler = handler;
    }

    public static Optional<SupportedBank> fromKey(String key) {
        return switch (key.toLowerCase()) {
            case "raiffeisen" -> Optional.of(RAIFFEISEN);
            default -> Optional.empty();
        };
    }
}

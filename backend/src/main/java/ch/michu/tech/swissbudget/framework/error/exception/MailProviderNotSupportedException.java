package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;
import lombok.Getter;

@Getter
public class MailProviderNotSupportedException extends AppException {

    private final String provider;

    public MailProviderNotSupportedException(String provider) {
        super(String.format("%s is no supported mail provider", provider), Status.NOT_FOUND);

        this.provider = provider;
    }
}

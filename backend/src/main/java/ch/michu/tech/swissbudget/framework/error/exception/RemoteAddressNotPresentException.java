package ch.michu.tech.swissbudget.framework.error.exception;

import jakarta.ws.rs.core.Response.Status;

public class RemoteAddressNotPresentException extends AppException {

    public RemoteAddressNotPresentException(String address) {
        super("received request with invalid or hidden remote address: " + address,
            Status.BAD_REQUEST);
    }
}

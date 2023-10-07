package ch.michu.tech.swissbudget.app.exception;

import ch.michu.tech.swissbudget.framework.error.exception.AppException;
import jakarta.ws.rs.core.Response.Status;
import lombok.Getter;

@Getter
public class BankNotSupportedException extends AppException {

    public BankNotSupportedException(String mail, String bank) {
        super(String.format("%s requested unsupported bank: %s", mail, bank), Status.NOT_FOUND);
        toggleReportToAdmin();
    }
}

import {Injectable} from '@angular/core';
import {ErrorDto} from "../dtos/ErrorDto";

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor() {
  }

  public static parseErrorMessage(error: ErrorDto): string {
    switch (error.errorKey) {
      case "DtoValidationException":
        return "Invalid Input";
      case "MailConnectionException":
        return "Could not connect to mail server. " +
            "(check password or check IMAP requirements of provider)";
      case "MailProviderNotSupportedException":
        return "Unfortunately this provider is not supported (@...). I would " +
            "happily add this provider to my list, just send me a message via the help feature. " +
            "But it has to be a private account, school or work accounts are not supported."
      case "UserAlreadyExistsException":
        return "A user with this mail already exists, either login or use a different mail."
      case "AgentNotRegisteredException":
        return "Login with new device detected"
      case "InvalidMfaCodeException":
        return "The provided code is not correct"
      case "InvalidSessionTokenException":
        return "Your session is no longer valid"
      case "RemoteAddressNotPresentException":
        return "Request invalid, please check browser settings or contact admin"
      case "MailSendException":
        return "The server failed to send a mail, please contact admin"
      case "MailTemplateNotFoundException":
        return "This type of mail is not configured properly, contact admin and tell him he needs to get his shit together"
      case "LoginFailedException":
        return "Login failed, check credentials"
      case "ResourceNotFoundException":
        return `We could not find a ${error.args.entity} by ${error.args.value}`
      case "UnexpectedDbException":
        return "There was an unexpected error in the DB, either try again later or contact the admin."
      case "UnexpectedServerException":
        return "There is an issue with the server. Please contact the admin."
      case "InvalidTransactionMailFormatException":
        return "Could not parse your transactions mails, please contact the admin or make sure that you have configured the correct bank."
      default:
        return "Failed, please contact admin.";
    }
  }

  public handleIfGlobalError(e: ErrorDto): boolean { // TODO implement
    switch (e.errorKey) {
      case "AgentNotRegisteredException":
        console.warn("should handle agent not registered, but not implemented")
        return true;
      case "InvalidSessionTokenException":
        console.warn("should handle invalid token, but not implemented")
        return true;
      default:
        return false;
    }
  }

  public showErrorDialog(e: ErrorDto): void {
    console.warn("should show error dialog", e);
  }
}

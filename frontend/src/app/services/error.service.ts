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
      default:
        return "Failed, please contact admin.";
    }
  }

  public handleAuthenticationError(e: ErrorDto): boolean {
    // write if to check if error is auth error and if so handle (either refresh token or login page)
    console.warn("caught auth error > NOT IMPLEMENTED")
    return false;
  }

  public showErrorDialog(e: ErrorDto): void {
    console.warn("should show error dialog", e);
  }
}

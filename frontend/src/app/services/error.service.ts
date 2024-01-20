import {HttpErrorResponse} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {DialogService} from '../components/framework/dialog/dialog.service';
import {DisplayErrorDialogComponent} from '../components/framework/display-error/display-error-dialog.component';
import {ErrorDto} from '../dtos/ErrorDto';
import {TokenService} from './auth.service';

@Injectable({
              providedIn: 'root',
            })
export class ErrorService {

  constructor(
    private dialogService: DialogService,
    private tokenService: TokenService,
  ) {
  }

  public static parseErrorMessage(error: ErrorDto): string {
    switch (error?.errorKey) {
      case 'DtoValidationException':
        return `Validation for '${error.args.field}' failed, specifically '${error.args.validator}' failed.`;
      case 'MailConnectionException':
        return 'Could not connect to mail server. ' +
          '(check password or check IMAP requirements of provider)';
      case 'MailProviderNotSupportedException':
        return 'Unfortunately this provider is not supported (@...). I would ' +
          'happily add this provider to my list, just send me a message via the help feature. ' +
          'But it has to be a private account, school or work accounts are not supported.';
      case 'UserAlreadyExistsException':
        return 'A user with this mail already exists, either login or use a different mail.';
      case 'AgentNotRegisteredException':
        return 'Login with new device detected';
      case 'InvalidMfaCodeException':
        return 'The provided code is not correct';
      case 'InvalidSessionTokenException':
        return 'Your session is no longer valid';
      case 'RemoteAddressNotPresentException':
        return 'Request invalid, please check browser settings or contact admin';
      case 'MailSendException':
        return 'The server failed to send a mail, please contact admin';
      case 'MailTemplateNotFoundException':
        return 'This type of mail is not configured properly, contact admin and tell him he needs to get his shit together';
      case 'LoginFailedException':
        return 'Login failed, check credentials';
      case 'ResourceNotFoundException':
        return `We could not find a ${error.args.entity} by ${error.args.value}`;
      case 'UnexpectedDbException':
        return 'There was an unexpected error in the DB, please try again later. The admin has already been notified.';
      case 'UnexpectedServerException':
        return 'There is an issue with the server. Please contact the admin.';
      case 'InvalidTransactionMailFormatException':
        return 'Could not parse your transactions mails, please contact the admin or make sure that you have configured the correct bank.';
      case 'WebApplicationException':
        return 'Please contact the admin. We will happily help you.';
      case 'ProcessingException':
        return `Please try again later or contact admin. Reason: ${error.args.cause}: ${error.args.message}`;
      case 'KeywordAlreadyExistsException':
        return `Keyword '${error.args.keyword}' already exists in tag: '${error.args.tag}'`;
      case 'LoginFromNewClientException':
        return 'An other client just logged in to this account. If this wasn\'t you, please contact the admin and change your password immediately.';
      default:
        return 'Failed, please contact admin.';
    }
  }

  public handleIfGlobalError(e: ErrorDto): boolean {
    switch (e?.errorKey) {
      case 'AgentNotRegisteredException': // TODO implement
        console.warn('should handle agent not registered, but not implemented');
        return true;
      case 'InvalidSessionTokenException':
        this.tokenService.removeToken();
        location.reload();
        return true;
      default:
        return false;
    }
  }

  public showErrorDialog(e: HttpErrorResponse): void {
    this.dialogService.openDialog(DisplayErrorDialogComponent, e);
  }
}

import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ApiService, endpoint} from "../../../services/api.service";
import {ErrorDto} from "../../../dtos/ErrorDto";

@Component({
  selector: 'app-setup-subpage',
  templateUrl: './setup.subpage.component.html',
  styleUrls: ['./setup.subpage.component.scss']
})
export class SetupSubpageComponent {

  form: SetupForm = new SetupForm();
  cursor: number = 0;
  errorMessage: string | undefined;
  showGoogleMessage: boolean = false;
  loadingMailTest: boolean = false;
  readonly supportedBanks = ["Raiffeisen"];

  constructor(
    private api: ApiService,
  ) {
    this.form.mail.valueChanges.subscribe(v => this.showGoogleMessage = v.includes("gmail"));
  }

  isMailAndPasswordInvalid() {
    return this.form.mail.invalid || this.form.password.invalid;
  }

  checkMailCredentials() {
    if (!this.isMailAndPasswordInvalid()) {
      this.errorMessage = "";
      this.loadingMailTest = true;

      this.api.post<null>(endpoint.CHECK_MAIL, {
        mail: this.form.mail.value,
        password: this.form.password.value
      }).subscribe({
        next: _ => {
          this.loadingMailTest = false;
          this.cursor++;
        },
        error: (err: { error: ErrorDto }) => {
          this.loadingMailTest = false;
          switch (err.error.errorKey) {
            case "DtoValidationException":
              this.errorMessage = "Invalid Input";
              break;
            case "MailConnectionException":
              this.errorMessage = "Could not connect to mail server. " +
                "(check password or check IMAP requirements of provider)";
              break;
            case "MailProviderNotSupportedException":
              this.errorMessage = this.form.mail.value + " is not from a supported provider. I would " +
                "happily add this provider to my list, just send me a message via the help feature. " +
                "But it has to be a private account, school or work accounts are not supported."
              break;
            default:
              this.errorMessage = "Failed, please contact admin.";
          }
        }
      })
    }
  }
}

export class SetupForm {
  mail: FormControl;
  password: FormControl;
  mailFolderName: FormControl;
  bank: FormControl;
  group: FormGroup;

  constructor(
    mail: string = "",
    password: string = "",
    mailFolderName: string = "",
    bank: SupportedBanks = "Raiffeisen") {
    this.mail = new FormControl(mail, [Validators.email, Validators.minLength(5), Validators.required]);
    this.password = new FormControl(password, [Validators.required]);
    this.mailFolderName = new FormControl(mailFolderName, [Validators.required, Validators.minLength(5)]);
    this.bank = new FormControl(bank, [Validators.required],);

    this.group = new FormGroup({
      mail: this.mail,
      password: this.password,
      mailFolderName: this.mailFolderName,
      bank: this.bank,
    })
  }
}

export type SupportedBanks = "Raiffeisen";

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
  loading: boolean = false;

  constructor(
      private api: ApiService,
  ) {
  }

  isInvalid() {
    return this.form.mail.invalid || this.form.password.invalid;
  }

  checkMailCredentials() { // FIXME form group is never valid
    console.log(this.form.group.valid)
    if (this.form.group.valid) {
      this.errorMessage = "";
      this.loading = true;
      console.log("sending")

      this.api.post<null>(endpoint.CHECK_MAIL, {
        mail: this.form.mail.value,
        password: this.form.password.value
      }).subscribe({
        next: _ => {
          console.log("done")
          this.loading = false;
          this.cursor++;
        },
        error: (err: ErrorDto) => {
          console.log(err)
          this.loading = false;
          this.errorMessage = err.errorKey;
        }
      })
    }
  }
}

export class SetupForm { // todo create some sort of abstraction layer component
  mail: FormControl;
  password: FormControl;
  mailFolderName: FormControl;
  bank: FormControl;
  group: FormGroup;

  constructor(
      mail: string = "",
      password: string = "",
      mailFolderName: string = "",
      bank: SupportedBanks = "raiffeisen") {
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

export type SupportedBanks = "raiffeisen";

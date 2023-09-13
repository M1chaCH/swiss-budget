import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";

@Component({
  selector: 'app-setup-subpage',
  templateUrl: './setup.subpage.component.html',
  styleUrls: ['./setup.subpage.component.scss']
})
export class SetupSubpageComponent {

  form: SetupForm = new SetupForm();
  cursor: number = 0;
  errorMessage: string | undefined;

  constructor() {
  }

  checkMailCredentials() {
    this.errorMessage = "not implemented!"
    // todo, test credentials in backend
    // this.cursor++;
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

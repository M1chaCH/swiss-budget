import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Subject} from "rxjs";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  form: LoginForm;

  constructor() {
    this.form = new LoginForm()

    this.form.subscribe(form => {
      console.log(form.mail.value, form.password.value)
    })
  }
}

export class LoginForm {
  mail: FormControl;
  password: FormControl;
  group: FormGroup;

  private submitEvent: Subject<LoginForm> = new Subject<LoginForm>();

  constructor(
    mail: string = "",
    password: string = "") {
    this.mail = new FormControl(mail, [Validators.email, Validators.minLength(5), Validators.required]);
    this.password = new FormControl(password, [Validators.required],);

    this.group = new FormGroup({
      mail: this.mail,
      password: this.password,
    })
  }

  submit() {
    if (this.group.valid)
      this.submitEvent.next(this)
  }

  subscribe(obs: (form: LoginForm) => void) {
    this.submitEvent.subscribe(obs);
  }
}

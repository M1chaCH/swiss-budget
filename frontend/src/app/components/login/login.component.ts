import {Component} from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  form: LoginForm;
  errorMessage: string | undefined;
  loading: boolean = false;

  constructor(
      private auth: AuthService,
  ) {
    this.form = new LoginForm();
  }

  login() {
    if (this.form.group.valid) {
      this.loading = true;
      this.errorMessage = undefined;
      this.auth.login(this.form.mail.value, this.form.password.value, this.form.stay.value).subscribe(response => {
        this.loading = false;
        if (response)
          this.errorMessage = response;
      });
    }
  }
}

export class LoginForm {
  mail: FormControl;
  password: FormControl;
  stay: FormControl;
  group: FormGroup;

  constructor(
      mail: string = "",
      password: string = "",
      stay: boolean = true) {
    this.mail = new FormControl(mail, [Validators.email, Validators.minLength(5), Validators.required]);
    this.password = new FormControl(password, [Validators.required],);
    this.stay = new FormControl(stay, [Validators.required],);

    this.group = new FormGroup({
      mail: this.mail,
      password: this.password,
      stay: this.stay,
    })
  }
}

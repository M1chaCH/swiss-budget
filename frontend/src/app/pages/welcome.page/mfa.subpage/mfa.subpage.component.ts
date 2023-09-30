import {Component, OnInit} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";
import {debounceTime} from "rxjs";
import {AuthService} from "../../../services/auth.service";
import {Router} from "@angular/router";
import {pages} from "../../../app-routing.module";

@Component({
  selector: 'app-mfa.subpage',
  templateUrl: './mfa.subpage.component.html',
  styleUrls: ['./mfa.subpage.component.scss']
})
export class MfaSubpageComponent implements OnInit {
  codeControl: FormControl;
  errorMessage: string | undefined;

  constructor(
      private auth: AuthService,
      private router: Router,
  ) {
    this.codeControl = new FormControl(null, [
      Validators.pattern(/^\d{6}$/), Validators.required
    ]);
  }

  ngOnInit(): void {
    const processId = localStorage.getItem(AuthService.MFA_PROCESS_ID);
    const userId = localStorage.getItem(AuthService.USER_ID);
    if (!processId || !userId)
      this.router.navigate([pages.LOGIN]).then();

    this.codeControl.valueChanges.pipe(debounceTime(500)).subscribe(v => {
      if (this.codeControl.valid)
        this.auth.validateMfaToken(processId ?? "", userId ?? "", v).subscribe(correct => {
          this.errorMessage = correct ? "" : "The provided code is invalid.";
        });
    });
  }
}

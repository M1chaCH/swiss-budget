import {Component, ViewChild} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {Router} from '@angular/router';
import {Observable, of, shareReplay, switchMap} from 'rxjs';
import {pages} from '../../../app-routing.module';
import {StepsPanelComponent} from '../../../components/framework/steps-panel/steps-panel.component';
import {ErrorDto} from '../../../dtos/ErrorDto';
import {SupportedBankDto} from '../../../dtos/SupportedBankDto';
import {ApiService, endpoint} from '../../../services/api.service';
import {AuthService} from '../../../services/auth.service';

@Component({
             selector: 'app-setup-subpage',
             templateUrl: './setup.subpage.component.html',
             styleUrls: ['./setup.subpage.component.scss'],
           })
export class SetupSubpageComponent {

  form: SetupForm = new SetupForm();
  currentError: ErrorDto | undefined;
  showGoogleMessage: boolean = false;
  loadingMailTest: boolean = false;
  loadingCreatingFolder: boolean = false;
  folderCreated: boolean = false;
  secondPasswordControl: FormControl = new FormControl(null, [Validators.required]);
  supportedBanks$: Observable<string[]>;
  protected readonly pages = pages;
  @ViewChild('stepper') private stepperComponent: StepsPanelComponent | undefined;

  constructor(
    private api: ApiService,
    private router: Router,
  ) {
    this.form.mail.valueChanges.subscribe(v => this.showGoogleMessage = v.includes('gmail'));
    this.supportedBanks$ = this.api.get<SupportedBankDto[]>(endpoint.SUPPORTED_BANK).pipe(
      switchMap(response => of(response.map(dto => dto.key))),
      shareReplay(1),
    );
  }

  isMailAndPasswordInvalid() {
    return this.form.mail.invalid || this.form.mailPassword.invalid;
  }

  arePasswordsInvalid() {
    return this.form.password.invalid || this.form.password.value !== this.secondPasswordControl.value;
  }

  createCustomInbox() {
    this.loadingCreatingFolder = true;

    this.api.post(endpoint.CREATE_MAIL_FOLDER, {
      folderName: this.form.folderName.value,
      credentials: {
        mail: this.form.mail.value,
        password: this.form.password.value,
      },
    }).subscribe({
                   next: () => {
                     this.currentError = undefined;
                     this.folderCreated = true;
                     this.loadingCreatingFolder = false;
                   },
                   error: e => {
                     this.currentError = e.error;
                     this.folderCreated = false;
                     this.loadingCreatingFolder = false;
                   },
                 });
  }

  checkMailCredentials() {
    if (!this.isMailAndPasswordInvalid()) {
      this.currentError = undefined;
      this.loadingMailTest = true;

      this.api.post<null>(endpoint.CHECK_MAIL, {
        mail: this.form.mail.value,
        password: this.form.mailPassword.value,
      }).subscribe({
                     next: _ => {
                       this.loadingMailTest = false;
                       this.stepperComponent?.next();
                     },
                     error: (err: { error: ErrorDto }) => {
                       this.loadingMailTest = false;
                       this.currentError = err.error;
                     },
                   });
    }
  }

  continueAfterCreateFolder(noFolder: boolean) {
    if (noFolder)
      this.form.folderName.setValue('INBOX');
    this.stepperComponent?.next();
  }

  completeSetup() {
    if (this.form.group.valid) {
      this.api.post<ErrorDto>(endpoint.REGISTER, {
        folderName: this.form.folderName.value,
        bank: this.form.bank.value,
        mail: this.form.mail.value,
        password: this.form.password.value,
        mailPassword: this.form.mailPassword.value,
      }, undefined, true).subscribe(newAgentError => {
        if (newAgentError.errorKey === 'AgentNotRegisteredException') {
          localStorage.setItem(AuthService.USER_ID, newAgentError.args.userId);
          localStorage.setItem(AuthService.MFA_PROCESS_ID, newAgentError.args.processId);
          this.router.navigate([pages.LOGIN, pages.login.MFA]).then();
        }
      });
    }
  }
}

export class SetupForm {
  mail: FormControl;
  mailPassword: FormControl;
  password: FormControl;
  bank: FormControl;
  folderName: FormControl;
  group: FormGroup;

  constructor(
    mail: string = '',
    mailPassword: string = '',
    password: string = '',
    bank: SupportedBanks = 'Raiffeisen',
    folderName: string = '',
  ) {
    this.mail = new FormControl(mail, [Validators.email, Validators.minLength(5), Validators.required]);
    this.mailPassword = new FormControl(mailPassword, [Validators.required]);
    this.password = new FormControl(password, [Validators.required]);
    this.bank = new FormControl(bank, [Validators.required]);
    this.folderName = new FormControl(folderName, [Validators.minLength(3), Validators.maxLength(32)]);

    this.group = new FormGroup({
                                 mail: this.mail,
                                 mailPassword: this.mailPassword,
                                 password: this.password,
                                 bank: this.bank,
                                 folderName: this.folderName,
                               });
  }
}

export type SupportedBanks = 'Raiffeisen';

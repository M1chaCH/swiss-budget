import {Component, SecurityContext} from '@angular/core';
import {FormControl, FormGroup, Validators} from '@angular/forms';
import {DomSanitizer} from '@angular/platform-browser';
import {ApiService, endpoint} from '../../services/api.service';
import {DialogService} from '../framework/dialog/dialog.service';

@Component({
             selector: 'app-help',
             templateUrl: './help-dialog.component.html',
             styleUrls: ['./help-dialog.component.scss'],
           })
export class HelpDialogComponent {
  form: HelpForm;
  loading: boolean = false;

  constructor(
    private dialogService: DialogService,
    private sanitizer: DomSanitizer,
    private api: ApiService,
  ) {
    this.form = new HelpForm();
  }

  sendRequest() {
    if (this.form.group.valid) {
      this.loading = true;
      const sanitizedContent = this.sanitizer.sanitize(SecurityContext.HTML, this.form.question.value) ?? '!!angular sanitation failed!!';
      this.api.post<null>(endpoint.CONTACT, {
        sourceAddress: this.form.mail.value,
        subject: this.form.subject.value,
        message: sanitizedContent,
      }, undefined, true).subscribe(() => {
        this.loading = false;
        this.dialogService.closeCurrentDialog();
      });
    }
  }
}

export class HelpForm {
  mail: FormControl;
  subject: FormControl;
  question: FormControl;
  group: FormGroup;

  constructor(
    mail: string = '', // TODO autofill from current user if logged in
    subject: string = 'SwissBudget Question',
    question: string = '',
  ) {
    this.mail = new FormControl(mail, [Validators.required, Validators.email]);
    this.subject = new FormControl(subject, [Validators.required, Validators.minLength(4)]);
    this.question = new FormControl(question, [Validators.required, Validators.minLength(25), Validators.maxLength(300)]);
    this.group = new FormGroup({
                                 mail: this.mail,
                                 question: this.question,
                               });
  }
}

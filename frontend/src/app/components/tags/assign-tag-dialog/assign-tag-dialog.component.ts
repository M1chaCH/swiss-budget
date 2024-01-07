import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {catchError, debounceTime, filter, of, switchMap, tap, throttleTime} from 'rxjs';
import {TagDto, TransactionDto} from '../../../dtos/TransactionDtos';
import {ErrorService} from '../../../services/error.service';
import {TagService} from '../../../services/tag.service';
import {AppDialogComponent, DialogService} from '../../framework/dialog/dialog.service';

@Component({
             selector: 'app-assign-tag-dialog',
             templateUrl: './assign-tag-dialog.component.html',
             styleUrls: ['./assign-tag-dialog.component.scss'],
           })
export class AssignTagDialogComponent implements AppDialogComponent<TransactionDto>, OnInit {
  data!: TransactionDto;

  addKeywordControl: FormControl = new FormControl(false);
  selectedTag: TagDto | undefined;
  keywordControl: FormControl = new FormControl<any>(null);
  errorMessage: string | undefined;
  keywordChecked: boolean = false;
  saving: boolean = false;

  constructor(
    private dialogService: DialogService,
    private tagService: TagService,
  ) {
  }

  get transaction() {
    return this.data;
  }

  ngOnInit() {
    this.keywordControl.valueChanges.pipe(
      tap(() => this.keywordChecked = false),
      debounceTime(500),
      filter(() => this.addKeywordControl.value),
      switchMap((keyword) => {
        if (!keyword) {
          this.errorMessage = 'Keyword can\'t be empty';
          return of(true);
        }

        if (!this.transaction.receiver.toLowerCase().includes(keyword.toLowerCase())) {
          this.errorMessage = 'Keyword could not be found in transaction';
          return of(true);
        }

        return this.tagService.isKeywordInTag(keyword).pipe(
          switchMap(() => of(false)),
          catchError(e => {
            this.errorMessage = ErrorService.parseErrorMessage(e?.error);
            return of(true);
          }),
        );
      }),
    ).subscribe(error => {
      if (!error)
        this.errorMessage = undefined;
      this.keywordChecked = true;
    });

    this.addKeywordControl.valueChanges.pipe(throttleTime(500)).subscribe(add => {
      this.errorMessage = undefined;
      if (add)
        this.keywordControl.setValue(this.transaction.receiver.split('\n')[0]);
      else
        this.keywordControl.setValue(null);
    });
  }

  tagSelected(tag: TagDto[]) {
    this.selectedTag = tag[0] ?? undefined;
  }

  save() {
    if (this.selectedTag && (!this.addKeywordControl.value || !this.errorMessage)) {
      this.saving = true;
      let newKeyword: string | undefined;
      if (this.addKeywordControl.value)
        newKeyword = this.keywordControl.value;

      this.tagService.assignTag(this.transaction.id, this.selectedTag.id, newKeyword)
          .pipe(catchError(() => {
            this.dialogService.closeCurrentDialog();
            return of(true);
          }))
          .subscribe(error => {
            if (!error) {
              this.saving = false;
              this.dialogService.closeCurrentDialog();
            }
          });
    }
  }

  cancel() {
    this.dialogService.closeCurrentDialog();
  }
}

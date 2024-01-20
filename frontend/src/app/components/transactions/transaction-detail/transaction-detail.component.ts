import {Component, Input, OnInit} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';
import {debounceTime, filter, merge, Observable, switchMap} from 'rxjs';
import {TransactionDto} from '../../../dtos/TransactionDtos';
import {TransactionService} from '../../../services/transaction.service';
import {DialogService} from '../../framework/dialog/dialog.service';
import {AssignTagDialogComponent} from '../../tags/assign-tag-dialog/assign-tag-dialog.component';
import {ChangeTagDialogComponent} from '../../tags/change-tag-dialog/change-tag-dialog.component';
import {ResolveTagConflictDialogComponent} from '../../tags/resolve-tag-conflict-dialog/resolve-tag-conflict-dialog.component';

@Component({
             selector: 'app-transaction-detail',
             templateUrl: './transaction-detail.component.html',
             styleUrls: ['./transaction-detail.component.scss'],
           })
export class TransactionDetailComponent implements OnInit {
  @Input() transaction!: TransactionDto;

  aliasInput: FormControl;
  noteInput: FormControl;

  constructor(
    private transactionService: TransactionService,
    private dialogService: DialogService,
  ) {
    this.aliasInput = new FormControl('', [Validators.maxLength(50)]);
    this.noteInput = new FormControl('', [Validators.maxLength(250)]);
  }

  ngOnInit() {
    this.aliasInput.patchValue(this.transaction.alias);
    this.noteInput.patchValue(this.transaction.note);

    merge(
      this.aliasInput.valueChanges,
      this.noteInput.valueChanges,
    ).pipe(
      debounceTime(850),
      filter(() => this.aliasInput.valid && this.noteInput.valid),
      switchMap(() => this.saveTransaction()),
    ).subscribe();
  }

  changeTag() {
    this.dialogService.openDialog(ChangeTagDialogComponent, this.transaction);
  }

  assignTag() {
    this.dialogService.openDialog(AssignTagDialogComponent, this.transaction);
  }

  resolveDuplicates() {
    this.dialogService.openDialog(ResolveTagConflictDialogComponent, this.transaction);
  }

  private saveTransaction(): Observable<unknown> {
    this.transaction.alias = this.aliasInput.value;
    this.transaction.note = this.noteInput.value;
    return this.transactionService.saveTransaction(this.transaction);
  }
}

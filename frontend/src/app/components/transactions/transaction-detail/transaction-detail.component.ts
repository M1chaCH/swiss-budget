import {Component, Input, OnInit} from '@angular/core';
import {TransactionDto} from "../../../dtos/TransactionDtos";
import {FormControl, Validators} from "@angular/forms";
import {debounceTime, filter, merge, Observable, switchMap} from "rxjs";
import {TransactionService} from "../../../services/transaction.service";

@Component({
  selector: 'app-transaction-detail',
  templateUrl: './transaction-detail.component.html',
  styleUrls: ['./transaction-detail.component.scss']
})
export class TransactionDetailComponent implements OnInit {
  @Input() transaction!: TransactionDto;

  aliasInput: FormControl;
  noteInput: FormControl;

  constructor(
      private transactionService: TransactionService,
  ) {
    this.aliasInput = new FormControl("", [Validators.maxLength(20)]);
    this.noteInput = new FormControl("", [Validators.maxLength(250)]);
  }

  ngOnInit() {
    this.aliasInput.patchValue(this.transaction.alias);
    this.noteInput.patchValue(this.transaction.note);

    merge(
        this.aliasInput.valueChanges,
        this.noteInput.valueChanges
    ).pipe(
        debounceTime(850),
        filter(() => this.aliasInput.valid && this.noteInput.valid),
        switchMap(() => this.saveTransaction()),
    ).subscribe();
  }

  private saveTransaction(): Observable<void> {
    this.transaction.alias = this.aliasInput.value;
    this.transaction.note = this.noteInput.value;
    return this.transactionService.saveTransaction(this.transaction);
  }
}

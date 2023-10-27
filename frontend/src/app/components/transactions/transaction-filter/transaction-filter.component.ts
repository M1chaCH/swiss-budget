import {Component, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";
import {TransactionService} from "../../../services/transaction.service";
import {debounceTime, merge, of, skip, switchMap} from "rxjs";
import * as moment from "moment/moment";

@Component({
  selector: 'app-transaction-filter',
  templateUrl: './transaction-filter.component.html',
  styleUrls: ['./transaction-filter.component.scss']
})
export class TransactionFilterComponent implements OnInit {

  queryControl: FormControl;
  tagsControl: FormControl;
  fromControl: FormControl<moment.Moment | null>;
  toControl: FormControl<moment.Moment | null>;

  constructor(
      private transactionService: TransactionService,
  ) {
    this.queryControl = new FormControl("");
    this.tagsControl = new FormControl("");
    this.fromControl = new FormControl(null);
    this.toControl = new FormControl(null);
  }

  ngOnInit() {
    merge(
        this.queryControl.valueChanges,
        this.tagsControl.valueChanges,
        this.fromControl.valueChanges,
        this.toControl.valueChanges,
    ).pipe(
        skip(4), // skip the first batch of changes
        debounceTime(850),
        switchMap(() => {
          const fromMoment = this.fromControl.valid ? this.fromControl.value ?? undefined : undefined;
          const toMoment = this.toControl.valid ? this.toControl.value ?? undefined : undefined;

          this.transactionService.reloadFilteredTransactions(this.queryControl.value, undefined, fromMoment, toMoment);
          return of();
        }),
    ).subscribe();
  }
}

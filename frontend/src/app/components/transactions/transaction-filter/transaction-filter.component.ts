import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl} from "@angular/forms";
import {TransactionService} from "../../../services/transaction.service";
import {debounceTime, filter, merge, of, switchMap} from "rxjs";
import * as moment from "moment/moment";
import {DatePickerComponent} from "../../form/date-picker/date-picker.component";

@Component({
  selector: 'app-transaction-filter',
  templateUrl: './transaction-filter.component.html',
  styleUrls: ['./transaction-filter.component.scss']
})
export class TransactionFilterComponent implements OnInit {

  @ViewChild("fromDatePicker", {static: true}) fromDatePicker!: DatePickerComponent;
  @ViewChild("toDatePicker", {static: true}) toDatePicker!: DatePickerComponent;

  queryControl: FormControl;
  tagsControl: FormControl;
  fromControl: FormControl<moment.Moment | null>;
  toControl: FormControl<moment.Moment | null>;
  needAttentionControl: FormControl;

  constructor(
      private transactionService: TransactionService,
  ) {
    this.queryControl = new FormControl("");
    this.tagsControl = new FormControl("");
    this.fromControl = new FormControl(null);
    this.toControl = new FormControl(null);
    this.needAttentionControl = new FormControl(false);
  }

  ngOnInit() {
    const currentFilter = this.transactionService.currentFilter;
    if (currentFilter !== undefined) {
      this.queryControl.setValue(currentFilter.query);
      this.fromControl.setValue(currentFilter.from ?? null);
      this.fromDatePicker.setValue(currentFilter.from ?? null);
      this.toControl.setValue(currentFilter.to ?? null);
      this.toDatePicker.setValue(currentFilter.to ?? null);
      this.needAttentionControl.setValue(currentFilter.needAttention);
    }

    merge(
        this.queryControl.valueChanges,
        this.tagsControl.valueChanges,
        this.fromControl.valueChanges,
        this.toControl.valueChanges,
        this.needAttentionControl.valueChanges,
    ).pipe(
        debounceTime(500),
        filter(() => {
          const lastValues = this.transactionService.currentFilter;
          if (lastValues === undefined)
            return true;

          const query: string | null = this.queryControl.value;
          const fromMoment = this.fromControl.valid ? this.fromControl.value ?? null : null;
          const toMoment = this.toControl.valid ? this.toControl.value ?? null : null;
          const needAttention: boolean = this.needAttentionControl.value ?? false;

          // false true true false FIXME find why dates are always different
          console.log(query !== lastValues.query, fromMoment !== lastValues.from, toMoment !== lastValues.to, needAttention !== lastValues.needAttention)
          return query !== lastValues.query || fromMoment !== lastValues.from || toMoment !== lastValues.to || needAttention !== lastValues.needAttention;
        }),
        switchMap(() => {
          const fromMoment = this.fromControl.valid ? this.fromControl.value ?? undefined : undefined;
          const toMoment = this.toControl.valid ? this.toControl.value ?? undefined : undefined;
          const query = this.queryControl.value;

          this.transactionService.reloadFilteredTransactions(query, undefined, fromMoment, toMoment, this.needAttentionControl.value);
          return of();
        }),
    ).subscribe();
  }

  resetFilter() {
    this.queryControl.setValue("");
    this.tagsControl.setValue("");
    this.fromControl.setValue(null);
    this.toControl.setValue(null);
    this.fromDatePicker.setValue(null);
    this.toDatePicker.setValue(null);
    this.needAttentionControl.setValue(false);
  }

  filterToday() {
    this.fromDatePicker.setValue(moment());
    this.toDatePicker.setValue(moment());
  }

  filterLastWeek() {
    this.fromDatePicker.setValue(moment().add("-1", "week"));
    this.toDatePicker.setValue(moment());
  }
}

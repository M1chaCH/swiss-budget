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

  private lastValues: { query: string | null, from: moment.Moment | null, to: moment.Moment | null } | undefined;

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
        debounceTime(500),
        filter(() => {
          if (!this.lastValues)
            this.lastValues = {
              query: "",
              from: null,
              to: null,
            }

          const query: string | null = this.queryControl.value;
          const fromMoment = this.fromControl.valid ? this.fromControl.value ?? null : null;
          const toMoment = this.toControl.valid ? this.toControl.value ?? null : null;

          return query !== this.lastValues.query || fromMoment !== this.lastValues.from || toMoment !== this.lastValues.to;
        }),
        switchMap(() => {
          const fromMoment = this.fromControl.valid ? this.fromControl.value ?? undefined : undefined;
          const toMoment = this.toControl.valid ? this.toControl.value ?? undefined : undefined;
          const query = this.queryControl.value;

          this.transactionService.reloadFilteredTransactions(query, undefined, fromMoment, toMoment);
          this.lastValues = {
            query: query,
            from: fromMoment ?? null,
            to: toMoment ?? null,
          }
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

import {Component, OnInit, ViewChild} from '@angular/core';
import {FormControl} from '@angular/forms';
import * as moment from 'moment/moment';
import {BehaviorSubject, debounceTime, filter, merge, of, skip, switchMap} from 'rxjs';
import {ApiService} from '../../../services/api.service';
import {TransactionService} from '../../../services/transaction.service';
import {DatePickerComponent} from '../../framework/form/date-picker/date-picker.component';

@Component({
             selector: 'app-transaction-filter',
             templateUrl: './transaction-filter.component.html',
             styleUrls: ['./transaction-filter.component.scss'],
           })
export class TransactionFilterComponent implements OnInit {

  @ViewChild('fromDatePicker', {static: true}) fromDatePicker!: DatePickerComponent;
  @ViewChild('toDatePicker', {static: true}) toDatePicker!: DatePickerComponent;
  queryControl: FormControl;
  fromControl: FormControl<moment.Moment | null>;
  toControl: FormControl<moment.Moment | null>;
  needAttentionControl: FormControl;

  constructor(
    private transactionService: TransactionService,
  ) {
    this.queryControl = new FormControl('');
    this._selectedTags = new BehaviorSubject<string[]>([]);
    this.fromControl = new FormControl(null);
    this.toControl = new FormControl(null);
    this.needAttentionControl = new FormControl(false);
  }

  _selectedTags: BehaviorSubject<string[]>;

  get selectedTags() {
    return this._selectedTags.getValue();
  }

  set selectedTags(value: string[]) {
    this._selectedTags.next(value);
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
      this.selectedTags = currentFilter.tags ?? [];
    }

    merge(
      this.queryControl.valueChanges,
      this._selectedTags.asObservable(),
      this.fromControl.valueChanges,
      this.toControl.valueChanges,
      this.needAttentionControl.valueChanges,
    ).pipe(
      skip(1),
      debounceTime(500),
      filter(() => {
        const lastValues = this.transactionService.currentFilter;
        if (lastValues === undefined)
          return true;

        const query: string | null = this.queryControl.value;
        const fromMoment = this.fromControl.valid ? this.fromControl.value ?? null : null;
        const toMoment = this.toControl.valid ? this.toControl.value ?? null : null;
        const needAttention: boolean = this.needAttentionControl.value ?? false;
        const tagIds = this.selectedTags;

        return query !== lastValues.query
          || fromMoment?.format(ApiService.API_DATE_FORMAT) !== lastValues.from?.format(ApiService.API_DATE_FORMAT)
          || toMoment?.format(ApiService.API_DATE_FORMAT) !== lastValues.to?.format(ApiService.API_DATE_FORMAT)
          || needAttention !== lastValues.needAttention
          || JSON.stringify(tagIds) !== JSON.stringify(lastValues.tags);
      }),
      switchMap(() => {
        const fromMoment = this.fromControl.valid ? this.fromControl.value ?? undefined : undefined;
        const toMoment = this.toControl.valid ? this.toControl.value ?? undefined : undefined;
        const query = this.queryControl.value;
        const tagIds = [...this.selectedTags];

        this.transactionService.reloadFilteredTransactions(query, tagIds, fromMoment, toMoment, this.needAttentionControl.value);
        return of();
      }),
    ).subscribe();
  }

  resetFilter() {
    this.queryControl.setValue('');
    this.selectedTags = [];
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
    this.fromDatePicker.setValue(moment().add('-1', 'week'));
    this.toDatePicker.setValue(moment());
  }
}

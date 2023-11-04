import {Component, Input, OnInit} from '@angular/core';
import * as moment from 'moment/moment';
import {FormControl, Validators} from "@angular/forms";
import {auditTime, BehaviorSubject, merge} from "rxjs";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-date-picker',
  templateUrl: './date-picker.component.html',
  styleUrls: ['./date-picker.component.scss']
})
export class DatePickerComponent implements OnInit {

  @Input() hint: string | undefined = "DD.MM.YYYY";
  @Input() control: FormControl<moment.Moment | null> = new FormControl<moment.Moment>(moment());
  @Input() required: boolean = false;
  @Input() height: string | undefined;
  @Input() width: string | undefined;

  errorMessage: string | undefined;

  dayControl: FormControl<number | null>;
  monthControl: FormControl<number | null>;
  yearControl: FormControl<number | null>;
  disabled: boolean;

  calendarOpen: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  readonly formId: string = `datePickerForm${crypto.randomUUID()}`;

  constructor() {
    this.dayControl = new FormControl<number | null>(null, [Validators.min(1), Validators.max(31)]);
    this.monthControl = new FormControl<number | null>(null, [Validators.min(1), Validators.max(12)]);
    this.yearControl = new FormControl<number | null>(null, [Validators.min(1), Validators.max(9999)]);

    merge(
        this.dayControl.valueChanges,
        this.monthControl.valueChanges,
        this.yearControl.valueChanges,
    ).pipe(
        takeUntilDestroyed(),
        auditTime(500)
    ).subscribe(() => {
      if (this.yearControl.invalid || this.monthControl.invalid || this.dayControl.invalid) {
        this.errorMessage = "date is not possible";
        this.control.setErrors({valid: "nope"})
        return;
      }

      this.errorMessage = undefined;
      this.control.setErrors(null);

      const currentMoment = moment(this.control.value);
      currentMoment.set("year", this.yearControl.value ?? new Date().getFullYear());
      currentMoment.set("M", (this.monthControl.value ?? 1) - 1);
      const currentDayValue = this.dayControl.value;
      if (currentDayValue && currentDayValue > currentMoment.daysInMonth()) {
        this.errorMessage = "day does not exist";
        this.control.setErrors({day: "invalid"});
        return;
      }

      currentMoment.set("date", this.dayControl.value ?? 1);
      this.control.setValue(currentMoment);

      if (!currentMoment.isValid())
        this.control.setErrors({invalid: "yup"});
    });

    this.control.registerOnDisabledChange(disabled => {
      this.disabled = disabled;
      if (disabled) {
        this.yearControl.disable();
        this.monthControl.disable();
        this.dayControl.disable();
      } else {
        this.yearControl.enable();
        this.monthControl.enable();
        this.dayControl.enable();
      }
    });
    this.disabled = this.control.disabled;
  }

  ngOnInit() {
    if (this.control.value)
      this.setValue(this.control.value)
  }

  setValue(v: moment.Moment | null) {
    this.dayControl.setValue(v?.date() ?? null);
    const monthValue = v?.month() ?? null;
    this.monthControl.setValue(monthValue === null ? null : monthValue + 1);
    this.yearControl.setValue(v?.year() ?? null);
    this.control.setValue(v);
  }

  valueSelected(value: moment.Moment) {
    this.dayControl.setValue(value.get("date"));
    this.monthControl.setValue(value.get("M") + 1);
    this.yearControl.setValue(value.get("year"));

    this.control.setValue(value);
    this.calendarOpen.next(false);
  }
}

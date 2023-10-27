import {Component, EventEmitter, Input, Output} from '@angular/core';
import * as moment from 'moment/moment';
import {stepSliderAnimation} from "../../../../animations";

@Component({
  selector: 'app-date-picker-form',
  templateUrl: './date-picker-form.component.html',
  styleUrls: ['./date-picker-form.component.scss'],
  animations: [stepSliderAnimation],
})
export class DatePickerFormComponent {
  @Output() valueChange: EventEmitter<moment.Moment> = new EventEmitter<moment.Moment>();
  readonly dayNames = ["MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"];
  readonly monthNames = ["January", "February", "March", "April", "Mai", "June", "July", "August", "September", "October", "November", "December"];
  days: number[] = [];
  years: number[] = [];
  currentYear: number = new Date().getFullYear();
  currentMonth: number = new Date().getMonth();
  value: moment.Moment = moment();

  constructor() {
    this.fillDays();
    this.fillYears();
  }

  @Input() set initialValue(v: moment.Moment | null) {
    if (v !== null && v.isValid())
      this.value = v;
  }

  private _state: "year" | "month" | "day" = "day";

  get state() {
    return this._state;
  }

  set state(s: "year" | "month" | "day") {
    this._state = s;

    if (s === "year") {
      setTimeout(() => {
        const yearGrid = document.getElementById("yearGrid")!;
        const totalScroll = yearGrid.scrollHeight;
        yearGrid.scrollTo({
          top: (totalScroll / 2) - 70,
          behavior: "smooth",
        })
      }, 50);
    }
  }

  addMonth(toAdd: number) {
    this.value.add(toAdd, "M");
    this.currentMonth = this.value.get("M");
    this.fillDays();
  }

  yearSelected(year: number) {
    this.value.set("year", year);
    this.state = "month";
  }

  monthSelected(month: number) {
    this.value.set("M", month);
    this.fillDays();
    this.state = "day";
  }

  daySelected(day: number) {
    this.value.set("date", day);
    this.valueChange.emit(this.value);
  }

  private fillDays() {
    this.days = [];
    const firstOfMonth = moment(this.value);
    firstOfMonth.set("date", 1);
    const firstDayName = firstOfMonth.format("ddd").toUpperCase();
    for (let dayName of this.dayNames) {
      if (dayName != firstDayName)
        this.days.push(-1);
      else if (dayName === firstDayName)
        break;
    }

    for (let i = 1; i < this.value.daysInMonth() + 1; i++) {
      this.days.push(i);
    }
  }

  private fillYears() {
    this.years = [];
    const start = moment().year() - 100;
    for (let i = start; i < start + 200; i++) {
      this.years.push(i);
    }
  }
}

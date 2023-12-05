import {Component, Self} from "@angular/core";
import {ControlValueAccessor, NgControl} from "@angular/forms";

@Component({
  selector: "app-tag-icon-selector",
  templateUrl: "./tag-icon-selector.component.html",
  styleUrls: ["./tag-icon-selector.component.scss"]
})
export class TagIconSelectorComponent implements ControlValueAccessor {

  readonly icons: string[] = [
    "favorite",
    "interests",
    "spa",
    "emergency",
    "emoji_objects",
    "shopping_cart",
    "directions_car",
    "train",
    "home",
    "mode_heat",
    "mode_cool",
    "sync",
    "token",
    "question_mark",
    "nightlife",
    "styler",
    "all_inclusive",
    "public",
    "vital_signs",
    "dentistry",
    "toys",
    "devices",
    "sports_soccer",
    "sports_hockey",
  ];

  constructor(
      @Self() private ngControl: NgControl,
  ) {
    ngControl.valueAccessor = this;
  }

  get control() {
    return this.ngControl.control;
  }

  select(v: string) {
    this.control?.setValue(v);
  }

  isSelected(v: string) {
    return this.control?.value === v;
  }

  onChange = (_: any) => {
  };

  onTouched = () => {
  };

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  writeValue(_: any): void {
  }
}

import {Component, Self} from "@angular/core";
import {ControlValueAccessor, NgControl} from "@angular/forms";

@Component({
  selector: "app-tag-color-selector",
  templateUrl: "./tag-color-selector.component.html",
  styleUrls: ["./tag-color-selector.component.scss"]
})
export class TagColorSelectorComponent implements ControlValueAccessor {

  readonly colors: string[] = [
    "#8ecae6",
    "#219ebc",
    "#023047",
    "#ffb703",
    "#fb8500",
    "#cad2c5",
    "#84a98c",
    "#52796f",
    "#354f52",
    "#2f3e46",
    "#3c3e3c",
    "#f7b538",
    "#db7c26",
    "#d8572a",
    "#c32f27",
    "#033f63",
    "#28666e",
    "#7c9885",
    "#b5b682",
    "#fedc97",
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

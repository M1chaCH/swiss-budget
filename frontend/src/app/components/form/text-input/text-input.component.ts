import {Component, EventEmitter, Input, Output, Self} from "@angular/core";
import {ControlValueAccessor, FormControl, NgControl, Validators} from "@angular/forms";

@Component({
  selector: 'app-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.scss']
})
export class TextInputComponent implements ControlValueAccessor {
  @Input() width: string = "100%"
  @Input() height: string = "100%";
  @Input() design: "primary" | "secondary" = "primary";

  @Input() placeholder: string | undefined = "Input";
  @Input() hint: string | undefined;
  @Input() errorText: string = "Field is invalid";

  @Input() password: boolean = false;

  @Output() submit: EventEmitter<void> = new EventEmitter<void>();
  @Output() cancel: EventEmitter<void> = new EventEmitter<void>();

  edited: boolean = false;

  constructor(
      @Self() private ngControl: NgControl,
  ) {
    ngControl.valueAccessor = this;
  }

  get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  isRequired() {
    return this.control.hasValidator(Validators.required);
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

import {Component, Input, OnInit} from '@angular/core';
import {FormControl} from "@angular/forms";

@Component({
  selector: 'app-select',
  templateUrl: './select.component.html',
  styleUrls: ['./select.component.scss']
})
export class SelectComponent implements OnInit {

  @Input() value!: FormControl<string | null>;
  @Input() placeholder: string = "";
  @Input() errorMessage: string = "Selection is invalid";

  open: boolean = false;
  filteredOptions: string[] = [];

  private _options: string[] = [];

  @Input() set options(value: string[] | null) {
    this._options = value ?? [];
    this.filteredOptions = this._options;
  }

  ngOnInit(): void {
    this.value.valueChanges.subscribe(_ => this.handleValueChange(this.value))
    this.handleValueChange(this.value);
  }

  isSelected(v: string): boolean {
    return this.value.value === v;
  }

  select(v: string) {
    this.value.setValue(v);
    this.open = false;
  }

  close(event?: any) {
    const hitBackdrop = event ? event.target.id === "backdrop" : true;
    if (hitBackdrop) {
      this.open = false;
    }
  }

  handleCompleteRequest() {
    if (this.filteredOptions.length === 1) {
      this.value.setValue(this.filteredOptions[0]);
    }
  }

  private handleValueChange(control: FormControl<string | null>): void {
    control.setErrors(null)
    const currentValue: string | null = control.value as string;

    this.filteredOptions = this.filteredOptions.filter(o => o.toLowerCase().includes(currentValue.toLowerCase()));

    if (!currentValue || this._options.includes(currentValue)) {
      this.filteredOptions = this._options ?? [];
    } else {
      control.setErrors({notAnOption: true});
    }
  }
}

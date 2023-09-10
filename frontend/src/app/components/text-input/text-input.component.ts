import {Component, Input} from '@angular/core';
import {FormControl, Validators} from "@angular/forms";

@Component({
  selector: 'app-text-input',
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.scss']
})
export class TextInputComponent {
  @Input() width: string = "100%"
  @Input() height: string = "100%";
  @Input() design: "primary" | "secondary" = "primary";

  @Input() value!: FormControl;
  @Input() placeholder: string | undefined = "Input";
  @Input() hint: string | undefined;
  @Input() errorText: string = "Field is invalid";

  @Input() password: boolean = false;

  edited: boolean = false;
  protected readonly Validators = Validators;
}

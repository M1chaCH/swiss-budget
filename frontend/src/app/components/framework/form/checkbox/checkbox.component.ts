import {Component, Input} from '@angular/core';
import {FormControl} from '@angular/forms';

@Component({
             selector: 'app-checkbox',
             templateUrl: './checkbox.component.html',
             styleUrls: ['./checkbox.component.scss'],
           })
export class CheckboxComponent {

  @Input() label: string | undefined;
  @Input() value!: FormControl<boolean>;
}

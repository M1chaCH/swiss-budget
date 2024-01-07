import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {FormControl, Validators} from '@angular/forms';

@Component({
             selector: 'app-text-area',
             templateUrl: './text-area.component.html',
             styleUrls: ['./text-area.component.scss'],
           })
export class TextAreaComponent implements OnInit {
  @Input() width: string = '100%';
  @Input() height: string = '100%';
  @Input() design: 'primary' | 'secondary' = 'primary';

  @Input() value!: FormControl;
  @Input() placeholder: string | undefined = 'Input';
  @Input() hint: string | undefined;
  @Input() errorText: string = 'Field is invalid';
  @Input() rows: number = 5;
  @Input() maxLength: number | undefined;

  @Output() submit: EventEmitter<void> = new EventEmitter<void>();
  @Output() cancel: EventEmitter<void> = new EventEmitter<void>();

  edited: boolean = false;
  length: number = 0;
  protected readonly Validators = Validators;

  ngOnInit(): void {
    this.value.valueChanges.subscribe(newValue => this.length = newValue.length);
    this.length = this.value.value?.length ?? 0;
  }
}

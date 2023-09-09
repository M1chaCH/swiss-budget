import {Component, EventEmitter, Input, Output} from '@angular/core';

@Component({
  selector: 'app-button',
  templateUrl: './button.component.html',
  styleUrls: ['./button.component.scss']
})
export class ButtonComponent {
  @Input() width: string = "100%"
  @Input() height: string = "100%";
  @Input() leftIcon: string | undefined;
  @Input() rightIcon: string | undefined;

  @Output() clicked: EventEmitter<boolean> = new EventEmitter<boolean>();
}

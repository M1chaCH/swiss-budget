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
  @Input() design: "primary" | "secondary" = "primary";
  @Input() disabled: boolean = false;

  @Input() useAnchor: boolean = false;
  @Input() target: "_blank" | "_parent" | "_self" | "_top" = "_self"
  @Input() routerLink: string | undefined;

  @Output() clicked: EventEmitter<boolean> = new EventEmitter<boolean>();
}

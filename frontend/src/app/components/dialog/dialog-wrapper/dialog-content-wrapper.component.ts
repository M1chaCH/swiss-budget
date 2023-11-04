import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-dialog-content-wrapper',
  templateUrl: './dialog-content-wrapper.component.html',
  styleUrls: ['./dialog-content-wrapper.component.scss']
})
export class DialogContentWrapperComponent {
  @Input() title: string | undefined;
  @Input() width: string | undefined;
}

import {Component} from '@angular/core';
import {DialogService} from "../dialog/dialog.service";

@Component({
  selector: 'app-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.scss']
})
export class HelpComponent {
  dialogOpen: boolean = false;

  constructor(
      private dialog: DialogService,
  ) {
    this.dialog.open.subscribe(o => this.dialogOpen = o)
  }


}

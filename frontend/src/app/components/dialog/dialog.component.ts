import {Component, TemplateRef} from '@angular/core';
import {DialogService} from "./dialog.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-dialog',
  templateUrl: './dialog.component.html',
  styleUrls: ['./dialog.component.scss']
})
export class DialogComponent {
  open: Observable<boolean>;
  dialogContent: TemplateRef<any> | undefined;

  constructor(
      private service: DialogService,
  ) {
    this.open = this.service.open;
    this.service.currentContent.subscribe(content =>
        this.dialogContent = content);
  }

  closeDialog(event: any) {
    if (event.target.id === "dialog-container") {
      this.service.closeDialog();
    }
  }
}

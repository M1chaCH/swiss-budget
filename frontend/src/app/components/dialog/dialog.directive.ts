import {Directive, Input, TemplateRef} from '@angular/core';
import {DialogService} from "./dialog.service";

@Directive({
  selector: '[appDialog]'
})
export class DialogDirective {

  constructor(
      private templateRef: TemplateRef<any>,
      private dialogService: DialogService,
  ) {
  }

  @Input()
  public set appDialog(open: boolean) {
    if (open) {
      this.dialogService.openDialog(this.templateRef);
    } else {
      this.dialogService.closeDialog();
    }
  }
}

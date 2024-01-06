import {Component, TemplateRef, ViewChild} from '@angular/core';
import {DialogHostDirective} from './dialog-host.directive';
import {AppDialogOpenItem, DialogService} from './dialog.service';

@Component({
             selector: 'app-dialog',
             templateUrl: './dialog.component.html',
             styleUrls: ['./dialog.component.scss'],
           })
export class DialogComponent {
  open: boolean = false;
  @ViewChild(DialogHostDirective, {static: true}) host!: DialogHostDirective;

  // TODO add state / option for not closable

  constructor(
    private service: DialogService,
  ) {
    service.dialogHostComponent = this;
  }

  openDialog(dialog: AppDialogOpenItem) {
    this.open = true;

    const viewContainer = this.host.viewContainerRef;
    viewContainer.clear();
    if (dialog.componentOrTemplate instanceof TemplateRef) {
      viewContainer.createEmbeddedView(dialog.componentOrTemplate);
    } else {
      const componentRef = viewContainer.createComponent(dialog.componentOrTemplate);
      componentRef.instance.data = dialog.data;
    }
  }

  closeDialog(event?: any) {
    const hitDialog = event ? event.target.id === 'dialog-container' : true;
    if (hitDialog) {
      this.service.closeCurrentDialog();
    }
  }
}

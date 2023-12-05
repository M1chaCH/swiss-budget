import {Injectable, TemplateRef, Type} from "@angular/core";
import {DialogComponent} from "./dialog.component";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  private dialogQueue: AppDialogOpenItem[] = [];
  private currentDialog: AppDialogOpenItem | undefined;

  private _dialogHostComponent!: DialogComponent;

  set dialogHostComponent(component: DialogComponent) {
    this._dialogHostComponent = component;
  }

  public openDialog<I, O>(dialog: Type<any> | TemplateRef<any>, data?: I, onClose?: (output: O | undefined) => void) {
    const dialogOpenItem: AppDialogOpenItem = {
      componentOrTemplate: dialog,
      data: data,
      onClose: onClose
    };
    this.dialogQueue.push(dialogOpenItem);
    this.openNextDialogIfClosed();
  }

  public closeAllDialogs() {
    this.dialogQueue = [];
    this.closeCurrentDialog();
  }

  public closeCurrentDialog(output?: any) {
    this._dialogHostComponent.open = false;
    this.currentDialog?.onClose?.call(this, output);
    setTimeout(() => {
      this.openNextDialogIfClosed();
    }, 300); // to let close animation go by, otherwise user won't notice that there is a SECONDS dialog
  }

  private openNextDialogIfClosed() {
    if (!this._dialogHostComponent.open) {
      this.currentDialog = this.dialogQueue.shift();

      if (this.currentDialog) {
        this._dialogHostComponent.openDialog({
          componentOrTemplate: this.currentDialog.componentOrTemplate,
          data: this.currentDialog.data,
        });
      }
    }
  }
}

export interface AppDialogComponent<T> {
  data: T,
}

export type AppDialogOpenItem = {
  componentOrTemplate: Type<any> | TemplateRef<any>,
  data: any,
  onClose?: (event: any) => void,
};

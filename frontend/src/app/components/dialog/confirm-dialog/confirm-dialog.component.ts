import {Component, OnInit} from "@angular/core";
import {AppDialogComponent, DialogService} from "../dialog.service";

@Component({
  selector: "app-confirm-dialog",
  templateUrl: "./confirm-dialog.component.html",
  styleUrls: ["./confirm-dialog.component.scss"]
})
export class ConfirmDialogComponent implements AppDialogComponent<ConfirmDialogData>, OnInit {
  data!: ConfirmDialogData;
  acceptDisabled: boolean = true;

  constructor(
      private dialogService: DialogService,
  ) {
  }

  ngOnInit() {
    this.acceptDisabled = !!this.data.throttleAccept;
    if (this.data.throttleAccept) {
      const throttleTime = this.data.throttleAcceptTime ?? 2000;
      setTimeout(() => this.acceptDisabled = false, throttleTime);
    }
  }

  close(accepted: boolean) {
    this.dialogService.closeCurrentDialog(accepted);
  }
}

export type ConfirmDialogData = {
  acceptLabel?: string,
  declineLabel?: string,
  decision: string,
  title?: string,
  throttleAccept?: boolean,
  throttleAcceptTime?: number,
}

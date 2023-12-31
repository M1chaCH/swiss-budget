import {HttpErrorResponse} from '@angular/common/http';
import {Component, Input} from '@angular/core';
import {HelpDialogComponent} from '../../help/help-dialog.component';
import {AppDialogComponent, DialogService} from '../dialog/dialog.service';

@Component({
             selector: 'app-display-error',
             templateUrl: './display-error-dialog.component.html',
             styleUrls: ['./display-error-dialog.component.scss'],
           })
export class DisplayErrorDialogComponent implements AppDialogComponent<HttpErrorResponse> {

  @Input() data!: HttpErrorResponse;
  protected readonly location = location;

  constructor(
    private dialogService: DialogService,
  ) {
  }

  openHelpDialog() {
    this.dialogService.openDialog(HelpDialogComponent);
    this.dialogService.closeCurrentDialog();
  }
}

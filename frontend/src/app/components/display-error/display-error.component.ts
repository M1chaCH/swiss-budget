import {Component, Input} from '@angular/core';
import {HttpErrorResponse} from "@angular/common/http";
import {AppDialogComponent} from "../dialog/dialog.service";

@Component({
  selector: 'app-display-error',
  templateUrl: './display-error.component.html',
  styleUrls: ['./display-error.component.scss']
})
export class DisplayErrorComponent implements AppDialogComponent<HttpErrorResponse> {

  @Input() data!: HttpErrorResponse;
}

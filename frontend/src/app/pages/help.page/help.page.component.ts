import {ChangeDetectionStrategy, Component} from '@angular/core';
import {AuthService} from "../../services/auth.service";
import {DialogService} from "../../components/dialog/dialog.service";
import {HelpDialogComponent} from "../../components/help/help-dialog.component";

@Component({
  selector: 'app-help.page',
  templateUrl: './help.page.component.html',
  styleUrls: ['./help.page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HelpPageComponent {

  constructor(
      public authService: AuthService,
      private dialogService: DialogService,
  ) {
  }

  openHelpDialog() {
    this.dialogService.openDialog(HelpDialogComponent);
  }
}

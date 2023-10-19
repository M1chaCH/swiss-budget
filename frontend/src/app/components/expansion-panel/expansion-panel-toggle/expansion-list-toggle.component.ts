import {Component, Input} from '@angular/core';
import {ExpansionService} from "../expansion.service";

@Component({
  selector: 'app-expansion-list-toggle',
  templateUrl: './expansion-list-toggle.component.html',
  styleUrls: ['./expansion-list-toggle.component.scss']
})
export class ExpansionListToggleComponent {
  @Input() topic!: string;

  constructor(
      private expansionService: ExpansionService,
  ) {
  }

  changeAll(open: boolean) {
    this.expansionService.changeTopic(this.topic, open);
  }
}

import {Component, Input} from '@angular/core';

@Component({
  selector: 'app-styled-amount',
  templateUrl: './styled-amount.component.html',
  styleUrls: ['./styled-amount.component.scss']
})
export class StyledAmountComponent {
  @Input() expense!: boolean;
  @Input() amount!: number;
}

import {Component, Input} from '@angular/core';
import {TransactionDto} from "../../../dtos/TransactionDtos";

@Component({
  selector: 'app-transaction-preview',
  templateUrl: './transaction-preview.component.html',
  styleUrls: ['./transaction-preview.component.scss']
})
export class TransactionPreviewComponent {

  @Input() transaction!: TransactionDto;
}

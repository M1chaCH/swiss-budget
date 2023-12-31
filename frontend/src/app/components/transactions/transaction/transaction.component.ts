import {Component, Input} from '@angular/core';
import {TransactionDto} from "../../../dtos/TransactionDtos";

@Component({
  selector: 'app-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss']
})
export class TransactionComponent {
  @Input() transaction!: TransactionDto;
}

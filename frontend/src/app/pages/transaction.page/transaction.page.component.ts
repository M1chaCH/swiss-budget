import {Component} from '@angular/core';
import {Observable} from "rxjs";
import {TransactionDto} from "../../dtos/TransactionDtos";
import {TransactionService} from "../../services/transaction.service";

@Component({
  selector: 'app-transaction.page',
  templateUrl: './transaction.page.component.html',
  styleUrls: ['./transaction.page.component.scss']
})
export class TransactionPageComponent {
  transactions$: Observable<TransactionDto[] | undefined>;

  constructor(
      service: TransactionService,
  ) {
    // TODO map to map<string, transaction[]> with dates (Today, Yesterday, Last Week, Months...), (they come ordered desc by date)
    this.transactions$ = service.transactions$;
  }
}
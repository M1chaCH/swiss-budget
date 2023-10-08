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
  transactions$: Observable<TransactionDto[]>;

  constructor(
      private service: TransactionService,
  ) {
    this.transactions$ = service.transactions$;
  }
}
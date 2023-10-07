import {Component} from '@angular/core';
import {Observable, of} from "rxjs";
import {TransactionDto} from "../../dtos/TransactionDtos";
import {ApiService, endpoint} from "../../services/api.service";

@Component({
  selector: 'app-transaction.page',
  templateUrl: './transaction.page.component.html',
  styleUrls: ['./transaction.page.component.scss']
})
export class TransactionPageComponent {
  transactions$: Observable<TransactionDto[]> = of();

  constructor(
      private api: ApiService,
  ) {
    // TODO move to service and cache in ram
    this.transactions$ = this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, [{key: "import", value: "true"}]);
  }
}

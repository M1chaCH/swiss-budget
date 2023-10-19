import {ChangeDetectionStrategy, Component} from '@angular/core';
import {map, Observable} from "rxjs";
import {TransactionDto} from "../../dtos/TransactionDtos";
import {TransactionService} from "../../services/transaction.service";
import * as moment from "moment";

@Component({
  selector: 'app-transaction.page',
  templateUrl: './transaction.page.component.html',
  styleUrls: ['./transaction.page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TransactionPageComponent {
  transactions$: Observable<Map<string, TransactionDto[]>>; // TODO implement lazy loading

  constructor(
      service: TransactionService,
  ) {
    // expects sorted results from backend
    this.transactions$ = service.transactions$.pipe(
        map(t => this.mapTransactionsToDates(t)),
    );
  }

  private mapTransactionsToDates(transactions: TransactionDto[] | undefined): Map<string, TransactionDto[]> {
    const mapped: Map<string, TransactionDto[]> = new Map<string, TransactionDto[]>();
    if (transactions === undefined || transactions.length < 1)
      return mapped;

    for (let transaction of transactions) {
      const key = this.calcMapKey(transaction);
      const current = mapped.get(key) ?? [];
      mapped.set(key, [...current, transaction]);
    }

    return mapped;
  }

  private calcMapKey(transaction: TransactionDto): string {
    const date = moment(transaction.transactionDate);
    const yesterday = moment(new Date()).add(-1, "days");
    const lastMonth = moment(new Date()).add(-1, "months");

    if (date.isSame(new Date(), "day"))
      return "Today";
    else if (date.isSame(yesterday, "day"))
      return "Yesterday";
    else if (date.isSame(new Date(), "month"))
      return "This month";
    else if (date.isSame(lastMonth, "month"))
      return "Last month";
    else
      return date.format("MMM yyyy");
  }
}
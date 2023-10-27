import {ChangeDetectionStrategy, Component, ElementRef, ViewChild} from '@angular/core';
import {map, Observable, tap} from "rxjs";
import {TransactionDto} from "../../dtos/TransactionDtos";
import {TransactionService} from "../../services/transaction.service";
import * as moment from "moment";
import {ScrollService} from "../../services/scroll.service";
import {takeUntilDestroyed} from "@angular/core/rxjs-interop";

@Component({
  selector: 'app-transaction.page',
  templateUrl: './transaction.page.component.html',
  styleUrls: ['./transaction.page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TransactionPageComponent {
  @ViewChild('loadMore', {static: false}) loadMoreDiv: ElementRef<HTMLDivElement> | undefined;
  transactions$: Observable<Map<string, TransactionDto[]>>; // TODO implement lazy loading
  moreTransactionsAvailable: boolean = true;

  constructor(
      private service: TransactionService,
      scrollService: ScrollService,
  ) {
    // expects sorted results from backend
    this.transactions$ = service.transactions$.pipe(
        map(t => this.mapTransactionsToDates(t)),
        tap(() => this.moreTransactionsAvailable = service.hasNextPage()),
    );

    scrollService.scrollChange$.pipe(takeUntilDestroyed())
    .subscribe(() => this.checkVisibilityOfLoadMore());
  }

  private checkVisibilityOfLoadMore() {
    if (!this.loadMoreDiv) {
      return;
    }

    const rect = this.loadMoreDiv.nativeElement.getBoundingClientRect();
    const visiblePercent = Math.max(0, Math.min(rect.bottom, window.innerHeight) - Math.max(rect.top, 0)) / rect.height * 100;

    if (visiblePercent > 60) {
      this.service.loadNextPage();
    }
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
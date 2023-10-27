import {Injectable} from '@angular/core';
import * as moment from 'moment';
import {BehaviorSubject, firstValueFrom, map, Observable, tap} from "rxjs";
import {TransactionDto} from "../dtos/TransactionDtos";
import {ApiService, endpoint} from "./api.service";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  transactions$: Observable<TransactionDto[] | undefined>;
  private readonly transactionUpdater: BehaviorSubject<TransactionDto[] | undefined>;

  private currentMaxLoadedPage: number = 1;
  private pageSize: number = -1;
  private latestLoadedSize: number = -1;
  private currentFilter: { query?: string, tags?: number[], from?: moment.Moment, to?: moment.Moment } | undefined;

  constructor(
      private api: ApiService,
  ) {
    this.transactionUpdater = new BehaviorSubject<TransactionDto[] | undefined>(undefined)
    this.transactions$ = this.transactionUpdater.asObservable().pipe(
        tap(result => result?.map(t => t.transactionDate = moment(t.transactionDate))),
        map(result => this.sortTransactions(result ?? [])),
    );

    this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, [], true)
    .subscribe(transactions => {
      this.pageSize = transactions.length;
      this.latestLoadedSize = transactions.length;
      this.transactionUpdater.next(transactions);
    })
  }

  async importTransactions(): Promise<void> {
    this.insertTransactions(await firstValueFrom(this.api.get<TransactionDto[]>(endpoint.IMPORT_TRANSACTIONS, [], true)));
  }

  loadNextPage() {
    if (this.hasNextPage()) {
      this.currentMaxLoadedPage++;
      const params = this.buildFilterParams(
          this.currentFilter?.query,
          this.currentFilter?.tags,
          this.currentFilter?.from,
          this.currentFilter?.to,
          this.currentMaxLoadedPage
      );

      this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, params, true)
      .subscribe(transactions => {
        this.latestLoadedSize = transactions.length;
        this.insertTransactions(transactions);
      });
    }
  }

  reloadFilteredTransactions(query?: string, tags?: number[], from?: moment.Moment, to?: moment.Moment) {
    const params = this.buildFilterParams(query, tags, from, to, 1);

    this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, params, true)
    .subscribe(transactions => {
      this.currentFilter = {query, tags, from, to};
      this.latestLoadedSize = transactions.length;
      this.transactionUpdater.next(transactions);
    });
  }

  // latest load was entire page -> next page will contain values.
  // if the latest load was smaller than the page size, then this had to be the last page.
  // if the latest load was larger, then something is wrong in the backend.
  hasNextPage() {
    return this.latestLoadedSize >= this.pageSize;
  }

  saveTransaction(transaction: TransactionDto): Observable<void> {
    const transactionDateString = transaction.transactionDate.format(ApiService.API_DATE_FORMAT); // todo maybe generic solution?
    const payload = {
      ...transaction,
      transactionDate: transactionDateString,
    }
    return this.api.put(endpoint.TRANSACTIONS, payload, [], true);
  }

  private insertTransactions(toInsert: TransactionDto[]) {
    let currentTransactions = this.transactionUpdater.getValue() ?? [];
    currentTransactions = [...currentTransactions, ...toInsert];
    this.transactionUpdater.next(currentTransactions);
  }

  private sortTransactions(transactions: TransactionDto[]) {
    return transactions.sort((a, b) => {
      const momentA = a.transactionDate;
      const momentB = b.transactionDate;
      if (momentA.isSame(momentB))
        return 0;
      if (momentA.isBefore(momentB))
        return 1;
      return -1;
    })
  }

  private buildFilterParams(query?: string, tags?: number[], from?: moment.Moment, to?: moment.Moment, page?: number): { key: string, value: string }[] {
    this.currentMaxLoadedPage = page ?? 1;
    const params: { key: string, value: string }[] = [];
    params.push({key: "page", value: `${this.currentMaxLoadedPage}`});
    params.push({key: "query", value: query ?? ""});
    params.push({key: "tagIds", value: tags?.join(";") ?? ""});
    if (from)
      params.push({key: "from", value: from.format(ApiService.API_DATE_FORMAT)});
    if (to)
      params.push({key: "to", value: to.format(ApiService.API_DATE_FORMAT)});

    return params;
  }
}

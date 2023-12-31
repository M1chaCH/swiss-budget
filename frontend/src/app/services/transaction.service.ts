import {Injectable} from "@angular/core";
import * as moment from "moment";
import {firstValueFrom, map, Observable, tap} from "rxjs";
import {TransactionDto} from "../dtos/TransactionDtos";
import {ApiService, endpoint} from "./api.service";
import {EntityCacheService} from "./EntityCacheService";

@Injectable({
  providedIn: "root"
})
export class TransactionService extends EntityCacheService<TransactionDto[]> {
  // latest load was entire page -> next page will contain values.
  private currentMaxLoadedPage: number = 1;
  private pageSize: number = -1;
  private latestLoadedSize: number = -1;

  constructor(
      private api: ApiService,
  ) {
    super();
    super.init();
  }

  private _currentFilter: TransactionFilter;

  public get currentFilter() {
    return this._currentFilter;
  }

  private set currentFilter(filter: TransactionFilter) {
    this._currentFilter = filter;
  }

  async importTransactions(): Promise<void> { // fixme fucks shit up if it is the first import, i think fix is that normal query should complete with empty result
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
          this.currentFilter?.needAttention,
          this.currentMaxLoadedPage
      );

      this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, params, true)
      .subscribe(transactions => {
        this.latestLoadedSize = transactions.length;
        this.insertTransactions(transactions);
      });
    }
  }

  reloadCurrentFilteredTransitions() {
    this.reloadFilteredTransactions(this.currentFilter?.query, this.currentFilter?.tags, this.currentFilter?.from, this.currentFilter?.to, this.currentFilter?.needAttention);
  }

  reloadFilteredTransactions(query?: string, tags?: string[], from?: moment.Moment, to?: moment.Moment, needAttention?: boolean) {
    const params = this.buildFilterParams(query, tags, from, to, needAttention, 1);

    this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, params, true)
    .subscribe(transactions => {
      this.currentFilter = {query, tags, from, to, needAttention};
      this.latestLoadedSize = transactions.length;
      super.updateData(transactions);
    });
  }

  // latest load was entire page -> next page will contain values.
  // if the latest load was smaller than the page size, then this had to be the last page.
  hasNextPage() {
    return this.latestLoadedSize >= this.pageSize;
  }

  saveTransaction(transaction: TransactionDto): Observable<void> {
    const transactionDateString = transaction.transactionDate.format(ApiService.API_DATE_FORMAT); // todo maybe generic solution?
    const payload = {
      ...transaction,
      transactionDate: transactionDateString,
    };
    return this.api.put(endpoint.TRANSACTIONS, payload, [], true);
  }

  protected override async loadData() {
    const transactions = await firstValueFrom(this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, [], true));
    this.pageSize = transactions.length;
    this.latestLoadedSize = transactions.length;
    return transactions;
  }

  protected override pipeResult(data: Observable<TransactionDto[] | undefined>): Observable<TransactionDto[] | undefined> {
    return data.pipe(
        tap(result => result?.map(t => t.transactionDate = moment(t.transactionDate))),
        map(result => this.sortTransactions(result ?? [])),
    );
  }

  private insertTransactions(toInsert: TransactionDto[]) {
    let currentTransactions = super.getCurrent() ?? [];
    currentTransactions = [...currentTransactions, ...toInsert];
    super.updateData(currentTransactions);
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
    });
  }

  private buildFilterParams(query?: string, tags?: string[], from?: moment.Moment, to?: moment.Moment, needAttention?: boolean, page?: number): {
    key: string,
    value: string
  }[] {
    this.currentMaxLoadedPage = page ?? 1;
    const params: { key: string, value: string }[] = [];
    params.push({key: "page", value: `${this.currentMaxLoadedPage}`});
    params.push({key: "query", value: query ?? ""});
    params.push({key: "tagIds", value: tags?.join(";") ?? ""});
    params.push({key: "needAttention", value: `${needAttention ?? false}`});
    if (from)
      params.push({key: "from", value: from.format(ApiService.API_DATE_FORMAT)});
    if (to)
      params.push({key: "to", value: to.format(ApiService.API_DATE_FORMAT)});

    return params;
  }
}

export type TransactionFilter = {
  query?: string,
  tags?: string[],
  from?: moment.Moment,
  to?: moment.Moment,
  needAttention?: boolean
} | undefined;
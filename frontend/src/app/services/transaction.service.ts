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

  constructor(
      private api: ApiService,
  ) {
    this.transactionUpdater = new BehaviorSubject<TransactionDto[] | undefined>(undefined)
    this.transactions$ = this.transactionUpdater.asObservable().pipe(
        tap(result => result?.map(t => t.transactionDate = moment(t.transactionDate))),
        map(result => this.sortTransactions(result ?? [])),
    );

    this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS).subscribe(transactions => this.transactionUpdater.next(transactions))
  }

  async importTransactions(): Promise<void> {
    this.insertTransactions(await firstValueFrom(this.api.get<TransactionDto[]>(endpoint.IMPORT_TRANSACTIONS, [], true)));
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
}

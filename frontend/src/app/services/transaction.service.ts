import {Injectable} from '@angular/core';
import * as moment from 'moment';
import {BehaviorSubject, firstValueFrom, Observable} from "rxjs";
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
    this.transactions$ = this.transactionUpdater.asObservable();

    this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS).subscribe(transactions => this.transactionUpdater.next(transactions))
  }

  async importTransactions(): Promise<void> {
    this.insertTransactions(await firstValueFrom(this.api.get<TransactionDto[]>(endpoint.IMPORT_TRANSACTIONS, [], true)));
  }

  private insertTransactions(toInsert: TransactionDto[]) {
    let currentTransactions = this.transactionUpdater.getValue() ?? [];
    currentTransactions = [...currentTransactions, ...toInsert];
    this.transactionUpdater.next(currentTransactions.sort((a, b) => moment(b.transactionDate).date() - moment(a.transactionDate).date()));
  }
}

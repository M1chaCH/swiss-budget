import {Injectable} from '@angular/core';
import {Observable, shareReplay} from "rxjs";
import {TransactionDto} from "../dtos/TransactionDtos";
import {ApiService, endpoint} from "./api.service";

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  transactions$: Observable<TransactionDto[]>;

  constructor(
      private api: ApiService,
  ) {
    this.transactions$ = this.api.get<TransactionDto[]>(endpoint.TRANSACTIONS, [{key: "import", value: "true"}]).pipe(
        shareReplay(1),
    );
  }
}

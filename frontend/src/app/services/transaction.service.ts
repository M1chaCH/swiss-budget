import {HttpResponse, HttpStatusCode} from '@angular/common/http';
import {Injectable} from '@angular/core';
import * as moment from 'moment';
import {Observable, tap} from 'rxjs';
import {BannerService} from '../components/framework/banner/banner.service';
import {TransactionImportBannerComponent} from '../components/transactions/transaction-importer/transaction-import-banner.component';
import {TransactionDto} from '../dtos/TransactionDtos';
import {ApiService, endpoint} from './api.service';
import {AuthService} from './auth.service';
import {PagedCachedRequest, PagedRequestCache} from './cache/PagedRequestCache';

@Injectable({
              providedIn: 'root',
            })
export class TransactionService extends PagedRequestCache<TransactionDto, TransactionFilter> {

  constructor(
    private auth: AuthService,
    private banner: BannerService,
  ) {
    super(endpoint.TRANSACTIONS, t => {
      t.transactionDate = moment(t.transactionDate); // TODO create generic solution for dates (in and out)
      return t;
    });
  }

  importTransactionsWhenLogin() {
    this.auth.whenLoggedIn().then(() => {
      const importBanner = this.banner.showBanner(TransactionImportBannerComponent);
      this.api.post<HttpResponse<any>>(endpoint.IMPORT_TRANSACTIONS, undefined, undefined, false, {observe: 'response'})
          .subscribe({
                       next: (r) => {
                         importBanner.activateSuccessState();
                         if (r && r.status === HttpStatusCode.Ok) {
                           this.invalidate();
                         }
                       },
                       error: () => importBanner.activateErrorState(),
                     });
    });
  }

  saveTransaction(transaction: TransactionDto): Observable<unknown> {
    const transactionDateString = transaction.transactionDate.format(ApiService.API_DATE_FORMAT); // todo maybe generic solution?
    const payload = {
      ...transaction,
      transactionDate: transactionDateString,
    };
    return this.api.put(endpoint.TRANSACTIONS, payload, undefined, true)
               .pipe(tap(() => this.invalidate()));
  }
}

export type TransactionRequest = PagedCachedRequest<TransactionDto, TransactionFilter>;
export type TransactionFilter = {
  query?: string,
  tagIds?: string[],
  from?: moment.Moment,
  to?: moment.Moment,
  needAttention?: boolean
};
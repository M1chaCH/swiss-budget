import {Injectable} from '@angular/core';
import {Observable, shareReplay, tap} from "rxjs";
import {TagDto} from "../dtos/TransactionDtos";
import {ApiService, endpoint} from "./api.service";
import {TransactionService} from "./transaction.service";

@Injectable({
  providedIn: 'root'
})
export class TagService {
  tags$: Observable<TagDto[] | undefined>;

  constructor(
      private api: ApiService,
      private transactionService: TransactionService,
  ) {
    this.tags$ = api.get<TagDto[]>(endpoint.TAG, undefined, true).pipe(shareReplay(1));
  }

  /**
   * assign a tag to a transaction that has no tag yet and optionally add a keyword to match with other transactions
   * @param transactionId the transaction to update
   * @param tagId the tag to assign
   * @param keyword the keyword to add to the tag and check for matches.
   */
  assignTag(transactionId: string, tagId: number, keyword?: string) {
    return this.api.put(endpoint.ASSIGN_TAG, {transactionId, tagId, keyword}, undefined, true).pipe(
        tap(() => this.transactionService.reloadCurrentFilteredTransitions()),
    );
  }

  /**
   *
   */
  changeTag(transactionId: string, tagId: number) {
    return this.api.put(endpoint.CHANGE_TAG, {transactionId, tagId}, undefined, true).pipe(
        tap(() => this.transactionService.reloadCurrentFilteredTransitions()),
    );
  }

  resolveConflict(transactionId: string, selectedTagId: number, matchingKeywordId: number, removeUnselectedKeywords: boolean): Observable<any> {
    return this.api.put(endpoint.RESOLVE_TAG_CONFLICT, {
      transactionId,
      selectedTagId,
      matchingKeywordId,
      removeUnselectedKeywords
    }, undefined, true).pipe(
        tap(() => this.transactionService.reloadCurrentFilteredTransitions()),
    );
  }

  isKeywordInTag(keyword: string): Observable<any> {
    return this.api.post(endpoint.VALIDATE_NO_KEYWORD, null, [{key: "keyword", value: keyword}]);
  }
}

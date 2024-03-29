import {Injectable} from '@angular/core';
import {Observable, take, tap} from 'rxjs';
import {TagDto} from '../dtos/TransactionDtos';
import {endpoint} from './api.service';
import {RequestCache} from './cache/RequestCache';
import {TransactionService} from './transaction.service';

@Injectable({
              providedIn: 'root',
            })
export class TagService extends RequestCache<TagDto> {

  constructor(
    private transactionService: TransactionService,
  ) {
    super(endpoint.TAG);
  }

  createTag(tagId: number, icon: string, color: string, name: string, keywords: string[]) {
    this.api.post(endpoint.TAG, {
      tagId: tagId,
      icon: icon,
      color: color,
      name: name,
      keywordsToAdd: keywords,
    }, undefined, true)
        .subscribe(() => this.tagsChanged());
  }

  updateTag(tagId: number, icon: string, color: string, name: string, keywordsToAdd: string[], keywordIdsToDelete: number[]) {
    this.api.put(endpoint.TAG, {tagId, icon, color, name, keywordsToAdd, keywordIdsToDelete}, undefined, true)
        .subscribe(() => this.tagsChanged());
  }

  deleteTag(tagId: string) {
    this.api.delete(`${endpoint.TAG}/${tagId}`, true)
        .subscribe(() => this.tagsChanged());
  }

  /**
   * assign a tag to a transaction that has no tag yet and optionally add a keyword to match with other transactions
   * @param transactionId the transaction to update
   * @param tagId the tag to assign
   * @param keyword the keyword to add to the tag and check for matches.
   */
  assignTag(transactionId: string, tagId: string, keyword?: string) {
    return this.api.put(endpoint.ASSIGN_TAG, {transactionId, tagId, keyword}, undefined, true).pipe(
      take(1),
      tap(() => this.tagsChanged()),
    );
  }

  /**
   * change the tag of a transaction
   * @param transactionId the transaction to change
   * @param tagId the new tag id
   */
  changeTag(transactionId: string, tagId: string) {
    return this.api.put(endpoint.CHANGE_TAG, {transactionId, tagId}, undefined, true).pipe(
      tap(() => this.tagsChanged()),
    );
  }

  resolveConflict(transactionId: string,
                  selectedTagId: string,
                  matchingKeywordId: string,
                  removeUnselectedKeywords: boolean,
  ): Observable<any> {
    return this.api.put(endpoint.RESOLVE_TAG_CONFLICT, {
      transactionId,
      selectedTagId,
      matchingKeywordId,
      removeUnselectedKeywords,
    }, undefined, true).pipe(
      tap(() => this.tagsChanged()),
    );
  }

  isKeywordInTag(keyword: string): Observable<any> {
    return this.api.post(endpoint.VALIDATE_NO_KEYWORD, null, new Map([['keyword', keyword]]));
  }

  private tagsChanged() {
    this.transactionService.invalidate();
    this.invalidate();
  }
}

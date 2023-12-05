import {Injectable} from "@angular/core";
import {firstValueFrom, Observable, take, tap} from "rxjs";
import {TagDto} from "../dtos/TransactionDtos";
import {ApiService, endpoint} from "./api.service";
import {EntityCacheService} from "./EntityCacheService";
import {TransactionService} from "./transaction.service";

@Injectable({
  providedIn: "root"
})
export class TagService extends EntityCacheService<TagDto[]> {

  constructor(
      private api: ApiService,
      private transactionService: TransactionService,
  ) {
    super();
    super.init();
  }

  createTag(tagId: number, icon: string, color: string, name: string, keywords: string[]) {
    this.api.post(endpoint.TAG, {
      tagId: tagId,
      icon: icon,
      color: color,
      name: name,
      keywordsToAdd: keywords
    }, undefined, true)
    .subscribe(() => this.transactionService.invalidateCache());
  }

  updateTag(tagId: number, icon: string, color: string, name: string, keywordsToAdd: string[], keywordIdsToDelete: number[]) {
    this.api.put(endpoint.TAG, {tagId, icon, color, name, keywordsToAdd, keywordIdsToDelete}, undefined, true)
    .subscribe(() => this.transactionService.invalidateCache());
  }

  deleteTag(tagId: number) {
    this.api.delete(`${endpoint.TAG}/${tagId}`, true)
    .subscribe(() => {
      const tags = super.getCurrent();
      if (tags) {
        tags.splice(tags.findIndex(t => t.id === tagId), 1);
        super.updateData(tags);
      }
      this.transactionService.invalidateCache();
    });
  }

  /**
   * assign a tag to a transaction that has no tag yet and optionally add a keyword to match with other transactions
   * @param transactionId the transaction to update
   * @param tagId the tag to assign
   * @param keyword the keyword to add to the tag and check for matches.
   */
  assignTag(transactionId: string, tagId: number, keyword?: string) {
    return this.api.put(endpoint.ASSIGN_TAG, {transactionId, tagId, keyword}, undefined, true).pipe(
        take(1),
        tap(() => this.transactionService.reloadFilteredTransactions()),
    );
  }

  /**
   * change the tag of a transaction
   * @param transactionId the transaction to change
   * @param tagId the new tag id
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

  protected override async loadData() {
    return firstValueFrom(this.api.get<TagDto[]>(endpoint.TAG, undefined, true));
  }
}

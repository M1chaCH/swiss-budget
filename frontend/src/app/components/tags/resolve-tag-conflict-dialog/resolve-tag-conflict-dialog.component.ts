import {Component, OnInit} from '@angular/core';
import {AppDialogComponent, DialogService} from "../../dialog/dialog.service";
import {TagDto, TransactionDto} from "../../../dtos/TransactionDtos";
import {catchError, Observable, of} from "rxjs";
import {FormControl} from "@angular/forms";
import {TagService} from "../../../services/tag.service";

@Component({
  selector: 'app-resolve-tag-conflict-dialog',
  templateUrl: './resolve-tag-conflict-dialog.component.html',
  styleUrls: ['./resolve-tag-conflict-dialog.component.scss']
})
export class ResolveTagConflictDialogComponent implements AppDialogComponent<TransactionDto>, OnInit {
  data!: TransactionDto;
  possibleTags?: Observable<TagDto[]>
  saving: boolean = false;

  removeKeywordsControl: FormControl = new FormControl(true);
  selectedTag: TagDto | undefined;

  constructor(
      private dialogService: DialogService,
      private tagService: TagService,
  ) {
  }

  get transaction() {
    return this.data;
  }

  ngOnInit(): void {
    const tags = [
      this.transaction.tag,
      ...(this.transaction.duplicatedTagMatches?.map(duplicate => duplicate.tag) ?? []),
    ];

    this.possibleTags = of(tags);
  }

  tagSelected(tags: TagDto[]) {
    this.selectedTag = tags?.length > 0 ? tags[0] : undefined;
  }

  cancel() {
    this.dialogService.closeCurrentDialog();
  }

  save() {
    if (this.selectedTag) {
      let matchingKeywordId;
      if (this.selectedTag.id === this.transaction.tagId) {
        matchingKeywordId = this.transaction.matchingKeywordId ?? -1;
        console.log("already matched", matchingKeywordId)
      } else {
        matchingKeywordId = this.transaction.duplicatedTagMatches!
        .filter(duplicate => duplicate.tag.id === this.selectedTag!.id)
        .map(duplicate => duplicate.matchingKeyword.id)[0];
        console.log("from dups", matchingKeywordId)
      }

      this.tagService.resolveConflict(
          this.transaction.id,
          this.selectedTag.id,
          matchingKeywordId,
          this.removeKeywordsControl.value ?? true
      ).pipe(catchError(() => {
        this.dialogService.closeCurrentDialog();
        return of(true);
      }))
      .subscribe(error => {
        if (!error) {
          this.saving = false;
          this.dialogService.closeCurrentDialog();
        }
      });
    }
  }
}
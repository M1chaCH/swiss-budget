import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {catchError, Observable, of} from 'rxjs';
import {TagDto, TransactionDto} from '../../../dtos/TransactionDtos';
import {TagService} from '../../../services/tag.service';
import {AppDialogComponent, DialogService} from '../../framework/dialog/dialog.service';

@Component({
             selector: 'app-resolve-tag-conflict-dialog',
             templateUrl: './resolve-tag-conflict-dialog.component.html',
             styleUrls: ['./resolve-tag-conflict-dialog.component.scss'],
           })
export class ResolveTagConflictDialogComponent implements AppDialogComponent<TransactionDto>, OnInit {
  data!: TransactionDto;
  possibleTags?: Observable<TagDto[]>;
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

  save() { // TODO remove logs
    if (this.selectedTag) {
      let matchingKeywordId;
      if (this.selectedTag.id === this.transaction.tagId) {
        matchingKeywordId = this.transaction.matchingKeywordId ?? '';
      } else {
        matchingKeywordId = this.transaction.duplicatedTagMatches!
        .filter(duplicate => duplicate.tag.id === this.selectedTag!.id)
        .map(duplicate => duplicate.matchingKeyword.id)[0];
      }

      this.tagService.resolveConflict(
        this.transaction.id,
        this.selectedTag.id,
        matchingKeywordId,
        this.removeKeywordsControl.value ?? true,
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

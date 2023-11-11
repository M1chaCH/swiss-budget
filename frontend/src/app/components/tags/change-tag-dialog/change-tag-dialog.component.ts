import {Component} from '@angular/core';
import {TagDto, TransactionDto} from "../../../dtos/TransactionDtos";
import {DialogService} from "../../dialog/dialog.service";
import {TagService} from "../../../services/tag.service";

@Component({
  selector: 'app-change-tag-dialog',
  templateUrl: './change-tag-dialog.component.html',
  styleUrls: ['./change-tag-dialog.component.scss']
})
export class ChangeTagDialogComponent {
  data!: TransactionDto;
  saving: boolean = false;

  selectedTag: TagDto | undefined;

  constructor(
      private dialogService: DialogService,
      private tagService: TagService,
  ) {
  }

  get transaction() {
    return this.data;
  }

  selectTag(tag: TagDto[]) {
    this.selectedTag = tag[0] ?? undefined;
  }

  save() {
    if (this.selectedTag) {
      if (this.selectedTag.id === this.transaction.tagId)
        return this.closeDialog();

      this.saving = true;
      this.tagService.changeTag(this.transaction.id, this.selectedTag.id)
      .subscribe({
        next: _ => this.closeDialog(),
        error: _ => this.closeDialog(),
      });
    }
  }

  cancel() {
    this.closeDialog();
  }

  closeDialog() {
    this.saving = false;
    this.dialogService.closeCurrentDialog();
  }
}

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {map, Observable} from "rxjs";
import {TagDto} from "../../../dtos/TransactionDtos";
import {TagService} from "../../../services/tag.service";

@Component({
  selector: 'app-tag-selector',
  templateUrl: './tag-selector.component.html',
  styleUrls: ['./tag-selector.component.scss']
})
export class TagSelectorComponent implements OnInit {

  @Input() multiple: boolean = false;
  @Input() allTags$: Observable<TagDto[] | undefined> | undefined;
  @Input() selectedTags: TagDto[] = [];
  @Output() selectedTagsChange = new EventEmitter<TagDto[]>();

  selectedTagIds: number[] = [];

  constructor(
      private tagService: TagService,
  ) {
  }

  ngOnInit() {
    if (!this.allTags$)
      this.allTags$ = this.tagService.tags$.pipe(
          map(tags => tags?.filter(t => !t.defaultTag)),
      );

    this.selectedTagIds = this.selectedTags.map(t => t.id);
  }

  selectTag(tags: TagDto[], id: number) {
    if (this.multiple) {
      this.selectedTagIds.push(id);
    } else {
      this.selectedTagIds = [id];
    }
    this.selectedTagsChange.emit(tags.filter(t => this.selectedTagIds.includes(t.id)));
  }

  isSelected(id: number) {
    return this.selectedTagIds.includes(id);
  }
}

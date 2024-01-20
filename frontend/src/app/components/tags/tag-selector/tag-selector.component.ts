import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {firstValueFrom, map, Observable} from 'rxjs';
import {TagDto} from '../../../dtos/TransactionDtos';
import {TagService} from '../../../services/tag.service';

@Component({
             selector: 'app-tag-selector',
             templateUrl: './tag-selector.component.html',
             styleUrls: ['./tag-selector.component.scss'],
           })
@UntilDestroy()
export class TagSelectorComponent implements OnInit {

  @Input() multiple: boolean = false;
  @Input() allTags$: Observable<TagDto[] | undefined> | undefined;
  @Input() selectedTags?: TagDto[];
  @Output() selectedTagsChange = new EventEmitter<TagDto[]>();
  @Input() selectedTagIds?: string[] = [];
  @Output() selectedTagIdsChange = new EventEmitter<string[]>();

  constructor(
    private tagService: TagService,
  ) {
  }

  async ngOnInit() {
    if (!this.allTags$)
      this.allTags$ = this.tagService.get().result$.pipe(
        untilDestroyed(this),
        map(tags => tags?.filter(t => !t.defaultTag)),
      );

    if (!this.selectedTags && !this.selectedTagIds) {
      this.selectedTags = [];
      this.selectedTagIds = [];
    } else if (this.selectedTags && !this.selectedTagIds) {
      this.selectedTagIds = this.selectedTags.map(tag => tag.id);
    } else if (this.selectedTagIds && !this.selectedTags && this.selectedTagsChange.observed) {
      this.selectedTags = (await firstValueFrom(this.allTags$))?.filter(tag => this.selectedTagIds!.includes(tag.id));
    }
  }

  selectTag(tags: TagDto[], id: string) {
    if (this.multiple) {
      const existingIndex = this.selectedTagIds!.indexOf(id);
      if (existingIndex === -1)
        this.selectedTagIds!.push(id);
      else
        this.selectedTagIds!.splice(existingIndex, 1);
    } else {
      this.selectedTagIds = [id];
    }
    this.selectedTagIdsChange.emit(this.selectedTagIds);
    this.selectedTagsChange.emit(tags.filter(t => this.selectedTagIds!.includes(t.id)));
  }

  isSelected(id: string) {
    return this.selectedTagIds!.includes(id);
  }
}

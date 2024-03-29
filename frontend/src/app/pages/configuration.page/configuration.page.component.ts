import {Component} from '@angular/core';
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {Observable} from 'rxjs';
import {APP_ROOT, pages} from '../../app-routing.module';
import {TagDto} from '../../dtos/TransactionDtos';
import {TagService} from '../../services/tag.service';

@Component({
             selector: 'app-configuration.page',
             templateUrl: './configuration.page.component.html',
             styleUrls: ['./configuration.page.component.scss'],
           })
export class ConfigurationPageComponent {
  tags$: Observable<TagDto[] | undefined>;

  constructor(
    tagService: TagService,
  ) {
    this.tags$ = tagService.get().result$.pipe(takeUntilDestroyed());
  }

  get helpPage() {
    return `/${APP_ROOT}/${pages.HELP}`;
  }
}

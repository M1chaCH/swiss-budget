import {Component} from '@angular/core';
import {pages} from '../../../app-routing.module';
import {PageStateService} from '../../../services/page-state.service';

@Component({
             selector: 'app-navigation',
             templateUrl: './navigation.component.html',
             styleUrl: './navigation.component.scss',
           })
export class NavigationComponent {
  protected readonly pages = pages;

  constructor(
    public pageService: PageStateService,
  ) {
  }

}

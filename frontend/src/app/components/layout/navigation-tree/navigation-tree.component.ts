import {Component} from '@angular/core';
import {pages} from '../../../app-routing.module';
import {PageStateService} from '../../../services/page-state.service';
import {WindowScrollService} from '../../../services/window-scroll.service';

@Component({
             selector: 'app-navigation-tree',
             templateUrl: './navigation-tree.component.html',
             styleUrl: './navigation-tree.component.scss',
           })
export class NavigationTreeComponent {

  protected readonly pages = pages;

  constructor(
    private scrollService: WindowScrollService,
    private pageService: PageStateService,
  ) {
  }

  onNavLinkClicked() {
    this.scrollService.scrollTo(0, false);
    if (window.innerWidth < 1001)
      this.pageService.setSideMenuOpen(false);
  }
}

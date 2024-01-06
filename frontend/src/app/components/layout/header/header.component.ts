import {Component} from '@angular/core';
import {switchAnimation} from '../../../animations';
import {AuthService} from '../../../services/auth.service';
import {PageStateService} from '../../../services/page-state.service';

@Component({
             selector: 'app-header',
             templateUrl: './header.component.html',
             styleUrl: './header.component.scss',
             animations: [switchAnimation],
           })
export class HeaderComponent {

  constructor(
    private auth: AuthService,
    public pageService: PageStateService,
  ) {
  }

  logout() {
    this.auth.logout();
  }
}

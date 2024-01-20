import {Component, OnInit} from '@angular/core';
import {NavigationStart, Router} from '@angular/router';
import {fadePageTransition} from './animations';
import {PageStateService} from './services/page-state.service';
import {ThemeService} from './services/theme.service';
import {TransactionService} from './services/transaction.service';

@Component({
             selector: 'app-root',
             templateUrl: './app.component.html',
             styleUrls: ['./app.component.scss'],
             animations: [fadePageTransition],
           })
export class AppComponent implements OnInit {
  currentRoute: string = '';

  constructor(
    private router: Router,
    private theme: ThemeService,
    private transactions: TransactionService,
    public pageService: PageStateService,
  ) {
    this.theme.init();
  }

  ngOnInit(): void {
    this.router.events.subscribe(e => {
      if (e instanceof NavigationStart)
        this.currentRoute = e.url;
    });

    this.transactions.importTransactionsWhenLogin();
  }
}

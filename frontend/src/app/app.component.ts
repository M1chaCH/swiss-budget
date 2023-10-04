import {Component, OnInit} from '@angular/core';
import {fadePageTransition, switchAnimation} from "./animations";
import {NavigationStart, Router} from "@angular/router";
import {PageStateService} from "./services/page-state.service";
import {ThemeService} from "./services/theme.service";
import {AuthService} from "./services/auth.service";
import {CurrentPage, CurrentPageService} from "./services/current-page.service";
import {pages} from "./app-routing.module";
import {Observable} from "rxjs";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [fadePageTransition, switchAnimation]
})
export class AppComponent implements OnInit {
  currentRoute: string = ""
  fullscreenPage: boolean = false;
  sideMenuOpen: boolean = window.innerWidth > 1000;
  currentPage$: Observable<CurrentPage>;
  protected readonly pages = pages;

  constructor(
      private router: Router,
      private pageState: PageStateService,
      private theme: ThemeService,
      private auth: AuthService,
      currentPageService: CurrentPageService,
  ) {
    this.theme.init();
    this.currentPage$ = currentPageService.pageChanges();
  }

  ngOnInit(): void {
    this.router.events.subscribe(e => {
      if (e instanceof NavigationStart)
        this.currentRoute = e.url
    });

    this.pageState.subscribe(fullscreen => {
      this.fullscreenPage = fullscreen;
    });
  }

  logout() {
    this.auth.logout();
  }
}

import {Component, OnInit} from '@angular/core';
import {fadePageTransition} from "./animations";
import {NavigationStart, Router} from "@angular/router";
import {PageStateService} from "./services/page-state.service";
import {ThemeService} from "./services/theme.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  animations: [fadePageTransition]
})
export class AppComponent implements OnInit {
  currentRoute: string = ""
  fullscreenPage: boolean = false;
  sideMenuOpen: boolean = true;

  constructor(
    private router: Router,
    private pageState: PageStateService,
    private theme: ThemeService,
  ) {
    this.theme.init();
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
}

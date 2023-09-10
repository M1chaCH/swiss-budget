import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {pages} from "../../app-routing.module";
import {ThemeService} from "../../services/theme.service";

@Component({
  selector: 'app-login.page',
  templateUrl: './login.page.component.html',
  styleUrls: ['./login.page.component.scss']
})
export class LoginPageComponent implements OnInit {
  subrouteActive: boolean = false;
  backgroundImageUrl: string = "url('/assets/welcome-large-background-light.svg')";

  constructor(
    private router: Router,
    private theme: ThemeService,
  ) {
  }

  ngOnInit(): void {
    this.router.events.subscribe(e => {
      if (e instanceof NavigationEnd)
        this.subrouteActive = !window.location.href.endsWith(pages.LOGIN)
    })
    this.subrouteActive = !window.location.href.endsWith(pages.LOGIN)

    if (this.theme.getCurrentAppliedColorTheme() === "dark") {
      this.backgroundImageUrl = "url('/assets/welcome-large-background-dark.svg')";
    }
  }
}

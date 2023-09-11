import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {pages} from "../../../app-routing.module";

@Component({
  selector: 'app-setup-subpage',
  templateUrl: './setup.subpage.component.html',
  styleUrls: ['./setup.subpage.component.scss']
})
export class SetupSubpageComponent {

  constructor(
    private router: Router,
  ) {
  }

  complete() {
    console.warn("not implemented yet")
  }

  navigateToLogin() {
    this.router.navigate([`/${pages.LOGIN}`]).then();
  }
}

import {Directive, Input, OnInit} from '@angular/core';
import {CurrentPageService} from "../services/current-page.service";

@Directive({
  selector: '[appPage]'
})
export class PageDirective implements OnInit {

  @Input() pageTitle!: string;
  @Input() pageRoute!: string;

  constructor(
      private currentPageService: CurrentPageService,
  ) {
  }

  ngOnInit(): void {
    this.currentPageService.registerCurrentPage(this.pageTitle, this.pageRoute);
  }
}

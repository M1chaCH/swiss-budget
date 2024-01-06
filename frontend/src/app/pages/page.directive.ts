import {booleanAttribute, Directive, HostBinding, Input, OnInit} from '@angular/core';
import {PageStateService} from '../services/page-state.service';

@Directive({
             selector: '[appPage]',
           })
export class PageDirective implements OnInit {

  @Input() pageTitle!: string;
  @Input() pageRoute!: string;
  @HostBinding('class')
  pageClass = 'page-container';

  constructor(
    private currentPageService: PageStateService,
  ) {
  }

  private _useFullscreen = false;

  @Input({transform: booleanAttribute})
  set useFullscreen(fullscreen: boolean) {
    this.pageClass = fullscreen ? '' : 'page-container';
    this._useFullscreen = fullscreen;
    this.currentPageService.setFullscreen(fullscreen);
  }

  ngOnInit(): void {
    this.currentPageService.registerCurrentPage(this.pageTitle, this.pageRoute, this._useFullscreen);
  }
}

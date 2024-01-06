import {Injectable} from '@angular/core';
import {auditTime, BehaviorSubject, Observable, shareReplay} from 'rxjs';
import {WindowScrollService} from './window-scroll.service';

@Injectable({
              providedIn: 'root',
            })
export class PageStateService {
  public readonly pageState$: Observable<PageState>;
  private readonly pageState: BehaviorSubject<PageState>;
  private useLargeMenuCache = true;

  constructor(
    private scrollService: WindowScrollService,
  ) {
    const initialPage: PageState = {
      title: 'Home',
      route: 'home',
      fullscreen: false,
      sideMenuOpen: true,
      useLargeMenu: true,
    };

    this.pageState = new BehaviorSubject<PageState>(initialPage);
    this.pageState$ = this.pageState.pipe(auditTime(300), shareReplay(1));
    this.handleResize();
    window.addEventListener('resize', () => this.handleResize());
  }

  setFullscreen(fullscreen: boolean) {
    const page = this.pageState.getValue();
    page.fullscreen = fullscreen;
    this.pageState.next(page);
  }

  setSideMenuOpen(open: boolean) {
    const page = this.pageState.getValue();
    page.sideMenuOpen = open;
    this.pageState.next(page);

    if (page.sideMenuOpen && !this.useLargeMenuCache) {
      this.scrollService.setScrollBlocker(true);
    } else {
      this.scrollService.setScrollBlocker(false);
    }
  }

  setUseLargeMenu(large: boolean) {
    const page = this.pageState.getValue();
    page.useLargeMenu = large;
    this.useLargeMenuCache = large;
    this.pageState.next(page);
  }

  registerCurrentPage(title: string, route: string, fullscreen: boolean = false) {
    const state = this.pageState.getValue();
    this.pageState.next({...state, title, route, fullscreen});
  }

  private handleResize() {
    if (window.innerWidth <= 1000 && this.useLargeMenuCache) {
      this.useLargeMenuCache = false;
      this.setUseLargeMenu(this.useLargeMenuCache);
      this.setSideMenuOpen(false);
    } else if (window.innerWidth > 1000 && !this.useLargeMenuCache) {
      this.useLargeMenuCache = true;
      this.setUseLargeMenu(true);
      this.setSideMenuOpen(true);
    }
  }
}

export type PageState = {
  title: string,
  route: string,
  fullscreen: boolean,
  sideMenuOpen: boolean,
  useLargeMenu: boolean,
}

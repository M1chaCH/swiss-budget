import {Injectable} from '@angular/core';
import {auditTime, BehaviorSubject, Observable} from 'rxjs';

@Injectable({
              providedIn: 'root',
            })
export class WindowScrollService {
  public scrollChange$: Observable<number>;
  private scrollUpdater: BehaviorSubject<number> = new BehaviorSubject<number>(window.scrollY);

  constructor() {
    this.scrollChange$ = this.scrollUpdater.asObservable().pipe(
      auditTime(500),
    );
    window.addEventListener('scroll', this.handleScrollChange);
  }

  scrollTo(y: number, smooth: boolean = true) {
    window.scrollTo({
                      top: y,
                      behavior: smooth ? 'smooth' : 'auto',
                    });
  }

  currentYScroll() {
    return window.scrollY ?? 0;
  }

  setScrollBlocker(active: boolean) {
    const element = document.getElementsByTagName('body')[0];
    if (active)
      element.classList.add('scroll-blocker');
    else
      element.classList.remove('scroll-blocker');
  }

  private readonly handleScrollChange = () => {
    this.scrollUpdater.next(this.currentYScroll());
  };
}

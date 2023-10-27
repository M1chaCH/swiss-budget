import {ElementRef, Injectable} from '@angular/core';
import {auditTime, Observable, ReplaySubject} from "rxjs";

export type ScrollChangedEvent = {
  scroll: number,
  element?: HTMLDivElement
};

@Injectable({
  providedIn: 'root'
})
export class ScrollService {
  public scrollChange$: Observable<ScrollChangedEvent>;
  private currentElement?: HTMLDivElement;
  private scrollUpdater: ReplaySubject<ScrollChangedEvent> = new ReplaySubject<ScrollChangedEvent>();

  constructor() {
    this.scrollChange$ = this.scrollUpdater.asObservable().pipe(
        auditTime(500),
    )
  }

  init(scrollElement: ElementRef<HTMLDivElement>) {
    if (this.currentElement)
      this.currentElement.removeEventListener("scroll", this.handleScrollChange);

    this.currentElement = scrollElement.nativeElement;
    this.currentElement.addEventListener("scroll", this.handleScrollChange);
  }

  private readonly handleScrollChange = () => {
    this.scrollUpdater.next({
      scroll: this.currentElement?.scrollTop ?? 0,
      element: this.currentElement,
    })
  };
}

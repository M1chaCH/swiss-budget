import {Directive, EventEmitter, HostListener, OnInit, Output} from '@angular/core';
import {Subject, throttleTime} from "rxjs";

@Directive({
  selector: '[appClickThrottler]'
})
export class ClickThrottlerDirective implements OnInit {

  @Output() onThrottledClick: EventEmitter<Event> = new EventEmitter<Event>();
  private clickSubject: Subject<Event> = new Subject();

  constructor() {
  }

  ngOnInit(): void {
    this.clickSubject.pipe(throttleTime(3000)).subscribe(e => {
      this.onThrottledClick.emit(e);
    })
  }

  @HostListener("click")
  throttleClick(event: Event) {
    this.clickSubject.next(event);
  }
}

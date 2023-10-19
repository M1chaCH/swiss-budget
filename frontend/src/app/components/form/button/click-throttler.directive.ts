import {Directive, EventEmitter, HostListener, Input, OnInit, Output} from '@angular/core';
import {Subject, throttleTime} from "rxjs";

@Directive({
  selector: '[appClickThrottler]'
})
export class ClickThrottlerDirective implements OnInit {

  @Output() onThrottledClick: EventEmitter<Event> = new EventEmitter<Event>();
  private clickSubject: Subject<Event> = new Subject();
  private throttleTimeMillis: number = 3000;

  constructor() {
  }

  @Input() set appClickThrottler(v: any) {
    this.throttleTimeMillis = v ?? 3000;
  }

  ngOnInit(): void {
    this.clickSubject.pipe(throttleTime(this.throttleTimeMillis)).subscribe(e => {
      this.onThrottledClick.emit(e);
    })
  }

  @HostListener("click")
  throttleClick(event: Event) {
    this.clickSubject.next(event);
  }
}

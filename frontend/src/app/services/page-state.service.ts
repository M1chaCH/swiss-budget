import {Injectable} from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PageStateService {
  private fullscreenStateChange: Subject<boolean> = new Subject<boolean>();

  public requestFullscreen() {
    this.fullscreenStateChange.next(true);
  }

  public removeFullscreen() {
    this.fullscreenStateChange.next(false)
  }

  public subscribe(listener: (value: boolean) => void) {
    this.fullscreenStateChange.subscribe(listener);
  }
}

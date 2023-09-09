import {Directive, OnDestroy} from '@angular/core';
import {PageStateService} from "../services/page-state.service";

@Directive({
  selector: '[appFullscreenPage]'
})
export class FullscreenPageDirective implements OnDestroy {

  constructor(
    private pageState: PageStateService,
  ) {
    pageState.requestFullscreen();
  }

  ngOnDestroy(): void {
    this.pageState.removeFullscreen();
  }
}

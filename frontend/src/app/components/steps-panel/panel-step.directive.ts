import {Directive, TemplateRef, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appPanelStep]'
})
export class PanelStepDirective {

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
  ) {
  }

  private _current: boolean = false;

  get current() {
    return this._current;
  }

  set current(isCurrent: boolean) {
    this._current = isCurrent;
    if (isCurrent)
      this.viewContainer.createEmbeddedView(this.templateRef)
    else
      this.viewContainer.clear();
  }
}

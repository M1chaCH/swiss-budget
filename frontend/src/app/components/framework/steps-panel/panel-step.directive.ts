import {Directive, Input, TemplateRef, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appPanelStep]'
})
export class PanelStepDirective {

  constructor(
      private templateRef: TemplateRef<any>,
      private viewContainer: ViewContainerRef,
  ) {
  }

  // foolish workaround because I can't find a better way to enable the first one
  // only one per panel can be true here & defines the first active one
  @Input() set appPanelStep(first: any) {
    this.current = !!first;
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

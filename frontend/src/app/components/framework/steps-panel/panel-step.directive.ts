import {Directive, TemplateRef, ViewContainerRef} from '@angular/core';

@Directive({
             selector: '[appPanelStep]',
           })
export class PanelStepDirective {

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
  ) {
  }

  public show() {
    this.viewContainer.createEmbeddedView(this.templateRef);
  }

  public hide() {
    this.viewContainer.clear();
  }
}

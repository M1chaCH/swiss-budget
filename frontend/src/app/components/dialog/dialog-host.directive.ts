import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
  selector: '[appDialogHost]'
})
export class DialogHostDirective { // used by to DialogComponent so that it knows where to insert the dialog content

  constructor(
      public viewContainerRef: ViewContainerRef,
  ) {
  }
}

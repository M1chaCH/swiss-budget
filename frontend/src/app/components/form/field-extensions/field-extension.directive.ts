import {Directive, Input, TemplateRef} from '@angular/core';
import {ExtensionSide, FieldExtensionsService} from "./field-extensions.service";
import {BehaviorSubject} from "rxjs";

@Directive({
  selector: '[appFieldExtension]'
})
export class FieldExtensionDirective {

  @Input() appFieldExtensionForNativeId!: string;
  @Input() appFieldExtensionSide: ExtensionSide = "bottom";

  constructor(
      private templateRef: TemplateRef<HTMLElement>,
      private service: FieldExtensionsService,
  ) {
  }

  @Input()
  set appFieldExtension(openSubject: BehaviorSubject<boolean>) {
    openSubject.subscribe(open => {
      const root: HTMLElement = document.getElementById(this.appFieldExtensionForNativeId)!;
      if (open) {
        this.service.openExtension(root, this.templateRef, this.appFieldExtensionSide);
      } else
        this.service.closeExtension();
    });
  }
}

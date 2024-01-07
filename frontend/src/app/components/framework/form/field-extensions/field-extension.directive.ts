import {booleanAttribute, ChangeDetectorRef, Directive, EventEmitter, Input, Output, TemplateRef} from '@angular/core';
import {ExtensionSide, FieldExtensionsService} from './field-extensions.service';

@Directive({
             selector: '[appFieldExtension]',
           })
export class FieldExtensionDirective {

  @Input() fieldExtensionFor!: HTMLElement;
  @Input() fieldExtensionSide: ExtensionSide = 'bottom';
  @Input({transform: booleanAttribute}) fieldExtensionMatchParentWidth: boolean = false;
  @Output() fieldExtensionClosed: EventEmitter<void> = new EventEmitter<void>();

  constructor(
    private templateRef: TemplateRef<HTMLElement>,
    private service: FieldExtensionsService,
    private changeDetector: ChangeDetectorRef,
  ) {
  }

  @Input()
  set appFieldExtension(open: boolean) {
    if (open) {
      this.service.openExtension(this.fieldExtensionFor,
                                 this.templateRef,
                                 this.fieldExtensionSide,
                                 this.fieldExtensionMatchParentWidth,
                                 this.fieldExtensionClosed,
      );
    } else
      this.service.closeExtension();
    this.changeDetector.detectChanges();
  }
}

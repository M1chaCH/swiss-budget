import {AfterViewInit, booleanAttribute, Component, ContentChildren, ElementRef, EventEmitter, Input, Output, QueryList, ViewChild} from '@angular/core';
import {stepSliderAnimation} from '../../../animations';
import {PanelStepDirective} from './panel-step.directive';

@Component({
             selector: 'app-steps-panel',
             templateUrl: './steps-panel.component.html',
             styleUrls: ['./steps-panel.component.scss'],
             animations: [stepSliderAnimation],
           })
export class StepsPanelComponent implements AfterViewInit {

  @Input() nextLabel: string = 'Next';
  @Input() previousLabel: string = 'Back';
  @Input() firstPreviousLabel: string | undefined;
  @Input() lastNextLabel: string | undefined;
  @Input({transform: booleanAttribute}) firstPreviousClickable: boolean = false;
  @Input({transform: booleanAttribute}) lastNextClickable: boolean = false;
  @Input() navigationPlace: 'above' | 'bellow' = 'bellow';
  @Input({transform: booleanAttribute}) showNavigation: boolean = true;

  @Output() firstPreviousClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() lastNextClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() cursorChange: EventEmitter<number> = new EventEmitter<number>();

  @ViewChild('stepContainer', {read: ElementRef<HTMLDivElement>, static: true}) stepContainer: ElementRef<HTMLDivElement> | undefined;
  @ContentChildren(PanelStepDirective) steps?: QueryList<PanelStepDirective>;
  private currentStep: PanelStepDirective | undefined;

  constructor() {
  }

  private _cursor: number = 0;

  get cursor() {
    return this._cursor;
  }

  @Input()
  set cursor(n: number) {
    const length = this.steps?.length ?? 0;
    const maxN = length > 0 ? length - 1 : 0;
    this._cursor = Math.max(0, Math.min(n, maxN));
    this.cursorChange.emit(this._cursor);
    this.updateChildren();
  }

  ngAfterViewInit(): void {
    this.updateChildren();
  }

  next() {
    if (this.isLast() && this.lastNextClickable) {
      this.lastNextClicked.emit();
    } else {
      this.cursor++;
      this.updateChildren();
    }
  }

  previous() {
    if (this.isFirst() && this.firstPreviousClickable) {
      this.firstPreviousClicked.emit();
    } else {
      this.cursor--;
      this.updateChildren();
    }
  }

  isFirst(): boolean {
    return this.cursor === 0;
  }

  isLast(): boolean {
    return (this.steps?.length ?? 0) - 1 === this.cursor;
  }

  private updateChildren() {
    this.stepContainer?.nativeElement.scrollTo({
                                                 top: 0,
                                                 behavior: 'smooth',
                                               });
    this.currentStep?.hide();
    this.currentStep = this.steps?.get(this.cursor);
    this.currentStep?.show();
  }
}

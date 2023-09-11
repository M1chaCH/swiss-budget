import {
  AfterViewInit,
  Component,
  ContentChildren,
  EventEmitter,
  Input,
  Output,
  QueryList
} from '@angular/core';
import {PanelStepDirective} from "./panel-step.directive";
import {stepSliderAnimation} from "../../animations";
import {Subject} from "rxjs";

@Component({
  selector: 'app-steps-panel',
  templateUrl: './steps-panel.component.html',
  styleUrls: ['./steps-panel.component.scss'],
  animations: [stepSliderAnimation]
})
export class StepsPanelComponent implements AfterViewInit {

  @Input() nextLabel: string = "Next";
  @Input() previousLabel: string = "Back";
  @Input() firstPreviousLabel: string | undefined;
  @Input() lastNextLabel: string | undefined;
  @Input() firstPreviousClickable: boolean = false;
  @Input() lastNextClickable: boolean = false;
  @Input() moveCursor: Subject<number> | undefined;
  @Input() navigationPlace: "above" | "bellow" = "bellow";

  @Output() firstPreviousClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() lastNextClicked: EventEmitter<void> = new EventEmitter<void>();
  @Output() stepChanged: EventEmitter<number> = new EventEmitter<number>()

  @ContentChildren(PanelStepDirective) steps?: QueryList<PanelStepDirective>;

  cursor: number = 0;

  constructor() {
  }

  ngAfterViewInit() {
    this.moveCursor?.subscribe(n => {
      const length = this.steps?.length ?? 0;
      const maxN = length > 0 ? length - 1 : 0;
      this.cursor = Math.min(0, Math.max(n, maxN));
      this.updateChildren();
    })
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
      this.updateChildren()
    }
  }

  isFirst(): boolean {
    return this.cursor === 0;
  }

  isLast(): boolean {
    return (this.steps?.length ?? 0) - 1 === this.cursor;
  }


  private updateChildren() {
    this.stepChanged.emit(this.cursor)
    this.steps?.forEach((step, i) => {
      step.current = i === this.cursor
    })
  }
}

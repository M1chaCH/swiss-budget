import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {openCloseAnimation} from "../../animations";
import {Expandable, ExpansionService} from "./expansion.service";

@Component({
  selector: 'app-expansion-panel',
  templateUrl: './expansion-panel.component.html',
  styleUrls: ['./expansion-panel.component.scss'],
  animations: [openCloseAnimation],
})
export class ExpansionPanelComponent implements Expandable, OnInit, OnDestroy {
  @Input() topic?: string;
  @Output() openChange: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(
      private expansionService: ExpansionService,
  ) {
  }

  private _open: boolean = false;

  get open() {
    return this._open;
  }

  @Input() set open(v: boolean) {
    this._open = v;
    this.openChange.emit(v);
  }

  ngOnInit() {
    if (this.topic)
      this.expansionService.registerExpandable(this.topic, this);
  }

  ngOnDestroy() {
    if (this.topic)
      this.expansionService.removeExpandable(this.topic, this);
  }

  toggleOpen() {
    this.open = !this.open;
  }

  changeState(open: boolean): void {
    this.open = open;
  }
}

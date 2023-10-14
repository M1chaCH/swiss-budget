import {Component, Input, OnInit} from '@angular/core';
import {TransactionDto} from "../../../dtos/TransactionDtos";
import {ColorService} from "../../../services/color.service";

@Component({
  selector: 'app-transaction',
  templateUrl: './transaction.component.html',
  styleUrls: ['./transaction.component.scss']
})
export class TransactionComponent implements OnInit {
  @Input() transaction!: TransactionDto;
  useBlackIcon: boolean = false;

  constructor(
      private colors: ColorService,
  ) {
  }

  ngOnInit(): void {
    this.useBlackIcon = this.colors.hasDarkBetterContrast(this.transaction.tag.color);
  }
}

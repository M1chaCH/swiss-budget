import {Component} from '@angular/core';
import {TagService} from '../../services/tag.service';
import {TransactionService} from '../../services/transaction.service';

@Component({
             selector: 'app-home.page',
             templateUrl: './home.page.component.html',
             styleUrls: ['./home.page.component.scss'],
           })
export class HomePageComponent {

  constructor(
    private tags: TagService,
    private transactions: TransactionService,
  ) {
  }

  reset() {
    this.tags.invalidate();
  }

  resetTransaction() {
    this.transactions.invalidate();
  }
}

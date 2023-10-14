import {Component, OnInit} from '@angular/core';
import {TransactionService} from "../../../services/transaction.service";

@Component({
  selector: 'app-transaction-importer',
  templateUrl: './transaction-importer.component.html',
  styleUrls: ['./transaction-importer.component.scss']
})
export class TransactionImporterComponent implements OnInit {
  state: "started" | "success" | "error" | undefined;

  constructor(
      private transactionService: TransactionService,
  ) {
  }

  ngOnInit(): void {
    this.state = "started";
    this.transactionService.importTransactions().then(() => this.activateSuccessState());
  }

  private activateSuccessState() {
    this.state = "success";
    setTimeout(() => this.state = undefined, 2500); // hide again
  }
}

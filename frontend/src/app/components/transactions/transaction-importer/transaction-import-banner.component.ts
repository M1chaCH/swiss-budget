import {Component} from '@angular/core';
import {AppBannerComponent, BannerService} from '../../framework/banner/banner.service';

@Component({
             selector: 'app-transaction-import-banner',
             templateUrl: './transaction-import-banner.component.html',
             styleUrls: ['./transaction-import-banner.component.scss'],
           })
export class TransactionImportBannerComponent implements AppBannerComponent<undefined> {
  data: undefined;
  state: 'started' | 'success' | 'error' = 'started';

  constructor(
    public banner: BannerService,
  ) {
  }

  activateSuccessState() {
    this.state = 'success';
    setTimeout(() => this.banner.closeCurrentBanner(), 2500); // hide again
  }

  activateErrorState() {
    this.state = 'error';
  }
}

import {Component, Type, ViewChild, ViewContainerRef} from '@angular/core';
import {AppBannerComponent, BannerService} from '../banner.service';

@Component({
             selector: 'app-banner-outlet',
             templateUrl: './banner-outlet.component.html',
             styleUrl: './banner-outlet.component.scss',
           })
export class BannerOutletComponent {

  @ViewChild('bannerHost', {static: true, read: ViewContainerRef}) hostContainer!: ViewContainerRef;

  constructor(
    service: BannerService,
  ) {
    service.setOutlet(this);
  }

  renderBanner<Data>(banner: Type<AppBannerComponent<Data>>): AppBannerComponent<Data> {
    const componentRef = this.hostContainer.createComponent<AppBannerComponent<Data>>(banner);
    return componentRef.instance;
  }

  clearBanner() {
    this.hostContainer.clear();
  }
}

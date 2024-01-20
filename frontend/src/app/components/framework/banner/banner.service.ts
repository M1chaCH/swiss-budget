import {Injectable, Type} from '@angular/core';
import {BannerOutletComponent} from './banner-outlet/banner-outlet.component';

export interface AppBannerComponent<Data> {
  data: Data,
  onClose?: () => void,
}

@Injectable({
              providedIn: 'root',
            })
export class BannerService {

  private outlet!: BannerOutletComponent;
  private openBanner: AppBannerComponent<any> | undefined;

  constructor() {
  }

  public showBanner<Data, Component extends AppBannerComponent<Data>>(dialog: Type<Component>, data?: Data): Component {
    this.closeCurrentBanner();
    this.openBanner = this.outlet.renderBanner(dialog);
    this.openBanner.data = data;
    return this.openBanner as Component;
  }

  public closeCurrentBanner() {
    this.openBanner?.onClose?.call(undefined);
    this.outlet.clearBanner();
  }

  public setOutlet(cmp: BannerOutletComponent) {
    this.outlet = cmp;
  }
}

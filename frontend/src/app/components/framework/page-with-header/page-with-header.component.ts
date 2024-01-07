import {AfterViewInit, Component, ElementRef, ViewChild} from '@angular/core';
import {WindowScrollService} from '../../../services/window-scroll.service';

@Component({
             selector: 'app-page-with-header',
             templateUrl: './page-with-header.component.html',
             styleUrls: ['./page-with-header.component.scss'],
           })
export class PageWithHeaderComponent implements AfterViewInit {
  @ViewChild('pageHead', {read: ElementRef, static: false}) header?: ElementRef<HTMLElement>;
  @ViewChild('pageContent', {read: ElementRef, static: false}) content?: ElementRef<HTMLElement>;

  constructor(
    private scrollService: WindowScrollService,
  ) {
  }

  ngAfterViewInit() {
    if (this.header)
      // plus 10px gap between header and body plus 12px padding of .page-container
      this.scrollService.scrollTo(this.header.nativeElement.getBoundingClientRect().height + 10 + 12);
  }
}

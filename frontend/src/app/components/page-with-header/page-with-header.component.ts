import {AfterViewInit, Component, ElementRef, HostListener, ViewChild} from '@angular/core';
import {ScrollService} from "../../services/scroll.service";

@Component({
  selector: 'app-page-with-header',
  templateUrl: './page-with-header.component.html',
  styleUrls: ['./page-with-header.component.scss']
})
export class PageWithHeaderComponent implements AfterViewInit {
  @ViewChild("pageHead", {read: ElementRef, static: false}) header?: ElementRef<HTMLElement>;
  @ViewChild("pageContent", {read: ElementRef, static: false}) content?: ElementRef<HTMLElement>;
  private marginTop: number = 0;

  constructor(
      private scrollService: ScrollService,
  ) {
  }

  ngAfterViewInit() {
    this.onResize();
  }

  @HostListener('window:resize')
  onResize() {
    if (this.header) {
      this.marginTop = this.header.nativeElement.getBoundingClientRect().height + 20;
      this.content!.nativeElement.style.marginTop = this.marginTop + "px";
      if (this.scrollService.currentYScroll() < this.marginTop) {
        // often does not work on initial page load because content needs to be loaded from the api and is not yet here.
        this.scrollService.scrollTo(this.marginTop, false);
      }
    }
  }
}

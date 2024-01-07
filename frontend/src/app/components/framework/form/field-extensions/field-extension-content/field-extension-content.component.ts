import {AfterViewInit, booleanAttribute, ChangeDetectionStrategy, ChangeDetectorRef, Component, ElementRef, Input, ViewChild} from '@angular/core';
import {ExtensionSide} from '../field-extensions.service';

export type ContentPosition = {
  top: string, right: string, bottom: string, left: string
}

@Component({
             selector: 'app-field-extension-content',
             templateUrl: './field-extension-content.component.html',
             styleUrls: ['./field-extension-content.component.scss'],
             changeDetection: ChangeDetectionStrategy.OnPush,
           })
export class FieldExtensionContentComponent implements AfterViewInit {
  @ViewChild('content', {read: ElementRef}) content!: ElementRef;
  @Input() root!: HTMLElement;
  @Input() side: ExtensionSide = 'bottom';
  @Input({transform: booleanAttribute}) useParentWidth: boolean = false;
  position: ContentPosition | undefined;

  constructor(
    private changeDetector: ChangeDetectorRef,
  ) {
  }

  ngAfterViewInit(): void {
    // keep in mind:
    // rect bottom & right are pixels from the top left to the bottom or right of the element
    // css bottom & right are pixels from the bottom to the bottom of the element...🤌
    // (this.position is used in css)
    const contentElementRect = this.content.nativeElement.getBoundingClientRect();
    const rootElementRect = this.root.getBoundingClientRect();
    switch (this.side) {
      case 'bottom':
        this.position = this.doesElementFitBottom(rootElementRect, contentElementRect);
        if (!this.position)
          this.position = this.doesElementFitTop(rootElementRect, contentElementRect);
        break;
      case 'top':
        this.position = this.doesElementFitTop(rootElementRect, contentElementRect);
        if (!this.position)
          this.position = this.doesElementFitBottom(rootElementRect, contentElementRect);
        break;
      case 'right':
        this.position = this.doesElementFitRight(rootElementRect, contentElementRect);
        if (!this.position)
          this.position = this.doesElementFitLeft(rootElementRect, contentElementRect);
        break;
      case 'left':
        this.position = this.doesElementFitLeft(rootElementRect, contentElementRect);
        if (!this.position)
          this.position = this.doesElementFitRight(rootElementRect, contentElementRect);
        break;
    }

    if (!this.position) {
      const verticalCenter = (window.innerHeight / 2) - (contentElementRect.height / 2);
      const horizontalCenter = (window.innerWidth / 2) - (contentElementRect.width / 2);
      this.position = {
        top: `${verticalCenter}px`,
        right: this.useParentWidth ? `${horizontalCenter + rootElementRect.width}px` : 'unset',
        bottom: 'unset',
        left: `${horizontalCenter}px`,
      };
    }

    this.changeDetector.detectChanges();
  }

  private doesElementFitBottom(root: DOMRect, content: DOMRect): ContentPosition | undefined {
    const spaceBellow = window.innerHeight - root.bottom;
    if (spaceBellow < content.height)
      return undefined;

    return {
      top: `${root.bottom}px`,
      right: this.useParentWidth ? `${window.innerWidth - root.right}px` : 'unset',
      bottom: 'unset',
      left: `${root.x}px`,
    };
  }

  private doesElementFitTop(root: DOMRect, content: DOMRect): ContentPosition | undefined {
    if (root.top < content.height)
      return undefined;

    return {
      top: `${root.top - content.height}px`,
      right: this.useParentWidth ? `${window.innerWidth - root.right}px` : 'unset',
      bottom: 'unset',
      left: `${root.left}px`,
    };
  }

  private doesElementFitRight(root: DOMRect, content: DOMRect): ContentPosition | undefined {
    const spaceRight = window.innerWidth - root.right;
    if ((this.useParentWidth && spaceRight < root.width) || spaceRight < content.width)
      return undefined;

    return {
      top: `${root.top}px`,
      left: `${root.right}px`,
      bottom: 'unset',
      right: this.useParentWidth ? `${window.innerWidth - root.right - root.width}px` : 'unset',
    };
  }

  private doesElementFitLeft(root: DOMRect, content: DOMRect): ContentPosition | undefined {
    if ((this.useParentWidth && root.left < root.width) || root.left < content.width)
      return undefined;

    return {
      top: `${root.top}px`,
      right: this.useParentWidth ? `${window.innerWidth - root.right + root.width}px` : 'unset',
      bottom: 'unset',
      left: this.useParentWidth ? `${root.left - root.width}px` : `${root.left - content.width}px`,
    };
  }
}

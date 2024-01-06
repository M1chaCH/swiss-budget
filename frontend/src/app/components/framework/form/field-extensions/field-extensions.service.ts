import {Injectable, TemplateRef} from '@angular/core';
import {ReplaySubject} from 'rxjs';

export type ExtensionSide = 'top' | 'bottom' | 'right' | 'left';

export type FieldExtension = {
  root: HTMLElement,
  content: TemplateRef<HTMLElement>,
  side: ExtensionSide,
}

@Injectable({
              providedIn: 'root',
            })
export class FieldExtensionsService {
  currentExtension: FieldExtension | undefined;

  private _open: ReplaySubject<boolean> = new ReplaySubject<boolean>();

  get open() {
    return this._open.asObservable();
  }

  public openExtension(root: HTMLElement, content: TemplateRef<HTMLElement>, side: ExtensionSide) {
    if (this._open)
      this.closeExtension();

    this.currentExtension = {
      root: root,
      content: content,
      side: side,
    };
    this._open.next(true);
  }

  public closeExtension() {
    this._open.next(false);
    this.currentExtension = undefined;
  }
}

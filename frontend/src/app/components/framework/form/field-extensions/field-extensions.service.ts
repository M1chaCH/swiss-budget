import {EventEmitter, Injectable, TemplateRef} from '@angular/core';
import {ReplaySubject} from 'rxjs';

export type ExtensionSide = 'top' | 'bottom' | 'right' | 'left';

export type FieldExtension = {
  root: HTMLElement,
  content: TemplateRef<HTMLElement>,
  side: ExtensionSide,
  useParentWidth: boolean,
  notifyClosed: EventEmitter<void>,
}

@Injectable({
              providedIn: 'root',
            })
export class FieldExtensionsService {
  currentExtension: FieldExtension | undefined;

  constructor() {
  }

  private _open: ReplaySubject<boolean> = new ReplaySubject<boolean>();

  get open() {
    return this._open.asObservable();
  }

  public openExtension(root: HTMLElement,
                       content: TemplateRef<HTMLElement>,
                       side: ExtensionSide,
                       useParentWidth: boolean,
                       notifyClosed: EventEmitter<void>,
  ) {
    if (this._open)
      this.closeExtension();

    this.currentExtension = {root, content, side, useParentWidth, notifyClosed};
    this._open.next(true);
  }

  public closeExtension() {
    this._open.next(false);
    this.currentExtension?.notifyClosed.emit();
    this.currentExtension = undefined;
  }
}

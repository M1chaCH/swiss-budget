import {Injectable, TemplateRef} from '@angular/core';
import {Subject} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class DialogService {

  private _currentContent: Subject<TemplateRef<any> | undefined> = new Subject<TemplateRef<any> | undefined>();

  get currentContent() {
    return this._currentContent.asObservable();
  }

  private _open: Subject<boolean> = new Subject();

  get open() {
    return this._open.asObservable();
  }

  public openDialog(content: TemplateRef<any>) {
    this._currentContent.next(content);
    this._open.next(true)
  }

  public closeDialog() {
    this._open.next(false);
    this._currentContent.next(undefined);
  }
}

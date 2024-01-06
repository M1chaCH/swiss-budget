import {Component} from '@angular/core';
import {catchError, map, Observable, of} from 'rxjs';
import {FieldExtension, FieldExtensionsService} from './field-extensions.service';


@Component({
             selector: 'app-field-extensions',
             templateUrl: './field-extensions.component.html',
             styleUrls: ['./field-extensions.component.scss'],
           })
export class FieldExtensionsComponent {

  open: Observable<boolean>;
  extension: FieldExtension | undefined;

  constructor(
    private service: FieldExtensionsService,
  ) {
    this.open = service.open.pipe(
      map(value => {
        if (value)
          this.openExtension(service.currentExtension);
        return value;
      }),
      catchError(e => {
        console.error(e);
        return of(false);
      }),
    );
  }

  closeExtension(event: any) {
    const hitExtension = event ? event.target.id === 'extension-container' : true;
    if (hitExtension)
      this.service.closeExtension();
  }

  private openExtension(extension?: FieldExtension) {
    if (!extension)
      throw 'extension not defined';
    this.extension = extension;
  }
}

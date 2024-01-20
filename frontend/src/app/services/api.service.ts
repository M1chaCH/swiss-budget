import {HttpClient, HttpErrorResponse, HttpEvent} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {catchError, NEVER, Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {ErrorService} from './error.service';

export const endpoint = {
  REGISTER: '/register',
  CHECK_MAIL: '/register/mail',
  CREATE_MAIL_FOLDER: '/register/mail/folder',
  CONTACT: '/contact',
  AUTH: '/auth',
  MFA: '/auth/mfa',
  SUPPORTED_BANK: '/register/bank',
  TRANSACTIONS: '/transaction',
  IMPORT_TRANSACTIONS: '/transaction/import',
  TAG: '/tag',
  VALIDATE_NO_KEYWORD: '/tag/validate_no_keyword',
  ASSIGN_TAG: '/tag/assign_tag',
  RESOLVE_TAG_CONFLICT: '/tag/resolve_conflict',
  CHANGE_TAG: '/tag/change_tag',
};


@Injectable({
              providedIn: 'root',
            })
export class ApiService {
  public static readonly API_DATE_FORMAT = 'yyyy-MM-DD';

  constructor(
    private http: HttpClient,
    private errorService: ErrorService,
  ) {
  }

  public get<T>(endpoint: string, queryParams?: object, showDialogOnError: boolean = false, httpOptions?: any): Observable<T> {
    let url: string = `${environment.API_URL}${endpoint}`;

    if (queryParams)
      url += this.parseQueryParams(queryParams);

    this.logRequest('GET', url);

    return this.http.get<T>(url, httpOptions).pipe(
      catchError((err) => this.handleError<T>(err, showDialogOnError)),
    ) as Observable<T>;
  }

  public getRaw<T>(url: string, options?: any): Observable<HttpEvent<T>> {
    let fullUrl: string = `${environment.API_URL}${url}`;
    this.logRequest('RAW-GET', fullUrl);
    return this.http.get<T>(fullUrl, options);
  }

  public post<T>(endpoint: string,
                 payload: any,
                 queryParams?: ApiQueryParams,
                 showDialogOnError: boolean = false,
                 httpOptions?: any,
  ): Observable<T> {
    let url: string = `${environment.API_URL}${endpoint}`;

    if (queryParams)
      url += this.parseQueryParams(queryParams);

    this.logRequest('POST', url, payload);

    return this.http.post<T>(url, payload, httpOptions).pipe(
      catchError((err) => this.handleError<T>(err, showDialogOnError)),
    ) as Observable<T>;
  }

  public put<T>(endpoint: string,
                payload: any,
                queryParams?: ApiQueryParams,
                showDialogOnError: boolean = false,
                httpOptions?: any,
  ): Observable<T> {
    let url: string = `${environment.API_URL}${endpoint}`;

    if (queryParams)
      url += this.parseQueryParams(queryParams);

    this.logRequest('PUT', url, payload);

    return this.http.put<T>(url, payload, httpOptions).pipe(
      catchError((err) => this.handleError<T>(err, showDialogOnError)),
    ) as Observable<T>;
  }

  public delete<T>(endpoint: string, showDialogOnError: boolean = false, httpOptions?: any): Observable<T> {
    let url: string = `${environment.API_URL}${endpoint}`;

    this.logRequest('DELETE', url);

    return this.http.delete<T>(url, httpOptions).pipe(
      catchError((err) => this.handleError<T>(err, showDialogOnError)),
    ) as Observable<T>;
  }

  private handleError<T>(err: HttpErrorResponse, showDialogOnError: boolean): Observable<T> {
    if (environment.IS_DEV)
      console.error(err);

    if (!this.errorService.handleIfGlobalError(err.error)) {
      if (showDialogOnError) this.errorService.showErrorDialog(err);
      else throw err;
    }
    return NEVER;
  }

  private parseQueryParams(params: object): string {
    let url: string = '?';
    const keys = Object.keys(params);
    const values = Object.values(params);
    for (let i = 0; i < keys.length; i++) {
      if (i > 0 && i <= keys.length - 1)
        url += '&';
      url += `${keys[i]}=${values[i]}`;
    }
    return url;
  }

  private logRequest(method: string, url: string, payload?: any): void {
    if (environment.IS_DEV)
      console.log(`${method} -> ${url}`, payload ?? '');
  }
}

export type ApiQueryParams = Map<string, string>;

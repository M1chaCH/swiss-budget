import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {catchError, NEVER, Observable} from "rxjs";
import {environment} from "../../environments/environment";
import {ErrorService} from "./error.service";

export const endpoint = {
  REGISTER: "/register",
  CHECK_MAIL: "/register/mail",
  CONTACT: "/contact",
}


@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(
      private http: HttpClient,
      private errorService: ErrorService,
  ) {
  }

  public get<T>(endpoint: string, queryParams?: { key: string, value: string }[], showDialogOnError: boolean = false): Observable<T> {
    let url: string = `${environment.API_URL}${endpoint}`;

    if (queryParams)
      url += this.parseQueryParams(queryParams);

    this.logRequest("GET", url);

    const request = this.http.get(url);
    return ((request as any) as Observable<T>).pipe(
        catchError((err, caught) => this.handleError(err, caught, showDialogOnError)),
    );
  }

  public post<T>(endpoint: string, payload: any, queryParams?: { key: string, value: string }[], showDialogOnError: boolean = false) {
    let url: string = `${environment.API_URL}${endpoint}`;

    if (queryParams)
      url += this.parseQueryParams(queryParams);

    this.logRequest("POST", url);

    const request = this.http.post(url, payload);
    return ((request as any) as Observable<T>).pipe(
        catchError((err, caught) => this.handleError(err, caught, showDialogOnError)),
    );
  }

  public put<T>(endpoint: string, payload: any, queryParams?: { key: string, value: string }[], showDialogOnError: boolean = false) {
    let url: string = `${environment.API_URL}${endpoint}`;

    if (queryParams)
      url += this.parseQueryParams(queryParams);

    this.logRequest("PUT", url);

    const request = this.http.put(url, payload);
    return ((request as any) as Observable<T>).pipe(
        catchError((err, caught) => this.handleError(err, caught, showDialogOnError)),
    );
  }

  public delete<T>(endpoint: string, showDialogOnError: boolean = false): Observable<T> {
    let url: string = `${environment.API_URL}${endpoint}`;

    this.logRequest("DELETE", url);

    const request = this.http.delete(url);
    return ((request as any) as Observable<T>).pipe(
        catchError((err, caught) => this.handleError(err, caught, showDialogOnError)),
    );
  }

  private handleError<T>(err: any, _: Observable<T>, showDialogOnError: boolean): Observable<T> {
    if (!this.errorService.handleAuthenticationError(err)) {
      if (showDialogOnError) this.errorService.showErrorDialog(err);
      else throw err;
    }
    return NEVER;
  }

  private parseQueryParams(params: { key: string, value: string }[]): string {
    let url: string = "?";
    for (let i = 0; i < params.length; i++) {
      if (i > 0 && i < params.length - 1)
        url += "&";
      const current: { key: string, value: string } = params[i];
      url += `${current.key}=${current.value}`;
    }
    return url;
  }

  private logRequest(method: string, url: string, payload?: any): void {
    if (environment.IS_DEV)
      console.log(`${method} -> ${url}`, payload ?? "");
  }
}
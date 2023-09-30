import {inject, Injectable, OnInit} from '@angular/core';
import {CanActivateFn, Router, UrlTree} from "@angular/router";
import {BehaviorSubject, catchError, concatMap, Observable, of, tap} from "rxjs";
import {pages} from "../app-routing.module";
import {ApiService, endpoint} from "./api.service";
import {CookieService} from "ngx-cookie-service";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {ErrorService} from "./error.service";
import {MessageDto} from "../dtos/MessageDto";
import {ErrorDto} from "../dtos/ErrorDto";
import {environment} from "../../environments/environment";

@Injectable({
  providedIn: 'root',
})
export class AuthService implements HttpInterceptor, OnInit {
  static readonly AUTH_TOKEN = "Auth-Token";
  static readonly MFA_PROCESS_ID = "mfa";
  static readonly USER_ID = "user";
  private authToken: string | undefined;
  private loggedIn: boolean | undefined;

  constructor(
      private api: ApiService,
      private cookie: CookieService,
      private router: Router,
  ) {
  }

  ngOnInit() {
    this.authToken = this.cookie.get(AuthService.AUTH_TOKEN);
  }

  login(mail: string, password: string, stay: boolean = true): Observable<string> {
    this.loggedIn = false;
    this.authToken = undefined;
    return this.api.post<MessageDto | ErrorDto>(endpoint.AUTH, {
      credentials: {
        mail, password
      }, stay
    }).pipe(
        tap(response => {
          if ((response as MessageDto).message) {
            this.storeToken(response as MessageDto);
            this.router.navigate([pages.HOME]).then()
          } else {
            const newDeviceError: ErrorDto = response as ErrorDto;
            if (newDeviceError.errorKey === "AgentNotRegisteredException") {
              const processId = newDeviceError.args.processId;
              const userId = newDeviceError.args.userId;
              localStorage.setItem(AuthService.MFA_PROCESS_ID, processId);
              localStorage.setItem(AuthService.USER_ID, userId)
              this.router.navigate([pages.LOGIN, pages.login.MFA]).then()
            }
          }
        }),
        concatMap(_ => of("")),
        catchError(err => of(ErrorService.parseErrorMessage(err.error))),
    );
  }

  isLoggedIn(): Observable<boolean> {
    const loginSubject: BehaviorSubject<boolean> = new BehaviorSubject(true);
    if (this.loggedIn) {
      loginSubject.next(true);
      return loginSubject;
    }

    this.authToken ??= this.cookie.get(AuthService.AUTH_TOKEN);
    if (!!this.authToken) {
      this.validateToken().subscribe(() => {
        loginSubject.next(true);
      })
    } else {
      loginSubject.next(false);
    }

    return loginSubject;
  }

  validateToken(): Observable<void> {
    return this.api.get(endpoint.AUTH);
  }

  validateMfaToken(processId: string, userId: string, code: number): Observable<boolean> {
    return this.api.post<MessageDto>(endpoint.MFA, {
      processId, userId, code
    }).pipe(
        tap(dto => {
          this.storeToken(dto);
          localStorage.removeItem(AuthService.MFA_PROCESS_ID);
          this.router.navigate([pages.HOME]).then()
        }),
        concatMap(_ => of(true)),
        catchError(_ => of(false))
    );
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    this.authToken ??= this.cookie.get(AuthService.AUTH_TOKEN);
    const requestWithHeaders = req.clone({
      setHeaders: {[AuthService.AUTH_TOKEN]: this.authToken},
    });
    return next.handle(requestWithHeaders);
  }

  private storeToken(tokenMessage: MessageDto) {
    const token = tokenMessage.message;
    this.authToken = token;
    this.loggedIn = true;
    this.cookie.set(AuthService.AUTH_TOKEN, token, new Date().setDate(new Date().getDate() + 300), "/", environment.DOMAIN, true, "Strict")
  }
}

export const authenticationGuard: CanActivateFn = (): Observable<UrlTree | boolean> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return new Observable<boolean | UrlTree>(sub => {
    authService.isLoggedIn().subscribe(loggedIn => {
      if (loggedIn)
        sub.next(true);
      else
        sub.next(router.parseUrl(pages.LOGIN))
    });
  });
}

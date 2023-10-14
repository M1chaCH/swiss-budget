import {inject, Injectable, OnInit} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {BehaviorSubject, catchError, concatMap, Observable, of, tap} from "rxjs";
import {pages} from "../app-routing.module";
import {ApiService, endpoint} from "./api.service";
import {CookieService} from "ngx-cookie-service";
import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {ErrorService} from "./error.service";
import {MessageDto} from "../dtos/MessageDto";
import {ErrorDto} from "../dtos/ErrorDto";
import {environment} from "../../environments/environment";
import * as moment from 'moment';

export type LoginState = "in" | "out" | "loading";

@Injectable({
  providedIn: 'root',
})
export class AuthService implements OnInit {
  static readonly AUTH_TOKEN = "Auth-Token";
  static readonly MFA_PROCESS_ID = "mfa";
  static readonly USER_ID = "user";

  private loginSubject: BehaviorSubject<LoginState> = new BehaviorSubject<LoginState>("loading");
  public isLoggedIn$: Observable<LoginState> = this.loginSubject.asObservable();
  private authToken: string | undefined;

  constructor(
      private api: ApiService,
      private cookie: CookieService,
      private router: Router,
  ) {
    this.authToken = this.cookie.get(AuthService.AUTH_TOKEN);
    this.loadLoginState();
  }

  ngOnInit() {
  }

  login(mail: string, password: string, stay: boolean = true): Observable<string> {
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
        catchError(err => {
          this.loginSubject.next("out");
          return of(ErrorService.parseErrorMessage(err.error));
        }),
    );
  }

  logout() {
    this.loginSubject.next("out");
    this.authToken = undefined;
    this.deleteToken();
    location.reload();
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

  private loadLoginState(): void {
    if (!this.authToken)
      this.authToken = this.cookie.get(AuthService.AUTH_TOKEN);
    if (!!this.authToken) {
      this.api.getRaw(endpoint.AUTH).subscribe({
        next: _ => this.loginSubject.next("in"),
        error: _ => {
          this.deleteToken();
          this.loginSubject.next("out");
        }
      });
    } else {
      this.loginSubject.next("out");
    }
  }

  private storeToken(tokenMessage: MessageDto) {
    const token = tokenMessage.message;
    this.authToken = token;
    this.loginSubject.next("in");
    let expires = moment(new Date()).add(300, "days");
    this.cookie.set(AuthService.AUTH_TOKEN, token, expires.toDate(), "/", environment.DOMAIN, true, "Strict")
  }

  private deleteToken() {
    this.cookie.delete(AuthService.AUTH_TOKEN, "/", environment.DOMAIN, true, "Strict");
  }
}

@Injectable({
  providedIn: 'root',
})
export class AuthTokenInterceptor implements HttpInterceptor {
  private authToken: string | undefined;

  constructor(
      private cookie: CookieService,
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    if (!this.authToken) {
      this.authToken = this.cookie.get(AuthService.AUTH_TOKEN);
    }
    const requestWithHeaders = req.clone({
      setHeaders: {[AuthService.AUTH_TOKEN]: this.authToken},
    });
    return next.handle(requestWithHeaders);
  }
}

export const authenticationGuard: CanActivateFn = (_route: ActivatedRouteSnapshot, _state: RouterStateSnapshot): Observable<UrlTree | boolean> => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return new Observable(obs => {
    authService.isLoggedIn$.subscribe(loggedIn => {
      if (loggedIn === "loading")
        return;

      if (loggedIn === "in")
        obs.next(true);
      else
        obs.next(router.parseUrl(pages.LOGIN));
    });
  });
}

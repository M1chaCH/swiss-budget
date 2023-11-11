import {inject, Injectable} from '@angular/core';
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
export class AuthService {
  static readonly AUTH_TOKEN = "Auth-Token";
  static readonly MFA_PROCESS_ID = "mfa";
  static readonly USER_ID = "id";

  private loginSubject: BehaviorSubject<LoginState> = new BehaviorSubject<LoginState>("loading");
  public isLoggedIn$: Observable<LoginState> = this.loginSubject.asObservable();

  constructor(
      private api: ApiService,
      private router: Router,
      private tokenService: TokenService,
  ) {
    this.loadLoginState();
  }

  login(mail: string, password: string, stay: boolean = true): Observable<string> {
    this.tokenService.removeToken();
    return this.api.post<MessageDto | ErrorDto>(endpoint.AUTH, {
      credentials: {
        mail, password
      }, stay
    }).pipe(
        tap(response => {
          const token: string | undefined = (response as MessageDto).message;
          if (token) {
            this.tokenService.token = token;
            this.loginSubject.next("in");
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
    this.tokenService.removeToken();
    location.reload();
  }

  validateMfaToken(processId: string, userId: string, code: number): Observable<boolean> {
    return this.api.post<MessageDto>(endpoint.MFA, {
      processId, userId, code
    }).pipe(
        tap(dto => {
          this.tokenService.token = dto.message;
          localStorage.removeItem(AuthService.MFA_PROCESS_ID);
          this.loginSubject.next("in");
          this.router.navigate([pages.HOME]).then()
        }),
        concatMap(_ => of(true)),
        catchError(_ => of(false))
    );
  }

  private loadLoginState(): void {
    if (this.tokenService.hasToken()) {
      this.api.getRaw(endpoint.AUTH).subscribe({
        next: _ => this.loginSubject.next("in"),
        error: _ => {
          this.tokenService.removeToken();
          this.loginSubject.next("out");
        }
      });
    } else {
      this.loginSubject.next("out");
    }
  }
}

@Injectable({
  providedIn: 'root',
})
export class AuthTokenInterceptor implements HttpInterceptor {

  constructor(
      private tokenService: TokenService,
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const requestWithHeaders = req.clone({
      setHeaders: {[AuthService.AUTH_TOKEN]: this.tokenService.token},
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

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  constructor(
      private cookie: CookieService,
  ) {
  }

  private _token: string | undefined;

  get token(): string {
    if (!this._token)
      this._token = this.cookie.get(AuthService.AUTH_TOKEN);
    return this._token;
  }

  set token(token: string) {
    let expires = moment(new Date()).add(300, "days");
    this.cookie.set(AuthService.AUTH_TOKEN, token, expires.toDate(), "/", environment.DOMAIN, true, "Strict");
    this._token = token;
  }

  hasToken(): boolean {
    return !!this.token;
  }

  removeToken() {
    this.cookie.delete(AuthService.AUTH_TOKEN, "/", environment.DOMAIN, true, "Strict");
  }
}
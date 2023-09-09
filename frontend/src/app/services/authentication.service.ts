import {inject, Injectable} from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
  UrlTree
} from "@angular/router";
import {Observable, of} from "rxjs";
import {pages} from "../app-routing.module";

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  constructor() {
  }


}

export const authenticationGuard: CanActivateFn = (next: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<UrlTree> | Observable<boolean> => {
  const authService = inject(AuthenticationService);
  const router = inject(Router);

  return of(router.parseUrl(pages.LOGIN));
}

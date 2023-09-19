import {Injectable} from '@angular/core';
import {ErrorDto} from "../dtos/ErrorDto";

@Injectable({
  providedIn: 'root'
})
export class ErrorService {

  constructor() {
  }

  public handleAuthenticationError(e: ErrorDto): boolean {
    // write if to check if error is auth error and if so handle (either refresh token or login page)
    console.warn("caught auth error > NOT IMPLEMENTED")
    return false;
  }

  public showErrorDialog(e: ErrorDto): void {
    console.warn("should show error dialog", e);
  }
}

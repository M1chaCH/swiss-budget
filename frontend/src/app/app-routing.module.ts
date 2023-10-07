import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomePageComponent} from "./pages/home.page/home.page.component";
import {LoginPageComponent} from "./pages/welcome.page/login.page.component";
import {SetupSubpageComponent} from "./pages/welcome.page/setup.subpage/setup.subpage.component";
import {authenticationGuard} from "./services/auth.service";
import {MfaSubpageComponent} from "./pages/welcome.page/mfa.subpage/mfa.subpage.component";
import {BudgetPageComponent} from "./pages/budget.page/budget.page.component";
import {TransactionPageComponent} from "./pages/transaction.page/transaction.page.component";

export const pages = {
  LOGIN: "login",
  login: {
    SETUP: "setup",
    MFA: "mfa",
  },
  HOME: "home",
  BUDGET: "budget",
  TRANSACTIONS: "transactions",
  SAVE: "save",
  CONFIGURATION: "configuration",
  PROFILE: "profile",
}

export const APP_ROOT = "app"

const routes: Routes = [
  {path: APP_ROOT, redirectTo: pages.HOME, pathMatch: "full"},
  {
    path: pages.LOGIN,
    component: LoginPageComponent,
    data: {animation: "LoginPage"},
    children: [
      {path: pages.login.SETUP, component: SetupSubpageComponent, data: {animation: "SetupPage"}},
      {path: pages.login.MFA, component: MfaSubpageComponent, data: {animation: "MfaPage"}},
    ]
  },
  {
    path: APP_ROOT,
    canActivate: [authenticationGuard],
    children: [
      {path: pages.HOME, component: HomePageComponent},
      {path: pages.BUDGET, component: BudgetPageComponent},
      {path: pages.TRANSACTIONS, component: TransactionPageComponent},
    ]
  },
  {path: "**", redirectTo: `${APP_ROOT}/${pages.HOME}`, pathMatch: "full"},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

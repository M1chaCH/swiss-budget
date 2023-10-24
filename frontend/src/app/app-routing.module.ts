import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomePageComponent} from "./pages/home.page/home.page.component";
import {LoginPageComponent} from "./pages/welcome.page/login.page.component";
import {SetupSubpageComponent} from "./pages/welcome.page/setup.subpage/setup.subpage.component";
import {authenticationGuard} from "./services/auth.service";
import {MfaSubpageComponent} from "./pages/welcome.page/mfa.subpage/mfa.subpage.component";
import {BudgetPageComponent} from "./pages/budget.page/budget.page.component";
import {TransactionPageComponent} from "./pages/transaction.page/transaction.page.component";
import {HelpPageComponent} from "./pages/help.page/help.page.component";

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
  HELP: "help",
}

export const APP_ROOT = "app"

const routes: Routes = [
  {path: APP_ROOT, redirectTo: pages.HOME, pathMatch: "full"},
  {
    path: pages.LOGIN,
    component: LoginPageComponent,
    children: [
      {path: pages.login.SETUP, component: SetupSubpageComponent},
      {path: pages.login.MFA, component: MfaSubpageComponent},
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
  {path: `${APP_ROOT}/${pages.HELP}`, component: HelpPageComponent},
  {path: "**", redirectTo: `${APP_ROOT}/${pages.HOME}`, pathMatch: "full"},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}

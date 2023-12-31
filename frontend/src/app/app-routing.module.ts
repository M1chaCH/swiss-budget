import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {BudgetPageComponent} from './pages/budget.page/budget.page.component';
import {ConfigurationPageComponent} from './pages/configuration.page/configuration.page.component';
import {HelpPageComponent} from './pages/help.page/help.page.component';
import {HomePageComponent} from './pages/home.page/home.page.component';
import {LoginPageComponent} from './pages/login.page/login.page.component';
import {MfaSubpageComponent} from './pages/login.page/mfa.subpage/mfa.subpage.component';
import {SetupSubpageComponent} from './pages/login.page/setup.subpage/setup.subpage.component';
import {TransactionPageComponent} from './pages/transaction.page/transaction.page.component';
import {authenticationGuard} from './services/auth.service';

export const pages = {
  LOGIN: 'login',
  login: {
    SETUP: 'setup',
    MFA: 'mfa',
  },
  HOME: 'home',
  BUDGET: 'budget',
  TRANSACTIONS: 'transactions',
  SAVE: 'save',
  CONFIGURATION: 'configuration',
  PROFILE: 'profile',
  HELP: 'help',
};

export const APP_ROOT = 'app';

const routes: Routes = [
  {path: APP_ROOT, redirectTo: pages.HOME, pathMatch: 'full'},
  {
    path: pages.LOGIN,
    component: LoginPageComponent,
    children: [
      {path: pages.login.SETUP, component: SetupSubpageComponent},
      {path: pages.login.MFA, component: MfaSubpageComponent},
    ],
  },
  {
    path: APP_ROOT,
    canActivate: [authenticationGuard],
    children: [
      {path: pages.HOME, component: HomePageComponent},
      {path: pages.BUDGET, component: BudgetPageComponent},
      {path: pages.TRANSACTIONS, component: TransactionPageComponent},
      {path: pages.CONFIGURATION, component: ConfigurationPageComponent},
    ],
  },
  {path: `${APP_ROOT}/${pages.HELP}`, component: HelpPageComponent},
  {path: '**', redirectTo: `${APP_ROOT}/${pages.HOME}`, pathMatch: 'full'},
];

@NgModule({
            imports: [RouterModule.forRoot(routes)],
            exports: [RouterModule],
          })
export class AppRoutingModule {
}

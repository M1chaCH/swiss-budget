import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomePageComponent} from "./pages/home.page/home.page.component";
import {LoginPageComponent} from "./pages/welcome.page/login.page.component";
import {authenticationGuard} from "./services/authentication.service";
import {SetupSubpageComponent} from "./pages/welcome.page/setup.subpage/setup.subpage.component";

export const pages = {
  LOGIN: "login",
  login: {SETUP: "setup"},
  HOME: "home",
}

export const APP_ROOT = "app"

const routes: Routes = [
  {path: APP_ROOT, redirectTo: pages.HOME, pathMatch: "full"},
  {
    path: pages.LOGIN,
    component: LoginPageComponent,
    data: {animation: "LoginPage"},
    children: [
      {path: pages.login.SETUP, component: SetupSubpageComponent, data: {animation: "SetupPage"}}
    ]
  },
  {
    path: APP_ROOT,
    canActivate: [authenticationGuard],
    children: [
      {path: pages.HOME, component: HomePageComponent},
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

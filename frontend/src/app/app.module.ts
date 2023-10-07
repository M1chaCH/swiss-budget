import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {SetupSubpageComponent} from './pages/welcome.page/setup.subpage/setup.subpage.component';
import {LoginPageComponent} from './pages/welcome.page/login.page.component';
import {HomePageComponent} from './pages/home.page/home.page.component';
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {FullscreenPageDirective} from './pages/fullscreen-page.directive';
import {NgOptimizedImage} from "@angular/common";
import {LoginComponent} from './components/login/login.component';
import {ButtonComponent} from './components/button/button.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TextInputComponent} from './components/text-input/text-input.component';
import {StepsPanelComponent} from './components/steps-panel/steps-panel.component';
import {PanelStepDirective} from './components/steps-panel/panel-step.directive';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {DialogComponent} from './components/dialog/dialog.component';
import {HelpComponent} from './components/help/help.component';
import {DialogDirective} from './components/dialog/dialog.directive';
import {TextAreaComponent} from './components/text-area/text-area.component';
import {SelectComponent} from './components/select/select.component';
import {ClickThrottlerDirective} from './components/button/click-throttler.directive';
import {CookieService} from "ngx-cookie-service";
import {AuthService} from "./services/auth.service";
import {MfaSubpageComponent} from './pages/welcome.page/mfa.subpage/mfa.subpage.component';
import {CheckboxComponent} from './components/checkbox/checkbox.component';
import {BudgetPageComponent} from './pages/budget.page/budget.page.component';
import {PageDirective} from './pages/page.directive';
import {ContentReplacerDirective} from "./animations";
import {TransactionPageComponent} from './pages/transaction.page/transaction.page.component';

@NgModule({
  declarations: [
    AppComponent,
    SetupSubpageComponent,
    LoginPageComponent,
    HomePageComponent,
    FullscreenPageDirective,
    LoginComponent,
    ButtonComponent,
    TextInputComponent,
    StepsPanelComponent,
    PanelStepDirective,
    DialogComponent,
    HelpComponent,
    DialogDirective,
    TextAreaComponent,
    SelectComponent,
    ClickThrottlerDirective,
    MfaSubpageComponent,
    CheckboxComponent,
    BudgetPageComponent,
    PageDirective,
    ContentReplacerDirective,
    TransactionPageComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    NgOptimizedImage,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
  ],
  providers: [
    {provide: HTTP_INTERCEPTORS, useClass: AuthService, multi: true},
    CookieService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

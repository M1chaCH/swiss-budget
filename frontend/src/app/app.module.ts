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
import {HttpClientModule} from "@angular/common/http";
import {DialogComponent} from './components/dialog/dialog.component';
import {HelpComponent} from './components/help/help.component';
import {DialogDirective} from './components/dialog/dialog.directive';

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
    DialogDirective
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
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}

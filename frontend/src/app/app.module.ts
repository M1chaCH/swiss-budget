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

@NgModule({
  declarations: [
    AppComponent,
    SetupSubpageComponent,
    LoginPageComponent,
    HomePageComponent,
    FullscreenPageDirective,
    LoginComponent,
    ButtonComponent,
    TextInputComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    NgOptimizedImage,
    FormsModule,
    ReactiveFormsModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}

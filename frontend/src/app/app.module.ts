import {DEFAULT_CURRENCY_CODE, NgModule} from '@angular/core';
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
import {ButtonComponent} from './components/form/button/button.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {TextInputComponent} from './components/form/text-input/text-input.component';
import {StepsPanelComponent} from './components/steps-panel/steps-panel.component';
import {PanelStepDirective} from './components/steps-panel/panel-step.directive';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {DialogComponent} from './components/dialog/dialog.component';
import {HelpDialogComponent} from './components/help/help-dialog.component';
import {TextAreaComponent} from './components/form/text-area/text-area.component';
import {SelectComponent} from './components/form/select/select.component';
import {ClickThrottlerDirective} from './components/form/button/click-throttler.directive';
import {CookieService} from "ngx-cookie-service";
import {AuthTokenInterceptor} from "./services/auth.service";
import {MfaSubpageComponent} from './pages/welcome.page/mfa.subpage/mfa.subpage.component';
import {CheckboxComponent} from './components/form/checkbox/checkbox.component';
import {BudgetPageComponent} from './pages/budget.page/budget.page.component';
import {PageDirective} from './pages/page.directive';
import {ContentReplacerDirective} from "./animations";
import {TransactionPageComponent} from './pages/transaction.page/transaction.page.component';
import {TransactionComponent} from './components/transactions/transaction/transaction.component';
import {TransactionImporterComponent} from './components/transactions/transaction-importer/transaction-importer.component';
import {ExpansionPanelComponent} from './components/expansion-panel/expansion-panel.component';
import {TransactionDetailComponent} from './components/transactions/transaction-detail/transaction-detail.component';
import {TagIconComponent} from './components/tags/tag-icon/tag-icon.component';
import {StyledAmountComponent} from './components/transactions/styled-amount/styled-amount.component';
import {ExpansionListToggleComponent} from './components/expansion-panel/expansion-panel-toggle/expansion-list-toggle.component';
import {DisplayErrorDialogComponent} from './components/display-error/display-error-dialog.component';
import {DialogHostDirective} from './components/dialog/dialog-host.directive';
import {TranslateErrorPipe} from './pipes/translate-error.pipe';
import {DialogContentWrapperComponent} from './components/dialog/dialog-wrapper/dialog-content-wrapper.component';
import {HelpPageComponent} from './pages/help.page/help.page.component';
import {TransactionFilterComponent} from './components/transactions/transaction-filter/transaction-filter.component';
import {FieldExtensionsComponent} from './components/form/field-extensions/field-extensions.component';
import {FieldExtensionDirective} from './components/form/field-extensions/field-extension.directive';
import {FieldExtensionContentComponent} from './components/form/field-extensions/field-extension-content/field-extension-content.component';
import {DatePickerComponent} from './components/form/date-picker/date-picker.component';
import {DatePickerFormComponent} from './components/form/date-picker/picker-form/date-picker-form.component';
import {PageWithHeaderComponent} from './components/page-with-header/page-with-header.component';
import {AssignTagDialogComponent} from './components/tags/assign-tag-dialog/assign-tag-dialog.component';
import {TagSelectorComponent} from './components/tags/tag-selector/tag-selector.component';
import {ResolveTagConflictDialogComponent} from './components/tags/resolve-tag-conflict-dialog/resolve-tag-conflict-dialog.component';
import {TransactionPreviewComponent} from './components/transactions/transaction-preview/transaction-preview.component';

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
    HelpDialogComponent,
    TextAreaComponent,
    SelectComponent,
    ClickThrottlerDirective,
    MfaSubpageComponent,
    CheckboxComponent,
    BudgetPageComponent,
    PageDirective,
    ContentReplacerDirective,
    TransactionPageComponent,
    TransactionComponent,
    TransactionImporterComponent,
    ExpansionPanelComponent,
    TransactionDetailComponent,
    TagIconComponent,
    StyledAmountComponent,
    ExpansionListToggleComponent,
    DisplayErrorDialogComponent,
    DialogHostDirective,
    TranslateErrorPipe,
    DialogContentWrapperComponent,
    HelpPageComponent,
    TransactionFilterComponent,
    FieldExtensionsComponent,
    FieldExtensionDirective,
    FieldExtensionContentComponent,
    DatePickerComponent,
    DatePickerFormComponent,
    PageWithHeaderComponent,
    AssignTagDialogComponent,
    TagSelectorComponent,
    ResolveTagConflictDialogComponent,
    TransactionPreviewComponent,
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
    {provide: HTTP_INTERCEPTORS, useClass: AuthTokenInterceptor, multi: true},
    {provide: DEFAULT_CURRENCY_CODE, useValue: ''},
    CookieService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}

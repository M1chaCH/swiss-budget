import {NgOptimizedImage} from "@angular/common";
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {DEFAULT_CURRENCY_CODE, NgModule} from "@angular/core";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserModule} from "@angular/platform-browser";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {CookieService} from "ngx-cookie-service";
import {ContentReplacerDirective} from "./animations";

import {AppRoutingModule} from "./app-routing.module";
import {AppComponent} from "./app.component";
import {ConfirmDialogComponent} from "./components/dialog/confirm-dialog/confirm-dialog.component";
import {DialogHostDirective} from "./components/dialog/dialog-host.directive";
import {DialogContentWrapperComponent} from "./components/dialog/dialog-wrapper/dialog-content-wrapper.component";
import {DialogComponent} from "./components/dialog/dialog.component";
import {DisplayErrorDialogComponent} from "./components/display-error/display-error-dialog.component";
import {ExpansionListToggleComponent} from "./components/expansion-panel/expansion-panel-toggle/expansion-list-toggle.component";
import {ExpansionPanelComponent} from "./components/expansion-panel/expansion-panel.component";
import {ButtonComponent} from "./components/form/button/button.component";
import {ClickThrottlerDirective} from "./components/form/button/click-throttler.directive";
import {CheckboxComponent} from "./components/form/checkbox/checkbox.component";
import {DatePickerComponent} from "./components/form/date-picker/date-picker.component";
import {DatePickerFormComponent} from "./components/form/date-picker/picker-form/date-picker-form.component";
import {FieldExtensionContentComponent} from "./components/form/field-extensions/field-extension-content/field-extension-content.component";
import {FieldExtensionDirective} from "./components/form/field-extensions/field-extension.directive";
import {FieldExtensionsComponent} from "./components/form/field-extensions/field-extensions.component";
import {SelectComponent} from "./components/form/select/select.component";
import {TextAreaComponent} from "./components/form/text-area/text-area.component";
import {TextInputComponent} from "./components/form/text-input/text-input.component";
import {HelpDialogComponent} from "./components/help/help-dialog.component";
import {LoginComponent} from "./components/login/login.component";
import {PageWithHeaderComponent} from "./components/page-with-header/page-with-header.component";
import {PanelStepDirective} from "./components/steps-panel/panel-step.directive";
import {StepsPanelComponent} from "./components/steps-panel/steps-panel.component";
import {AssignTagDialogComponent} from "./components/tags/assign-tag-dialog/assign-tag-dialog.component";
import {ChangeTagDialogComponent} from "./components/tags/change-tag-dialog/change-tag-dialog.component";
import {ResolveTagConflictDialogComponent} from "./components/tags/resolve-tag-conflict-dialog/resolve-tag-conflict-dialog.component";
import {TagColorSelectorComponent} from "./components/tags/tag-color-selector/tag-color-selector.component";
import {TagDetailComponent} from "./components/tags/tag-detail/tag-detail.component";
import {TagIconSelectorComponent} from "./components/tags/tag-icon-selector/tag-icon-selector.component";
import {TagIconComponent} from "./components/tags/tag-icon/tag-icon.component";
import {TagSelectorComponent} from "./components/tags/tag-selector/tag-selector.component";
import {TagComponent} from "./components/tags/tag/tag.component";
import {StyledAmountComponent} from "./components/transactions/styled-amount/styled-amount.component";
import {TransactionDetailComponent} from "./components/transactions/transaction-detail/transaction-detail.component";
import {TransactionFilterComponent} from "./components/transactions/transaction-filter/transaction-filter.component";
import {TransactionImporterComponent} from "./components/transactions/transaction-importer/transaction-importer.component";
import {TransactionPreviewComponent} from "./components/transactions/transaction-preview/transaction-preview.component";
import {TransactionComponent} from "./components/transactions/transaction/transaction.component";
import {BudgetPageComponent} from "./pages/budget.page/budget.page.component";
import {ConfigurationPageComponent} from "./pages/configuration.page/configuration.page.component";
import {FullscreenPageDirective} from "./pages/fullscreen-page.directive";
import {HelpPageComponent} from "./pages/help.page/help.page.component";
import {HomePageComponent} from "./pages/home.page/home.page.component";
import {PageDirective} from "./pages/page.directive";
import {TransactionPageComponent} from "./pages/transaction.page/transaction.page.component";
import {LoginPageComponent} from "./pages/welcome.page/login.page.component";
import {MfaSubpageComponent} from "./pages/welcome.page/mfa.subpage/mfa.subpage.component";
import {SetupSubpageComponent} from "./pages/welcome.page/setup.subpage/setup.subpage.component";
import {TranslateErrorPipe} from "./pipes/translate-error.pipe";
import {AuthTokenInterceptor} from "./services/auth.service";

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
    ChangeTagDialogComponent,
    ConfigurationPageComponent,
    TagComponent,
    ConfirmDialogComponent,
    TagDetailComponent,
    TagColorSelectorComponent,
    TagIconSelectorComponent,
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

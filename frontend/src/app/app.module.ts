import {NgOptimizedImage} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {DEFAULT_CURRENCY_CODE, NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserModule} from '@angular/platform-browser';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CookieService} from 'ngx-cookie-service';
import {ContentReplacerDirective} from './animations';
import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BannerOutletComponent} from './components/framework/banner/banner-outlet/banner-outlet.component';
import {ConfirmDialogComponent} from './components/framework/dialog/confirm-dialog/confirm-dialog.component';
import {DialogHostDirective} from './components/framework/dialog/dialog-host.directive';
import {DialogOutletComponent} from './components/framework/dialog/dialog-outlet.component';
import {DialogContentWrapperComponent} from './components/framework/dialog/dialog-wrapper/dialog-content-wrapper.component';
import {DisplayErrorDialogComponent} from './components/framework/display-error/display-error-dialog.component';
import {ExpansionListToggleComponent} from './components/framework/expansion-panel/expansion-panel-toggle/expansion-list-toggle.component';
import {ExpansionPanelComponent} from './components/framework/expansion-panel/expansion-panel.component';
import {ButtonComponent} from './components/framework/form/button/button.component';
import {ClickThrottlerDirective} from './components/framework/form/button/click-throttler.directive';
import {CheckboxComponent} from './components/framework/form/checkbox/checkbox.component';
import {DatePickerComponent} from './components/framework/form/date-picker/date-picker.component';
import {DatePickerFormComponent} from './components/framework/form/date-picker/picker-form/date-picker-form.component';
import {FieldExtensionContentComponent} from './components/framework/form/field-extensions/field-extension-content/field-extension-content.component';
import {FieldExtensionDirective} from './components/framework/form/field-extensions/field-extension.directive';
import {FieldExtensionsComponent} from './components/framework/form/field-extensions/field-extensions.component';
import {SelectComponent} from './components/framework/form/select/select.component';
import {TextAreaComponent} from './components/framework/form/text-area/text-area.component';
import {TextInputComponent} from './components/framework/form/text-input/text-input.component';
import {PageWithHeaderComponent} from './components/framework/page-with-header/page-with-header.component';
import {PanelStepDirective} from './components/framework/steps-panel/panel-step.directive';
import {StepsPanelComponent} from './components/framework/steps-panel/steps-panel.component';
import {HelpDialogComponent} from './components/help/help-dialog.component';
import {HeaderComponent} from './components/layout/header/header.component';
import {NavigationTreeComponent} from './components/layout/navigation-tree/navigation-tree.component';
import {LoginFormComponent} from './components/login/login-form/login-form.component';
import {WelcomeComponent} from './components/login/welcome/welcome.component';
import {AssignTagDialogComponent} from './components/tags/assign-tag-dialog/assign-tag-dialog.component';
import {ChangeTagDialogComponent} from './components/tags/change-tag-dialog/change-tag-dialog.component';
import {ResolveTagConflictDialogComponent} from './components/tags/resolve-tag-conflict-dialog/resolve-tag-conflict-dialog.component';
import {TagColorSelectorComponent} from './components/tags/tag-color-selector/tag-color-selector.component';
import {TagDetailComponent} from './components/tags/tag-detail/tag-detail.component';
import {TagIconSelectorComponent} from './components/tags/tag-icon-selector/tag-icon-selector.component';
import {TagIconComponent} from './components/tags/tag-icon/tag-icon.component';
import {TagSelectorComponent} from './components/tags/tag-selector/tag-selector.component';
import {TagComponent} from './components/tags/tag/tag.component';
import {StyledAmountComponent} from './components/transactions/styled-amount/styled-amount.component';
import {TransactionDetailComponent} from './components/transactions/transaction-detail/transaction-detail.component';
import {TransactionFilterComponent} from './components/transactions/transaction-filter/transaction-filter.component';
import {TransactionImportBannerComponent} from './components/transactions/transaction-importer/transaction-import-banner.component';
import {TransactionPreviewComponent} from './components/transactions/transaction-preview/transaction-preview.component';
import {TransactionComponent} from './components/transactions/transaction/transaction.component';
import {BudgetPageComponent} from './pages/budget.page/budget.page.component';
import {ConfigurationPageComponent} from './pages/configuration.page/configuration.page.component';
import {HelpPageComponent} from './pages/help.page/help.page.component';
import {HomePageComponent} from './pages/home.page/home.page.component';
import {LoginPageComponent} from './pages/login.page/login.page.component';
import {MfaSubpageComponent} from './pages/login.page/mfa.subpage/mfa.subpage.component';
import {SetupSubpageComponent} from './pages/login.page/setup.subpage/setup.subpage.component';
import {PageDirective} from './pages/page.directive';
import {TransactionPageComponent} from './pages/transaction.page/transaction.page.component';
import {TranslateErrorPipe} from './pipes/translate-error.pipe';
import {AuthTokenInterceptor} from './services/auth.service';

@NgModule({
            bootstrap: [AppComponent],
            declarations: [
              AppComponent,
              SetupSubpageComponent,
              LoginPageComponent,
              HomePageComponent,
              LoginFormComponent,
              ButtonComponent,
              TextInputComponent,
              StepsPanelComponent,
              PanelStepDirective,
              DialogOutletComponent,
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
              TransactionImportBannerComponent,
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
              HeaderComponent,
              WelcomeComponent,
              NavigationTreeComponent,
              BannerOutletComponent,
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
          })
export class AppModule {
}

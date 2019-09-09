package com.flutterwave.raveandroid.di;

import com.flutterwave.raveandroid.TransactionStatusCheckerTest;
import com.flutterwave.raveandroid.account.AccountPresenter;
import com.flutterwave.raveandroid.account.AccountPresenterTest;
import com.flutterwave.raveandroid.card.CardPresenter;
import com.flutterwave.raveandroid.card.CardPresenterTest;
import com.flutterwave.raveandroid.di.components.AppComponent;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyPresenter;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyPresenterTest;
import com.flutterwave.raveandroid.mpesa.MpesaPresenter;
import com.flutterwave.raveandroid.mpesa.MpesaPresenterTest;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyPresenter;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyPresenterTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestAndroidModule.class, TestNetworkModule.class})
public interface TestAppComponent extends AppComponent {

    void inject(CardPresenterTest cardPresenterTest);

    void inject(AccountPresenterTest accountPresenterTest);

    void inject(MpesaPresenterTest mpesaPresenterTest);

    void inject(UgMobileMoneyPresenterTest ugMobileMoneyPresenterTest);

    void inject(GhMobileMoneyPresenterTest ghMobileMoneyPresenterTest);

    void inject(TransactionStatusCheckerTest transactionStatusCheckerTest);

    void inject(CardPresenter cardPresenter);

    void inject(AccountPresenter accountPresenter);

    void inject(MpesaPresenter mpesaPresenter);

    void inject(UgMobileMoneyPresenter ugMobileMoneyPresenter);

    void inject(GhMobileMoneyPresenter ghMobileMoneyPresenter);

}

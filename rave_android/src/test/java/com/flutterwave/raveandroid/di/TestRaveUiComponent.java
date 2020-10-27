package com.flutterwave.raveandroid.di;

import com.flutterwave.raveandroid.TransactionStatusCheckerTest;
import com.flutterwave.raveandroid.account.AccountPresenterTest;
import com.flutterwave.raveandroid.account.AccountUiPresenter;
import com.flutterwave.raveandroid.ach.AchPresenter;
import com.flutterwave.raveandroid.ach.AchPresenterTest;
import com.flutterwave.raveandroid.acquireddotcom.AcquiredPresenter;
import com.flutterwave.raveandroid.acquireddotcom.AcquiredPresenterTest;
import com.flutterwave.raveandroid.banktransfer.BankTransferPresenter;
import com.flutterwave.raveandroid.banktransfer.BankTransferPresenterTest;
import com.flutterwave.raveandroid.card.CardPresenterTest;
import com.flutterwave.raveandroid.card.CardUiPresenter;
import com.flutterwave.raveandroid.di.components.RaveUiComponent;
import com.flutterwave.raveandroid.francMobileMoney.FrancMobileMoneyPresenter;
import com.flutterwave.raveandroid.francMobileMoney.FrancMobileMoneyPresenterTest;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyPresenter;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyPresenterTest;
import com.flutterwave.raveandroid.mpesa.MpesaPresenter;
import com.flutterwave.raveandroid.mpesa.MpesaPresenterTest;
import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyPresenter;
import com.flutterwave.raveandroid.rwfmobilemoney.RwfMobileMoneyPresenterTest;
import com.flutterwave.raveandroid.sabankaccount.SaBankAccountPresenter;
import com.flutterwave.raveandroid.sabankaccount.SaBankAccountPresenterTest;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyPresenter;
import com.flutterwave.raveandroid.ugmobilemoney.UgMobileMoneyPresenterTest;
import com.flutterwave.raveandroid.uk.UkPresenter;
import com.flutterwave.raveandroid.uk.UkPresenterTest;
import com.flutterwave.raveandroid.ussd.UssdPresenter;
import com.flutterwave.raveandroid.ussd.UssdPresenterTest;
import com.flutterwave.raveandroid.zmmobilemoney.ZmMobileMoneyPresenter;
import com.flutterwave.raveandroid.zmmobilemoney.ZmMobileMoneyPresenterTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestAndroidModule.class, TestNetworkModule.class})
public interface TestRaveUiComponent extends RaveUiComponent {

    void inject(CardPresenterTest cardPresenterTest);

    void inject(AccountPresenterTest accountPresenterTest);

    void inject(SaBankAccountPresenterTest accountPresenterTest);

    void inject(BankTransferPresenterTest bankTransferPresenterTest);

    void inject(UssdPresenterTest ussdPresenterTest);

    void inject(MpesaPresenterTest mpesaPresenterTest);

    void inject(UgMobileMoneyPresenterTest ugMobileMoneyPresenterTest);

    void inject(GhMobileMoneyPresenterTest ghMobileMoneyPresenterTest);

    void inject(RwfMobileMoneyPresenterTest rwfMobileMoneyPresenterTest);

    void inject(ZmMobileMoneyPresenterTest zmMobileMoneyPresenterTest);

    void inject(TransactionStatusCheckerTest transactionStatusCheckerTest);

    void inject(AchPresenterTest achPresenterTest);
    
    void inject(UkPresenterTest ukPresenterTest);

    void inject(FrancMobileMoneyPresenterTest francMobileMoneyPresenterTest);

    void inject(AcquiredPresenterTest acquiredPresenterTest);

    void inject(CardUiPresenter cardUiPresenter);

    void inject(AccountUiPresenter accountUiPresenter);

    void inject(SaBankAccountPresenter accountPresenter);

    void inject(BankTransferPresenter bankTransferPresenter);

    void inject(UssdPresenter ussdPresenter);

    void inject(MpesaPresenter mpesaPresenter);

    void inject(UgMobileMoneyPresenter ugMobileMoneyPresenter);

    void inject(GhMobileMoneyPresenter ghMobileMoneyPresenter);

    void inject(AchPresenter achPresenter);

    void inject(UkPresenter ukPresenter);

    void inject(FrancMobileMoneyPresenter francMobileMoneyPresenter);

    void inject(RwfMobileMoneyPresenter rwfMobileMoneyPresenter);

    void inject(ZmMobileMoneyPresenter zmMobileMoneyPresenter);

    void inject(AcquiredPresenter acquiredPresenter);

}

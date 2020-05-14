package com.flutterwave.raveandroid.rave_presentation;

import com.flutterwave.raveandroid.rave_presentation.account.AccountHandler;
import com.flutterwave.raveandroid.rave_presentation.account.AccountHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.ach.AchHandler;
import com.flutterwave.raveandroid.rave_presentation.ach.AchHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.banktransfer.BankTransferHandler;
import com.flutterwave.raveandroid.rave_presentation.banktransfer.BankTransferHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentHandler;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.francmobilemoney.FrancMobileMoneyHandler;
import com.flutterwave.raveandroid.rave_presentation.francmobilemoney.FrancMobileMoneyHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.ghmobilemoney.GhMobileMoneyHandler;
import com.flutterwave.raveandroid.rave_presentation.ghmobilemoney.GhMobileMoneyHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.mpesa.MpesaHandler;
import com.flutterwave.raveandroid.rave_presentation.mpesa.MpesaHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney.RwfMobileMoneyHandler;
import com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney.RwfMobileMoneyHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.sabankaccount.SaBankAccountHandler;
import com.flutterwave.raveandroid.rave_presentation.sabankaccount.SaBankAccountHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgMobileMoneyHandler;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgMobileMoneyHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.uk.UkHandler;
import com.flutterwave.raveandroid.rave_presentation.uk.UkHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.ussd.UssdHandler;
import com.flutterwave.raveandroid.rave_presentation.ussd.UssdHandlerTest;
import com.flutterwave.raveandroid.rave_presentation.zmmobilemoney.ZmMobileMoneyHandler;
import com.flutterwave.raveandroid.rave_presentation.zmmobilemoney.ZmMobileMoneyHandlerTest;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TestNetworkModule.class, TestUtilsModule.class})
public interface TestRaveComponent {

    void inject(AccountHandler paymentHandler);

    void inject(AccountHandlerTest test);

    void inject(AchHandler paymentHandler);

    void inject(AchHandlerTest test);

    void inject(BankTransferHandler paymentHandler);

    void inject(BankTransferHandlerTest test);

    void inject(CardPaymentHandler paymentHandler);

    void inject(CardPaymentHandlerTest test);

    void inject(FrancMobileMoneyHandler paymentHandler);

    void inject(FrancMobileMoneyHandlerTest test);

    void inject(GhMobileMoneyHandler paymentHandler);

    void inject(GhMobileMoneyHandlerTest test);

    void inject(MpesaHandler paymentHandler);

    void inject(MpesaHandlerTest test);

    void inject(RwfMobileMoneyHandler paymentHandler);

    void inject(RwfMobileMoneyHandlerTest test);

    void inject(SaBankAccountHandler paymentHandler);

    void inject(SaBankAccountHandlerTest test);

    void inject(UgMobileMoneyHandler paymentHandler);

    void inject(UgMobileMoneyHandlerTest test);

    void inject(UkHandler paymentHandler);

    void inject(UkHandlerTest test);

    void inject(UssdHandler paymentHandler);

    void inject(UssdHandlerTest test);

    void inject(ZmMobileMoneyHandler paymentHandler);

    void inject(ZmMobileMoneyHandlerTest test);

}

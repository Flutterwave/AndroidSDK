package com.flutterwave.raveandroid.ach;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_presentation.ach.AchContract;

public interface AchUiContract {

    interface View extends AchContract.Interactor {
        void showProgressIndicator(boolean active);

        void onAmountValidated(String amountToSet, int visibility);

        void showRedirectMessage(boolean b);

        void showWebView(String authUrl, String flwRef);

        void showAmountError(String msg);

        void showFee(String authUrl, String flwRef, String chargedAmount, String currency);

        void onValidationSuccessful(String amount);
    }

    interface UserActionsListener extends AchContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(RavePayInitializer ravePayInitializer, String amount);

        void onFeeConfirmed(String authUrl, String flwRef);

        void processTransaction(String amount, RavePayInitializer ravePayInitializer, boolean isDisplayFee);

        void onAttachView(View view);

        void onDetachView();

        void logEvent(Event event, String publicKey);
    }

}

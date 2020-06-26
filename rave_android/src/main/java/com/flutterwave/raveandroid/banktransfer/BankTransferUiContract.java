package com.flutterwave.raveandroid.banktransfer;

import android.os.Bundle;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_presentation.banktransfer.BankTransferContract;

import java.util.HashMap;


public interface BankTransferUiContract {

    interface View extends BankTransferContract.BankTransferInteractor {
        void showToast(String message);

        void onAmountValidationSuccessful(String valueOf);

        void showFieldError(int viewID, String message, Class<?> viewType);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void onAmountValidationFailed();
    }

    interface UserActionsListener extends BankTransferContract.BankTransferHandler {
        void init(RavePayInitializer ravePayInitializer);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void startPaymentVerification(int pollingTimeoutInSeconds);

        void cancelPolling();

        Bundle getState();

        void restoreState(Bundle savedInstanceState);

        void onAttachView(View view);

        void onDetachView();

        void logEvent(Event event, String publicKey);
    }
}

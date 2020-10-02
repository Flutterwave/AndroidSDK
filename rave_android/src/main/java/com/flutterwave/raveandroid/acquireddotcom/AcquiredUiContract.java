package com.flutterwave.raveandroid.acquireddotcom;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.acquireddotcom.AcquiredContract;

import java.util.HashMap;

public interface AcquiredUiContract {
    interface View extends AcquiredContract.Interactor {
        void onAmountValidationSuccessful(String amountToPay, String currency);

        void onAmountValidationFailed();
    }

    interface UserActionsListener extends AcquiredContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(RavePayInitializer ravePayInitializer, boolean appIsInDarkMode);

        void onAttachView(AcquiredUiContract.View view);

        void onDetachView();
    }
}


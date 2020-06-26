package com.flutterwave.raveandroid.sabankaccount;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.sabankaccount.SaBankAccountContract;

import java.util.HashMap;

public interface SaBankAccountUiContract {
    interface View extends SaBankAccountContract.Interactor {
        void onAmountValidationSuccessful(String amountToPay, String currency);

        void showFieldError(int viewID, String message, Class<?> viewType);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);
    }

    interface UserActionsListener extends SaBankAccountContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(RavePayInitializer ravePayInitializer);

        void onAttachView(SaBankAccountUiContract.View view);

        void onDetachView();
    }
}

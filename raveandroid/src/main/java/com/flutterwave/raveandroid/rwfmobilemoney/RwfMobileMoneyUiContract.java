package com.flutterwave.raveandroid.rwfmobilemoney;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney.RwfMobileMoneyContract;

import java.util.HashMap;


public interface RwfMobileMoneyUiContract {

    interface View extends RwfMobileMoneyContract.Interactor {
        void onAmountValidationSuccessful(String amountToPay);

        void showFieldError(int viewID, String message, Class<?> viewType);

        void onPhoneValidated(String phoneToSet, boolean isEditable);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

    }

    interface UserActionsListener extends RwfMobileMoneyContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();
    }
}

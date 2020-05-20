

package com.flutterwave.raveandroid.ussd;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.ussd.UssdContract;

import java.util.HashMap;

public interface UssdUiContract {

    interface View extends UssdContract.Interactor {
        void onAmountValidationSuccessful(String amountToPay);

        void showFieldError(int viewID, String message, Class<?> viewType);

        void onDataValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void onAmountValidationFailed();

    }

    interface UserActionsListener extends UssdContract.Handler {

        void init(RavePayInitializer ravePayInitializer);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void onAttachView(View view);

        void onDetachView();
    }
}

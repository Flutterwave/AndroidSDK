package com.flutterwave.raveandroid.ugmobilemoney;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.ugmobilemoney.UgMobileMoneyContract;

import java.util.HashMap;

/**
 * Created by Jeremiah on 28/06/2018.
 */

public interface UgMobileMoneyUiContract {

    interface View extends UgMobileMoneyContract.Interactor {
        void onAmountValidationSuccessful(String amountToPay);

        void onPhoneValidated(String phoneToSet, boolean isEditable);

        void showFieldError(int viewID, String message, Class<?> viewType);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);
    }

    interface UserActionsListener extends UgMobileMoneyContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();

    }
}

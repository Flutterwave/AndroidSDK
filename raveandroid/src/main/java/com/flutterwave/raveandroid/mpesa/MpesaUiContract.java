package com.flutterwave.raveandroid.mpesa;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.mpesa.MpesaContract;

import java.util.HashMap;

/**
 * Created by hfetuga on 27/06/2018.
 */

public interface MpesaUiContract {

    interface View extends MpesaContract.Interactor {
        void onAmountValidationSuccessful(String amountToPay);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void onPhoneValidated(String phoneToSet, boolean isEditable);

        void showFieldError(int viewID, String message, Class<?> viewType);
    }

    interface UserActionsListener extends MpesaContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();
    }
}

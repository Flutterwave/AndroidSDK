package com.flutterwave.raveandroid.uk;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.uk.UkContract;

import java.util.HashMap;

/**
 * Created by hfetuga on 27/06/2018.
 */

public interface UkUiContract {

    interface View extends UkContract.Interactor {

        void onAmountValidationSuccessful(String amountToPay);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showFieldError(int viewID, String message, Class<?> viewType);

    }

    interface UserActionsListener extends UkContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();

    }
}

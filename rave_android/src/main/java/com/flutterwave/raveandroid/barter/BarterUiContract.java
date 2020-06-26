package com.flutterwave.raveandroid.barter;


import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.barter.BarterContract;

import java.util.HashMap;

public interface BarterUiContract {

    interface View extends BarterContract.Interactor {

        void showFieldError(int viewID, String message, Class<?> viewType);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void onAmountValidationSuccessful(String amountToPay);

    }

    interface UserActionsListener {

        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();
    }
}

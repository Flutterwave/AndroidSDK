package com.flutterwave.raveandroid.ghmobilemoney;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.ghmobilemoney.GhMobileMoneyContract;

import java.util.HashMap;

/**
 * Created by hfetuga on 28/06/2018.
 */

public interface GhMobileMoneyUiContract {

    interface View extends GhMobileMoneyContract.Interactor {
        void showToast(String message);

        void showFetchFeeFailed(String s);

        void onPhoneValidated(String phoneToSet, boolean isEditable);

        void onAmountValidationSuccessful(String valueOf);

        void onPaymentFailed(String message, String responseAsJSONString);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

    }

    interface UserActionsListener extends GhMobileMoneyContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();

    }
}

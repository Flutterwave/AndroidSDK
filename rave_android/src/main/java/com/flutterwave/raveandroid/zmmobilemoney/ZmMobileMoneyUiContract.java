package com.flutterwave.raveandroid.zmmobilemoney;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.zmmobilemoney.ZmMobileMoneyContract;

import java.util.HashMap;

/**
 * Created by hfetuga on 28/06/2018.
 */

public interface ZmMobileMoneyUiContract {

    interface View extends ZmMobileMoneyContract.Interactor {
        void onAmountValidationSuccessful(String valueOf);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void onPhoneValidated(String phoneToSet, boolean isEditable);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showToast(String validNetworkPrompt);
    }

    interface UserActionsListener extends ZmMobileMoneyContract.Handler {
        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();
    }
}

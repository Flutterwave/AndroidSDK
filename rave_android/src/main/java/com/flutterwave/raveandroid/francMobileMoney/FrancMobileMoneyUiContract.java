package com.flutterwave.raveandroid.francMobileMoney;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_presentation.francmobilemoney.FrancMobileMoneyContract;

import java.util.HashMap;

/**
 * Created by hfetuga on 27/06/2018.
 */

public interface FrancMobileMoneyUiContract {

    interface View extends FrancMobileMoneyContract.Interactor {
        void onAmountValidationSuccessful(String amountToPay);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void onPhoneValidated(String phoneToSet, boolean isEditable);

        void showFieldError(int viewID, String message, Class<?> viewType);

    }

    interface UserActionsListener extends FrancMobileMoneyContract.Handler {

        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void onAttachView(View view);

        void onDetachView();

    }
}

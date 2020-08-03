package com.flutterwave.raveandroid.account;


import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_presentation.account.AccountContract;

import java.util.HashMap;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public interface AccountUiContract {

    interface View extends AccountContract.AccountInteractor {

        void onDataValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void showGTBankAmountIssue();

        void onEmailValidated(String emailToSet, int visibility);

        void onAmountValidated(String amountToSet, int visibility);

        void onPhoneNumberValidated(String phoneNumber, int visibility);

        void showDateOfBirth(int isVisible);

        void showBVN(int isVisible);

        void showAccountNumberField(int isVisible);
    }

    interface UserActionsListener extends AccountContract.AccountHandler {
        void onAttachView(AccountUiContract.View view);

        void onDetachView();

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void init(RavePayInitializer ravePayInitializer);

        void onBankSelected(Bank bank);
    }

}

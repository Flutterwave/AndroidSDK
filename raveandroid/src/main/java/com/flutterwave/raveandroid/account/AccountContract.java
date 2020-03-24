package com.flutterwave.raveandroid.account;


import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.Bank;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public interface AccountContract {

    interface View {
        void showToast(String message);
        void showBanks(List<Bank> banks);
        void showProgressIndicator(boolean active);

        void onGetBanksRequestFailed(String message);

        void validateAccountCharge(String pbfPubKey, String flwRef, String validateInstruction);

        void onDisplayInternetBankingPage(String authurl, String flwRef);

        void onChargeAccountFailed(String message, String responseAsJSONString);

        void onPaymentSuccessful(String status, String responseAsJSONString);

        void onPaymentFailed(String status, String responseAsJSONString);

        void onValidationSuccessful(String flwRef, String responseAsJSONString);

        void onValidateError(String message, String responseAsJSONString);

        void onPaymentError(String s);

        void displayFee(String charge_amount, Payload payload, boolean internetbanking);

        void showFetchFeeFailed(String s);

        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void showGTBankAmountIssue();

        void onEmailValidated(String emailToSet, int visibility);

        void onAmountValidated(String amountToSet, int visibility);

        void showDateOfBirth(int isVisible);

        void showBVN(int isVisible);

        void showAccountNumberField(int isVisible);
    }

    interface UserActionsListener {
        void getBanks();

        void chargeAccount(Payload body, String encryptionKey, boolean internetBanking);

        void validateAccountCharge(String flwRef, String otp, String publicKey);

        void fetchFee(Payload body, boolean internetbanking);

        void onAttachView(AccountContract.View view);

        void onDetachView();

        void verifyRequeryResponseStatus(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void init(RavePayInitializer ravePayInitializer);

        void onBankSelected(Bank bank);

        void logEvent(Event event, String publicKey);
    }

}

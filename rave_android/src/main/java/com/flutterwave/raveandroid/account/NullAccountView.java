package com.flutterwave.raveandroid.account;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hfetuga on 06/02/2018.
 */

public class NullAccountView implements AccountUiContract.View {


    @Override
    public void onBanksListRetrieved(List<Bank> banks) {

    }

    @Override
    public void onPhoneNumberValidated(String phoneNumber, int visibility) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onGetBanksRequestFailed(String message) {

    }

    @Override
    public void collectOtp(String publicKey, String flutterwaveReference, String validateInstruction) {

    }

    @Override
    public void displayInternetBankingPage(String authurl, String flwRef) {

    }

    @Override
    public void onPaymentSuccessful(String responseAsJSONString) {

    }

    @Override
    public void onPaymentFailed(String responseAsJSONString) {

    }

    @Override
    public void onPaymentError(String errorMessage) {

    }

    @Override
    public void onFeeFetchError(String errorMessage) {

    }

    @Override
    public void onTransactionFeeRetrieved(String chargeAmount, Payload payload, String fee) {

    }

    @Override
    public void onDataValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewtype) {

    }

    @Override
    public void showGTBankAmountIssue() {

    }

    @Override
    public void onEmailValidated(String emailToSet, int visibility) {

    }

    @Override
    public void onAmountValidated(String amountToSet, int visibility) {

    }

    @Override
    public void showDateOfBirth(int isVisible) {

    }

    @Override
    public void showBVN(int isVisible) {

    }

    @Override
    public void showAccountNumberField(int isVisible) {

    }
}

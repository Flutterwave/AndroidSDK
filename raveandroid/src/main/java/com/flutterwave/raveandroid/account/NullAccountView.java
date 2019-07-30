package com.flutterwave.raveandroid.account;

import android.support.v4.app.Fragment;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.responses.RequeryResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hfetuga on 06/02/2018.
 */

public class NullAccountView extends Fragment implements AccountContract.View {

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showBanks(List<Bank> banks) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onGetBanksRequestFailed(String message) {

    }

    @Override
    public void validateAccountCharge(String pbfPubKey, String flwRef, String validateInstruction) {

    }

    @Override
    public void onDisplayInternetBankingPage(String authurl, String flwRef) {

    }

    @Override
    public void onChargeAccountFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onPaymentSuccessful(String status, String responseAsJSONString) {

    }

    @Override
    public void onPaymentFailed(String status, String responseAsJSONString) {

    }

    @Override
    public void onValidationSuccessful(String flwRef, String responseAsJSONString) {

    }

    @Override
    public void onValidateError(String message, String responseAsJSONString) {

    }

    @Override
    public void onPaymentError(String s) {

    }

    @Override
    public void displayFee(String charge_amount, Payload payload, boolean internetbanking) {

    }

    @Override
    public void showFetchFeeFailed(String s) {

    }

    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString) {

    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

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
    public void showInternetBankingSelected(int isVisible) {

    }
}

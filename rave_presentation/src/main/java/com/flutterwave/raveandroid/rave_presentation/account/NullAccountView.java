package com.flutterwave.raveandroid.rave_presentation.account;

import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;

import java.util.List;

/**
 * Created by hfetuga on 06/02/2018.
 */

public class NullAccountView implements AccountContract.AccountInteractor {
    @Override
    public void onBanksListRetrieved(List<Bank> banks) {

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
}

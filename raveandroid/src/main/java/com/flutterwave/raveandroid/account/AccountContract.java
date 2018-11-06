package com.flutterwave.raveandroid.account;


import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.responses.RequeryResponse;

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

        void onValidateSuccessful(String flwRef, String responseAsJSONString);

        void onValidateError(String message, String responseAsJSONString);

        void onPaymentError(String s);

        void displayFee(String charge_amount, Payload payload, boolean internetbanking);

        void showFetchFeeFailed(String s);

        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString);
    }

    interface UserActionsListener {
        void getBanks();

        void chargeAccount(Payload body, String encryptionKey, boolean internetBanking);

        void validateAccountCharge(String flwRef, String otp, String publicKey);

        void fetchFee(Payload body, boolean internetbanking);

        void onAttachView(AccountContract.View view);

        void onDetachView();

        void verifyRequeryResponseStatus(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer);
    }

}

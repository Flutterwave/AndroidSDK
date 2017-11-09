package com.flutterwave.raveandroid.account;


import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.data.Bank;

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

        void validateAccountCharge(String pbfPubKey, String flwRef);

        void onDisplayInternetBankingPage(String authurl, String flwRef);

        void onChargeAccountFailed(String message, String responseAsJSONString);

        void onPaymentSuccessful(String status, String responseAsJSONString);

        void onPaymentFailed(String status, String responseAsJSONString);

        void onValidateSuccessful(String flwRef, String responseAsJSONString);

        void onValidateError(String message, String responseAsJSONString);

        void onPaymentError(String s);

        void displayFee(String charge_amount, Payload payload, boolean internetbanking);

        void showFetchFeeFailed(String s);
    }

    interface UserActionsListener {
        void getBanks();

        void chargeAccount(Payload body, boolean internetBanking);

        void validateAccountCharge(String flwRef, String otp, String publicKey);

        void fetchFee(Payload body, boolean internetbanking);
    }

}

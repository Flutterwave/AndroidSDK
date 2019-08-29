package com.flutterwave.raveandroid.card;


import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.GhChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.RequeryResponsev2;

import java.util.List;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class TestCallbacks {

    public interface onTestGhanaChargeRequestComplete {
        void onTestSuccess(GhChargeResponse response, String responseAsJSONString);

        void onTestError(String message, String responseAsJSONString);
    }

    public interface OnChargeRequestComplete {
        void onSuccessTest(ChargeResponse response, String responseAsJSONString);

        void onErrorTest(String message, String responseAsJSONString);
    }

    public interface OnValidateChargeCardRequestComplete {
        void onSuccessTest(ChargeResponse response, String responseAsJSONString);

        void onErrorTest(String message, String responseAsJSONString);
    }

    public interface OnRequeryRequestComplete {
        void onSuccessTest(RequeryResponse response, String responseAsJSONString);

        void onErrorTest(String message, String responseAsJSONString);
    }

    public interface OnRequeryRequestv2Complete {
        void onSuccessTest(RequeryResponsev2 response, String responseAsJSONString);

        void onErrorTest(String message, String responseAsJSONString);
    }

    public interface OnGetBanksRequestComplete {
        void onSuccessTest(List<Bank> banks);

        void onErrorTest(String message);
    }

    public interface BankSelectedListener {
        void onBankSelected(Bank b);
    }

    public interface SavedCardSelectedListener {
        void onCardSelected(SavedCard savedCard);
    }

    public interface OnGetFeeRequestComplete {
        void onSuccessTest(FeeCheckResponse response);

        void onErrorTest(String message);
    }
}

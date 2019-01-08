package com.flutterwave.raveandroid.data;


import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.GhChargeResponse;
import com.flutterwave.raveandroid.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.RequeryResponsev2;
import com.flutterwave.raveandroid.responses.SaveCardResponse;
import com.flutterwave.raveandroid.responses.SendRaveOtpResponse;

import java.util.List;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class Callbacks {

    public interface OnGhanaChargeRequestComplete {
        void onSuccess(GhChargeResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnChargeRequestComplete {
        void onSuccess(ChargeResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnValidateChargeCardRequestComplete {
        void onSuccess(ChargeResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnRequeryRequestComplete {
        void onSuccess(RequeryResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnSaveCardRequestComplete {
        void onSuccess(SaveCardResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnLookupSavedCardsRequestComplete {
        void onSuccess(LookupSavedCardsResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnSendRaveOTPRequestComplete {
        void onSuccess(SendRaveOtpResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnRequeryRequestv2Complete {
        void onSuccess(RequeryResponsev2 response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnGetBanksRequestComplete {
        void onSuccess(List<Bank> banks);
        void onError(String message);
    }

    public interface BankSelectedListener {
        void onBankSelected(Bank b);
    }

    public interface SavedCardSelectedListener {
        void onCardSelected(SavedCard savedCard);
    }

    public interface OnGetFeeRequestComplete {
        void onSuccess(FeeCheckResponse response);
        void onError(String message);
    }
}

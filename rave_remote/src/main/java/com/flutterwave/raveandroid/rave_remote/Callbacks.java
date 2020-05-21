package com.flutterwave.raveandroid.rave_remote;


import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.rave_remote.responses.MobileMoneyChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponsev2;
import com.flutterwave.raveandroid.rave_remote.responses.SaBankAccountResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SendRaveOtpResponse;

import java.util.List;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class Callbacks {

    public interface OnGhanaChargeRequestComplete {
        void onSuccess(MobileMoneyChargeResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnChargeRequestComplete {
        void onSuccess(ChargeResponse response, String responseAsJSONString);
        void onError(String message, String responseAsJSONString);
    }

    public interface OnSaChargeRequestComplete{
        void onSuccess(SaBankAccountResponse response, String responseAsJSONString);
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

    public interface OnLogEventComplete {
        void onSuccess(String response);

        void onError(String message);
    }
}

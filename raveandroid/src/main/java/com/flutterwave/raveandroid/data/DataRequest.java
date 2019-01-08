package com.flutterwave.raveandroid.data;

import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.card.ChargeRequestBody;

import java.util.List;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public interface DataRequest {

    interface NetworkRequest {
        void chargeCard(ChargeRequestBody chargeRequestBody, Callbacks.OnChargeRequestComplete callback);
        void chargeGhanaMobileMoneyWallet(ChargeRequestBody chargeRequestBody, Callbacks.OnGhanaChargeRequestComplete callback);
        void validateChargeCard(ValidateChargeBody cardRequestBody, Callbacks.OnValidateChargeCardRequestComplete callback);
        void validateAccountCard(ValidateChargeBody cardRequestBody, Callbacks.OnValidateChargeCardRequestComplete callback);

        void sendRaveOtp(SendOtpRequestBody requestBody,
                         Callbacks.OnSendRaveOTPRequestComplete callback);

        void requeryTx(RequeryRequestBody requeryRequestBody, Callbacks.OnRequeryRequestComplete callback);
        void requeryTxv2(RequeryRequestBodyv2 requeryRequestBody, Callbacks.OnRequeryRequestv2Complete callback);
        void getBanks(Callbacks.OnGetBanksRequestComplete callback);
        void chargeAccount(ChargeRequestBody accountRequestBody, Callbacks.OnChargeRequestComplete callback);
        void chargeToken(Payload payload, Callbacks.OnChargeRequestComplete callback);
        void getFee(FeeCheckRequestBody body, Callbacks.OnGetFeeRequestComplete callback);
        void saveCardToRave(SaveCardRequestBody saveCardRequestBody, Callbacks
                .OnSaveCardRequestComplete callback);
        public void lookupSavedCards(LookupSavedCardsRequestBody requestBody,
                                     final Callbacks.OnLookupSavedCardsRequestComplete callback);
    }

    interface SharedPrefsRequest {

        void saveCardToSharedPreference(List<SavedCard> card, String email);
        List<SavedCard> getSavedCards(String phoneNumber);
        void saveFlwRef(String flwRef);
        String fetchFlwRef();

        void savePhoneNumber(String phoneNumber);

        String fetchPhoneNumber();
    }
}

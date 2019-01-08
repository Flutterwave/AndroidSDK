package com.flutterwave.raveandroid.card;


import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SaveCardResponse;

import java.util.List;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public interface CardContract {

    interface View {
        void showProgressIndicator(boolean active);
        void onPaymentError(String message);

        void onPinAuthModelSuggested(Payload payload);

        void showToast(String message);

        void showOTPLayout(String flwRef, String chargeResponseMessage);

        void onValidateSuccessful(String message, String responseAsString);

        void onValidateError(String message);

        void onVBVAuthModelUsed(String authUrlCrude, String flwRef);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

        void onPaymentFailed(String status, String responseAsString);

        void onTokenRetrieved(String flwRef, String cardBIN, String token);

        void onTokenRetrievalError(String s);

        void displayFee(String charge_amount, Payload payload, int why);

        void showFetchFeeFailed(String s);

        void onChargeTokenComplete(ChargeResponse response);

        void onChargeCardSuccessful(ChargeResponse response);

        void onAVS_VBVSECURECODEModelSuggested(Payload payload);

        void onAVSVBVSecureCodeModelUsed(String authurl, String flwRef);

        void onValidateCardChargeFailed(String flwRef, String responseAsJSON);

        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef);

        void onNoAuthInternationalSuggested(Payload payload);

        void onNoAuthUsed(String flwRef, String publicKey);

        void onCardSaveSuccessful(SaveCardResponse response, String responseAsJSONString);

        void onCardSaveFailed(String message, String responseAsJSONString);

        void onLookupSavedCardsSuccessful(LookupSavedCardsResponse response, String responseAsJSONString, String verifyResponseAsJSONString);

        void onLookupSavedCardsFailed(String message, String responseAsJSONString, String verifyResponseAsJSONString);


        void setSavedCards(List<SavedCard> savedCards);

        void setPhoneNumber(String phoneNumber);

        void showOTPLayoutForSavedCard(Payload payload, String authInstruction);

        void onSendRaveOtpFailed(String message, String responseAsJSONString);
    }

    interface UserActionsListener {
        void chargeCard(Payload payload, String encryptionKey);

        void chargeCardWithSuggestedAuthModel(Payload payload, String zipOrPin, String authModel, String encryptionKey);

        void validateCardCharge(String flwRef, String otp, String publicKey);

        void requeryTx(String flwRef, String publicKey);

        void saveCardToRave(String phoneNumber, String email, String FlwRef, String publicKey, String deviceFingerprint, String verifyResponse);

        void chargeToken(Payload payload);

        void fetchFee(Payload payload, int reason);

        void retrieveSavedCardsFromMemory(String email);

        void onAttachView(CardContract.View view);

        void onDetachView();

        void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef);

        void sendRaveOTP(Payload payload);

        void chargeCardWithAVSModel(Payload payload, String address, String city, String zipCode,
                                    String country, String state, String avsVbvsecurecode, String encryptionKey);

        void lookupSavedCards(String publicKey, String phoneNumber, String verifyResponseAsJSONString);

        void saveCardToSharedPreferences(LookupSavedCardsResponse response);

        void chargeSavedCard(Payload payload, String encryptionKey);

        void retrievePhoneNumberFromMemory();
    }

}

package com.flutterwave.raveandroid.card;


import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;

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

        void showSavedCards(List<SavedCard> cards);

        void onTokenRetrieved(String flwRef, String cardBIN, String token);

        void onTokenRetrievalError(String s);

        void displayFee(String charge_amount, Payload payload, int why);

        void showFetchFeeFailed(String s);

        void hideSavedCardsButton();

        void onChargeTokenComplete(ChargeResponse response);

        void onChargeCardSuccessful(ChargeResponse response);

        void onAVS_VBVSECURECODEModelSuggested(Payload payload);

        void onAVSVBVSecureCodeModelUsed(String authurl, String flwRef);

        void onValidateCardChargeFailed(String flwRef, String responseAsJSON);

        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef);

        void onNoAuthInternationalSuggested(Payload payload);

        void onNoAuthUsed(String flwRef, String publicKey);
    }

    interface UserActionsListener {
        void chargeCard(Payload payload, String encryptionKey);

        void chargeCardWithSuggestedAuthModel(Payload payload, String zipOrPin, String authModel, String encryptionKey);

        void validateCardCharge(String flwRef, String otp, String publicKey);

        void requeryTx(String flwRef, String publicKey, boolean shouldISaveCard);

        void savePotentialCardDets(String cardFirst6, String cardLast4);

        void onSavedCardsClicked(String email);

        void chargeToken(Payload payload);

        void fetchFee(Payload payload, int reason);

        void checkForSavedCards(String email);

        void onAttachView(CardContract.View view);

        void onDetachView();

        void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef);

        void chargeCardWithAVSModel(Payload payload, String address, String city, String zipCode,
                                    String country, String state, String avsVbvsecurecode, String encryptionKey);
    }

}

package com.flutterwave.raveandroid.card;


import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public interface CardContract {

    interface View {
        void showProgressIndicator(boolean active);

        void onPaymentError(String message);

        void showToast(String message);

        void onValidateError(String message);

        void showFetchFeeFailed(String s);

        void onTokenRetrievalError(String s);

        void onEmailValidated(String emailToSet, int visibility);

        void onAmountValidated(String amountToSet, int visibility);

        void showSavedCards(List<SavedCard> cards);

        void onPinAuthModelSuggested(Payload payload);

        void onChargeTokenComplete(ChargeResponse response);

        void onChargeCardSuccessful(ChargeResponse response);

        void onAVS_VBVSECURECODEModelSuggested(Payload payload);

        void onVBVAuthModelUsed(String authUrlCrude, String flwRef);

        void showOTPLayout(String flwRef, String chargeResponseMessage);

        void onValidateSuccessful(String message, String responseAsString);

        void displayFee(String charge_amount, Payload payload, int why);

        void onPaymentFailed(String status, String responseAsString);

        void onTokenRetrieved(String flwRef, String cardBIN, String token);

        void onAVSVBVSecureCodeModelUsed(String authurl, String flwRef);

        void onValidateCardChargeFailed(String flwRef, String responseAsJSON);

        void onNoAuthInternationalSuggested(Payload payload);

        void onNoAuthUsed(String flwRef, String publicKey);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString);

        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef);
    }

    interface UserActionsListener {

        void onDetachView();

        void chargeToken(Payload payload);

        void fetchFee(Payload payload, int reason);

        void onAttachView(CardContract.View view);

        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void chargeCard(Payload payload, String encryptionKey);

        void validateCardCharge(String flwRef, String otp, String publicKey);

        void requeryTx(String flwRef, String publicKey, boolean shouldISaveCard);

        void chargeCardWithSuggestedAuthModel(Payload payload, String zipOrPin, String authModel, String encryptionKey);

        void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef);

        void chargeCardWithAVSModel(Payload payload, String address, String city, String zipCode,
                                    String country, String state, String avsVbvsecurecode, String encryptionKey);

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void logEvent(Event event, String publicKey);



    }

}

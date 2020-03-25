package com.flutterwave.raveandroid.card;


import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

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

        void onValidateSuccessful(String message);

        void displayFee(String charge_amount, Payload payload, int why);

        void onPaymentFailed(String status, String responseAsString);

        void onTokenRetrieved(String flwRef, String cardBIN, String token);

        void onValidateCardChargeFailed(String flwRef);

        void onNoAuthInternationalSuggested(Payload payload);

        void onNoAuthUsed(String flwRef, String publicKey);

        void onCardSaveSuccessful(SaveCardResponse response, String responseAsJSONString, String phoneNumber);

        void onCardSaveFailed(String message, String responseAsJSONString);

        void onLookupSavedCardsSuccessful(LookupSavedCardsResponse response, String verifyResponseAsJSONString);

        void onLookupSavedCardsFailed(String message, String verifyResponseAsJSONString);

        void showOTPLayoutForSavedCard(Payload payload, String authInstruction);

        void onSendRaveOtpFailed(String message);

        void showSavedCardsLayout(List<SavedCard> savedCardsList);

        void setHasSavedCards(boolean b);

        void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap);

        void showFieldError(int viewID, String message, Class<?> viewtype);

        void onPaymentSuccessful(String status, String flwRef, String responseAsString, RavePayInitializer ravePayInitializer);

        void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef);

        void onPhoneNumberValidated(String phoneNumber);

        void showCardSavingOption(boolean b);
    }

    interface UserActionsListener {

        void onDetachView();

        void saveCardToRave(String phoneNumber, String email, String FlwRef, String publicKey, String verifyResponse);

        void chargeToken(Payload payload);

        void fetchFee(Payload payload, int reason);

        void retrieveSavedCardsFromMemory(String email, String publicKey);

        void onAttachView(CardContract.View view);

        void init(RavePayInitializer ravePayInitializer);

        void onDataCollected(HashMap<String, ViewObject> dataHashMap);

        void chargeCard(Payload payload, String encryptionKey);

        void validateCardCharge(String flwRef, String otp, String publicKey);

        void requeryTx(String flwRef, String publicKey);

        void chargeCardWithSuggestedAuthModel(Payload payload, String zipOrPin, String authModel, String encryptionKey);

        void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef);

        void sendRaveOTP(Payload payload);

        void chargeCardWithAVSModel(Payload payload, String address, String city, String zipCode,
                                    String country, String state, String avsVbvsecurecode, String encryptionKey);

        void lookupSavedCards(String publicKey, String phoneNumber, String verifyResponseAsJSONString);

        void saveCardToSharedPreferences(LookupSavedCardsResponse response, String publicKey);

        void chargeSavedCard(Payload payload, String encryptionKey);

        void checkForSavedCardsInMemory(RavePayInitializer ravePayInitializer);

        List<SavedCard> getSavedCards();

        void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void logEvent(Event event, String publicKey);

        void onDataForSavedCardChargeCollected(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer);

        void processSavedCardTransaction(SavedCard savedCard, RavePayInitializer ravePayInitializer);
    }

}

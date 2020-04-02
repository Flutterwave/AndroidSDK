package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

class CardInteractorImpl implements CardContract.CardInteractor {

    private CardPaymentCallback callback;
    private String flwRef;
    private String authModel;
    private Payload payload;


    CardInteractorImpl(CardPaymentCallback callback) {
        this.callback = callback;
    }

    @Override
    public void showProgressIndicator(boolean active) {
        callback.showProgressIndicator(active);
    }

    @Override
    public void collectCardPin(Payload payload) {
        this.payload = payload;
        callback.collectCardPin();
    }

    @Override
    public void collectOtp(String flwRef, String message) {
        this.flwRef = flwRef;
        callback.collectOtp(message);
    }

    @Override
    public void collectCardAddressDetails(Payload payload, String authModel) {
        this.payload = payload;
        this.authModel = authModel;
        callback.collectAddress();
    }

    @Override
    public void showWebPage(String authenticationUrl, String flwRef) {
        this.flwRef = flwRef;
        callback.showAuthenticationWebPage(authenticationUrl);
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsJSONString) {
        callback.onSuccessful(flwRef);
    }

    @Override
    public void onPaymentFailed(String status, String responseAsJsonString) {
        try {
            Type type = new TypeToken<JsonObject>() {
            }.getType();
            JsonObject responseJson = new Gson().fromJson(responseAsJsonString, type);
            flwRef = responseJson.getAsJsonObject("data").get("flwref").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        callback.onError("Transaction Failed", flwRef);
    }

    @Override
    public void onPaymentError(String errorMessage) {
        callback.onError(errorMessage, null);
    }

    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {

    }

    @Override
    public void onSavedCardsLookupFailed(String message) {

    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, Payload payload) {

    }

    @Override
    public void onFetchFeeError(String errorMessage) {

    }

    @Override
    public void collectOtpForSaveCardCharge(Payload payload) {

    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber) {

    }

    @Override
    public void onCardSaveFailed(String message) {

    }

    public String getFlwRef() {
        return flwRef;
    }

    public Payload getPayload() {
        return payload;
    }

    public String getAuthModel() {
        return authModel;
    }
}

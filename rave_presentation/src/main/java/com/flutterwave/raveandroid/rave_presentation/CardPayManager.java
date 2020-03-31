package com.flutterwave.raveandroid.rave_presentation;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.card.CardContract;
import com.flutterwave.raveandroid.rave_presentation.card.CardPresenter;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.di.CardModule;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.List;

import javax.inject.Inject;

public class CardPayManager implements CardContract.View {

    @Inject
    public CardPresenter cardPresenter;
    private CardPaymentCallback cardPaymentCallback;


    public CardPayManager(RaveComponent raveComponent, CardPaymentCallback cardPaymentCallback){
        this.cardPaymentCallback = cardPaymentCallback;
        raveComponent.plus(new CardModule(this))
                .inject(this);
    }

    public void chargeCard(Payload payload, String encryptionKey){
        cardPresenter.chargeCard(payload, encryptionKey);
    }


    @Override
    public void showProgressIndicator(boolean active) {
        cardPaymentCallback.showProgressIndicator(active);
    }

    @Override
    public void collectCardPin(Payload payload) {
        cardPaymentCallback.collectCardPin(payload);
    }

    @Override
    public void collectOtp(String flwRef, String message) {
        cardPaymentCallback.collectOtp(flwRef, message);
    }

    @Override
    public void onPaymentError(String errorMessage) {
        cardPaymentCallback.onError(errorMessage);
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsJSONString) {
        cardPaymentCallback.onSuccessful(flwRef);
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
    public void collectCardAddressDetails(Payload payload, String authModel) {

    }

    @Override
    public void showWebPage(String authenticationUrl, String flwRef) {

    }

    @Override
    public void onPaymentFailed(String status, String responseAsJsonString) {

    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber) {

    }

    @Override
    public void onCardSaveFailed(String message) {

    }
}

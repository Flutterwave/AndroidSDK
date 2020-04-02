package com.flutterwave.raveandroid.rave_presentation.card;

import android.view.View;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.List;

/**
 * Created by hfetuga on 06/02/2018.
 */

public class NullCardInteractor implements View.OnClickListener, CardContract.CardInteractor {

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber) {

    }

    @Override
    public void onCardSaveFailed(String message) {

    }

    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {

    }

    @Override
    public void onSavedCardsLookupFailed(String message) {

    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {

    }

    @Override
    public void onPaymentError(String errorMessage) {

    }

    @Override
    public void collectCardPin(Payload payload) {

    }

    @Override
    public void collectOtp(String flwRef, String message) {

    }

    @Override
    public void showWebPage(String authenticationUrl, String flwRef) {

    }

    @Override
    public void onPaymentFailed(String status, String responseAsJsonString) {

    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, Payload payload) {

    }

    @Override
    public void onFetchFeeError(String errorMessage) {

    }


    @Override
    public void collectCardAddressDetails(Payload payload, String authModel) {

    }

    @Override
    public void collectOtpForSaveCardCharge(Payload payload) {

    }
}

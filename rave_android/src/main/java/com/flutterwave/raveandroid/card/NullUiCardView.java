package com.flutterwave.raveandroid.card;

import android.view.View;

import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hfetuga on 06/02/2018.
 */

public class NullUiCardView implements View.OnClickListener, CardUiContract.View {

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onSavedCardRemoveSuccessful() {

    }

    @Override
    public void onSavedCardRemoveFailed(String message) {

    }

    @Override
    public void setHasSavedCards(boolean b, List<SavedCard> savedCards) {

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
    public void setSavedCardsLayoutVisibility(boolean showPhoneEmailFields) {

    }

    @Override
    public void showSavedCardsLayout(List<SavedCard> savedCardsList) {

    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewtype) {

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
    public void onEmailValidated(String emailToSet, int visibility) {

    }

    @Override
    public void onAmountValidated(String amountToSet, int visibility) {

    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, Payload payload, String fee) {

    }

    @Override
    public void onFetchFeeError(String errorMessage) {

    }

    @Override
    public void collectCardAddressDetails(Payload payload, String authModel) {

    }

    @Override
    public void onPhoneNumberValidated(String phoneNumber) {

    }

    @Override
    public void showCardSavingOption(boolean b) {

    }

    @Override
    public void collectOtpForSaveCardCharge(Payload payload) {

    }
}

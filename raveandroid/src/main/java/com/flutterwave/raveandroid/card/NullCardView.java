package com.flutterwave.raveandroid.card;

import android.support.v4.app.Fragment;
import android.view.View;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SaveCardResponse;

import java.util.List;

/**
 * Created by hfetuga on 06/02/2018.
 */

public class NullCardView extends Fragment implements View.OnClickListener, CardContract.View {

    @Override
    public void onClick(View v) {

    }

    @Override
    public void showProgressIndicator(boolean active) {

    }

    @Override
    public void onNoAuthUsed(String flwRef, String secretKey) {

    }

    @Override
    public void onCardSaveFailed(String message, String responseAsJSONString) {

    }

    @Override
    public void onLookupSavedCardsSuccessful(LookupSavedCardsResponse response, String responseAsJSONString, String verifyResponseAsJSONString) {

    }

    @Override
    public void onLookupSavedCardsFailed(String message, String responseAsJSONString, String verifyResponseAsJSONString) {

    }

    @Override
    public void setPhoneNumber(String phoneNumber) {

    }

    @Override
    public void showOTPLayoutForSavedCard(Payload payload, String authInstruction) {

    }

    @Override
    public void onSendRaveOtpFailed(String message, String responseAsJSONString) {

    }

    @Override
    public String getPhoneNumber() {
        return null;
    }

    @Override
    public void showSavedCardsLayout(List<SavedCard> savedCardsList) {

    }

    @Override
    public void setHasSavedCards(boolean b) {

    }

    @Override
    public void onNoAuthInternationalSuggested(Payload payload) {

    }

    @Override
    public void onPaymentError(String message) {

    }

    @Override
    public void onPinAuthModelSuggested(Payload payload) {

    }

    @Override
    public void showToast(String message) {

    }

    @Override
    public void showOTPLayout(String flwRef, String chargeResponseMessage) {

    }

    @Override
    public void onValidateSuccessful(String message, String responseAsString) {

    }

    @Override
    public void onValidateError(String message) {

    }

    @Override
    public void onVBVAuthModelUsed(String authUrlCrude, String flwRef) {

    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {

    }

    @Override
    public void onPaymentFailed(String status, String responseAsString) {

    }



    @Override
    public void onTokenRetrieved(String flwRef, String cardBIN, String token) {

    }

    @Override
    public void onTokenRetrievalError(String s) {

    }

    @Override
    public void displayFee(String charge_amount, Payload payload, int why) {

    }

    @Override
    public void showFetchFeeFailed(String s) {

    }

    @Override
    public void onChargeTokenComplete(ChargeResponse response) {

    }

    @Override
    public void onChargeCardSuccessful(ChargeResponse response) {

    }

    @Override
    public void onAVS_VBVSECURECODEModelSuggested(Payload payload) {

    }

    @Override
    public void onAVSVBVSecureCodeModelUsed(String authurl, String flwRef) {

    }

    @Override
    public void onValidateCardChargeFailed(String flwRef, String responseAsJSON) {

    }

    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef) {

    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String responseAsJSONString) {

    }
}

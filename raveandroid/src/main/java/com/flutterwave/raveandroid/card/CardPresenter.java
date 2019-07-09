package com.flutterwave.raveandroid.card;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.CardDetsToSave;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.data.SharedPrefsRequestImpl;
import com.flutterwave.raveandroid.data.ValidateChargeBody;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CardNoStrippedValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;

import java.util.HashMap;
import java.util.List;

import okhttp3.internal.Util;

import static com.flutterwave.raveandroid.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.RaveConstants.PIN;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class CardPresenter implements CardContract.UserActionsListener {
    private Context context;
    private CardContract.View mView;
    TextInputEditText amountEt;
    TextInputEditText emailEt;
    TextInputEditText cardNoTv;
    TextInputEditText cardExpiryTv;
    TextInputEditText cvvTv;
    TextInputLayout amountTil;
    TextInputLayout emailTil;
    TextInputLayout cardNoTil;
    TextInputLayout cardExpiryTil;
    TextInputLayout cvvTil;
    Button payButton;

    public CardPresenter(Context context, CardContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void chargeCard(final Payload payload, final String encryptionKey) {

        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = Utils.getEncryptedData(cardRequestBodyAsString, encryptionKey);

        final ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().chargeCard(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {

                    if (response.getData().getSuggested_auth() != null) {
                        String suggested_auth = response.getData().getSuggested_auth();


                        if (suggested_auth.equals(RaveConstants.PIN)) {
                            mView.onPinAuthModelSuggested(payload);
                        }
                        else if (suggested_auth.equals(AVS_VBVSECURECODE)) { //address verification then verification by visa
                            mView.onAVS_VBVSECURECODEModelSuggested(payload);
                        }
                        else if (suggested_auth.equalsIgnoreCase(RaveConstants.NOAUTH_INTERNATIONAL)) {
                            mView.onNoAuthInternationalSuggested(payload);
                        }
                        else {
                            mView.onPaymentError("Unknown auth model");
                        }
                    }
                    else {
                        String authModelUsed = response.getData().getAuthModelUsed();

                        if (authModelUsed != null) {

                            if (authModelUsed.equalsIgnoreCase(RaveConstants.VBV)) {
                                String authUrlCrude = response.getData().getAuthurl();
                                String flwRef = response.getData().getFlwRef();

                                mView.onVBVAuthModelUsed(authUrlCrude, flwRef);
                            }
                            else if (authModelUsed.equalsIgnoreCase(RaveConstants.GTB_OTP)
                                    ||  authModelUsed.equalsIgnoreCase(RaveConstants.ACCESS_OTP)
                                    || authModelUsed.toLowerCase().contains("otp")) {
                                String flwRef = response.getData().getFlwRef();
                                String chargeResponseMessage = response.getData().getChargeResponseMessage();
                                chargeResponseMessage = chargeResponseMessage == null ? "Enter your one  time password (OTP)" : chargeResponseMessage;
                                mView.showOTPLayout(flwRef, chargeResponseMessage);
                            }
                            else if (authModelUsed.equalsIgnoreCase(RaveConstants.NOAUTH)) {
                                String flwRef = response.getData().getFlwRef();

                                mView.onNoAuthUsed(flwRef, payload.getPBFPubKey());
                            }
                        }
                    }
                }
                else {
                    mView.onPaymentError("No response data was returned");
                }

            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });
    }

    @Override
    public void chargeCardWithAVSModel(Payload payload, String address, String city, String zipCode, String country, String state, String authModel, String encryptionKey) {

        payload.setSuggestedAuth(authModel);
        payload.setBillingaddress(address);
        payload.setBillingcity(city);
        payload.setBillingzip(zipCode);
        payload.setBillingcountry(country);
        payload.setBillingstate(state);

        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = Utils.getEncryptedData(cardRequestBodyAsString, encryptionKey).trim().replaceAll("\\n", "");

//        Log.d("encrypted", encryptedCardRequestBody);

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().chargeCard(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null && response.getData().getChargeResponseCode() != null) {
                    String chargeResponseCode = response.getData().getChargeResponseCode();

                    if (chargeResponseCode.equalsIgnoreCase("00")) {
//                        mView.showToast("Payment successful");
                        mView.onChargeCardSuccessful(response);
                    }
                    else if (chargeResponseCode.equalsIgnoreCase("02")) {
                        String authModelUsed = response.getData().getAuthModelUsed();
                        if (authModelUsed.equalsIgnoreCase(RaveConstants.PIN)) {
                            String flwRef = response.getData().getFlwRef();
                            String chargeResponseMessage = response.getData().getChargeResponseMessage();
                            chargeResponseMessage = (chargeResponseMessage == null || chargeResponseMessage.length() == 0) ? "Enter your one  time password (OTP)" : chargeResponseMessage;
                            mView.showOTPLayout(flwRef, chargeResponseMessage);
                        }
                        else if (authModelUsed.equalsIgnoreCase(RaveConstants.VBV)){
                            String flwRef = response.getData().getFlwRef();
                            mView.onAVSVBVSecureCodeModelUsed(response.getData().getAuthurl(), flwRef);
                        }
                        else {
                            mView.onPaymentError("Unknown Auth Model");
                        }
                    }
                    else {
                        mView.onPaymentError("Unknown charge response code");
                    }
                }
                else {
                    mView.onPaymentError("Invalid charge response code");
                }

            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });

    }

    @Override
    public void validate(HashMap<String, ViewObject> dataHashMap) {

       Boolean valid = true;

        int amountID = dataHashMap.get("amount").getViewId();
        String amount = dataHashMap.get("amount").getData();
        Class amountViewType = dataHashMap.get("amount").getViewType();

        int emailID = dataHashMap.get("email").getViewId();
        String email = dataHashMap.get("email").getData();
        Class emailViewType = dataHashMap.get("email").getViewType();

        int cvvID = dataHashMap.get("cvv").getViewId();
        String cvv = dataHashMap.get("cvv").getData();
        Class cvvViewType = dataHashMap.get("cvv").getViewType();

        int cardExpiryID = dataHashMap.get("cardExpiry").getViewId();
        String cardExpiry = dataHashMap.get("cardExpiry").getData();
        Class cardExpiryViewType = dataHashMap.get("cardExpiry").getViewType();

        int cardNoStrippedID = dataHashMap.get("cardNoStripped").getViewId();
        String cardNoStripped = dataHashMap.get("cardNoStripped").getData();
        Class cardNoStrippedViewType = dataHashMap.get("cardNoStripped").getViewType();

        try{

              Boolean isAmountValidated = new AmountValidator().check(amount);
                 if (!isAmountValidated) {
                        valid = false; mView.showFieldError(amountID, "Enter a valid amount", amountViewType);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    valid = false; mView.showFieldError(amountID, "Enter a valid amount", amountViewType);
                }

                Boolean isEmailValidated = new EmailValidator().check(email);
                if (!isEmailValidated) {
                    valid = false; mView.showFieldError(emailID, "Enter a valid email", emailViewType);
                }

                Boolean isCVVValidated = new CvvValidator().check(cvv);
                if (!isCVVValidated) {
                    valid = false; mView.showFieldError(cvvID, "Enter a valid cvv", cvvViewType);
                }

                Boolean isCardExpiryValidated = new CardExpiryValidator().check(cardExpiry);

                if (!isCardExpiryValidated) {
                    valid = false;  mView.showFieldError(cardExpiryID, "Enter a valid expiry date", cardExpiryViewType);
                }

                Boolean isCardNoStrippedValidator = new CardNoStrippedValidator().check(cardNoStripped);
                if (!isCardNoStrippedValidator) {
                    valid = false; mView.showFieldError(cardNoStrippedID, "Enter a valid credit card number", cardNoStrippedViewType);
                }

                if (valid) {
                    mView.onValidationSuccessful(dataHashMap);
                }

    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer, Activity activity) {

        String amount = dataHashMap.get("amount").getData();
        String email = dataHashMap.get("email").getData();
        String cvv = dataHashMap.get("cvv").getData();
        String cardExpiry = dataHashMap.get("cardExpiry").getData();
        String cardNoStripped = dataHashMap.get("cardNoStripped").getData();

        String txRef = ravePayInitializer.getTxRef();
        Log.d("txRef", txRef);

        ravePayInitializer.setAmount(Double.parseDouble(amount));

        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(ravePayInitializer.getAmount() + "").setCardno(cardNoStripped)
                .setCountry(ravePayInitializer.getCountry()).setCurrency(ravePayInitializer.getCurrency())
                .setCvv(cvv).setEmail(email).setFirstname(ravePayInitializer.getfName())
                .setLastname(ravePayInitializer.getlName()).setIP(Utils.getDeviceImei(activity)).setTxRef(ravePayInitializer.getTxRef())
                .setExpiryyear(cardExpiry.substring(3, 5)).setExpirymonth(cardExpiry.substring(0, 2))
                .setMeta(ravePayInitializer.getMeta())
                .setSubAccount(ravePayInitializer.getSubAccount())
                .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                .setPBFPubKey(ravePayInitializer.getPublicKey()).setDevice_fingerprint(Utils.getDeviceImei(activity));

        if (ravePayInitializer.getPayment_plan() != null) {
            builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
        }

        Payload body = builder.createPayload();

        if (ravePayInitializer.getIsDisplayFee()) {
            fetchFee(body, RaveConstants.MANUAL_CARD_CHARGE);
        } else {
            chargeCard(body, ravePayInitializer.getEncryptionKey());
        }
    }


    @Override
    public void chargeCardWithSuggestedAuthModel(Payload payload, String zipOrPin, String authModel, String encryptionKey) {

        if (authModel.equalsIgnoreCase(AVS_VBVSECURECODE)) {
            payload.setBillingzip(zipOrPin);
        }
        else if (authModel.equalsIgnoreCase(PIN)){
            payload.setPin(zipOrPin);
        }

        payload.setSuggestedAuth(authModel);

        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = Utils.getEncryptedData(cardRequestBodyAsString, encryptionKey).trim().replaceAll("\\n", "");

//        Log.d("encrypted", encryptedCardRequestBody);

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().chargeCard(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null && response.getData().getChargeResponseCode() != null) {
                    String chargeResponseCode = response.getData().getChargeResponseCode();

                    if (chargeResponseCode.equalsIgnoreCase("00")) {
//                        mView.showToast("Payment successful");
                        mView.onChargeCardSuccessful(response);
                    }
                    else if (chargeResponseCode.equalsIgnoreCase("02")) {
                        String authModelUsed = response.getData().getAuthModelUsed();
                        if (authModelUsed.equalsIgnoreCase(RaveConstants.PIN)) {
                            String flwRef = response.getData().getFlwRef();
                            String chargeResponseMessage = response.getData().getChargeResponseMessage();
                            chargeResponseMessage = (chargeResponseMessage == null || chargeResponseMessage.length() == 0) ? "Enter your one  time password (OTP)" : chargeResponseMessage;
                            mView.showOTPLayout(flwRef, chargeResponseMessage);
                        }
                        else if (authModelUsed.equalsIgnoreCase(RaveConstants.AVS_VBVSECURECODE)){
                            String flwRef = response.getData().getFlwRef();
                            mView.onAVSVBVSecureCodeModelUsed(response.getData().getAuthurl(), flwRef);
                        }
                        else {
                            mView.onPaymentError("Unknown Auth Model");
                        }
                    }
                    else {
                        mView.onPaymentError("Unknown charge response code");
                    }
                }
                else {
                    mView.onPaymentError("Invalid charge response code");
                }

            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });

    }

    @Override
    public void validateCardCharge(final String flwRef, String otp, String PBFPubKey) {

        ValidateChargeBody body = new ValidateChargeBody();
        body.setPBFPubKey(PBFPubKey);
        body.setOtp(otp);
        body.setTransaction_reference(flwRef);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().validateChargeCard(body, new Callbacks.OnValidateChargeCardRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);

                if (response.getStatus() != null) {
                    String status = response.getStatus();
                    String message = response.getMessage();

                    if (status.equalsIgnoreCase("success")) {
                        mView.onValidateSuccessful(status, responseAsJSONString);
                    }
                    else {
                        mView.onValidateError(message);
                    }
                }
                else {
                    mView.onValidateCardChargeFailed(flwRef, responseAsJSONString);
                }
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });

    }

    @Override
    public void requeryTx(final String flwRef, final String publicKey, final boolean shouldISaveCard) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onRequerySuccessful(response, responseAsJSONString, flwRef);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef) {

        boolean wasTxSuccessful = Utils.wasTxSuccessful(ravePayInitializer, responseAsJSONString);

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(response.getStatus(), flwRef, responseAsJSONString);
        }
        else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void savePotentialCardDets(String cardFirst6, String cardLast4) {
        new SharedPrefsRequestImpl(context).saveCardDetsToSave(new CardDetsToSave(cardFirst6, cardLast4));
    }

    @Override
    public void onSavedCardsClicked(String email) {

        SharedPrefsRequestImpl sharedMgr = new SharedPrefsRequestImpl(context);

        List<SavedCard> cards = sharedMgr.getSavedCards(email);

        mView.showSavedCards(cards);

    }

    @Override
    public void fetchFee(final Payload payload, final int reason) {

        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPBFPubKey(payload.getPBFPubKey());

        if (payload.getCardno() == null || payload.getCardno().length() == 0) {
            body.setCard6(payload.getCardBIN());
        }
        else  {
            body.setCard6(payload.getCardno().substring(0, 6));
        }

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().getFee(body, new Callbacks.OnGetFeeRequestComplete() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload, reason);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    mView.showFetchFeeFailed("An error occurred while retrieving transaction fee");
                }
             }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RaveConstants.RAVEPAY, message);
                mView.showFetchFeeFailed("An error occurred while retrieving transaction fee");
            }
        });

    }

    @Override
    public void checkForSavedCards(String email) {
        SharedPrefsRequestImpl sharedMgr = new SharedPrefsRequestImpl(context);

        List<SavedCard> cards = sharedMgr.getSavedCards(email);

        if (cards == null || cards.size() == 0) {
            mView.hideSavedCardsButton();
        }
    }

    @Override
    public void chargeToken(Payload payload) {

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().chargeToken(payload, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);
                mView.onChargeTokenComplete(response);

            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);


                if (responseAsJSONString.contains("token not found")) {
                    mView.onPaymentError("Token not found");
                }
                else if (responseAsJSONString.contains("expired")) {
                    mView.onPaymentError("Token expired");
                }
                else {
                    mView.onPaymentError(message);
                }


            }
        });

    }

    @Override
    public void onDetachView() {
        this.mView = new NullCardView();
    }

    @Override
    public void onAttachView(CardContract.View view) {
        this.mView = view;
    }
}

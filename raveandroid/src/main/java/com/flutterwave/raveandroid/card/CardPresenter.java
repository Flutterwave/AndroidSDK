package com.flutterwave.raveandroid.card;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;


import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.LookupSavedCardsRequestBody;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.SaveCardRequestBody;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.data.SendOtpRequestBody;
import com.flutterwave.raveandroid.data.SharedPrefsRequestImpl;
import com.flutterwave.raveandroid.data.ValidateChargeBody;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SaveCardResponse;
import com.flutterwave.raveandroid.responses.SendRaveOtpResponse;

import java.util.ArrayList;
import java.util.List;

import static com.flutterwave.raveandroid.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.RaveConstants.PIN;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class CardPresenter implements CardContract.UserActionsListener {
    private Context context;
    private CardContract.View mView;
    private boolean cardSaveInProgress = false;
    SharedPrefsRequestImpl sharedManager;

    public boolean isCardSaveInProgress() {
        return cardSaveInProgress;
    }

    public void setCardSaveInProgress(boolean cardSaveInProgress) {
        this.cardSaveInProgress = cardSaveInProgress;
    }


    public CardPresenter(Context context, CardContract.View mView) {
        this.context = context;
        this.mView = mView;
        sharedManager = new SharedPrefsRequestImpl(context);
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
                        } else if (suggested_auth.equals(AVS_VBVSECURECODE)) { //address verification then verification by visa
                            mView.onAVS_VBVSECURECODEModelSuggested(payload);
                        } else if (suggested_auth.equalsIgnoreCase(RaveConstants.NOAUTH_INTERNATIONAL)) {
                            mView.onNoAuthInternationalSuggested(payload);
                        } else {
                            mView.onPaymentError("Unknown auth model");
                        }
                    } else {
                        String authModelUsed = response.getData().getAuthModelUsed();

                        if (authModelUsed != null) {

                            if (authModelUsed.equalsIgnoreCase(RaveConstants.VBV)) {
                                String authUrlCrude = response.getData().getAuthurl();
                                String flwRef = response.getData().getFlwRef();

                                mView.onVBVAuthModelUsed(authUrlCrude, flwRef);
                            } else if (authModelUsed.equalsIgnoreCase(RaveConstants.GTB_OTP)) {
                                String flwRef = response.getData().getFlwRef();
                                String chargeResponseMessage = response.getData().getChargeResponseMessage();
                                chargeResponseMessage = chargeResponseMessage == null ? "Enter your one time password (OTP)" : chargeResponseMessage;
                                mView.showOTPLayout(flwRef, chargeResponseMessage);
                            } else if (authModelUsed.equalsIgnoreCase(RaveConstants.NOAUTH)) {
                                String flwRef = response.getData().getFlwRef();

                                mView.onNoAuthUsed(flwRef, payload.getPBFPubKey());
                            }
                        }
                    }
                } else {
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
    public void chargeSavedCard(Payload payload, String encryptionKey) {


        if (payload.getOtp() == null || payload.getOtp() == "") {
            sendRaveOTP(payload);
        } else {
            // Charge saved card
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
                        Log.d("Saved card charge",responseAsJSONString);
                        mView.onChargeCardSuccessful(response);

                    } else {
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


    }

    @Override
    public void sendRaveOTP(final Payload payload) {
        SendOtpRequestBody body = new SendOtpRequestBody();
        body.setDevice_key(payload.getPhonenumber());
        body.setPublic_key(payload.getPBFPubKey());
        body.setCard_hash(payload.getCard_hash());

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().sendRaveOtp(body, new Callbacks.OnSendRaveOTPRequestComplete() {
            @Override
            public void onSuccess(SendRaveOtpResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                String authInstruction = "Enter the one time password (OTP) sent to " +
                Utils.obfuscatePhoneNumber(payload
                        .getPhonenumber());
                mView.showOTPLayoutForSavedCard(payload, authInstruction);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onSendRaveOtpFailed(message, responseAsJSONString);
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
                    } else if (chargeResponseCode.equalsIgnoreCase("02")) {
                        String authModelUsed = response.getData().getAuthModelUsed();
                        if (authModelUsed.equalsIgnoreCase(RaveConstants.PIN)) {
                            String flwRef = response.getData().getFlwRef();
                            String chargeResponseMessage = response.getData().getChargeResponseMessage();
                            chargeResponseMessage = (chargeResponseMessage == null || chargeResponseMessage.length() == 0) ? "Enter your one  time password (OTP)" : chargeResponseMessage;
                            mView.showOTPLayout(flwRef, chargeResponseMessage);
                        } else if (authModelUsed.equalsIgnoreCase(RaveConstants.VBV)) {
                            String flwRef = response.getData().getFlwRef();
                            mView.onAVSVBVSecureCodeModelUsed(response.getData().getAuthurl(), flwRef);
                        } else {
                            mView.onPaymentError("Unknown Auth Model");
                        }
                    } else {
                        mView.onPaymentError("Unknown charge response code");
                    }
                } else {
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
    public void chargeCardWithSuggestedAuthModel(Payload payload, String zipOrPin, String authModel, String encryptionKey) {

        if (authModel.equalsIgnoreCase(AVS_VBVSECURECODE)) {
            payload.setBillingzip(zipOrPin);
        } else if (authModel.equalsIgnoreCase(PIN)) {
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
                    } else if (chargeResponseCode.equalsIgnoreCase("02")) {
                        String authModelUsed = response.getData().getAuthModelUsed();
                        if (authModelUsed.equalsIgnoreCase(RaveConstants.PIN)) {
                            String flwRef = response.getData().getFlwRef();
                            String chargeResponseMessage = response.getData().getChargeResponseMessage();
                            chargeResponseMessage = (chargeResponseMessage == null || chargeResponseMessage.length() == 0) ? "Enter your one  time password (OTP)" : chargeResponseMessage;
                            mView.showOTPLayout(flwRef, chargeResponseMessage);
                        } else if (authModelUsed.equalsIgnoreCase(RaveConstants.AVS_VBVSECURECODE)) {
                            String flwRef = response.getData().getFlwRef();
                            mView.onAVSVBVSecureCodeModelUsed(response.getData().getAuthurl(), flwRef);
                        } else {
                            mView.onPaymentError("Unknown Auth Model");
                        }
                    } else {
                        mView.onPaymentError("Unknown charge response code");
                    }
                } else {
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
                    } else {
                        mView.onValidateError(message);
                    }
                } else {
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
    public void requeryTx(final String flwRef, final String publicKey) {

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
        } else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void saveCardToRave(String phoneNumber, String email, String FlwRef, String publicKey, String deviceFingerprint, final String verifyResponse) {
        SaveCardRequestBody body = new SaveCardRequestBody();
        body.setDevice(deviceFingerprint);
        body.setDevice_email(email);
        body.setDevice_key(phoneNumber);
        body.setProcessor_reference(FlwRef);
        body.setPublic_key(publicKey);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().saveCardToRave(body, new Callbacks.OnSaveCardRequestComplete() {
            @Override
            public void onSuccess(SaveCardResponse response, String responseAsJSONString) {
                mView.onCardSaveSuccessful(response, verifyResponse);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onCardSaveFailed(message, verifyResponse);
            }
        });
    }

    @Override
    public void lookupSavedCards(String publicKey, String phoneNumber, final String verifyResponseAsJSONString) {
        LookupSavedCardsRequestBody body = new LookupSavedCardsRequestBody();
        body.setDevice_key(phoneNumber);
        body.setPublic_key(publicKey);


        new NetworkRequestImpl().lookupSavedCards(body, new Callbacks.OnLookupSavedCardsRequestComplete() {
            @Override
            public void onSuccess(LookupSavedCardsResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onLookupSavedCardsSuccessful(response, responseAsJSONString, verifyResponseAsJSONString);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onLookupSavedCardsFailed(message, responseAsJSONString, verifyResponseAsJSONString);
            }
        });
    }

    @Override
    public void saveCardToSharedPreferences(LookupSavedCardsResponse response) {
        String phoneNumber = response.getData()[0].getMobile_number();
        List<SavedCard> cards = new ArrayList<>();

        for (LookupSavedCardsResponse.Data d : response.getData()) {
            SavedCard card = new SavedCard();
            card.setEmail(d.getEmail());
            card.setCardHash(d.getCard_hash());
            card.setCard_brand(d.getCard().getCard_brand());
            card.setMasked_pan(d.getCard().getMasked_pan());

            cards.add(card);
        }

        sharedManager.saveCardToSharedPreference(cards, phoneNumber);


    }


    @Override
    public void fetchFee(final Payload payload, final int reason) {

        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPBFPubKey(payload.getPBFPubKey());

        if (payload.getCardno() == null || payload.getCardno().length() == 0 || payload.getCardBIN() != null) {
            body.setCard6(payload.getCardBIN());
        } else {
            body.setCard6(payload.getCardno().substring(0, 6));
        }

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().getFee(body, new Callbacks.OnGetFeeRequestComplete() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload, reason);
                } catch (Exception e) {
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
    public void retrieveSavedCardsFromMemory(String phoneNumber) {
        List<SavedCard> cards = sharedManager.getSavedCards(phoneNumber);

        mView.setSavedCards(cards);
    }

    @Override
    public void retrievePhoneNumberFromMemory() {
        String phoneNumber = sharedManager.fetchPhoneNumber();
        mView.setPhoneNumber(phoneNumber);
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
                } else if (responseAsJSONString.contains("expired")) {
                    mView.onPaymentError("Token expired");
                } else {
                    mView.onPaymentError(message);
                }


            }
        });

    }

    @Override
    public void onDetachView() {
        if (!this.cardSaveInProgress)
            this.mView = new NullCardView();
    }

    @Override
    public void onAttachView(CardContract.View view) {
        this.mView = view;
    }
}

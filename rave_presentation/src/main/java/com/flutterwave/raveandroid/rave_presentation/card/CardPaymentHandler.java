package com.flutterwave.raveandroid.rave_presentation.card;

import android.util.Log;

import com.flutterwave.raveandroid.rave_core.models.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.ValidationAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.validators.CardNoValidator;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.LookupSavedCardsRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RemoveSavedCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SaveCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SendOtpRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.CheckCardResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SendRaveOtpResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.ACCESS_OTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.GTB_OTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NOAUTH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NOAUTH_INTERNATIONAL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NOAUTH_SAVED_CARD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VBV;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.cardNotAllowed;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.enterOTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.unknownAuthmsg;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class CardPaymentHandler implements CardContract.CardPaymentHandler {

    private CardContract.CardInteractor mCardInteractor;

    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    CardNoValidator cardNoValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    Gson gson;
    List<SavedCard> savedCards;
    private boolean cardSaveInProgress = false;
    private String requeryInstruction = "Transaction is under processing, please use transaction requery to check status";

    public boolean isCardSaveInProgress() {
        return cardSaveInProgress;
    }

    public void setCardSaveInProgress(boolean cardSaveInProgress) {
        this.cardSaveInProgress = cardSaveInProgress;
    }


    @Inject
    public CardPaymentHandler(CardContract.CardInteractor mCardInteractor) {
        this.mCardInteractor = mCardInteractor;
    }

    /**
     * Makes a generic call to the charge endpoint with the payload provided. Handles both conditions
     * for initial charge request and when the suggested auth has been added.
     *
     * @param payload       {@link Payload} object to be sent.
     * @param encryptionKey Rave encryption key gotten from dashboard
     */
    @Override
    public void chargeCard(final Payload payload, final String encryptionKey) {

        String cardRequestBodyAsString = payloadToJsonConverter.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey);

        final ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mCardInteractor.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("Card").getEvent(), payload.getPBFPubKey());


        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mCardInteractor.showProgressIndicator(false);

                if (response.getData() != null) {

                    if (response.getData().getSuggested_auth() != null) {
                        String suggested_auth = response.getData().getSuggested_auth();

                        if (suggested_auth.equals(PIN)) {
                            mCardInteractor.collectCardPin(payload);
                        } else if (suggested_auth.equals(AVS_VBVSECURECODE)) { //address verification then verification by visa
                            mCardInteractor.collectCardAddressDetails(payload, AVS_VBVSECURECODE);
                        } else if (suggested_auth.equalsIgnoreCase(NOAUTH_INTERNATIONAL)) {
                            mCardInteractor.collectCardAddressDetails(payload, NOAUTH_INTERNATIONAL);
                        } else {
                            mCardInteractor.onPaymentError(unknownAuthmsg);
                        }
                    } else {
                        // Check if transaction is already successful
                        if (response.getData().getChargeResponseCode() != null && response.getData().getChargeResponseCode().equalsIgnoreCase("00")) {
                            String flwRef = response.getData().getFlwRef();

                            requeryTx(flwRef, payload.getPBFPubKey());

                        } else {

                            String authModelUsed = response.getData().getAuthModelUsed();

                            if (authModelUsed != null) {
                                String flwRef = response.getData().getFlwRef();

                                if (authModelUsed.equalsIgnoreCase(VBV) || authModelUsed.equalsIgnoreCase(AVS_VBVSECURECODE) || authModelUsed.equalsIgnoreCase(NOAUTH_SAVED_CARD)) {
                                    String authUrlCrude = response.getData().getAuthurl();
                                    mCardInteractor.showWebPage(authUrlCrude, flwRef);
                                } else if (authModelUsed.equalsIgnoreCase(GTB_OTP)
                                        || authModelUsed.equalsIgnoreCase(ACCESS_OTP)
                                        || authModelUsed.toLowerCase().contains("otp")
                                        || authModelUsed.equalsIgnoreCase(PIN)) {
                                    String chargeResponseMessage = response.getData().getChargeResponseMessage();
                                    chargeResponseMessage = (chargeResponseMessage == null || chargeResponseMessage.length() == 0) ? enterOTP : chargeResponseMessage;
                                    mCardInteractor.collectOtp(flwRef, chargeResponseMessage);
                                } else if (authModelUsed.equalsIgnoreCase(NOAUTH)) {
                                    requeryTx(flwRef, payload.getPBFPubKey());
                                } else {
                                    mCardInteractor.onPaymentError(unknownAuthmsg);
                                }
                            } else {
                                mCardInteractor.onPaymentError(unknownAuthmsg);
                            }
                        }
                    }
                } else {
                    mCardInteractor.onPaymentError(noResponse);
                }
            }

            @Override
            public void onError(String message) {
                mCardInteractor.showProgressIndicator(false);
                mCardInteractor.onPaymentError(message);
            }
        });
    }

    @Override
    public void checkCard(String cardFirstSix, final Payload body, final Boolean isDisplayFee, final String encryptionKey, final String barterCountry) {

        mCardInteractor.showProgressIndicator(true);

        networkRequest.checkCard(cardFirstSix, new ResultCallback<CheckCardResponse>() {
            @Override
            public void onSuccess(CheckCardResponse response) {
                mCardInteractor.showProgressIndicator(false);

                try{
                    String[] countryArray = response.getCountry().split(" ");
                    String countryCode = countryArray[countryArray.length-1];
                    if (countryCode.equalsIgnoreCase(barterCountry)){
                        continueCharge( isDisplayFee,  body,  encryptionKey);
                    } else {
                        mCardInteractor.onPaymentError(cardNotAllowed);
                    }

                } catch (Exception ignored){
                    continueCharge(isDisplayFee, body, encryptionKey);
                }

            }

            @Override
            public void onError(String message) {
                continueCharge(isDisplayFee, body, encryptionKey);
//                mCardInteractor.showProgressIndicator(false);
//                mCardInteractor.onPaymentError(message);
            }
        });

    }

    private void continueCharge(boolean isDisplayFee, Payload body, String encryptionKey) {
        if (isDisplayFee) {
            fetchFee(body);
        } else {
            chargeCard(body, encryptionKey);
        }
    }

    @Override
    public void chargeSavedCard(Payload payload, String encryptionKey) {
        if (payload.getOtp() == null || payload.getOtp() == "") {
            sendRaveOTP(payload);
        } else chargeCard(payload, encryptionKey);
    }

    public void sendRaveOTP(final Payload payload) {
        SendOtpRequestBody body = new SendOtpRequestBody();
        body.setDevice_key(payload.getPhonenumber());
        body.setPublic_key(payload.getPBFPubKey());
        body.setCard_hash(payload.getCard_hash());

        mCardInteractor.showProgressIndicator(true);

        networkRequest.sendRaveOtp(body, new ResultCallback<SendRaveOtpResponse>() {
            @Override
            public void onSuccess(SendRaveOtpResponse response) {
                mCardInteractor.showProgressIndicator(false);
                mCardInteractor.collectOtpForSaveCardCharge(payload);
            }

            @Override
            public void onError(String message) {
                mCardInteractor.showProgressIndicator(false);
                mCardInteractor.onPaymentError(message);
            }
        });
    }

    @Override
    public void chargeCardWithAddressDetails(Payload payload, AddressDetails address, String encryptionKey, String authModel) {
        payload.setSuggestedAuth(authModel);
        payload.setBillingaddress(address.getStreetAddress());
        payload.setBillingcity(address.getCity());
        payload.setBillingzip(address.getZipCode());
        payload.setBillingcountry(address.getCountry());
        payload.setBillingstate(address.getState());

        logEvent(new ChargeAttemptEvent("AVS Card").getEvent(), payload.getPBFPubKey());

        chargeCard(payload, encryptionKey);

    }

    @Override
    public void chargeCardWithPinAuthModel(final Payload payload, String pin, String encryptionKey) {
        payload.setPin(pin);
        payload.setSuggestedAuth(PIN);

        chargeCard(payload, encryptionKey);

    }

    @Override
    public void validateCardCharge(final String flwRef, String otp, final String publicKey) {

        ValidateChargeBody body = new ValidateChargeBody();
        body.setPBFPubKey(publicKey);
        body.setOtp(otp);
        body.setTransaction_reference(flwRef);

        mCardInteractor.showProgressIndicator(true);

        logEvent(new ValidationAttemptEvent("Card").getEvent(), publicKey);

        networkRequest.validateCardCharge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {
                mCardInteractor.showProgressIndicator(false);

                if (response.getStatus() != null) {
                    String status = response.getStatus();
                    String message = response.getMessage();

                    if (status.equalsIgnoreCase(success)) {
                        requeryTx(flwRef, publicKey);
                    } else {
                        mCardInteractor.onPaymentError(message);
                    }
                } else {
                    requeryTx(flwRef, publicKey);
                }
            }

            @Override
            public void onError(String message) {
                mCardInteractor.showProgressIndicator(false);
                mCardInteractor.onPaymentError(message);
            }
        });

    }

    @Override
    public void requeryTx(final String flwRef, final String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        mCardInteractor.showProgressIndicator(true);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                mCardInteractor.showProgressIndicator(false);
                verifyRequeryResponse(response, responseAsJSONString, flwRef);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mCardInteractor.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    public void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, String flwRef) {

        boolean wasTxSuccessful = transactionStatusChecker
                .getTransactionStatus(
                        responseAsJSONString
                );

        if (wasTxSuccessful) {
            mCardInteractor.onPaymentSuccessful(response.getStatus(), flwRef, responseAsJSONString);
        } else {
            mCardInteractor.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void saveCardToRave(final String phoneNumber, String email, String FlwRef, String publicKey) {
        SaveCardRequestBody body = new SaveCardRequestBody();
        body.setDevice(deviceIdGetter.getDeviceId());
        body.setDevice_email(email);
        body.setDevice_key(phoneNumber);
        body.setProcessor_reference(FlwRef);
        body.setPublic_key(publicKey);

        mCardInteractor.showProgressIndicator(true);

        networkRequest.saveCardToRave(body, new ResultCallback<SaveCardResponse>() {
            @Override
            public void onSuccess(SaveCardResponse response) {
                mCardInteractor.onCardSaveSuccessful(response, phoneNumber);
            }

            @Override
            public void onError(String message) {
                mCardInteractor.onCardSaveFailed(message);
            }
        });
    }

    @Override
    public void deleteASavedCard(String cardHash, String phoneNumber, String publicKey) {
        mCardInteractor.showProgressIndicator(true);
        RemoveSavedCardRequestBody body = new RemoveSavedCardRequestBody(cardHash, phoneNumber, publicKey);
        networkRequest.deleteASavedCard(body, new ResultCallback<SaveCardResponse>() {
            @Override
            public void onSuccess(SaveCardResponse response) {
                mCardInteractor.showProgressIndicator(false);
                mCardInteractor.onSavedCardRemoveSuccessful();
            }

            @Override
            public void onError(String message) {
                mCardInteractor.showProgressIndicator(false);
                mCardInteractor.onSavedCardRemoveFailed(message);
            }
        });
    }

    @Override
    public void lookupSavedCards(String publicKey,
                                 final String phoneNumber,
                                 boolean showLoader
    ) {
        LookupSavedCardsRequestBody body = new LookupSavedCardsRequestBody();
        body.setDevice_key(phoneNumber);
        body.setPublic_key(publicKey);

        if (showLoader)
            mCardInteractor.showProgressIndicator(true);


        networkRequest.lookupSavedCards(body, new ResultCallback<LookupSavedCardsResponse>() {
            @Override
            public void onSuccess(LookupSavedCardsResponse response) {
                mCardInteractor.showProgressIndicator(false);
                List<SavedCard> cards = new ArrayList<>();

                for (LookupSavedCardsResponse.Data d : response.getData()) {
                    SavedCard card = new SavedCard();
                    card.setEmail(d.getEmail());
                    card.setCardHash(d.getCard_hash());
                    card.setCard_brand(d.getCard().getCard_brand());
                    card.setMasked_pan(d.getCard().getMasked_pan());

                    cards.add(card);
                }

                mCardInteractor.onSavedCardsLookupSuccessful(cards, phoneNumber);
            }

            @Override
            public void onError(String message) {
                mCardInteractor.showProgressIndicator(false);
                mCardInteractor.onSavedCardsLookupSuccessful(new ArrayList<SavedCard>(), phoneNumber);
                mCardInteractor.onSavedCardsLookupFailed(message);
            }
        });
    }

    @Override
    public void fetchFee(final Payload payload) {

        boolean isCardnoValid = cardNoValidator.isCardNoStrippedValid(payload.getCardno());

        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPBFPubKey(payload.getPBFPubKey());

        if (isCardnoValid) {
            body.setCard6(payload.getCardno().substring(0, 6));
        } else {
            body.setCard6(payload.getCardBIN());
        }

        mCardInteractor.showProgressIndicator(true);

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mCardInteractor.showProgressIndicator(false);

                try {
                    mCardInteractor.onTransactionFeeFetched(response.getData().getCharge_amount(), payload, response.getData().getFee());
                } catch (Exception e) {
                    e.printStackTrace();
                    mCardInteractor.onFetchFeeError(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                mCardInteractor.showProgressIndicator(false);
                Log.e(RAVEPAY, message);
                mCardInteractor.onFetchFeeError(message);
            }
        });

    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }
}

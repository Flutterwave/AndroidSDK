package com.flutterwave.raveandroid.card;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.PhoneNumberObfuscator;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.TransactionStatusChecker;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.data.events.RequeryEvent;
import com.flutterwave.raveandroid.data.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.data.events.ValidationAttemptEvent;
import com.flutterwave.raveandroid.di.components.RaveUiComponent;
import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.LookupSavedCardsRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SaveCardRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.SendOtpRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SendRaveOtpResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CardNoValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.ACCESS_OTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.GTB_OTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.MANUAL_CARD_CHARGE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NOAUTH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NOAUTH_INTERNATIONAL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VBV;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.enterOTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldcardNoStripped;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.unknownAuthmsg;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validAmountPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validCreditCardPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validCvvPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validExpiryDatePrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validPhonePrompt;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class CardPresenter implements CardContract.UserActionsListener {

    private CardContract.View mView;

    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    AmountValidator amountValidator;
    @Inject
    CvvValidator cvvValidator;
    @Inject
    EmailValidator emailValidator;
    @Inject
    CardExpiryValidator cardExpiryValidator;
    @Inject
    CardNoValidator cardNoValidator;
    private Context context;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PhoneNumberObfuscator phoneNumberObfuscator;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    SharedPrefsRepo sharedManager;
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
    public CardPresenter(Context context, CardContract.View mView) {
        this.mView = mView;
        this.context = context;
    }

    public CardPresenter(Context context, CardContract.View mView, RaveUiComponent raveUiComponent) {
        this.context = context;
        this.mView = mView;
        this.eventLogger = raveUiComponent.eventLogger();
        this.networkRequest = raveUiComponent.networkImpl();
        this.amountValidator = raveUiComponent.amountValidator();
        this.cvvValidator = raveUiComponent.cvvValidator();
        this.emailValidator = raveUiComponent.emailValidator();
        this.cardExpiryValidator = raveUiComponent.cardExpiryValidator();
        this.cardNoValidator = raveUiComponent.cardNoValidator();
        this.deviceIdGetter = raveUiComponent.deviceIdGetter();
        this.phoneNumberObfuscator = raveUiComponent.phoneNumberObfuscator();
        this.transactionStatusChecker = raveUiComponent.transactionStatusChecker();
        this.payloadEncryptor = raveUiComponent.payloadEncryptor();
        this.sharedManager = raveUiComponent.sharedManager();
        this.gson = raveUiComponent.gson();
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

        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey);

        final ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("Card").getEvent(), payload.getPBFPubKey());


        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {

                    if (response.getData().getSuggested_auth() != null) {
                        String suggested_auth = response.getData().getSuggested_auth();

                        if (suggested_auth.equals(PIN)) {
                            mView.onPinAuthModelSuggested(payload);
                        } else if (suggested_auth.equals(AVS_VBVSECURECODE)) { //address verification then verification by visa
                            mView.onAVS_VBVSECURECODEModelSuggested(payload);
                        } else if (suggested_auth.equalsIgnoreCase(NOAUTH_INTERNATIONAL)) {
                            mView.onNoAuthInternationalSuggested(payload);
                        } else {
                            mView.onPaymentError(unknownAuthmsg);
                        }
                    } else {
                        // Check if transaction is already successful
                        if (response.getData().getChargeResponseCode() != null && response.getData().getChargeResponseCode().equalsIgnoreCase("00")) {
                            mView.onChargeCardSuccessful(response);

                        } else {

                            String authModelUsed = response.getData().getAuthModelUsed();

                            if (authModelUsed != null) {

                                if (authModelUsed.equalsIgnoreCase(VBV) || authModelUsed.equalsIgnoreCase(AVS_VBVSECURECODE)) {
                                    String authUrlCrude = response.getData().getAuthurl();
                                    String flwRef = response.getData().getFlwRef();

                                    mView.onVBVAuthModelUsed(authUrlCrude, flwRef);
                                } else if (authModelUsed.equalsIgnoreCase(GTB_OTP)
                                        || authModelUsed.equalsIgnoreCase(ACCESS_OTP)
                                        || authModelUsed.toLowerCase().contains("otp")
                                        || authModelUsed.equalsIgnoreCase(PIN)) {
                                    String flwRef = response.getData().getFlwRef();
                                    String chargeResponseMessage = response.getData().getChargeResponseMessage();
                                    chargeResponseMessage = (chargeResponseMessage == null || chargeResponseMessage.length() == 0) ? enterOTP : chargeResponseMessage;
                                    mView.showOTPLayout(flwRef, chargeResponseMessage);

                                } else if (authModelUsed.equalsIgnoreCase(NOAUTH)) {
                                    String flwRef = response.getData().getFlwRef();
                                    mView.onNoAuthUsed(flwRef, payload.getPBFPubKey());
                                } else {
                                    mView.onPaymentError(unknownAuthmsg);
                                }
                            } else {
                                mView.onPaymentError(unknownAuthmsg);
                            }
                        }
                    }
                } else {
                    mView.onPaymentError(noResponse);
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });
    }

    @Override
    public void chargeSavedCard(Payload payload, String encryptionKey) {
        if (payload.getOtp() == null || payload.getOtp() == "") {
            sendRaveOTP(payload);
        } else chargeCard(payload, encryptionKey);
    }

    @Override
    public void sendRaveOTP(final Payload payload) {
        SendOtpRequestBody body = new SendOtpRequestBody();
        body.setDevice_key(payload.getPhonenumber());
        body.setPublic_key(payload.getPBFPubKey());
        body.setCard_hash(payload.getCard_hash());

        mView.showProgressIndicator(true);

        networkRequest.sendRaveOtp(body, new ResultCallback<SendRaveOtpResponse>() {
            @Override
            public void onSuccess(SendRaveOtpResponse response) {
                mView.showProgressIndicator(false);
                String authInstruction = "Enter the one time password (OTP) sent to " +
                        phoneNumberObfuscator.obfuscatePhoneNumber(payload
                                .getPhonenumber());
                mView.showOTPLayoutForSavedCard(payload, authInstruction);
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onSendRaveOtpFailed(message);
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

        logEvent(new ChargeAttemptEvent("AVS Card").getEvent(), payload.getPBFPubKey());

        chargeCard(payload, encryptionKey);

    }

    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

        boolean valid = true;

        int amountID = dataHashMap.get(fieldAmount).getViewId();
        String amount = dataHashMap.get(fieldAmount).getData();
        Class amountViewType = dataHashMap.get(fieldAmount).getViewType();

        int emailID = dataHashMap.get(fieldEmail).getViewId();
        String email = dataHashMap.get(fieldEmail).getData();
        Class emailViewType = dataHashMap.get(fieldEmail).getViewType();

        int cvvID = dataHashMap.get(fieldCvv).getViewId();
        String cvv = dataHashMap.get(fieldCvv).getData();
        Class cvvViewType = dataHashMap.get(fieldCvv).getViewType();

        int cardExpiryID = dataHashMap.get(fieldCardExpiry).getViewId();
        String cardExpiry = dataHashMap.get(fieldCardExpiry).getData();
        Class cardExpiryViewType = dataHashMap.get(fieldCardExpiry).getViewType();

        int cardNoStrippedID = dataHashMap.get(fieldcardNoStripped).getViewId();
        String cardNoStripped = dataHashMap.get(fieldcardNoStripped).getData().replaceAll(" ", "");
        dataHashMap.get(fieldcardNoStripped).setData(cardNoStripped);

        Class cardNoStrippedViewType = dataHashMap.get(fieldcardNoStripped).getViewType();

        boolean isAmountValid = amountValidator.isAmountValid(amount);
        boolean isEmailValid = emailValidator.isEmailValid(email);
        boolean isCVVValid = cvvValidator.isCvvValid(cvv);
        boolean isCardExpiryValid = cardExpiryValidator.isCardExpiryValid(cardExpiry);
        boolean isCardNoValid = cardNoValidator.isCardNoStrippedValid(cardNoStripped);

        if (!isAmountValid) {
            valid = false;
            mView.showFieldError(amountID, validAmountPrompt, amountViewType);
        }

        if (!isEmailValid) {
            valid = false;
            mView.showFieldError(emailID, validPhonePrompt, emailViewType);
        }

        if (!isCVVValid) {
            valid = false;
            mView.showFieldError(cvvID, validCvvPrompt, cvvViewType);
        }

        if (!isCardExpiryValid) {
            valid = false;
            mView.showFieldError(cardExpiryID, validExpiryDatePrompt, cardExpiryViewType);
        }

        if (!isCardNoValid) {
            valid = false;
            mView.showFieldError(cardNoStrippedID, validCreditCardPrompt, cardNoStrippedViewType);
        }

        if (valid) {
            mView.onValidationSuccessful(dataHashMap);
        }

    }

    @Override
    public void onDataForSavedCardChargeCollected(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        boolean valid = true;

        int amountID = dataHashMap.get(fieldAmount).getViewId();
        String amount = dataHashMap.get(fieldAmount).getData();
        Class amountViewType = dataHashMap.get(fieldAmount).getViewType();

        int emailID = dataHashMap.get(fieldEmail).getViewId();
        String email = dataHashMap.get(fieldEmail).getData();
        Class emailViewType = dataHashMap.get(fieldEmail).getViewType();


        boolean isAmountValid = amountValidator.isAmountValid(amount);
        boolean isEmailValid = emailValidator.isEmailValid(email);

        if (!isAmountValid) {
            valid = false;
            mView.showFieldError(amountID, validAmountPrompt, amountViewType);
        }

        if (!isEmailValid) {
            valid = false;
            mView.showFieldError(emailID, validPhonePrompt, emailViewType);
        }


        if (valid) {
            ravePayInitializer.setAmount(Double.parseDouble(amount));
            ravePayInitializer.setEmail(email);

            if (savedCards == null)
                checkForSavedCardsInMemory(ravePayInitializer);
            mView.showSavedCardsLayout(savedCards);

        }
    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(fieldAmount).getData()));

            if (dataHashMap.containsKey(fieldPhone)) {
                String phoneNumber = dataHashMap.get(fieldPhone).getData();
                if (!phoneNumber.isEmpty()) ravePayInitializer.setPhoneNumber(phoneNumber);
            }

            String deviceID = deviceIdGetter.getDeviceId();


            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setCardno(dataHashMap.get(fieldcardNoStripped).getData())
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setCvv(dataHashMap.get(fieldCvv).getData())
                    .setEmail(dataHashMap.get(fieldEmail).getData())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceID).setTxRef(ravePayInitializer.getTxRef())
                    .setExpiryyear(dataHashMap.get(fieldCardExpiry).getData().substring(3, 5))
                    .setExpirymonth(dataHashMap.get(fieldCardExpiry).getData().substring(0, 2))
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(deviceID);

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body, MANUAL_CARD_CHARGE);
            } else {
                chargeCard(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }

    @Override
    public void processSavedCardTransaction(SavedCard savedCard, RavePayInitializer ravePayInitializer) {
        if (ravePayInitializer != null) {

            String deviceID = deviceIdGetter.getDeviceId();


            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceID)
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(deviceID)
                    .setIs_saved_card_charge(true)
                    .setSavedCard(savedCard)
                    .setPhonenumber(ravePayInitializer.getPhoneNumber());

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createSavedCardChargePayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body, RaveConstants.SAVED_CARD_CHARGE);
            } else {
                chargeSavedCard(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }


    @Override
    public void chargeCardWithSuggestedAuthModel(final Payload payload, String zipOrPin, String authModel, String encryptionKey) {

        if (authModel.equalsIgnoreCase(AVS_VBVSECURECODE)) {
            payload.setBillingzip(zipOrPin);
        } else if (authModel.equalsIgnoreCase(PIN)) {
            payload.setPin(zipOrPin);
        }

        payload.setSuggestedAuth(authModel);

        chargeCard(payload, encryptionKey);

    }

    @Override
    public void validateCardCharge(final String flwRef, String otp, String PBFPubKey) {

        ValidateChargeBody body = new ValidateChargeBody();
        body.setPBFPubKey(PBFPubKey);
        body.setOtp(otp);
        body.setTransaction_reference(flwRef);

        mView.showProgressIndicator(true);

        logEvent(new ValidationAttemptEvent("Card").getEvent(), PBFPubKey);

        networkRequest.validateCardCharge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {
                mView.showProgressIndicator(false);

                if (response.getStatus() != null) {
                    String status = response.getStatus();
                    String message = response.getMessage();

                    if (status.equalsIgnoreCase(success)) {
                        mView.onValidateSuccessful(status);
                    } else {
                        mView.onValidateError(message);
                    }
                } else {
                    mView.onValidateCardChargeFailed(flwRef);
                }
            }

            @Override
            public void onError(String message) {
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

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
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

        boolean wasTxSuccessful = transactionStatusChecker
                .getTransactionStatus(
                        String.valueOf(ravePayInitializer.getAmount()),
                        ravePayInitializer.getCurrency(),
                        responseAsJSONString
                );

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(response.getStatus(), flwRef, responseAsJSONString, ravePayInitializer);
        } else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void saveCardToRave(final String phoneNumber, String email, String FlwRef, String publicKey, final String verifyResponse) {
        SaveCardRequestBody body = new SaveCardRequestBody();
        body.setDevice(deviceIdGetter.getDeviceId());
        body.setDevice_email(email);
        body.setDevice_key(phoneNumber);
        body.setProcessor_reference(FlwRef);
        body.setPublic_key(publicKey);

        mView.showProgressIndicator(true);

        networkRequest.saveCardToRave(body, new ResultCallback<SaveCardResponse>() {
            @Override
            public void onSuccess(SaveCardResponse response) {
                mView.onCardSaveSuccessful(response, verifyResponse, phoneNumber);
            }

            @Override
            public void onError(String message) {
                mView.onCardSaveFailed(message, verifyResponse);
            }
        });
    }

    @Override
    public void lookupSavedCards(String publicKey,
                                 String phoneNumber,
                                 final String verifyResponseAsJSONString
    ) {
        LookupSavedCardsRequestBody body = new LookupSavedCardsRequestBody();
        body.setDevice_key(phoneNumber);
        body.setPublic_key(publicKey);


        networkRequest.lookupSavedCards(body, new ResultCallback<LookupSavedCardsResponse>() {
            @Override
            public void onSuccess(LookupSavedCardsResponse response) {
                mView.showProgressIndicator(false);
                mView.setHasSavedCards(true);
                mView.onLookupSavedCardsSuccessful(response, verifyResponseAsJSONString);
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onLookupSavedCardsFailed(message, verifyResponseAsJSONString);
            }
        });
    }

    @Override
    public void saveCardToSharedPreferences(LookupSavedCardsResponse response, String publicKey) {
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

        sharedManager.saveCardToSharedPreference(cards, phoneNumber, publicKey);
    }


    @Override
    public void fetchFee(final Payload payload, final int reason) {

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

        mView.showProgressIndicator(true);

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload, reason);
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showFetchFeeFailed(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RAVEPAY, message);
                mView.showFetchFeeFailed(message);
            }
        });

    }

    @Override
    public void retrieveSavedCardsFromMemory(String phoneNumber, String publicKey) {
        if (phoneNumber != null)
            if (!phoneNumber.isEmpty())
                savedCards = sharedManager.getSavedCards(phoneNumber, publicKey);
    }

    private void retrievePhoneNumberFromMemory(RavePayInitializer ravePayInitializer) {
        String phoneNumber = sharedManager.fetchPhoneNumber();
        if (ravePayInitializer.getPhoneNumber() == null || ravePayInitializer.getPhoneNumber().isEmpty()) {
            ravePayInitializer.setPhoneNumber(phoneNumber);
        }
    }

    @Override
    public void checkForSavedCardsInMemory(RavePayInitializer ravePayInitializer) {
        if (savedCards == null) {
            savedCards = new ArrayList<>();
        }

        retrievePhoneNumberFromMemory(ravePayInitializer);
        retrieveSavedCardsFromMemory(ravePayInitializer.getPhoneNumber(), ravePayInitializer.getPublicKey());

        if (!savedCards.isEmpty()) {
            mView.setHasSavedCards(true);
        }
    }

    @Override
    public List<SavedCard> getSavedCards() {
        return savedCards;
    }

    @Override
    public void chargeToken(Payload payload) {

        mView.showProgressIndicator(true);


        logEvent(new ChargeAttemptEvent("Card Token").getEvent(), payload.getPBFPubKey());


        networkRequest.chargeToken(payload, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {
                mView.showProgressIndicator(false);
                mView.onChargeTokenComplete(response);

            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                    mView.onPaymentError(message);
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

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("Card Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            if (ravePayInitializer.isSaveCardFeatureAllowed()) {
                mView.showCardSavingOption(true);
            }

            checkForSavedCardsInMemory(ravePayInitializer);

            // Check for saved cards on Rave server
            if (ravePayInitializer.getPhoneNumber() != null) {
                if (ravePayInitializer.getPhoneNumber().length() > 0) {
                    lookupSavedCards(ravePayInitializer.getPublicKey(),
                            ravePayInitializer.getPhoneNumber(), "");
                    mView.onPhoneNumberValidated(ravePayInitializer.getPhoneNumber());
                }
            }


            boolean isEmailValid = emailValidator.isEmailValid(ravePayInitializer.getEmail());
            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());

            if (isEmailValid) {
                mView.onEmailValidated(ravePayInitializer.getEmail(), View.GONE);
            } else {
                mView.onEmailValidated("", View.VISIBLE);
            }
            if (isAmountValid) {
                mView.onAmountValidated(String.valueOf(ravePayInitializer.getAmount()), View.GONE);
            } else {
                mView.onAmountValidated("", View.VISIBLE);
            }
        }
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }
}

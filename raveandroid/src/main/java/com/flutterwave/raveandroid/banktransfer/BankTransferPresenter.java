package com.flutterwave.raveandroid.banktransfer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.di.components.RaveUiComponent;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by hfetuga on 27/06/2018.
 */


public class BankTransferPresenter implements BankTransferContract.UserActionsListener {
    private static final String ACCOUNT_NUMBER = "account_number";
    private static final String BANK_NAME = "bank_name";
    private static final String BENEFICIARY_NAME = "benef_name";
    private static final String AMOUNT = "amount";
    private static final String TX_REF = "txref";
    private static final String FLW_REF = "flwref";
    private static final String PUBLIC_KEY = "pbfkey";

    @Inject
    EventLogger eventLogger;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;

    public boolean pollingCancelled = false;
    public boolean hasTransferDetails = false;
    BankTransferContract.View mView;
    private String txRef = null, flwRef = null, publicKey = null;
    private long requeryCountdownTime = 0;
    private String beneficiaryName, accountNumber, amount, bankName;

    @Inject
    public BankTransferPresenter(Context context, BankTransferContract.View mView) {
        this.mView = mView;
    }

    public BankTransferPresenter(BankTransferContract.View mView, RaveUiComponent raveUiComponent) {
        this.mView = mView;
        this.eventLogger = raveUiComponent.eventLogger();
        this.networkRequest = raveUiComponent.networkImpl();
        this.amountValidator = raveUiComponent.amountValidator();
        this.payloadToJson = raveUiComponent.payloadToJson();
        this.deviceIdGetter = raveUiComponent.deviceIdGetter();
        this.payloadEncryptor = raveUiComponent.payloadEncryptor();
    }

    @Override
    public void fetchFee(final Payload payload) {
        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPtype("3");
        body.setPBFPubKey(payload.getPBFPubKey());

        mView.showProgressIndicator(true);

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload);
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.showFetchFeeFailed("An error occurred while retrieving transaction fee");
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RaveConstants.RAVEPAY, message);
                mView.showFetchFeeFailed(message);
            }
        });
    }

    @Override
    public void payWithBankTransfer(final Payload payload, final String encryptionKey) {
        String cardRequestBodyAsString = payloadToJson.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey);
        encryptedCardRequestBody = encryptedCardRequestBody.trim().replaceAll("\\n", "");

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("Bank Transfer").getEvent(), payload.getPBFPubKey());

        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {
                    hasTransferDetails = true;

                    flwRef = response.getData().getFlw_reference();
                    txRef = response.getData().getTx_ref();
                    publicKey = payload.getPBFPubKey();
                    if (response.getData().getNote() != null && response.getData().getNote().contains("to ")) {
                        beneficiaryName = response.getData().getNote().substring(
                                response.getData().getNote().indexOf("to ") + 3
                        );
                    }
                    amount = response.getData().getAmount();
                    accountNumber = response.getData().getAccountnumber();
                    bankName = response.getData().getBankname();
                    mView.onTransferDetailsReceived(
                            amount,
                            accountNumber,
                            bankName,
                            beneficiaryName);
                } else {
                    mView.onPaymentError("No response data was returned");
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
    public void startPaymentVerification() {
        requeryCountdownTime = System.currentTimeMillis();
        mView.showPollingIndicator(true);
        requeryTx(flwRef, txRef, publicKey, pollingCancelled, requeryCountdownTime);
    }

    @Override
    public void cancelPolling() {
        pollingCancelled = true;
    }

    @Override
    public Bundle getState() {
        if (hasTransferDetails) {
            Bundle state = new Bundle();
            state.putString(ACCOUNT_NUMBER, accountNumber);
            state.putString(BANK_NAME, bankName);
            state.putString(BENEFICIARY_NAME, beneficiaryName);
            state.putString(AMOUNT, amount);
            state.putString(TX_REF, txRef);
            state.putString(FLW_REF, flwRef);
            state.putString(PUBLIC_KEY, publicKey);
            return state;
        } else return null;
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            hasTransferDetails = true;
            accountNumber = savedInstanceState.getString(ACCOUNT_NUMBER);
            bankName = savedInstanceState.getString(BANK_NAME);
            beneficiaryName = savedInstanceState.getString(BENEFICIARY_NAME);
            amount = savedInstanceState.getString(AMOUNT);
            txRef = savedInstanceState.getString(TX_REF);
            flwRef = savedInstanceState.getString(FLW_REF);
            publicKey = savedInstanceState.getString(PUBLIC_KEY);

            mView.onTransferDetailsReceived(amount, accountNumber, bankName, beneficiaryName);
        }

    }

    @Override
    public void requeryTx(final String flwRef, final String txRef, final String publicKey, final boolean pollingCancelled, final long requeryCountdownTime) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        logEvent(new RequeryEvent().getEvent(), publicKey);

        networkRequest.requeryPayWithBankTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getData() == null) {
                    mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
                } else if (response.getData().getChargeResponseCode().equals("01")) {
                    if (pollingCancelled) {
                        mView.showPollingIndicator(false);
                        mView.onPollingCanceled(flwRef, txRef, responseAsJSONString);
                    } else {
                        if ((System.currentTimeMillis() - requeryCountdownTime) < 300000) {
                            requeryTx(flwRef, txRef, publicKey, pollingCancelled, requeryCountdownTime);
                        } else {
                            mView.showPollingIndicator(false);
                            mView.onPollingTimeout(flwRef, txRef, responseAsJSONString);
                        }
                    }
                } else if (response.getData().getChargeResponseCode().equals("00")) {
                    mView.showPollingIndicator(false);
                    mView.onPaymentSuccessful(flwRef, txRef, responseAsJSONString);
                } else {
                    mView.showProgressIndicator(false);
                    mView.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                }
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }


    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("Bank Transfer Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()));
            } else mView.onAmountValidationFailed();

        }
    }

    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

        boolean valid = true;

        int amountID = dataHashMap.get(RaveConstants.fieldAmount).getViewId();
        String amount = dataHashMap.get(RaveConstants.fieldAmount).getData();
        Class amountViewType = dataHashMap.get(RaveConstants.fieldAmount).getViewType();


        if (!amountValidator.isAmountValid(amount)) {
            valid = false;
            mView.showFieldError(amountID, RaveConstants.validAmountPrompt, amountViewType);
        }

        if (valid) {
            mView.onValidationSuccessful(dataHashMap);
        }

    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {


        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(RaveConstants.fieldAmount).getData()));

            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceIdGetter.getDeviceId())
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setDevice_fingerprint(deviceIdGetter.getDeviceId())
                    .setNarration(ravePayInitializer.getNarration())
                    .setfrequency(ravePayInitializer.getFrequency())
                    .setDuration(ravePayInitializer.getDuration())
                    .setIsPermanent(ravePayInitializer.getIsPermanent());

            Payload body = builder.createBankTransferPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                payWithBankTransfer(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }

    @Override
    public void onAttachView(BankTransferContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullBankTransferView();
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }


}

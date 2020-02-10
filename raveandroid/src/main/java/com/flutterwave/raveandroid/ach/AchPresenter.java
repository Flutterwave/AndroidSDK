package com.flutterwave.raveandroid.ach;

import android.content.Context;
import android.view.View;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.PayloadEncryptor;
import com.flutterwave.raveandroid.PayloadToJsonConverter;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.TransactionStatusChecker;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.EventLogger;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.SharedPrefsRequestImpl;
import com.flutterwave.raveandroid.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.data.events.RequeryEvent;
import com.flutterwave.raveandroid.data.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import javax.inject.Inject;


public class AchPresenter implements AchContract.UserActionsListener {

    Context context;
    @Inject
    SharedPrefsRequestImpl sharedMgr;
    @Inject
    EventLogger eventLogger;
    @Inject
    AmountValidator amountValidator;
    @Inject
    NetworkRequestImpl networkRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private AchContract.View mView;

    @Inject
    public AchPresenter(Context context, AchContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("ACH Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());

            if (isAmountValid) {
                mView.onAmountValidated(String.valueOf(ravePayInitializer.getAmount()), View.GONE);
                mView.showRedirectMessage(true);
            } else {
                mView.onAmountValidated("", View.VISIBLE);
                mView.showRedirectMessage(false);
            }
        }

    }

    @Override
    public void onDataCollected(RavePayInitializer ravePayInitializer, String amount) {

        mView.showAmountError(null);

        boolean isAmountValid = amountValidator.isAmountValid(amount);

        if (isAmountValid) {
            mView.onValidationSuccessful(amount);
        } else {
            mView.showAmountError(RaveConstants.validAmountPrompt);
        }

    }

    @Override
    public void processTransaction(String amount, final RavePayInitializer ravePayInitializer, final boolean isDisplayFee) {

        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(amount));
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
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setIsUsBankCharge(ravePayInitializer.getOrderedPaymentTypesList().contains(RaveConstants.PAYMENT_TYPE_ACH))
                    .setDevice_fingerprint(deviceIdGetter.getDeviceId());

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createBankPayload();

            chargeAccount(body, ravePayInitializer.getEncryptionKey(), ravePayInitializer.getIsDisplayFee());
        }
    }


    public void chargeAccount(Payload payload, String encryptionKey, final boolean isDisplayFee) {

        String requestBodyAsString = payloadToJsonConverter.convertChargeRequestPayloadToJson(payload);
        String accountRequestBody = payloadEncryptor.getEncryptedData(requestBodyAsString, encryptionKey);

        final ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(accountRequestBody);

        mView.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("ACH").getEvent(), payload.getPBFPubKey());

        networkRequest.charge(body, new Callbacks.OnChargeRequestComplete() {

            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {

                    if (response.getData().getAuthurl() != null) {
                        String authUrl = response.getData().getAuthurl();
                        String flwRef = response.getData().getFlwRef();
                        String chargedAmount = response.getData().getChargedAmount();
                        String currency = response.getData().getCurrency();
                        sharedMgr.saveFlwRef(flwRef);

                        if (isDisplayFee) {
                            mView.showFee(authUrl, flwRef, chargedAmount, currency);
                        } else {
                            mView.showWebView(authUrl, flwRef);
                        }
                    } else {
                        mView.onPaymentError(RaveConstants.no_authurl_was_returnedmsg);
                    }

                } else {
                    mView.onPaymentError(RaveConstants.noResponse);
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
    public void onFeeConfirmed(String authUrl, String flwRef) {
        mView.showWebView(authUrl, flwRef);
    }

    public void requeryTx(String publicKey) {
        final String flwRef = sharedMgr.fetchFlwRef();
        //todo call requery

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
            mView.onPaymentSuccessful(response.getStatus(), flwRef, responseAsJSONString);
        } else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void onAttachView(AchContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullAchView();
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        eventLogger.logEvent(event, publicKey);
    }
}

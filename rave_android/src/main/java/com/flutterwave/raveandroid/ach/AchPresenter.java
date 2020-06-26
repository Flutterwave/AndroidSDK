package com.flutterwave.raveandroid.ach;

import android.view.View;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_presentation.ach.AchHandler;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import javax.inject.Inject;


public class AchPresenter extends AchHandler implements AchUiContract.UserActionsListener {

    private AchUiContract.View mView;

    @Inject
    SharedPrefsRepo sharedMgr;

    @Inject
    EventLogger eventLogger;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;

    @Inject
    public AchPresenter(AchUiContract.View mView) {
        super(mView);
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

        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {

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
                        }
                        else {
                            mView.showWebView(authUrl, flwRef);
                        }
                    }
                    else {
                        mView.onPaymentError(RaveConstants.no_authurl_was_returnedmsg);
                    }

                }
                else {
                    mView.onPaymentError(RaveConstants.noResponse);
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
                verifyRequeryResponseStatus(responseAsJSONString);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(responseAsJSONString);
            }
        });
    }

    public void verifyRequeryResponse(String responseAsJSONString) {

        boolean wasTxSuccessful = transactionStatusChecker
                .getTransactionStatus(
                        responseAsJSONString
                );

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(responseAsJSONString);
        }
        else {
            mView.onPaymentFailed(responseAsJSONString);
        }
    }

    @Override
    public void onAttachView(AchUiContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullAchView();
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }
}

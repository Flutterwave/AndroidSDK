package com.flutterwave.raveandroid.sabankaccount;

import android.content.Context;
import android.util.Log;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.di.components.RaveUiComponent;
import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaBankAccountResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.inValidRedirectUrl;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;

public class SaBankAccountPresenter implements SaBankAccountContract.UserActionsListener {
    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    AmountValidator amountValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    SharedPrefsRepo sharedMgr;

    private Context context;
    private SaBankAccountContract.View mView;

    @Inject
    public SaBankAccountPresenter(Context context, SaBankAccountContract.View mView){
        this.context = context;
        this.mView = mView;
    }

    public SaBankAccountPresenter(Context context, SaBankAccountContract.View mView, RaveUiComponent raveUiComponent) {
        this.context = context;
        this.mView = mView;
        this.eventLogger = raveUiComponent.eventLogger();
        this.amountValidator = raveUiComponent.amountValidator();
        this.networkRequest = raveUiComponent.networkImpl();
        this.deviceIdGetter = raveUiComponent.deviceIdGetter();
        this.payloadEncryptor = raveUiComponent.payloadEncryptor();
        this.transactionStatusChecker = raveUiComponent.transactionStatusChecker();
        this.sharedMgr = raveUiComponent.sharedManager();
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {
        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("SA Bank Account Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()),
                        String.valueOf(ravePayInitializer.getCurrency()));
            }
        }
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
    public void processTransaction(RavePayInitializer ravePayInitializer) {
        if (ravePayInitializer != null) {

            String deviceID = deviceIdGetter.getDeviceId();
            if (deviceID == null) {
                deviceID = Utils.getDeviceId(context);
            }

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
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setDevice_fingerprint(deviceID);

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createSaBankAccountPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                chargeSaBankAccount(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }

    @Override
    public void onAttachView(SaBankAccountContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullSaBankAccountView();
    }

    @Override
    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }

    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

    }

    @Override
    public void verifyRequeryResponseStatus(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer) {
        mView.showProgressIndicator(true);

        boolean wasTxSuccessful = transactionStatusChecker
                .getTransactionStatus(
                        responseAsJSONString
                );

        mView.showProgressIndicator(false);

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(response.getStatus(), responseAsJSONString);
        } else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void requeryTx(String publicKey) {
        final String flwRef = sharedMgr.fetchFlwRef();

        RequeryRequestBody body = new RequeryRequestBody();
        body.setPBFPubKey(publicKey);
        body.setFlw_ref(flwRef);

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
    public void chargeSaBankAccount(final Payload payload, String encryptionKey) {
        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey).trim().replaceAll("\\n", "");

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("SA Bank Account").getEvent(), payload.getPBFPubKey());


        networkRequest.chargeSaBankAccount(body, new ResultCallback<SaBankAccountResponse>() {
            @Override
            public void onSuccess(SaBankAccountResponse response) {

                mView.showProgressIndicator(false);

                if (response.getData().getData().getRedirectUrl() != null) {
                    String authUrl = response.getData().getData().getRedirectUrl();
                    String flwRef = response.getData().getData().getFlwReference();

                    sharedMgr.saveFlwRef(flwRef);

                    mView.showWebView(authUrl, flwRef);
                } else {
                    mView.onPaymentError(inValidRedirectUrl);
                }

            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });
    }
}

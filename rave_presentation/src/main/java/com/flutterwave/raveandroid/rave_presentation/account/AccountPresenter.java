package com.flutterwave.raveandroid.rave_presentation.account;

import android.util.Log;

import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.events.ChargeAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.RequeryEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.ValidationAttemptEvent;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.rave_presentation.di.AppComponent;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidCharge;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class AccountPresenter implements AccountContract.UserActionsListener {

    @Inject
    UrlValidator urlValidator;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    EventLogger eventLogger;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private AccountContract.View mView;

    @Inject
    public AccountPresenter(AccountContract.View mView) {
        this.mView = mView;
    }

    public AccountPresenter(AccountContract.View mView, AppComponent appComponent) {
        this.mView = mView;
        this.eventLogger = appComponent.eventLogger();
        this.networkRequest = appComponent.networkImpl();
        this.transactionStatusChecker = appComponent.transactionStatusChecker();
        this.payloadEncryptor = appComponent.payloadEncryptor();
        this.urlValidator = appComponent.urlValidator();
        this.payloadToJsonConverter = appComponent.payloadToJsonConverter();
        this.payloadEncryptor = appComponent.payloadEncryptor();
    }

    @Override
    public void getBanksList() {

        mView.showProgressIndicator(true);

        networkRequest.getBanks(new ResultCallback<List<Bank>>() {
            @Override
            public void onSuccess(List<Bank> banks) {
                mView.showProgressIndicator(false);
                mView.onBanksListRetrieved(banks);
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onGetBanksRequestFailed("An error occurred while retrieving banks");
            }
        });

    }

    @Override
    public void chargeAccount(final Payload payload, String encryptionKey) {

        String cardRequestBodyAsString = payloadToJsonConverter.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = payloadEncryptor.getEncryptedData(cardRequestBodyAsString, encryptionKey);

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        logEvent(new ChargeAttemptEvent("Account").getEvent(), payload.getPBFPubKey());

        networkRequest.charge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {
                mView.showProgressIndicator(false);

                if (response.getData() != null) {
                    String authUrlCrude = response.getData().getAuthurl();
                    String flwRef = response.getData().getFlwRef();
                    boolean isValidUrl = urlValidator.isUrlValid(authUrlCrude);
                    if (authUrlCrude != null && isValidUrl) {
                        mView.displayInternetBankingPage(authUrlCrude, flwRef);
                    } else {
                        if (response.getData().getValidateInstruction() != null) {
                            mView.collectOtp(payload.getPBFPubKey(), flwRef, response.getData().getValidateInstruction());
                        } else if (response.getData().getValidateInstructions() != null &&
                                response.getData().getValidateInstructions().getInstruction() != null) {
                            mView.collectOtp(payload.getPBFPubKey(), flwRef, response.getData().getValidateInstructions().getInstruction());
                        } else {
                            mView.collectOtp(payload.getPBFPubKey(), flwRef, null);
                        }
                    }
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
    public void authenticateAccountCharge(final String flwRef, String otp, final String PBFPubKey) {

        ValidateChargeBody body = new ValidateChargeBody();
        body.setPBFPubKey(PBFPubKey);
        body.setOtp(otp);
        body.setTransactionreference(flwRef);

        mView.showProgressIndicator(true);

        logEvent(new ValidationAttemptEvent("Account").getEvent(), PBFPubKey);

        networkRequest.validateAccountCharge(body, new ResultCallback<ChargeResponse>() {
            @Override
            public void onSuccess(ChargeResponse response) {
                mView.showProgressIndicator(false);

                if (response.getStatus() != null) {
                    String status = response.getStatus();
                    String message = response.getMessage();

                    if (status.equalsIgnoreCase(success)) {
                        requeryTx(flwRef, PBFPubKey);
                    } else {
                        mView.onPaymentError(status);
                    }
                } else {
                    mView.onPaymentError(invalidCharge);
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
    public void fetchFee(final Payload payload) {

        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPtype("2");
        body.setPBFPubKey(payload.getPBFPubKey());

        mView.showProgressIndicator(true);

        networkRequest.getFee(body, new ResultCallback<FeeCheckResponse>() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.onTransactionFeeRetrieved(response.getData().getCharge_amount(), payload);
                } catch (Exception e) {
                    e.printStackTrace();
                    mView.onPaymentError(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RAVEPAY, message);
                mView.onPaymentError(message);
            }
        });
    }

    @Override
    public void requeryTx(String flwRef, String publicKey) {
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
                mView.showProgressIndicator(false);
                mView.onPaymentFailed(responseAsJSONString);
            }
        });
    }

    public void verifyRequeryResponseStatus(String responseAsJSONString) {
        mView.showProgressIndicator(true);

        boolean wasTxSuccessful = transactionStatusChecker
                .getTransactionStatus(
                        responseAsJSONString
                );

        mView.showProgressIndicator(false);

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(responseAsJSONString);
        } else {
            mView.onPaymentFailed(responseAsJSONString);
        }
    }


    @Override
    public void onAttachView(AccountContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullAccountView();
    }

    public void logEvent(Event event, String publicKey) {
        event.setPublicKey(publicKey);
        eventLogger.logEvent(event);
    }
}

package com.flutterwave.raveandroid.rave_presentation.ach;

import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.DaggerTestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestNetworkModule;
import com.flutterwave.raveandroid.rave_presentation.TestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestUtilsModule;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AchHandlerTest {


    AchHandler paymentHandler;
    @Mock
    AchContract.Interactor interactor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    UrlValidator urlValidator;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new AchHandler(interactor);

        TestRaveComponent component = DaggerTestRaveComponent.builder()
                .testNetworkModule(new TestNetworkModule())
                .testUtilsModule(new TestUtilsModule())
                .build();

        component.inject(this);
        component.inject(paymentHandler);
    }


    @Test
    public void chargeAccount_noDisplayFee_onSuccess_validResponseReturned_showWebViewCalled() {

        Payload payload = generatePayload();

        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeAccount(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse());

        verify(interactor).showProgressIndicator(false);
        verify(interactor, never()).onTransactionFeeRetrieved(any(String.class), any(Payload.class), any(String.class));
        verify(interactor).showWebView(any(String.class), any(String.class));

    }

    @Test
    public void chargeAccount_onSuccess_nullChargeResponseReturned_onPaymentErrorCalled_noResponseMessage() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeAccount(payload, generateRandomString());

        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse());
        verify(interactor).showProgressIndicator(false);

        verify(interactor).onPaymentError(RaveConstants.noResponse);

    }

    @Test
    public void chargeAccount_onSuccess_inValidResponseReturned_onPaymentErrorCalled_noAuthUrlMessage() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeAccount(payload, generateRandomString());

        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse());
        verify(interactor).showProgressIndicator(false);

        verify(interactor).onPaymentError(RaveConstants.no_authurl_was_returnedmsg);

    }

    @Test
    public void chargeAccount_onError_onPaymentErrorCalled_messageReturned() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        String message = generateRandomString();

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeAccount(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message);

        verify(interactor).showProgressIndicator(false);
        verify(interactor).onPaymentError(message);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalledWithCorrectParams() {

        String message = generateRandomString();
        String jsonResponse = generateRandomString();

        paymentHandler.requeryTx(generateRandomString(), generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, jsonResponse);
        verify(interactor).onPaymentFailed(jsonResponse);

    }

    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalled() {
        when(transactionStatusChecker.getTransactionStatus(anyString())).thenReturn(false);
        paymentHandler.verifyRequeryResponseStatus(generateRandomString());
        verify(interactor).onPaymentFailed(anyString());
    }

    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalled() {

        when(transactionStatusChecker.getTransactionStatus(any(String.class)))
                .thenReturn(true);

        paymentHandler.verifyRequeryResponseStatus(generateRandomString());
        verify(interactor).onPaymentSuccessful(anyString());
    }


    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

    private boolean generateRandomBoolean() {
        return new Random().nextBoolean();
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    private RequeryResponse generateRequerySuccessful() {
        return new RequeryResponse();
    }

    private Payload generatePayload() {
        List<Meta> metas = new ArrayList<>();
        List<SubAccount> subAccounts = new ArrayList<>();
        return new Payload(generateRandomString(), metas, subAccounts, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
    }

    private ChargeResponse generateNullChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(null);

        return chargeResponse;
    }

    private PayloadBuilder generatePayloadBuilder() {
        PayloadBuilder payloadBuilder = new PayloadBuilder();
        payloadBuilder.setPaymentPlan(generateRandomString());
        return payloadBuilder;
    }

    private ChargeResponse generateRandomChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(new ChargeResponse.Data());
        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.getData().setAuthurl(null);
        return chargeResponse;
    }

    private ChargeResponse generateValidChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(new ChargeResponse.Data());
        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.getData().setCurrency(generateRandomString());
        chargeResponse.getData().setChargedAmount(generateRandomString());
        chargeResponse.getData().setAuthurl(generateRandomString());
        chargeResponse.getData().setFlwRef(generateRandomString());
        return chargeResponse;
    }
}
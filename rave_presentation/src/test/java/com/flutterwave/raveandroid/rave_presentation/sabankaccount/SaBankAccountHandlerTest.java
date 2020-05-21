package com.flutterwave.raveandroid.rave_presentation.sabankaccount;

import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.DaggerTestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestNetworkModule;
import com.flutterwave.raveandroid.rave_presentation.TestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestUtilsModule;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
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

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaBankAccountHandlerTest {

    @Mock
    SaBankAccountContract.Interactor interactor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    private SaBankAccountHandler paymentHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new SaBankAccountHandler(interactor);


        TestRaveComponent component = DaggerTestRaveComponent.builder()
                .testNetworkModule(new TestNetworkModule())
                .testUtilsModule(new TestUtilsModule())
                .build();

        component.inject(this);
        component.inject(paymentHandler);
    }


    @Test
    public void fetchFee_onError_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        String message = generateRandomString();

        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message);

        //assert
        verify(interactor).showFetchFeeFailed(message);

    }

    @Test
    public void fetchFee_onSuccess_onTransactionFeeRetrievedWithCorrectParams() {

        //arrange
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();
        Payload payload = generatePayload();

        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(interactor).onTransactionFeeRetrieved(feeCheckResponse.getData().getCharge_amount(), payload, feeCheckResponse.getData().getFee());

    }

    @Test
    public void chargeSaBankAccount_noDisplayFee_onSuccess_validResponseReturned_showWebinteractorCalled() {

        Payload payload = generatePayload();

        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());


        paymentHandler.chargeSaBankAccount(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).chargeSaBankAccount(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse());

//        verify(sharedPrefsRequest).saveFlwRef(any(String.class));
        verify(interactor).showProgressIndicator(false);
        verify(interactor, never()).onTransactionFeeRetrieved(any(String.class), any(Payload.class), anyString());
        verify(interactor).showWebView(any(String.class), any(String.class));

    }


    @Test
    public void chargeSaBankAccount_onSuccess_noRedirectUrlReturned_onPaymentErrorCalled_invalidRedirectMessage() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeSaBankAccount(payload, generateRandomString());

        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).chargeSaBankAccount(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateChargeResponseWithNoRedirectUrl());
        verify(interactor).showProgressIndicator(false);

        verify(interactor).onPaymentError(RaveConstants.inValidRedirectUrl);

    }


    @Test
    public void chargeSaBankAccount_onError_onPaymentErrorCalled_messageReturned() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        String message = generateRandomString();

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeSaBankAccount(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).chargeSaBankAccount(any(ChargeRequestBody.class), captor.capture());

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
        verify(interactor).onPaymentFailed(message, jsonResponse);

    }

    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalled() {
        when(transactionStatusChecker.getTransactionStatus(anyString())).thenReturn(false);
        paymentHandler.verifyRequeryResponseStatus(generateRequerySuccessful(), generateRandomString());
        verify(interactor).onPaymentFailed(String.valueOf(anyObject()), anyString());
    }

    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalled() {

        when(transactionStatusChecker.getTransactionStatus(any(String.class)))
                .thenReturn(true);

        paymentHandler.verifyRequeryResponseStatus(generateRequerySuccessful(), generateRandomString());
        verify(interactor).onPaymentSuccessful(String.valueOf(anyObject()), anyString());
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

    private SaBankAccountResponse generateChargeResponseWithNoRedirectUrl() {
        SaBankAccountResponse chargeResponse = generateValidChargeResponse();

        chargeResponse.getData().getData().setRedirectUrl(null);

        return chargeResponse;
    }

    private SaBankAccountResponse generateValidChargeResponse() {
        SaBankAccountResponse chargeResponse = new SaBankAccountResponse();
        chargeResponse.setData(new SaBankAccountResponse.Data());
        chargeResponse.getData().setResponseCode("00");
        chargeResponse.getData().setResponseMessage(generateRandomString());
        chargeResponse.getData().setData(new SaBankAccountResponse.Data_());
        chargeResponse.getData().getData().setRedirectUrl(generateRandomString());
        chargeResponse.getData().getData().setFlwReference(generateRandomString());
        return chargeResponse;
    }

    private FeeCheckResponse generateFeeCheckResponse() {
        FeeCheckResponse feeCheckResponse = new FeeCheckResponse();
        FeeCheckResponse.Data feeCheckResponseData = new FeeCheckResponse.Data();

        feeCheckResponseData.setCharge_amount(generateRandomString());
        feeCheckResponse.setData(feeCheckResponseData);
        feeCheckResponse.getData().setFee(generateRandomString());

        return feeCheckResponse;
    }
}
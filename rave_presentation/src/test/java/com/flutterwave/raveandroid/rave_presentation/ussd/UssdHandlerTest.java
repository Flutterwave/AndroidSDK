package com.flutterwave.raveandroid.rave_presentation.ussd;

import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.DaggerTestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestNetworkModule;
import com.flutterwave.raveandroid.rave_presentation.TestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestUtilsModule;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UssdHandlerTest {
    @Mock
    UssdContract.Interactor interactor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Mock
    UssdHandler paymentHandlerMock;
    @Mock
    RequeryRequestBody requeryRequestBody;
    private UssdHandler paymentHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new UssdHandler(interactor);


        TestRaveComponent component = DaggerTestRaveComponent.builder()
                .testNetworkModule(new TestNetworkModule())
                .testUtilsModule(new TestUtilsModule())
                .build();

        component.inject(this);
        component.inject(paymentHandler);
    }


    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled_errorOccurredMessage() {

        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        String errorMessage = generateRandomString();
        captor.getAllValues().get(0).onError(errorMessage);
        verify(interactor).showProgressIndicator(false);
        verify(interactor).showFetchFeeFailed(errorMessage);

    }

    @Test
    public void fetchFee_onSuccess_onTransactionFeeFetchedCalled() {

        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(interactor).onTransactionFeeFetched(anyString(), any(Payload.class), anyString());

    }

    @Test
    public void fetchFee_onSuccess_Exception_showFetchFeeFailedCalled() throws NullPointerException {

        doThrow(NullPointerException.class).when(interactor).onTransactionFeeFetched(any(String.class), any(Payload.class), anyString());
        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(new FeeCheckResponse());
        verify(interactor).showFetchFeeFailed("An error occurred while retrieving transaction fee");

    }

    @Test
    public void payWithUssd_chargeCard_onSuccess_onUssdDetailsReceivedCalled() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.payWithUssd(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        ChargeResponse chargeResponse = generateValidChargeResponse();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        verify(interactor).showProgressIndicator(false);
        verify(interactor).onUssdDetailsReceived(chargeResponse.getData().getUssdData().getNote(), chargeResponse.getData().getUssdData().getReference_code());
    }

    @Test
    public void payWithUssd_chargeCard_onSuccess_nullResponse_onUssdDetailsReceivedCalled() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.payWithUssd(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        ChargeResponse chargeResponse = new ChargeResponse();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        verify(interactor).onPaymentError("No response data was returned");
    }

    @Test
    public void payWithUssd_chargeCard_onError_onPaymentErrorCalled() {
        Payload payload = generatePayload();

        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.payWithUssd(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        String message = generateRandomString();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message);

        verify(interactor).showProgressIndicator(false);
        verify(interactor).onPaymentError(message);
    }

    @Test
    public void startPaymentVerification_requeryTxCalled() {
        paymentHandler.startPaymentVerification(300);
        long time = System.currentTimeMillis();

        String randomflwRef = generateRandomString();
        String randomPubKey = generateRandomString();
        verify(interactor).showPollingIndicator(true);

        paymentHandlerMock.requeryTx(randomflwRef, randomPubKey, time, 300);
        verify(paymentHandlerMock).requeryTx(randomflwRef, randomPubKey, time, 300);
    }

    @Test
    public void cancelPolling_pollingCancelledTrue() {
        paymentHandler.cancelPolling();

        assertTrue(paymentHandler.pollingCancelled);
    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_00_Called() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = System.currentTimeMillis();

        paymentHandler.requeryTx(randomflwRef, randomPubKey, time, 300);
        requeryRequestBody.setFlw_ref(generateRandomString());
        requeryRequestBody.setPBFPubKey(generateRandomString());
        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("00"), responseJson);

        verify(interactor).showPollingIndicator(false);
        verify(interactor).onPaymentSuccessful(randomflwRef, responseJson);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_02_onPollingTimeoutCalled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = 400000;
        paymentHandler.requeryTx(randomflwRef, randomPubKey, time, 300);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("02"), responseJson);

        verify(interactor).showPollingIndicator(false);
        verify(interactor).onPollingTimeout(randomflwRef, responseJson);

    }


    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCode02_requeryTxCalled() {

        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        paymentHandler.requeryTx(flwRef, encryptionKey, System.currentTimeMillis(), 300000);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful("02");
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(networkRequest, times(2)).requeryTx(any(RequeryRequestBody.class), any(Callbacks.OnRequeryRequestComplete.class));

    }

    @Test
    public void requeryTx_onSuccess_nullResponse_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), time, 300);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccess_chargeResponseCodeNeither00Nor02_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), time, 300);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("099"), jsonResponse);

        verify(interactor).showProgressIndicator(false);
        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), time, 300);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());

        verify(interactor).onPaymentFailed(anyString(), anyString());

    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    private Payload generatePayload() {
        List<Meta> metas = new ArrayList<>();
        List<SubAccount> subAccounts = new ArrayList<>();
        return new Payload(generateRandomString(), metas, subAccounts, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
    }

    private RequeryResponse generateRequerySuccessful(String responseCode) {
        RequeryResponse requeryResponse = new RequeryResponse();
        RequeryResponse.Data data = new RequeryResponse.Data();
        data.setChargeResponseCode(responseCode);
        requeryResponse.setData(data);
        return requeryResponse;
    }

    private ChargeResponse generateValidChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(new ChargeResponse.Data());
        chargeResponse.getData().setUssdData(new ChargeResponse.Data());

        ChargeResponse.Data data = chargeResponse.getData().getUssdData();

        data.setFlw_reference(generateRandomString());
        data.setNote(generateRandomString());
        data.setReference_code(generateRandomString());


        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.getData().setChargedAmount(generateRandomString());
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
package com.flutterwave.raveandroid.rave_presentation.uk;

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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UkHandlerTest {

    @Mock
    UkContract.Interactor interactor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private UkHandler paymentHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new UkHandler(interactor);


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

        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        String error = generateRandomString();
        captor.getAllValues().get(0).onError(error);

        //assert
        verify(interactor).showFetchFeeFailed(error);

    }

    @Test
    public void fetchFee_onSuccess_onTransactionFeeFetchedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();

        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(interactor).onTransactionFeeFetched(feeCheckResponse.getData().getCharge_amount(), payload, feeCheckResponse.getData().getFee());

    }

    @Test
    public void fetchFee_onSuccess_exceptionThrown_showFetchFeeFailedCalledWithCorrectParams() {

        paymentHandler.fetchFee(generatePayload());

        doThrow(new NullPointerException()).when(interactor).onTransactionFeeFetched(any(String.class), any(Payload.class), anyString());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(interactor, times(1)).showFetchFeeFailed(transactionError);

    }

    @Test(expected = Exception.class)
    public void fetchFee_onSuccess_displayFeeException_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();
        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        doThrow(new Exception()).when(interactor).onTransactionFeeFetched(feeCheckResponse.getData().getCharge_amount(), payload, feeCheckResponse.getData().getFee());

        //assert
        verify(interactor).showFetchFeeFailed(transactionError);

    }


    @Test
    public void chargeUK_onSuccess_requeryTxCalled() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();

        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        paymentHandler.chargeUk(payload, encryptionKey);

        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).chargeWithPolling(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateValidChargeResponse());

        //assert
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

    }

    @Test
    public void chargeUK_onError_onPaymentErrorCalled() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        paymentHandler.chargeUk(payload, encryptionKey);

        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);
        String message = generateRandomString();
        verify(networkRequest).chargeWithPolling(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message);

        verify(interactor).onPaymentError(message);

    }

    @Test
    public void chargeUK_onSuccessWithNullData_onPaymentError_noResponse_Called() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        paymentHandler.chargeUk(payload, encryptionKey);

        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).chargeWithPolling(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateNullChargeResponse());

        //assert
        verify(interactor).onPaymentError(noResponse);

    }


    @Test
    public void requeryTx_onSuccess_nullData_onPaymentFailedCalled() {

        //arrange
        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        //act
        paymentHandler.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateNullQuery();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithChargeResponseCode00_onPaymentSuccessfulCalled() {

        //arrange
        String flwRef = generateRandomString();
        String txRef = generateRandomString();

        //act
        paymentHandler.requeryTx(flwRef, txRef, generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_00();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(interactor).onPaymentSuccessful(flwRef, txRef, jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithChargeResponseCode02_onPollingRoundComplete() {

        //arrange
        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        //act
        paymentHandler.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_02();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(networkRequest, times(2)).requeryTx(any(RequeryRequestBody.class), any(Callbacks.OnRequeryRequestComplete.class));

    }

    @Test
    public void requeryTx_onSuccessWithChargeResponseCodeNot00or02_onPaymentFailedCalled() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        paymentHandler.chargeUk(payload, encryptionKey);
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRandomRequerySuccessful();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        //arrange
        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();
        String message = generateRandomString();
        String jsonResponse = generateRandomString();

        //act
        paymentHandler.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message, jsonResponse);

        //assert
        verify(interactor).onPaymentFailed(message, jsonResponse);

    }


    private FeeCheckResponse generateFeeCheckResponse() {
        FeeCheckResponse feeCheckResponse = new FeeCheckResponse();
        FeeCheckResponse.Data feeCheckResponseData = new FeeCheckResponse.Data();

        feeCheckResponseData.setCharge_amount(generateRandomString());
        feeCheckResponse.setData(feeCheckResponseData);
        feeCheckResponse.getData().setFee(generateRandomString());

        return feeCheckResponse;
    }

    private RequeryResponse generateNullQuery() {
        return new RequeryResponse();
    }

    private RequeryResponse generateRequerySuccessful_00() {
        RequeryResponse requeryResponse = new RequeryResponse();
        RequeryResponse.Data data = new RequeryResponse.Data();
        data.setChargeResponseCode("00");
        requeryResponse.setData(data);
        return requeryResponse;
    }

    private RequeryResponse generateRequerySuccessful_02() {
        RequeryResponse requeryResponse = new RequeryResponse();
        RequeryResponse.Data data = new RequeryResponse.Data();
        data.setChargeResponseCode("02");
        requeryResponse.setData(data);
        return requeryResponse;
    }

    private RequeryResponse generateRandomRequerySuccessful() {
        RequeryResponse requeryResponse = new RequeryResponse();
        RequeryResponse.Data data = new RequeryResponse.Data();
        data.setChargeResponseCode("03");
        requeryResponse.setData(data);
        return requeryResponse;
    }

    private Payload generatePayload() {
        List<Meta> metas = new ArrayList<>();
        List<SubAccount> subAccounts = new ArrayList<>();
        return new Payload(generateRandomString(), metas, subAccounts, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
    }

    private ChargeResponse generateRandomChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        ChargeResponse.Data chargeResponseData = new ChargeResponse.Data();

        chargeResponseData.setChargeResponseCode(generateRandomString());
        chargeResponse.setData(chargeResponseData);

        return chargeResponse;
    }

    private ChargeResponse generateNullChargeResponse() {
        return new ChargeResponse();
    }

    private ChargeResponse generateValidChargeResponse() {
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.setStatus(success);
        chargeResponse.getData().setAuthurl("http://www.rave.com");
        chargeResponse.getData().setFlwRef(generateRandomString());
        chargeResponse.getData().setTx_ref(generateRandomString());
        return chargeResponse;
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }


}
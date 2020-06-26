package com.flutterwave.raveandroid.rave_presentation.rwfmobilemoney;

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
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.MobileMoneyChargeResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RwfMobileMoneyHandlerTest {

    @Mock
    RwfMobileMoneyContract.Interactor interactor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private RwfMobileMoneyHandler paymentHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new RwfMobileMoneyHandler(interactor);


        TestRaveComponent component = DaggerTestRaveComponent.builder()
                .testNetworkModule(new TestNetworkModule())
                .testUtilsModule(new TestUtilsModule())
                .build();

        component.inject(this);
        component.inject(paymentHandler);
    }


    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled() {

        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        verify(interactor).showFetchFeeFailed(anyString());

    }

    @Test
    public void fetchFee_onSuccess_onTransactionFeeRetrievedCalled() {

        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(interactor).onTransactionFeeRetrieved(anyString(), any(Payload.class), anyString());

    }


    @Test
    public void chargeRwfMobileMoney_onSuccess_requeryTxCalled() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeRwfMobileMoney(payload, generateRandomString());

        ArgumentCaptor<ResultCallback> onGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), onGhanaChargeRequestCompleteArgumentCaptor.capture());

        onGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateValidRwfChargeResponse());

        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

    }

    @Test
    public void chargeRwfMobileMoney_onError_onPaymentErrorCalled() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeRwfMobileMoney(payload, generateRandomString());

        ArgumentCaptor<ResultCallback> OnGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);
        String message = generateRandomString();
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), OnGhanaChargeRequestCompleteArgumentCaptor.capture());

        OnGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message);

        verify(interactor).onPaymentError(message);

    }

    @Test
    public void chargeRwfMobileMoney_onSuccessWithNullData_onPaymentError_noResponse_Called() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.chargeRwfMobileMoney(payload, generateRandomString());

        ArgumentCaptor<ResultCallback> onGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), onGhanaChargeRequestCompleteArgumentCaptor.capture());

        onGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateNullRwfChargeResponse());

        verify(interactor).onPaymentError(noResponse);

    }


    @Test
    public void requeryTx_onSuccess_nullData_onPaymentFailedCalled() {

        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateNullQuery();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCode00_onPaymentSuccessfulCalled() {

        String flwRef = generateRandomString();
        String txRef = generateRandomString();

        paymentHandler.requeryTx(flwRef, txRef, generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_00();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(interactor).onPaymentSuccessful(flwRef, txRef, jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCode02_onPollingRoundCompleteCalled() {

        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        paymentHandler.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_02();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(networkRequest, times(2)).requeryTx(any(RequeryRequestBody.class), any(Callbacks.OnRequeryRequestComplete.class));

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCodeNot00or02_onPaymentFailedCalled() {

        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRandomRequerySuccessful();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        String message = generateRandomString();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onError(message, jsonResponse);

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

    private MobileMoneyChargeResponse generateRandomChargeResponse() {
        MobileMoneyChargeResponse chargeResponse = new MobileMoneyChargeResponse();
        MobileMoneyChargeResponse.Data chargeResponseData = new MobileMoneyChargeResponse.Data();

        chargeResponseData.setChargeResponseCode(generateRandomString());
        chargeResponse.setData(chargeResponseData);

        return chargeResponse;
    }

    private MobileMoneyChargeResponse generateNullRwfChargeResponse() {
        return new MobileMoneyChargeResponse();
    }

    private MobileMoneyChargeResponse generateValidRwfChargeResponse() {
        MobileMoneyChargeResponse mobileMoneyChargeResponse = generateRandomChargeResponse();
        mobileMoneyChargeResponse.getData().setChargeResponseCode("00");
        mobileMoneyChargeResponse.setStatus(success);
        mobileMoneyChargeResponse.getData().setAuthurl("http://www.rave.com");
        mobileMoneyChargeResponse.getData().setFlwRef(generateRandomString());
        mobileMoneyChargeResponse.getData().setTx_ref(generateRandomString());
        return mobileMoneyChargeResponse;
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }
}
package com.flutterwave.raveandroid.rave_presentation.banktransfer;

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

import static junit.framework.TestCase.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BankTransferHandlerTest {


    @Mock
    BankTransferContract.BankTransferInteractor interactor;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    RemoteRepository networkRequest;
    @Mock
    RequeryRequestBody requeryRequestBody;
    private BankTransferHandler paymentHandler;
    @Mock
    private BankTransferHandler paymentHandlerMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new BankTransferHandler(interactor);


        TestRaveComponent component = DaggerTestRaveComponent.builder()
                .testNetworkModule(new TestNetworkModule())
                .testUtilsModule(new TestUtilsModule())
                .build();

        component.inject(this);
        component.inject(paymentHandler);
    }

    @Test
    public void fetchFee_onError_onFetchFeeErrorCalled() {

        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());
        verify(interactor).showProgressIndicator(false);
        verify(interactor).onFetchFeeError(anyString());

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
        verify(interactor).onFetchFeeError(anyString());

    }

    @Test
    public void payWithBankTransfer_chargeCard_onSuccess_onTransferDetailsReceivedCalled() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.payWithBankTransfer(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        ChargeResponse chargeResponse = generateValidChargeResponse();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        verify(interactor).showProgressIndicator(false);
        verify(interactor).onTransferDetailsReceived(chargeResponse.getData().getAmount(), chargeResponse.getData().getAccountnumber(), chargeResponse.getData().getBankname(), chargeResponse.getData().getNote().substring(
                chargeResponse.getData().getNote().indexOf("to ") + 3));
    }

    @Test
    public void payWithBankTransfer_chargeCard_onSuccess_nullResponse_onTransferDetailsReceivedCalled() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
        paymentHandler.payWithBankTransfer(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        ChargeResponse chargeResponse = new ChargeResponse();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        verify(interactor).onPaymentError("No response data was returned");
    }

    @Test
    public void payWithBankTransfer_chargeCard_onError_onPaymentErrorCalled() {
        Payload payload = generatePayload();
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        paymentHandler.payWithBankTransfer(payload, generateRandomString());
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
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        verify(interactor).showPollingIndicator(true);

        paymentHandlerMock.requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);
        verify(paymentHandlerMock).requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);
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

        paymentHandler.requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);
        requeryRequestBody.setFlw_ref(generateRandomString());
        requeryRequestBody.setPBFPubKey(generateRandomString());
        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("00"), responseJson);

        verify(interactor).showPollingIndicator(false);
        verify(interactor).onPaymentSuccessful(randomflwRef, randomTxRef, responseJson);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_01_onPollingTimeoutCalled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = 400000;
        paymentHandler.requeryTx(randomflwRef, randomTxRef, randomPubKey, false, time, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("01"), responseJson);

        verify(interactor).showPollingIndicator(false);
        verify(interactor).onPollingTimeout(randomflwRef, randomTxRef, responseJson);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_01_requeryTxCalled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = System.currentTimeMillis() - 10000;

        doCallRealMethod().when(paymentHandlerMock).requeryTx(
                any(String.class),
                any(String.class),
                any(String.class),
                anyBoolean(),
                anyLong(), eq(time));

        paymentHandlerMock.networkRequest = networkRequest;
        paymentHandlerMock.pollingCancelled = false;
        paymentHandlerMock.mInteractor = interactor;
        paymentHandlerMock.requeryTx(randomflwRef, randomTxRef, randomPubKey, false, time, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("01"), responseJson);

        verify(paymentHandlerMock, times(2))
                .requeryTx(anyString(), anyString(), anyString(), anyBoolean(), anyLong(), eq(time));

    }


    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_01_PollingCancelled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();

        long time = System.currentTimeMillis();
        paymentHandler.requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("01"), responseJson);

        verify(interactor).showPollingIndicator(false);
        verify(interactor).onPollingCanceled(randomflwRef, randomTxRef, responseJson);

    }


    @Test
    public void requeryTx_onSuccess_nullResponse_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString(), true, time, time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccess_chargeResponseCodeNeither00Nor01_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString(), true, time, time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("099"), jsonResponse);

        verify(interactor).showProgressIndicator(false);
        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        paymentHandler.requeryTx(generateRandomString(), generateRandomString(), generateRandomString(), true, time, time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
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

        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.getData().setNote("Please transfer to Abakaliki General");
        chargeResponse.getData().setChargedAmount(generateRandomString());
        chargeResponse.getData().setBankName(generateRandomString());
        chargeResponse.getData().setAccountnumber(generateRandomString());
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
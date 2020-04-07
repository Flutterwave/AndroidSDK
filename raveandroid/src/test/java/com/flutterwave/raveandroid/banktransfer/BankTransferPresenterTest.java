package com.flutterwave.raveandroid.banktransfer;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestremoteModule;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class BankTransferPresenterTest {

    @Mock
    BankTransferUiContract.View view;
    @Inject
    Context context;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    Bundle bundle;
    @Mock
    BankTransferPresenter bankTransferPresenterMock;
    @Mock
    PayloadBuilder payloadBuilder;
    @Mock
    RequeryRequestBody requeryRequestBody;
    private BankTransferPresenter bankTransferPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        bankTransferPresenter = new BankTransferPresenter(context, view);

        TestRaveUiComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testremoteModule(new TestremoteModule())
                .build();

        component.inject(this);
        component.inject(bankTransferPresenter);
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled_errorOccuredMessage() {

        bankTransferPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());
        verify(view).showProgressIndicator(false);
        verify(view).onFetchFeeError("An error occurred while retrieving transaction fee");

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalled() {

        bankTransferPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view).onTransactionFeeFetched(anyString(), any(Payload.class), );

    }

    @Test
    public void fetchFee_onSuccess_Exception_showFetchFeeFailedCalled() throws NullPointerException {

        doThrow(NullPointerException.class).when(view).onTransactionFeeFetched(any(String.class), any(Payload.class), );
        bankTransferPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(new FeeCheckResponse());
        verify(view).onFetchFeeError("An error occurred while retrieving transaction fee");

    }

    @Test
    public void payWithBankTransfer_chargeCard_onSuccess_onTransferDetailsReceivedCalled() {
        Payload payload = generatePayload();
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        bankTransferPresenter.payWithBankTransfer(payload, generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        ChargeResponse chargeResponse = generateValidChargeResponse();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, any(String.class));

        verify(view).showProgressIndicator(false);
        verify(view).onTransferDetailsReceived(chargeResponse.getData().getAmount(), chargeResponse.getData().getAccountnumber(), chargeResponse.getData().getBankname(), chargeResponse.getData().getNote().substring(
                chargeResponse.getData().getNote().indexOf("to ") + 3));
    }

    @Test
    public void payWithBankTransfer_chargeCard_onSuccess_nullResponse_onTransferDetailsReceivedCalled() {
        Payload payload = generatePayload();
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
        bankTransferPresenter.payWithBankTransfer(payload, generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        ChargeResponse chargeResponse = new ChargeResponse();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        verify(view).onPaymentError("No response data was returned");
    }

    @Test
    public void payWithBankTransfer_chargeCard_onError_onPaymentErrorCalled() {
        Payload payload = generatePayload();
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        bankTransferPresenter.payWithBankTransfer(payload, generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        String message = generateRandomString();

        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(message);
    }

    @Test
    public void startPaymentVerification_requeryTxCalled() {
        bankTransferPresenter.startPaymentVerification(pollingTimeoutInSeconds);
        long time = System.currentTimeMillis();

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        verify(view).showPollingIndicator(true);

        bankTransferPresenterMock.requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);
        verify(bankTransferPresenterMock).requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);
    }

    @Test
    public void cancelPolling_pollingCancelledTrue() {
        bankTransferPresenter.cancelPolling();

        assertTrue(bankTransferPresenter.pollingCancelled);
    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_00_Called() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = System.currentTimeMillis();

        bankTransferPresenter.requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);
        requeryRequestBody.setFlw_ref(generateRandomString());
        requeryRequestBody.setPBFPubKey(generateRandomString());
        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("00"), responseJson);

        verify(view).showPollingIndicator(false);
        verify(view).onPaymentSuccessful(randomflwRef, randomTxRef, responseJson);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_01_onPollingTimeoutCalled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = 400000;
        bankTransferPresenter.requeryTx(randomflwRef, randomTxRef, randomPubKey, false, time, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("01"), responseJson);

        verify(view).showPollingIndicator(false);
        verify(view).onPollingTimeout(randomflwRef, randomTxRef, responseJson);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_01_requeryTxCalled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = System.currentTimeMillis() - 10000;

        doCallRealMethod().when(bankTransferPresenterMock).requeryTx(
                any(String.class),
                any(String.class),
                any(String.class),
                anyBoolean(),
                anyLong(), time);

        bankTransferPresenterMock.networkRequest = networkRequest;
        bankTransferPresenterMock.pollingCancelled = false;
        bankTransferPresenterMock.mView = view;
        bankTransferPresenterMock.requeryTx(randomflwRef, randomTxRef, randomPubKey, false, time, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("01"), responseJson);

        verify(bankTransferPresenterMock, times(2))
                .requeryTx(anyString(), anyString(), anyString(), anyBoolean(), anyLong(), time);

    }


    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_01_PollingCancelled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();

        long time = System.currentTimeMillis();
        bankTransferPresenter.requeryTx(randomflwRef, randomTxRef, randomPubKey, true, time, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("01"), responseJson);

        verify(view).showPollingIndicator(false);
        verify(view).onPollingCanceled(randomflwRef, randomTxRef, responseJson);

    }


    @Test
    public void requeryTx_onSuccess_nullResponse_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        bankTransferPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString(), true, time, time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccess_chargeResponseCodeNeither00Nor01_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        bankTransferPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString(), true, time, time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("099"), jsonResponse);

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        bankTransferPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString(), true, time, time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryPayWithBankTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());

        verify(view).onPaymentFailed(anyString(), anyString());

    }


    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        bankTransferPresenter.init(ravePayInitializer);

        verify(view).onAmountValidationSuccessful(amount.toString());

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        bankTransferPresenter.init(ravePayInitializer);
        verify(view).onAmountValidationFailed();

    }


    @Test
    public void onDataCollected_inValidDataPassed_showFieldErrorCalled() {
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> hashMap = generateViewData(viewID);
        int failedValidations = 1;
        generateViewValidation(failedValidations);

        bankTransferPresenter.onDataCollected(hashMap);

        verify(view, times(failedValidations)).showFieldError(viewID, RaveConstants.validAmountPrompt, hashMap.get(fieldAmount).getViewType());

    }


    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID);
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        bankTransferPresenter.onDataCollected(map);
        //assert
        verify(view).onValidationSuccessful(map);

    }


    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        bankTransferPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(true);

    }

    @Test
    public void processTransaction_displayFeeIsEnabled_payWithBankTransferCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        bankTransferPresenter.processTransaction(data, ravePayInitializer);
        bankTransferPresenterMock.payWithBankTransfer(generatePayload(), generateRandomString());
        //assert
        verify(view).showProgressIndicator(true);
        verify(bankTransferPresenterMock).payWithBankTransfer(any(Payload.class), any(String.class));

    }

    @Test
    public void processTransaction_payloadBuilderCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        bankTransferPresenter.processTransaction(data, ravePayInitializer);
        payloadBuilder.createBankPayload();
        //assert
        verify(view).showProgressIndicator(true);
        verify(payloadBuilder).createBankPayload();

    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeAccountCalled() {
        //arrange
        int viewID = generateRandomInt();
        Payload payload = generatePayload();
        HashMap<String, ViewObject> data = generateViewData(viewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadBuilder.createBankTransferPayload()).thenReturn(payload);
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(isNull(String.class), any(String.class))).thenReturn(generateRandomString());

        bankTransferPresenter.payWithBankTransfer(payload, generateRandomString());
        //act
        bankTransferPresenter.processTransaction(data, ravePayInitializer);
        //assert
        bankTransferPresenter.payWithBankTransfer(payload, generateRandomString());


    }

    @Test
    public void restoreState_onTransferDetailsReceivedCalled() {

        when(bundle.getString("amount")).thenReturn(generateRandomString());
        when(bundle.getString("benef_name")).thenReturn(generateRandomString());
        when(bundle.getString("bank_name")).thenReturn(generateRandomString());
        when(bundle.getString("account_number")).thenReturn(generateRandomString());

        bankTransferPresenter.restoreState(bundle);
        verify(view).onTransferDetailsReceived(any(String.class), any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void getState_hasTransferDetailsFalse_nullReturned() {
        bankTransferPresenter.hasTransferDetails = false;
        Bundle bundle = bankTransferPresenter.getState();

        assertNull(bundle);
    }

    @Test
    public void getState_hasTransferDetailsTrue_nullReturned() {
        bankTransferPresenter.hasTransferDetails = true;
        Bundle bundle = bankTransferPresenter.getState();

        assertNotNull(bundle);
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

    private HashMap<String, ViewObject> generateViewData(int viewID) {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(viewID, generateRandomDouble().toString(), TextInputLayout.class));

        return viewData;
    }

    private void generateViewValidation(int failedValidations) {

        List<Boolean> falses = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            if (i < failedValidations) {
                falses.add(false);
            } else {
                falses.add(true);
            }
        }

        when(amountValidator.isAmountValid(anyString())).thenReturn(falses.get(0));
    }

    private int generateRandomInt() {
        return new Random().nextInt();
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
        chargeResponse.getData().setNote(generateRandomString());
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

        return feeCheckResponse;
    }

    private boolean throwException() {
        throw new NullPointerException();
    }

    Bundle generateBundle() {
        Bundle bundle = new Bundle();
        bundle.putString("amount", "100");
        return bundle;
    }
}
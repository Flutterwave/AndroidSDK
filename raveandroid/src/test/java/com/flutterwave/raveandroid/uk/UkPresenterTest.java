package com.flutterwave.raveandroid.uk;

import android.content.Context;
import android.support.design.widget.TextInputLayout;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.PayloadEncryptor;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestAppComponent;
import com.flutterwave.raveandroid.di.TestremoteModule;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAccountBank;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAccountName;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAccountNumber;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UkPresenterTest {

    UkPresenter ukPresenter;
    @Mock
    UkContract.View view;
    @Inject
    Context context;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    PayloadEncryptor payloadEncryptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ukPresenter = new UkPresenter(context, view);

        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testremoteModule(new TestremoteModule())
                .build();

        component.inject(this);
        component.inject(ukPresenter);
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();

        //act
        ukPresenter.fetchFee(payload);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        //assert
        verify(view).showFetchFeeFailed(transactionError);

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();

        //act
        ukPresenter.fetchFee(payload);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(view).displayFee(feeCheckResponse.getData().getCharge_amount(), payload);

    }

    @Test
    public void fetchFee_onSuccess_exceptionThrown_showFetchFeeFailedCalledWithCorrectParams() {

        ukPresenter.fetchFee(generatePayload());

        doThrow(new NullPointerException()).when(view).displayFee(any(String.class), any(Payload.class));

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view, times(1)).showFetchFeeFailed(transactionError);

    }

    @Test(expected = Exception.class)
    public void fetchFee_onSuccess_displayFeeException_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        ukPresenter.fetchFee(payload);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        doThrow(new Exception()).when(view).displayFee(feeCheckResponse.getData().getCharge_amount(), payload);

        //assert
        verify(view).showFetchFeeFailed(transactionError);

    }


    @Test
    public void chargeUK_onSuccess_requeryTxCalled() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();

        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        ukPresenter.chargeUk(payload, encryptionKey);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeWithPolling(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());

        //assert
        ukPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

    }

    @Test
    public void chargeUK_onError_onPaymentErrorCalled() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        ukPresenter.chargeUk(payload, encryptionKey);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        String message = generateRandomString();
        verify(networkRequest).chargeWithPolling(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message, generateRandomString());

        verify(view).onPaymentError(message);

    }

    @Test
    public void chargeUK_onSuccessWithNullData_onPaymentError_noResponse_Called() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        ukPresenter.chargeUk(payload, encryptionKey);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeWithPolling(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateNullChargeResponse(), generateRandomString());

        //assert
        verify(view).onPaymentError(noResponse);

    }


    @Test
    public void requeryTx_onSuccess_nullData_onPaymentFailedCalled() {

        //arrange
        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        //act
        ukPresenter.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateNullQuery();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithChargeResponseCode00_onPaymentSuccessfulCalled() {

        //arrange
        String flwRef = generateRandomString();
        String txRef = generateRandomString();

        //act
        ukPresenter.requeryTx(flwRef, txRef, generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_00();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(view).onPaymentSuccessful(flwRef, txRef, jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithChargeResponseCode02_onPollingRoundComplete() {

        //arrange
        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        //act
        ukPresenter.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_02();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(view).onPollingRoundComplete(flwRef, txRef, encryptionKey);

    }

    @Test
    public void requeryTx_onSuccessWithChargeResponseCodeNot00or02_onPaymentFailedCalled() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        ukPresenter.chargeUk(payload, encryptionKey);
        ukPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRandomRequerySuccessful();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

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
        ukPresenter.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message, jsonResponse);

        //assert
        verify(view).onPaymentFailed(message, jsonResponse);

    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        ukPresenter.init(ravePayInitializer);

        verify(view).onAmountValidationSuccessful(String.valueOf(amount));
    }


    @Test
    public void onDataCollected_InvalidDataPassed_showFieldErrorCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 1;
        generateViewValidation(failedValidations);
        //act
        ukPresenter.onDataCollected(map);
        //assert
        verify(view, times(failedValidations)).showFieldError(anyInt(), anyString(), (Class<?>) anyObject());

    }

    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        ukPresenter.onDataCollected(map);
        //assert
        verify(view).onValidationSuccessful(any(HashMap.class));

    }


    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        ArgumentCaptor<Boolean> booleanArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        ukPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(booleanArgumentCaptor.capture());

        assertEquals(true, booleanArgumentCaptor.getAllValues().get(0));
    }

    @Test
    public void processTransaction_displayFeeIsEnabled_getFeeCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        ukPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), any(Callbacks.OnGetFeeRequestComplete.class));
    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeUKCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
        //act
        ukPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).chargeWithPolling(any(ChargeRequestBody.class), any(Callbacks.OnChargeRequestComplete.class));
    }


    private HashMap<String, ViewObject> generateViewData() {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(generateRandomInt(), generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldPhone, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        viewData.put(fieldAccountName, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        viewData.put(fieldAccountNumber, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        viewData.put(fieldAccountBank, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
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

    private FeeCheckResponse generateFeeCheckResponse() {
        FeeCheckResponse feeCheckResponse = new FeeCheckResponse();
        FeeCheckResponse.Data feeCheckResponseData = new FeeCheckResponse.Data();

        feeCheckResponseData.setCharge_amount(generateRandomString());
        feeCheckResponse.setData(feeCheckResponseData);

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

    private int generateRandomInt() {
        return new Random().nextInt();
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

}
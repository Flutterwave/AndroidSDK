package com.flutterwave.raveandroid.ghmobilemoney;

import android.content.Context;
import android.support.design.widget.TextInputLayout;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestremoteModule;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.MobileMoneyChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.NetworkValidator;
import com.flutterwave.raveandroid.validators.PhoneValidator;

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
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldNetwork;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldVoucher;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.networkPosition;
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

public class GhMobileMoneyPresenterTest {

    GhMobileMoneyPresenter ghMobileMoneyPresenter;
    @Mock
    GhMobileMoneyContract.View view;
    @Inject
    Context context;
    @Inject
    AmountValidator amountValidator;
    @Inject
    PhoneValidator phoneValidator;
    @Inject
    NetworkValidator networkValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    RemoteRepository networkRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ghMobileMoneyPresenter = new GhMobileMoneyPresenter(context, view);

        TestRaveUiComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testremoteModule(new TestremoteModule())
                .build();

        component.inject(this);
        component.inject(ghMobileMoneyPresenter);

    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled() {

        ghMobileMoneyPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        verify(view).showFetchFeeFailed(anyString());

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalled() {

        ghMobileMoneyPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view).displayFee(anyString(), any(Payload.class));

    }

    @Test
    public void fetchFee_onSuccess_exceptionThrown_showFetchFeeFailedCalledWithCorrectParams() {

        ghMobileMoneyPresenter.fetchFee(generatePayload());

        doThrow(new NullPointerException()).when(view).displayFee(any(String.class), any(Payload.class));

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view, times(1)).showFetchFeeFailed(transactionError);

    }


    @Test
    public void chargeGhMobileMoney_onSuccess_requeryTxCalled() {
        Payload payload = generatePayload();
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        ghMobileMoneyPresenter.chargeGhMobileMoney(payload, generateRandomString());

        ArgumentCaptor<Callbacks.OnGhanaChargeRequestComplete> onGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGhanaChargeRequestComplete.class);
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), onGhanaChargeRequestCompleteArgumentCaptor.capture());

        onGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateValidGhChargeResponse(), generateRandomString());

        ghMobileMoneyPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

    }

    @Test
    public void chargeGhMobileMoney_onError_onPaymentErrorCalled() {
        Payload payload = generatePayload();
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        ghMobileMoneyPresenter.chargeGhMobileMoney(payload, generateRandomString());

        ArgumentCaptor<Callbacks.OnGhanaChargeRequestComplete> OnGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGhanaChargeRequestComplete.class);
        String message = generateRandomString();
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), OnGhanaChargeRequestCompleteArgumentCaptor.capture());

        OnGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message, generateRandomString());

        verify(view).onPaymentError(message);

    }

    @Test
    public void chargeGhMobileMoney_onSuccessWithNullData_onPaymentErrorCalled() {
        Payload payload = generatePayload();
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        ghMobileMoneyPresenter.chargeGhMobileMoney(payload, generateRandomString());

        ArgumentCaptor<Callbacks.OnGhanaChargeRequestComplete> onGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGhanaChargeRequestComplete.class);
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), onGhanaChargeRequestCompleteArgumentCaptor.capture());

        onGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateNullUGChargeResponse(), generateRandomString());

        verify(view).onPaymentError(noResponse);

    }


    @Test
    public void requeryTx_onSuccessWithNullData_onPaymentFailedCalled() {

        ghMobileMoneyPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateNullQuery();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCode00_onPaymentSuccessfulCalled() {

        String flwRef = generateRandomString();
        String txRef = generateRandomString();

        ghMobileMoneyPresenter.requeryTx(flwRef, txRef, generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful("00");
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPaymentSuccessful(flwRef, txRef, jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCode02_onPollingRoundCompleteCalled() {

        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        ghMobileMoneyPresenter.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful("02");
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPollingRoundComplete(flwRef, txRef, encryptionKey);

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCodeNot00or02_onPaymentFailedCalled() {

        ghMobileMoneyPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful("03");
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        ghMobileMoneyPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        String message = generateRandomString();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onError(message, jsonResponse);

        verify(view).onPaymentFailed(message, jsonResponse);

    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {

        Double amount = generateRandomDouble();
        when(ravePayInitializer.getAmount()).thenReturn(amount);
        when(amountValidator.isAmountValid(amount)).thenReturn(true);

        ghMobileMoneyPresenter.init(ravePayInitializer);

        verify(view).onAmountValidationSuccessful(amount.toString());

    }


    @Test
    public void onDataCollected_InvalidDataPassed_showFieldErrorCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 2;
        generateViewValidation(failedValidations);
        //act
        ghMobileMoneyPresenter.onDataCollected(map);
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
        ghMobileMoneyPresenter.onDataCollected(map);
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
        ghMobileMoneyPresenter.processTransaction(data, ravePayInitializer);
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
        ghMobileMoneyPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), any(Callbacks.OnGetFeeRequestComplete.class));
    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeGhMobileMoneyCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        Payload payload = generatePayload();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
        //act
        ghMobileMoneyPresenter.processTransaction(data, ravePayInitializer);
        //assert
        ghMobileMoneyPresenter.chargeGhMobileMoney(payload, generateRandomString());
    }


    private HashMap<String, ViewObject> generateViewData() {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(generateRandomInt(), generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldPhone, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        viewData.put(fieldNetwork, new ViewObject(generateRandomInt(), String.valueOf(generateRandomInt()), TextInputLayout.class));
        viewData.put(fieldVoucher, new ViewObject(generateRandomInt(), String.valueOf(generateRandomInt()), TextInputLayout.class));
        viewData.put(networkPosition, new ViewObject(generateRandomInt(), String.valueOf(generateRandomInt()), TextInputLayout.class));
        return viewData;
    }

    private void generateViewValidation(int failedValidations) {

        List<Boolean> falses = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            if (i < failedValidations) {
                falses.add(false);
            } else {
                falses.add(true);
            }
        }

        when(amountValidator.isAmountValid(anyString())).thenReturn(falses.get(0));
        when(phoneValidator.isPhoneValid(anyString())).thenReturn(falses.get(1));
        when(networkValidator.isNetworkValid(anyInt())).thenReturn(falses.get(2));

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

    private RequeryResponse generateRequerySuccessful(String chargeResponseCode) {
        RequeryResponse requeryResponse = new RequeryResponse();
        RequeryResponse.Data data = new RequeryResponse.Data();
        data.setChargeResponseCode(chargeResponseCode);
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

    private MobileMoneyChargeResponse generateNullUGChargeResponse() {
        return new MobileMoneyChargeResponse();
    }

    private MobileMoneyChargeResponse generateValidGhChargeResponse() {
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

    private int generateRandomInt() {
        return new Random().nextInt();
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }
}
package com.flutterwave.raveandroid.rwfmobilemoney;

import android.content.Context;
import android.support.design.widget.TextInputLayout;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestAppComponent;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.MobileMoneyChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SubAccount;
import com.flutterwave.raveandroid.validators.AmountValidator;
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

import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.RaveConstants.success;
import static com.flutterwave.raveandroid.RaveConstants.transactionError;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RwfMobileMoneyPresenterTest {

    RwfMobileMoneyPresenter presenter;
    @Mock
    RwfMobileMoneyContract.View view;
    @Inject
    Context context;
    @Inject
    AmountValidator amountValidator;
    @Inject
    PhoneValidator phoneValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    NetworkRequestImpl networkRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new RwfMobileMoneyPresenter(context, view);

        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(presenter);
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled() {

        presenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        verify(view).showFetchFeeFailed(anyString());

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalled() {

        presenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view).displayFee(anyString(), any(Payload.class));

    }

    @Test
    public void fetchFee_onSuccess_exceptionThrown_showFetchFeeFailed() throws NullPointerException {

        doThrow(NullPointerException.class).when(view).displayFee(any(String.class), any(Payload.class));
        presenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view).showFetchFeeFailed(transactionError);

    }

    @Test
    public void chargeRwfMobileMoney_onSuccess_requeryTxCalled() {

        presenter.chargeRwfMobileMoney(generatePayload(), generateRandomString());

        ArgumentCaptor<Callbacks.OnGhanaChargeRequestComplete> onGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGhanaChargeRequestComplete.class);
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), onGhanaChargeRequestCompleteArgumentCaptor.capture());

        onGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateValidRwfChargeResponse(), generateRandomString());

        presenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

    }

    @Test
    public void chargeRwfMobileMoney_onError_onPaymentErrorCalled() {

        presenter.chargeRwfMobileMoney(generatePayload(), generateRandomString());

        ArgumentCaptor<Callbacks.OnGhanaChargeRequestComplete> OnGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGhanaChargeRequestComplete.class);
        String message = generateRandomString();
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), OnGhanaChargeRequestCompleteArgumentCaptor.capture());

        OnGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message, generateRandomString());

        verify(view).onPaymentError(message);

    }

    @Test
    public void chargeRwfMobileMoney_onSuccessWithNullData_onPaymentError_noResponse_Called() {

        presenter.chargeRwfMobileMoney(generatePayload(), generateRandomString());

        ArgumentCaptor<Callbacks.OnGhanaChargeRequestComplete> onGhanaChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGhanaChargeRequestComplete.class);
        verify(networkRequest).chargeMobileMoneyWallet(any(ChargeRequestBody.class), onGhanaChargeRequestCompleteArgumentCaptor.capture());

        onGhanaChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateNullRwfChargeResponse(), generateRandomString());

        verify(view).onPaymentError(noResponse);

    }


    @Test
    public void requeryTx_onSuccess_nullData_onPaymentFailedCalled() {

        presenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

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

        presenter.requeryTx(flwRef, txRef, generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_00();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPaymentSuccessful(flwRef, txRef, jsonResponse);

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCode02_onPollingRoundCompleteCalled() {

        String flwRef = generateRandomString();
        String txRef = generateRandomString();
        String encryptionKey = generateRandomString();

        presenter.requeryTx(flwRef, txRef, encryptionKey);

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRequerySuccessful_02();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPollingRoundComplete(flwRef, txRef, encryptionKey);

    }

    @Test
    public void requeryTx_onSuccessWithValidDataAndChargeResponseCodeNot00or02_onPaymentFailedCalled() {

        presenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());

        RequeryResponse requeryResponse = generateRandomRequerySuccessful();
        String jsonResponse = generateRandomString();

        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        presenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomString());

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

        presenter.init(ravePayInitializer);

        verify(view).onAmountValidationSuccessful(amount.toString());

    }


    @Test
    public void onDataCollected_InvalidDataPassed_showFieldErrorCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 2;
        generateViewValidation(failedValidations);
        //act
        presenter.onDataCollected(map);
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
        presenter.onDataCollected(map);
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
        presenter.processTransaction(data, ravePayInitializer);
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
        presenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), any(Callbacks.OnGetFeeRequestComplete.class));
    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeRwfMobileMoneyCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        presenter.processTransaction(data, ravePayInitializer);
        //assert
        presenter.chargeRwfMobileMoney(generatePayload(), generateRandomString());
    }


    private HashMap<String, ViewObject> generateViewData() {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(generateRandomInt(), generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldPhone, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
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

    private int generateRandomInt() {
        return new Random().nextInt();
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }
}
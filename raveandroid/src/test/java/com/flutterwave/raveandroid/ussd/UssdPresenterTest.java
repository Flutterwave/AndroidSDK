package com.flutterwave.raveandroid.ussd;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatSpinner;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.RaveConstants;
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
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SubAccount;
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

import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldUssdBank;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UssdPresenterTest {

    @Mock
    UssdContract.View view;
    @Inject
    Context context;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    NetworkRequestImpl networkRequest;
    @Inject
    Bundle bundle;
    @Mock
    UssdPresenter ussdPresenterMock;
    @Mock
    PayloadBuilder payloadBuilder;
    @Mock
    RequeryRequestBody requeryRequestBody;
    private UssdPresenter ussdPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ussdPresenter = new UssdPresenter(context, view);

        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(ussdPresenter);
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled_errorOccurredMessage() {

        ussdPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());
        verify(view).showProgressIndicator(false);
        verify(view).showFetchFeeFailed("An error occurred while retrieving transaction fee");

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalled() {

        ussdPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view).displayFee(anyString(), any(Payload.class));

    }

    @Test
    public void fetchFee_onSuccess_Exception_showFetchFeeFailedCalled() throws NullPointerException {

        doThrow(NullPointerException.class).when(view).displayFee(any(String.class), any(Payload.class));
        ussdPresenter.fetchFee(generatePayload());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(new FeeCheckResponse());
        verify(view).showFetchFeeFailed("An error occurred while retrieving transaction fee");

    }

    @Test
    public void payWithUssd_chargeCard_onSuccess_onUssdDetailsReceivedCalled() {
        ussdPresenter.payWithUssd(generatePayload(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        ChargeResponse chargeResponse = generateValidChargeResponse();

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, any(String.class));

        verify(view).showProgressIndicator(false);
        verify(view).onUssdDetailsReceived(chargeResponse.getData().getUssdData().getNote(), chargeResponse.getData().getUssdData().getReference_code());
    }

    @Test
    public void payWithUssd_chargeCard_onSuccess_nullResponse_onUssdDetailsReceivedCalled() {
        ussdPresenter.payWithUssd(generatePayload(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        ChargeResponse chargeResponse = new ChargeResponse();

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        verify(view).onPaymentError("No response data was returned");
    }

    @Test
    public void payWithUssd_chargeCard_onError_onPaymentErrorCalled() {
        ussdPresenter.payWithUssd(generatePayload(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        String message = generateRandomString();

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(message);
    }

    @Test
    public void startPaymentVerification_requeryTxCalled() {
        ussdPresenter.startPaymentVerification();
        long time = System.currentTimeMillis();

        String randomflwRef = generateRandomString();
        String randomPubKey = generateRandomString();
        verify(view).showPollingIndicator(true);

        ussdPresenterMock.requeryTx(randomflwRef, randomPubKey, time);
        verify(ussdPresenterMock).requeryTx(randomflwRef, randomPubKey, time);
    }

    @Test
    public void cancelPolling_pollingCancelledTrue() {
        ussdPresenter.cancelPolling();

        assertTrue(ussdPresenter.pollingCancelled);
    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_00_Called() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = System.currentTimeMillis();

        ussdPresenter.requeryTx(randomflwRef, randomPubKey, time);
        requeryRequestBody.setFlw_ref(generateRandomString());
        requeryRequestBody.setPBFPubKey(generateRandomString());
        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("00"), responseJson);

        verify(view).showPollingIndicator(false);
        verify(view).onPaymentSuccessful(randomflwRef, responseJson);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_02_onPollingTimeoutCalled() {

        String randomflwRef = generateRandomString();
        String randomTxRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = 400000;
        ussdPresenter.requeryTx(randomflwRef, randomPubKey, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("02"), responseJson);

        verify(view).showPollingIndicator(false);
        verify(view).onPollingTimeout(randomflwRef, responseJson);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessful_onPaymentSuccessful_02_requeryTxCalled() {

        String randomflwRef = generateRandomString();
        String randomPubKey = generateRandomString();
        long time = System.currentTimeMillis() - 10000;

        doCallRealMethod().when(ussdPresenterMock).requeryTx(
                any(String.class),
                any(String.class),
                anyLong());

        ussdPresenterMock.networkRequest = networkRequest;
        ussdPresenterMock.pollingCancelled = false;
        ussdPresenterMock.mView = view;
        ussdPresenterMock.requeryTx(randomflwRef, randomPubKey, time);

        String responseJson = generateRandomString();
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);


        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("02"), responseJson);

        verify(ussdPresenterMock, times(2))
                .requeryTx(anyString(), anyString(), anyLong());

    }


    @Test
    public void requeryTx_onSuccess_nullResponse_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        ussdPresenter.requeryTx(generateRandomString(), generateRandomString(), time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }

    @Test
    public void requeryTx_onSuccess_chargeResponseCodeNeither00Nor02_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        RequeryResponse requeryResponse = new RequeryResponse();
        String jsonResponse = generateRandomString();
        ussdPresenter.requeryTx(generateRandomString(), generateRandomString(), time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful("099"), jsonResponse);

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentFailed(requeryResponse.getStatus(), jsonResponse);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        long time = System.currentTimeMillis();
        ussdPresenter.requeryTx(generateRandomString(), generateRandomString(), time);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);

        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());

        verify(view).onPaymentFailed(anyString(), anyString());

    }


    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        ussdPresenter.init(ravePayInitializer);

        verify(view).onAmountValidationSuccessful(amount.toString());

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        ussdPresenter.init(ravePayInitializer);
        verify(view).onAmountValidationFailed();

    }


    @Test
    public void onDataCollected_inValidDataPassed_showFieldErrorCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(amountViewID, banksSpinnerViewID);
        int failedValidations = 1;
        generateViewValidation(failedValidations);

        ussdPresenter.onDataCollected(map);

        verify(view, times(failedValidations)).showFieldError(amountViewID, RaveConstants.validAmountPrompt, map.get(RaveConstants.fieldAmount).getViewType());

    }


    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(amountViewID, banksSpinnerViewID);
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        ussdPresenter.onDataCollected(map);
        //assert
        verify(view).onDataValidationSuccessful(map);

    }


    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(amountViewID, banksSpinnerViewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        ussdPresenter.processTransaction(map, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(true);

    }

    @Test
    public void processTransaction_displayFeeIsEnabled_payWithUssdCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(amountViewID, banksSpinnerViewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        ussdPresenter.processTransaction(data, ravePayInitializer);
        ussdPresenterMock.payWithUssd(generatePayload(), generateRandomString());
        //assert
        verify(view).showProgressIndicator(true);
        verify(ussdPresenterMock).payWithUssd(any(Payload.class), any(String.class));

    }

    @Test
    public void processTransaction_payloadBuilderCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(amountViewID, banksSpinnerViewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        ussdPresenter.processTransaction(data, ravePayInitializer);
        payloadBuilder.createBankPayload();
        //assert
        verify(view).showProgressIndicator(true);
        verify(payloadBuilder).createBankPayload();

    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeAccountCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(amountViewID, banksSpinnerViewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        Payload payload = generatePayload();
        //act
        ussdPresenter.processTransaction(data, ravePayInitializer);
        //assert
        ussdPresenter.payWithUssd(payload, generateRandomString());
    }


    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

    private HashMap<String, ViewObject> generateViewData(int amountViewID, int banksSpinnerViewID) {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(amountViewID, generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldUssdBank, new ViewObject(banksSpinnerViewID, RaveConstants.bankNameGtb, AppCompatSpinner.class));

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
        chargeResponse.getData().setUssdData(new ChargeResponse.Data.UssdData());

        ChargeResponse.Data.UssdData data = chargeResponse.getData().getUssdData();

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

        return feeCheckResponse;
    }

}
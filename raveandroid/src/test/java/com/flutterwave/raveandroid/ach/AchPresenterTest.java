package com.flutterwave.raveandroid.ach;

import android.content.Context;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.GetEncryptedData;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.PayloadToJson;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.TransactionStatusChecker;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.SharedPrefsRequestImpl;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestAppComponent;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SubAccount;
import com.flutterwave.raveandroid.validators.AmountValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AchPresenterTest {

    @Mock
    AchContract.View view;
    @Inject
    Context context;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Mock
    RavePayInitializer ravePayInitializerMock;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    GetEncryptedData getEncryptedData;
    @Inject
    NetworkRequestImpl networkRequest;
    @Inject
    SharedPrefsRequestImpl sharedPrefsRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Mock
    AchPresenter achPresenterMock;
    @Mock
    PayloadBuilder payloadBuilderMock;
    private AchPresenter achPresenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        achPresenter = new AchPresenter(context, view);

        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(achPresenter);

    }

    @Test
    public void init_validAmount_showRedirectMessageTrue() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        achPresenter.init(ravePayInitializer);

        verify(view).showAmountField(false);
        verify(view).showRedirectMessage(true);

    }

    @Test
    public void init_inValidAmount_showRedirectMessageFalse() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        achPresenter.init(ravePayInitializer);

        verify(view).showAmountField(true);
        verify(view).showRedirectMessage(false);

    }


    @Test
    public void processTransaction_chargeAccountCalled_createBankPayloadCalled_assertParameters() {
        //arrange
        String amount = generateRandomString();
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());
        String encryptionKey = generateRandomString();
        boolean isDisplayFee = true;

        //act
        achPresenter.processTransaction(amount, ravePayInitializer);

        achPresenterMock.chargeAccount(payload, encryptionKey, isDisplayFee);

        ArgumentCaptor<Payload> captorPayload = ArgumentCaptor.forClass(Payload.class);
        ArgumentCaptor<String> captorEncryptionKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> captorIsDisplayFee = ArgumentCaptor.forClass(Boolean.class);

        verify(achPresenterMock).chargeAccount(captorPayload.capture(), captorEncryptionKey.capture(), captorIsDisplayFee.capture());

        //assert
        assertEquals(payload, captorPayload.getValue());
        assertEquals(encryptionKey, captorEncryptionKey.getValue());
        assertEquals(isDisplayFee, captorIsDisplayFee.getValue());

    }


    @Test
    public void processTransaction_setAmountCalled_assertParameters() {
        //arrange
        String amount = generateRandomString();
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        Double amountDouble = 10.0;
        //act
        achPresenter.processTransaction(amount, ravePayInitializer);

        ravePayInitializerMock.setAmount(amountDouble);

        ArgumentCaptor<Double> captorAmount = ArgumentCaptor.forClass(Double.class);

        verify(ravePayInitializerMock).setAmount(captorAmount.capture());
        assertEquals(amountDouble, captorAmount.getValue());
    }

    @Test
    public void processTransaction_createBankPayloadCalled_assertParameters() {
        //arrange
        String amount = generateRandomString();
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        //act
        achPresenter.processTransaction(amount, ravePayInitializer);

        payloadBuilderMock.createBankPayload();


        verify(payloadBuilderMock).createBankPayload();
    }


    @Test
    public void processTransaction_setPaymentPlanCalled_assertPaymentPlan() {
        //arrange
        String amount = generateRandomString();

        ravePayInitializer.setPayment_plan(generateRandomString());
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        String paymentPlan = generateRandomString();
        //act
        achPresenter.processTransaction(amount, ravePayInitializer);

        PayloadBuilder payloadBuilder = generatePayloadBuilder();
        payloadBuilder.setPaymentPlan(ravePayInitializer.getPayment_plan());

        payloadBuilderMock.setPaymentPlan(paymentPlan);

        ArgumentCaptor<String> captorString = ArgumentCaptor.forClass(String.class);

        verify(payloadBuilderMock).setPaymentPlan(captorString.capture());

        //assert
        assertEquals(paymentPlan, captorString.getValue());
    }

    @Test
    public void chargeAccount_noDisplayFee_onSuccess_validResponseReturned_showWebViewCalled() {

        Payload payload = generatePayload();

        payload.setPBFPubKey(generateRandomString());

        when(payloadToJson.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(getEncryptedData.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());


        achPresenter.chargeAccount(payload, generateRandomString(), false);
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());

        verify(sharedPrefsRequest).saveFlwRef(any(String.class));
        verify(view).showProgressIndicator(false);

        verify(view).showWebView(any(String.class), any(String.class));

    }

    @Test
    public void chargeAccount_displayFee_chargeCard_onSuccess_saveFlwRef_validResponseReturned_showFeeCalled() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJson.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(getEncryptedData.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        achPresenter.chargeAccount(payload, generateRandomString(), true);

        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());

        verify(sharedPrefsRequest).saveFlwRef(any(String.class));
        verify(view).showProgressIndicator(false);

        verify(view).showFee(any(String.class), any(String.class), any(String.class), any(String.class));

    }


    @Test
    public void chargeAccount_onSuccess_nullChargeResponseReturned_onPaymentErrorCalled_noResponseMessage() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJson.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(getEncryptedData.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        achPresenter.chargeAccount(payload, generateRandomString(), true);

        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse(), any(String.class));
        verify(view).showProgressIndicator(false);

        verify(view).onPaymentError(RaveConstants.noResponse);

    }

    @Test
    public void chargeAccount_onSuccess_inValidResponseReturned_onPaymentErrorCalled_noAuthUrlMessage() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJson.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(getEncryptedData.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        achPresenter.chargeAccount(payload, generateRandomString(), true);

        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse(), generateRandomString());
        verify(view).showProgressIndicator(false);

        verify(view).onPaymentError(RaveConstants.no_authurl_was_returnedmsg);

    }

    @Test
    public void chargeAccount_onError_onPaymentErrorCalled_messageReturned() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        String message = generateRandomString();

        when(payloadToJson.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(getEncryptedData.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        achPresenter.chargeAccount(payload, generateRandomString(), true);
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message, generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(message);

    }

    @Test
    public void onFeeConfirmed_showWebViewCalled() {
        achPresenter.onFeeConfirmed(anyString(), anyString());
        verify(view).showWebView(anyString(), anyString());
    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalled() {
        when(sharedPrefsRequest.fetchFlwRef()).thenReturn(generateRandomString());
        achPresenter.requeryTx(generateRandomString());

        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful(), generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onRequerySuccessful(any(RequeryResponse.class), anyString(), anyString());

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        achPresenter.requeryTx(generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());
        verify(view).onPaymentFailed(anyString(), anyString());

    }

    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalled() {
        when(transactionStatusChecker.getTransactionStatus(anyString(), anyString(), anyString())).thenReturn(false);
        achPresenter.verifyRequeryResponse(generateRequerySuccessful(), generateRandomString(), ravePayInitializer, generateRandomString());
        verify(view).onPaymentFailed(String.valueOf(anyObject()), anyString());
    }

    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalled() {

        when(transactionStatusChecker.getTransactionStatus(any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        when(ravePayInitializer.getAmount()).thenReturn(generateRandomDouble());
        when(ravePayInitializer.getCurrency()).thenReturn(generateRandomString());

        achPresenter.verifyRequeryResponse(generateRequerySuccessful(), generateRandomString(), ravePayInitializer, generateRandomString());
        verify(view).onPaymentSuccessful(String.valueOf(anyObject()), anyString(), anyString());
    }

    @Test
    public void onPayButtonClicked_validAmount_showAmountError_onValidationSuccessfulCalled() {
        when(amountValidator.isAmountValid(ravePayInitializer.getAmount())).thenReturn(true);
        achPresenter.onPayButtonClicked(ravePayInitializer, anyString());
        verify(view).showAmountError(null);
        verify(view).onValidationSuccessful(any(String.class));
    }

    @Test
    public void onPayButtonClicked_inValidAmount_showAmountError_onValidationSuccessfulCalled() {
        when(amountValidator.isAmountValid(ravePayInitializer.getAmount())).thenReturn(false);
        achPresenter.onPayButtonClicked(ravePayInitializer, anyString());
        verify(view).showAmountError(null);
        verify(view).showAmountError(RaveConstants.validAmountPrompt);
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
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

    private ChargeResponse generateNullChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(null);

        return chargeResponse;
    }

    private PayloadBuilder generatePayloadBuilder() {
        PayloadBuilder payloadBuilder = new PayloadBuilder();
        payloadBuilder.setPaymentPlan(generateRandomString());
        return payloadBuilder;
    }

    private ChargeResponse generateRandomChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(new ChargeResponse.Data());
        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.getData().setAuthurl(null);
        return chargeResponse;
    }

    private ChargeResponse generateValidChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(new ChargeResponse.Data());
        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.getData().setCurrency(generateRandomString());
        chargeResponse.getData().setChargedAmount(generateRandomString());
        chargeResponse.getData().setAuthurl(generateRandomString());
        chargeResponse.getData().setFlwRef(generateRandomString());
        return chargeResponse;
    }
}
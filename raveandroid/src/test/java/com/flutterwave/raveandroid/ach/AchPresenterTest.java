package com.flutterwave.raveandroid.ach;

import android.content.Context;
import android.view.View;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadEncryptor;
import com.flutterwave.raveandroid.PayloadToJsonConverter;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
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
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    NetworkRequestImpl networkRequest;
    @Inject
    SharedPrefsRequestImpl sharedPrefsRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
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
    public void init_validAmount_onAmountValidatedCalledWithCorrectParams_showRedirectMessageCalledWithCorrectParams() {

        //arrange
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        //act
        achPresenter.init(ravePayInitializer);

        //assert
        verify(view).onAmountValidated(amount.toString(), View.GONE);
        verify(view).showRedirectMessage(true);

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithCorrectParams_showRedirectMessageCalledWithCorrectParams() {

        //arrange
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        //act
        achPresenter.init(ravePayInitializer);

        //assert
        verify(view).onAmountValidated("", View.VISIBLE);
        verify(view).showRedirectMessage(false);

    }

    @Test
    public void processTransaction_setAmountCalledOnRavePayInitializerWithCorrectParam() {
        //arrange
        String amount = generateRandomDouble().toString();
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        achPresenter.processTransaction(amount, ravePayInitializer, false);

        //assert
        verify(ravePayInitializer).setAmount(Double.parseDouble(amount));

    }


    @Test
    public void processTransaction_noDisplayFee_onSuccess_validResponseReturned_showWebViewCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();

        payload.setPBFPubKey(generateRandomString());

        ChargeResponse chargeResponse = generateValidChargeResponse();

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class)))
                .thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class)))
                .thenReturn(generateRandomString());

        //act
        achPresenter.processTransaction(generateRandomDouble().toString(), ravePayInitializer, false);
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(sharedPrefsRequest).saveFlwRef(any(String.class));
        verify(view).showProgressIndicator(false);

        verify(view, never()).showFee(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getChargedAmount(), chargeResponse.getData().getCurrency());
        verify(view).showWebView(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef());

    }

    @Test
    public void processTransaction_displayFee_chargeCard_onSuccess_validResponseReturned_showFeeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        ChargeResponse chargeResponse = generateValidChargeResponse();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(ravePayInitializer.getPublicKey()).thenReturn(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        achPresenter.processTransaction(generateRandomDouble().toString(), ravePayInitializer, true);

        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        verify(sharedPrefsRequest).saveFlwRef(any(String.class));
        verify(view).showProgressIndicator(false);

        //assert
        verify(view).showFee(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getChargedAmount(), chargeResponse.getData().getCurrency());
        verify(view, never()).showWebView(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef());

    }

    @Test
    public void processTransaction_onSuccess_nullChargeResponseReturned_onPaymentErrorCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        achPresenter.processTransaction(generateRandomDouble().toString(), ravePayInitializer, false);

        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse(), any(String.class));

        //assert
        verify(view).showProgressIndicator(false);

        verify(view).onPaymentError(RaveConstants.noResponse);

    }

    @Test
    public void processTransaction_onSuccess_inValidResponseReturned_onPaymentErrorCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        achPresenter.processTransaction(generateRandomDouble().toString(), ravePayInitializer, false);

        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse(), generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(RaveConstants.no_authurl_was_returnedmsg);

    }

    @Test
    public void processTransaction_onError_onPaymentErrorCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        String message = generateRandomString();

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        //act
        achPresenter.processTransaction(generateRandomDouble().toString(), ravePayInitializer, false);

        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(message);

    }

    @Test
    public void onFeeConfirmed_showWebViewCalledWithCorrectParams() {
        String authUrl = generateRandomString();
        String flwRef = generateRandomString();
        achPresenter.onFeeConfirmed(authUrl, flwRef);
        verify(view).showWebView(authUrl, flwRef);
    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalledWithCorrectParams() {

        //arrange
        String flwRef = generateRandomString();
        RequeryResponse requeryResponse = generateRequerySuccessful();
        String jsonResponse = generateRandomString();

        when(sharedPrefsRequest.fetchFlwRef()).thenReturn(flwRef);

        //act
        achPresenter.requeryTx(generateRandomString());

        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onRequerySuccessful(requeryResponse, jsonResponse, flwRef);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();
        String jsonResponse = generateRandomString();

        //act
        achPresenter.requeryTx(generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, jsonResponse);

        //assert
        verify(view).onPaymentFailed(message, jsonResponse);

    }

    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalledWithCorrectParams() {

        //arrange
        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();
        when(transactionStatusChecker.getTransactionStatus(anyString(), anyString(), anyString())).thenReturn(false);

        //act
        achPresenter.verifyRequeryResponse(requeryResponse, responseAsJsonString, ravePayInitializer, generateRandomString());

        //assert
        verify(view).onPaymentFailed(requeryResponse.getStatus(), responseAsJsonString);
    }

    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalledWithCorrectParams() {

        //arrange
        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();
        String flwRef = generateRandomString();
        when(transactionStatusChecker.getTransactionStatus(any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        when(ravePayInitializer.getAmount()).thenReturn(generateRandomDouble());
        when(ravePayInitializer.getCurrency()).thenReturn(generateRandomString());

        //act
        achPresenter.verifyRequeryResponse(requeryResponse, responseAsJsonString, ravePayInitializer, flwRef);

        //assert
        verify(view).onPaymentSuccessful(requeryResponse.getStatus(), flwRef, responseAsJsonString);
    }

    @Test
    public void onPayButtonClicked_validAmount_showAmountError_onValidationSuccessfulCalledWithCorrectParams() {

        //arrange
        String amount = generateRandomDouble().toString();
        when(amountValidator.isAmountValid(ravePayInitializer.getAmount())).thenReturn(true);

        //act
        achPresenter.onPayButtonClicked(ravePayInitializer, amount);

        //assert
        verify(view).showAmountError(null);
        verify(view).onValidationSuccessful(amount);
        verify(view, never()).showAmountError(RaveConstants.validAmountPrompt);
    }

    @Test
    public void onPayButtonClicked_inValidAmount_showAmountErrorWithCorrectParams() {

        //arrange
        when(amountValidator.isAmountValid(ravePayInitializer.getAmount())).thenReturn(false);

        //act
        achPresenter.onPayButtonClicked(ravePayInitializer, anyString());

        //assert
        verify(view).showAmountError(null);
        verify(view).showAmountError(RaveConstants.validAmountPrompt);
        verify(view, never()).onValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()));
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
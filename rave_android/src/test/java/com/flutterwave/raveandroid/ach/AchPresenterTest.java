package com.flutterwave.raveandroid.ach;

import android.view.View;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.di.DaggerTestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
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
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AchPresenterTest {

    @Mock
    AchUiContract.View view;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    SharedPrefsRepo sharedPrefsRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Mock
    AchPresenter achPresenterMock;

    @Mock
    ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();
    private AchPresenter achPresenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        achPresenter = new AchPresenter(view);

        TestRaveUiComponent component = DaggerTestRaveUiComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(achPresenter);

    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithCorrectParams_showRedirectMessageTrue() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        achPresenter.init(ravePayInitializer);

        verify(view).onAmountValidated(String.valueOf(amount), View.GONE);
        verify(view).showRedirectMessage(true);

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithCorrectParams_showRedirectMessageFalse() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        achPresenter.init(ravePayInitializer);

        verify(view).onAmountValidated("", View.VISIBLE);
        verify(view).showRedirectMessage(false);

    }


    @Test
    public void processTransaction_chargeAccountCalledWithCorrectParams() {
        //arrange

        boolean isDisplayFee = generateRandomBoolean();
        String encryptionKey = generateRandomString();
        String amount = generateRandomDouble().toString();
        String country = generateRandomString();
        String currency = generateRandomString();
        String email = generateRandomString();
        String firstName = generateRandomString();
        String lastName = generateRandomString();
        String deviceId = generateRandomString();
        String txRef = generateRandomString();
        String meta = generateRandomString();
        String pubKey = generateRandomString();
        boolean isAch = generateRandomBoolean();
        String fingerPrint = deviceId;

        achPresenterMock.deviceIdGetter = deviceIdGetter;
        when(deviceIdGetter.getDeviceId()).thenReturn(deviceId);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(isDisplayFee);
        when(ravePayInitializer.getEncryptionKey()).thenReturn(encryptionKey);
        when(ravePayInitializer.getAmount()).thenReturn(Double.parseDouble(amount));
        when(ravePayInitializer.getCountry()).thenReturn(country);
        when(ravePayInitializer.getCurrency()).thenReturn(currency);
        when(ravePayInitializer.getEmail()).thenReturn(email);
        when(ravePayInitializer.getfName()).thenReturn(firstName);
        when(ravePayInitializer.getlName()).thenReturn(lastName);
        when(ravePayInitializer.getTxRef()).thenReturn(txRef);
        when(ravePayInitializer.getMeta()).thenReturn(meta);
        when(ravePayInitializer.getOrderedPaymentTypesList()).thenReturn(orderedPaymentTypesList);
        when(orderedPaymentTypesList.contains(RaveConstants.PAYMENT_TYPE_ACH)).thenReturn(isAch);
        when(ravePayInitializer.getPublicKey()).thenReturn(pubKey);

        //act
        doCallRealMethod().when(achPresenterMock).processTransaction(any(String.class), any(RavePayInitializer.class), anyBoolean());
        achPresenterMock.processTransaction(amount, ravePayInitializer, ravePayInitializer.getIsDisplayFee());

        ArgumentCaptor<String> captorEncryptionKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> captorIsDisplayFee = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Payload> payloadCaptor = ArgumentCaptor.forClass(Payload.class);

        verify(achPresenterMock).chargeAccount(payloadCaptor.capture(),
                captorEncryptionKey.capture(),
                captorIsDisplayFee.capture());

        //assert
        assertEquals(encryptionKey, captorEncryptionKey.getValue());
        assertEquals(isDisplayFee, captorIsDisplayFee.getValue());

        Payload capturedPayload = payloadCaptor.getValue();
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());
        assertEquals(amount, capturedPayload.getAmount());
        assertEquals(country, capturedPayload.getCountry());
        assertEquals(currency, capturedPayload.getCurrency());
        assertEquals(email, capturedPayload.getEmail());
        assertEquals(firstName, capturedPayload.getFirstname());
        assertEquals(lastName, capturedPayload.getLastname());
        assertEquals(fingerPrint, capturedPayload.getIP());
        assertEquals(txRef, capturedPayload.getTxRef());
        assertEquals(pubKey, capturedPayload.getPBFPubKey());
        assertEquals(isAch, capturedPayload.isIs_us_bank_charge());
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());

    }


    @Test
    public void processTransaction_setAmountCalledOnRavePayInitializerWithCorrectParam() {
        //arrange
        String amount = generateRandomDouble().toString();
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        achPresenter.processTransaction(amount, ravePayInitializer, ravePayInitializer.getIsDisplayFee());

        //assert
        verify(ravePayInitializer).setAmount(Double.parseDouble(amount));

    }

    @Test
    public void onFeeConfirmed_showWebViewCalledWithCorrectParams() {
        String authUrl = generateRandomString();
        String flwRef = generateRandomString();
        achPresenter.onFeeConfirmed(authUrl, flwRef);
        verify(view).showWebView(authUrl, flwRef);
    }

    @Test
    public void onDataCollected_validAmount_showAmountError_onValidationSuccessfulCalledWithCorrectParams() {

        String amount = generateRandomDouble().toString();

        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        achPresenter.onDataCollected(ravePayInitializer, amount);
        verify(view).showAmountError(null);
        verify(view).onValidationSuccessful(amount);
    }

    @Test
    public void onDataCollected_inValidAmount_showAmountErrorWithCorrectParams() {
        when(amountValidator.isAmountValid(ravePayInitializer.getAmount())).thenReturn(false);
        achPresenter.onDataCollected(ravePayInitializer, anyString());
        verify(view).showAmountError(null);
        verify(view).showAmountError(RaveConstants.validAmountPrompt);
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

    private boolean generateRandomBoolean() {
        return new Random().nextBoolean();
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
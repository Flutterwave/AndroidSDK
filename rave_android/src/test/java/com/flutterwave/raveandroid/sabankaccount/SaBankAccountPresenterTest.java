package com.flutterwave.raveandroid.sabankaccount;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.di.DaggerTestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.validators.AmountValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaBankAccountPresenterTest {

    @Mock
    SaBankAccountUiContract.View view;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Mock
    SaBankAccountPresenter presenterMock;

    @Mock
    ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();
    private SaBankAccountPresenter presenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        presenter = new SaBankAccountPresenter(view);

        TestRaveUiComponent component = DaggerTestRaveUiComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(presenter);

    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithCorrectParams() {

        Double amount = generateRandomDouble();
        String currency = generateRandomString();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);
        when(ravePayInitializer.getCurrency()).thenReturn(currency);

        presenter.init(ravePayInitializer);

        verify(view).onAmountValidationSuccessful(String.valueOf(amount), currency);

    }

    @Test
    public void processTransaction_chargeSaBankAccountCalledWithCorrectParams() {
        //arrange

        boolean isDisplayFee = false;
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
        boolean isSaBankCharge = generateRandomBoolean();
        String fingerPrint = deviceId;

        presenterMock.deviceIdGetter = deviceIdGetter;
        when(deviceIdGetter.getDeviceId()).thenReturn(deviceId);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
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
        when(orderedPaymentTypesList.contains(RaveConstants.PAYMENT_TYPE_SA_BANK_ACCOUNT)).thenReturn(isSaBankCharge);
        when(ravePayInitializer.getPublicKey()).thenReturn(pubKey);

        //act
        doCallRealMethod().when(presenterMock).processTransaction(any(RavePayInitializer.class));
        presenterMock.processTransaction(ravePayInitializer);

        ArgumentCaptor<String> captorEncryptionKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> captorIsDisplayFee = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Payload> payloadCaptor = ArgumentCaptor.forClass(Payload.class);

        verify(presenterMock).chargeSaBankAccount(payloadCaptor.capture(),
                captorEncryptionKey.capture());

        //assert
        assertEquals(encryptionKey, captorEncryptionKey.getValue());

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
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());

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
}
package com.flutterwave.raveandroid.acquireddotcom;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.di.DaggerTestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.validators.AmountValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AcquiredPresenterTest {

    @Mock
    AcquiredUiContract.View view;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;

    @Inject
    AmountValidator amountValidator;

    @Mock
    AcquiredPresenter acquiredPresenterMock;

    private AcquiredPresenter acquiredPresenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        acquiredPresenter = new AcquiredPresenter(view);

        TestRaveUiComponent component = DaggerTestRaveUiComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(acquiredPresenter);
    }

    @Test
    public void init_isParameterValid_showOnAmountValidationSuccessful() {

        Double amount = generateRandomDouble();
        String currency = "GB";

        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);
        when(ravePayInitializer.getCurrency()).thenReturn(currency);

        acquiredPresenter.init(ravePayInitializer);

        assertEquals(amount, Double.valueOf(ravePayInitializer.getAmount()));
        assertEquals(currency, ravePayInitializer.getCurrency());

        verify(view).onAmountValidationSuccessful(amount.toString(), currency);
    }

    @Test
    public void init_isParameterNotValid_showOnAmountValidationFailed() {

        Double amount = generateRandomDouble();
        String currency = "GE";

        when(amountValidator.isAmountValid(amount)).thenReturn(false);
        when(ravePayInitializer.getAmount()).thenReturn(amount);
        when(ravePayInitializer.getCurrency()).thenReturn(currency);

        acquiredPresenter.init(ravePayInitializer);

        assertNotEquals("-10.0", ravePayInitializer.getAmount());
        assertNotEquals("GB", ravePayInitializer.getCurrency());

        verify(view).onAmountValidationFailed();
    }

    @Test
    public void init_isAmountInValid_showOnAmountValidationFailed() {
        Double amount = generateRandomDouble();
        String currency = "GB";

        when(amountValidator.isAmountValid(amount)).thenReturn(false);
        when(ravePayInitializer.getAmount()).thenReturn(amount);
        when(ravePayInitializer.getCurrency()).thenReturn(currency);

        acquiredPresenter.init(ravePayInitializer);

        verify(view).onAmountValidationFailed();
    }


    @Test
    public void processTransaction_chargeAcquiredCalledWithCorrectParams() {
        //arrange
        String deviceId = generateRandomString();
        String pubKey = generateRandomString();
        boolean isInDarkMode = generateRandomBoolean();

        acquiredPresenterMock.deviceIdGetter = deviceIdGetter;
        when(deviceIdGetter.getDeviceId()).thenReturn(deviceId);
        when(ravePayInitializer.getPublicKey()).thenReturn(pubKey);

        //act
        doCallRealMethod().when(acquiredPresenterMock).processTransaction(ravePayInitializer, isInDarkMode);
        acquiredPresenterMock.processTransaction(ravePayInitializer, isInDarkMode);

        ArgumentCaptor<String> captorEncryptionKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Payload> payloadCaptor = ArgumentCaptor.forClass(Payload.class);

        verify(acquiredPresenterMock).chargeAcquired(payloadCaptor.capture(),
                captorEncryptionKey.capture());

        //assert
        Payload capturedPayload = payloadCaptor.getValue();
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());
        assertEquals(pubKey, capturedPayload.getPBFPubKey());

    }


    @Test
    public void processTransaction_chargeAcquiredCalledOnRavePayInitializerWithCorrectParam() {
        //arrange
        String encryptionKey = generateRandomString();
        String deviceId = generateRandomString();
        boolean isInDarkMode = generateRandomBoolean();
        PayloadBuilder payloadBuilder = generatePayloadBuilder();

        acquiredPresenterMock.deviceIdGetter = deviceIdGetter;
        when(ravePayInitializer.getAmount()).thenReturn(Double.parseDouble(payloadBuilder.getAmount()));
        when(ravePayInitializer.getCurrency()).thenReturn(payloadBuilder.getCurrency());
        when(ravePayInitializer.getCountry()).thenReturn(payloadBuilder.getCountry());
        when(ravePayInitializer.getEmail()).thenReturn(payloadBuilder.getEmail());
        when(ravePayInitializer.getfName()).thenReturn(payloadBuilder.getFirstname());
        when(ravePayInitializer.getlName()).thenReturn(payloadBuilder.getLastname());
        when(ravePayInitializer.getTxRef()).thenReturn(payloadBuilder.getTxRef());
        when(ravePayInitializer.getMeta()).thenReturn(payloadBuilder.getMeta());
        when(ravePayInitializer.getSubAccount()).thenReturn(payloadBuilder.getSubAccounts());
        when(ravePayInitializer.getEncryptionKey()).thenReturn(encryptionKey);
        when(ravePayInitializer.getPublicKey()).thenReturn(payloadBuilder.getPbfPubKey());
        when(ravePayInitializer.getIsPreAuth()).thenReturn(payloadBuilder.isPreAuth());
        when(ravePayInitializer.getPhoneNumber()).thenReturn(payloadBuilder.getPhonenumber());
        when(deviceIdGetter.getDeviceId()).thenReturn(deviceId);

        //act
        doCallRealMethod().when(acquiredPresenterMock).processTransaction(ravePayInitializer, isInDarkMode);
        acquiredPresenterMock.processTransaction(ravePayInitializer, isInDarkMode);

        //assert
        assertEquals(payloadBuilder.getAmount(), String.valueOf(ravePayInitializer.getAmount()));
        assertEquals(payloadBuilder.getCountry(), ravePayInitializer.getCountry());
        assertEquals(payloadBuilder.getCurrency(), ravePayInitializer.getCurrency());
        assertEquals(payloadBuilder.getEmail(), ravePayInitializer.getEmail());
        assertEquals(payloadBuilder.getFirstname(), ravePayInitializer.getfName());
        assertEquals(payloadBuilder.getLastname(), ravePayInitializer.getlName());
        assertEquals(payloadBuilder.getTxRef(), ravePayInitializer.getTxRef());
        assertEquals(payloadBuilder.getMeta(), ravePayInitializer.getMeta());
        assertEquals(payloadBuilder.getSubAccounts(), ravePayInitializer.getSubAccount());
        assertEquals(payloadBuilder.getPbfPubKey(), ravePayInitializer.getPublicKey());
        assertEquals(payloadBuilder.isPreAuth(), ravePayInitializer.getIsPreAuth());
        assertEquals(payloadBuilder.getPhonenumber(), ravePayInitializer.getPhoneNumber());
        assertEquals(encryptionKey, ravePayInitializer.getEncryptionKey());

        if (ravePayInitializer.getPayment_plan() != null) {
            payloadBuilder.setPaymentPlan(ravePayInitializer.getPayment_plan());
        }

        ArgumentCaptor<String> captorEncryptionKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Payload> payloadCaptor = ArgumentCaptor.forClass(Payload.class);

        verify(acquiredPresenterMock).chargeAcquired(payloadCaptor.capture(),
                captorEncryptionKey.capture());

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

    private PayloadBuilder generatePayloadBuilder() {
        PayloadBuilder payloadBuilder = new PayloadBuilder();

        payloadBuilder.setAmount(generateRandomDouble().toString());
        payloadBuilder.setCurrency("GB");
        payloadBuilder.setCountry(generateRandomString());
        payloadBuilder.setEmail(generateRandomString());
        payloadBuilder.setFirstname(generateRandomString());
        payloadBuilder.setLastname(generateRandomString());
        payloadBuilder.setIP(generateRandomString());
        payloadBuilder.setTxRef(generateRandomString());
        payloadBuilder.setMeta(generateRandomString());
        payloadBuilder.setSubAccount(generateRandomString());
        payloadBuilder.setPBFPubKey(generateRandomString());
        payloadBuilder.setIsPermanent(generateRandomBoolean());
        payloadBuilder.setPhonenumber(generateRandomString());
        payloadBuilder.setDevice_fingerprint(generateRandomString());
        return payloadBuilder;
    }

}
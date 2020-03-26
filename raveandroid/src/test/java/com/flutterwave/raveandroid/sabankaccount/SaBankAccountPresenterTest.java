package com.flutterwave.raveandroid.sabankaccount;

import android.content.Context;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.PayloadEncryptor;
import com.flutterwave.raveandroid.PayloadToJsonConverter;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.TransactionStatusChecker;
import com.flutterwave.raveandroid.data.SharedPrefsRequestImpl;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestAppComponent;
import com.flutterwave.raveandroid.di.TestremoteModule;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.rave_remote.responses.SaBankAccountResponse;
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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaBankAccountPresenterTest {

    @Mock
    SaBankAccountContract.View view;
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
    RemoteRepository networkRequest;
    @Inject
    SharedPrefsRequestImpl sharedPrefsRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Mock
    SaBankAccountPresenter presenterMock;

    @Mock
    ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();
    private SaBankAccountPresenter presenter;

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);
        presenter = new SaBankAccountPresenter(context, view);

        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testremoteModule(new TestremoteModule())
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
    public void fetchFee_onError_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        String message = generateRandomString();

        //act
        presenter.fetchFee(payload);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message);

        //assert
        verify(view).showFetchFeeFailed(transactionError);

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalledWithCorrectParams() {

        //arrange
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();
        Payload payload = generatePayload();

        //act
        presenter.fetchFee(payload);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(view).displayFee(feeCheckResponse.getData().getCharge_amount(), payload);

    }

    @Test
    public void fetchFee_onSuccess_exceptionThrown_showFetchFeeFailedCalledWithCorrectParams() {

        presenter.fetchFee(generatePayload());

        doThrow(new NullPointerException()).when(view).displayFee(any(String.class), any(Payload.class));

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view, times(1)).showFetchFeeFailed(transactionError);

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

    @Test
    public void chargeSaBankAccount_noDisplayFee_onSuccess_validResponseReturned_showWebViewCalled() {

        Payload payload = generatePayload();

        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);


        presenter.chargeSaBankAccount(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnSaChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnSaChargeRequestComplete.class);

        verify(networkRequest).chargeSaBankAccount(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());

        verify(sharedPrefsRequest).saveFlwRef(any(String.class));
        verify(view).showProgressIndicator(false);
        verify(view, never()).displayFee(any(String.class), any(Payload.class));
        verify(view).showWebView(any(String.class), any(String.class));

    }


    @Test
    public void chargeSaBankAccount_onSuccess_noRedirectUrlReturned_onPaymentErrorCalled_invalidRedirectMessage() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        presenter.chargeSaBankAccount(payload, generateRandomString());

        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnSaChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnSaChargeRequestComplete.class);

        verify(networkRequest).chargeSaBankAccount(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(generateChargeResponseWithNoRedirectUrl(), any(String.class));
        verify(view).showProgressIndicator(false);

        verify(view).onPaymentError(RaveConstants.inValidRedirectUrl);

    }


    @Test
    public void chargeSaBankAccount_onError_onPaymentErrorCalled_messageReturned() {

        Payload payload = generatePayload();
        payload.setPBFPubKey(generateRandomString());

        String message = generateRandomString();

        when(payloadToJsonConverter.convertChargeRequestPayloadToJson(any(Payload.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());

        presenter.chargeSaBankAccount(payload, generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnSaChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnSaChargeRequestComplete.class);

        verify(networkRequest).chargeSaBankAccount(any(ChargeRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message, generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(message);

    }


    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalledWithCorrectParams() {
        String flwRef = generateRandomString();
        RequeryResponse requeryResponse = generateRequerySuccessful();
        String jsonResponse = generateRandomString();

        when(sharedPrefsRequest.fetchFlwRef()).thenReturn(flwRef);
        presenter.requeryTx(generateRandomString());

        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(requeryResponse, jsonResponse);

        verify(view).showProgressIndicator(false);
        verify(view).onRequerySuccessful(requeryResponse, jsonResponse, flwRef);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalledWithCorrectParams() {

        String message = generateRandomString();
        String jsonResponse = generateRandomString();

        presenter.requeryTx(generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, jsonResponse);
        verify(view).onPaymentFailed(message, jsonResponse);

    }

    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalled() {
        when(transactionStatusChecker.getTransactionStatus(anyString(), anyString(), anyString())).thenReturn(false);
        presenter.verifyRequeryResponseStatus(generateRequerySuccessful(), generateRandomString(), ravePayInitializer);
        verify(view).onPaymentFailed(String.valueOf(anyObject()), anyString());
    }

    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalled() {

        when(transactionStatusChecker.getTransactionStatus(any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        when(ravePayInitializer.getAmount()).thenReturn(generateRandomDouble());
        when(ravePayInitializer.getCurrency()).thenReturn(generateRandomString());

        presenter.verifyRequeryResponseStatus(generateRequerySuccessful(), generateRandomString(), ravePayInitializer);
        verify(view).onPaymentSuccessful(String.valueOf(anyObject()), anyString());
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

    private SaBankAccountResponse generateChargeResponseWithNoRedirectUrl() {
        SaBankAccountResponse chargeResponse = generateValidChargeResponse();

        chargeResponse.getData().getData().setRedirectUrl(null);

        return chargeResponse;
    }

    private SaBankAccountResponse generateValidChargeResponse() {
        SaBankAccountResponse chargeResponse = new SaBankAccountResponse();
        chargeResponse.setData(new SaBankAccountResponse.Data());
        chargeResponse.getData().setResponseCode("00");
        chargeResponse.getData().setResponseMessage(generateRandomString());
        chargeResponse.getData().setData(new SaBankAccountResponse.Data_());
        chargeResponse.getData().getData().setRedirectUrl(generateRandomString());
        chargeResponse.getData().getData().setFlwReference(generateRandomString());
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
package com.flutterwave.raveandroid.account;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestremoteModule;
import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.RavePayInitializer;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AccountNoValidator;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.BankCodeValidator;
import com.flutterwave.raveandroid.validators.BanksMinimum100AccountPaymentValidator;
import com.flutterwave.raveandroid.validators.BvnValidator;
import com.flutterwave.raveandroid.validators.DateOfBirthValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAccount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldBVN;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldBankCode;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldDOB;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidAccountNoMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidBankCodeMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidBvnMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidCharge;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidDateOfBirthMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validAmountPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validEmailPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validPhonePrompt;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountUiPresenterTest {

    AccountUiPresenter accountUiPresenter;
    @Mock
    AccountUiContract.View view;
    @Inject
    Context context;
    @Inject
    EmailValidator emailValidator;
    @Inject
    AmountValidator amountValidator;
    @Inject
    PhoneValidator phoneValidator;
    @Inject
    DateOfBirthValidator dateOfBirthValidator;
    @Inject
    BvnValidator bvnValidator;
    @Inject
    AccountNoValidator accountNoValidator;
    @Inject
    BankCodeValidator bankCodeValidator;
    @Inject
    UrlValidator urlValidator;
    @Inject
    BanksMinimum100AccountPaymentValidator minimum100AccountPaymentValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Mock
    AccountUiPresenter accountUiPresenterMock;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        accountUiPresenter = new AccountUiPresenter(context, view);

        TestRaveUiComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testremoteModule(new TestremoteModule())
                .build();

        component.inject(this);
        component.inject(accountUiPresenter);
    }

    @Test
    public void chargeAccount_onSuccess_onDisplayInternetBankingPageCalledWithCorrectParams() {

        //arrange
        String authurl = generateRandomString();
        String flwRef = generateRandomString();
        ChargeResponse chargeResponse = generateValidChargeResponse();

        //act
        accountUiPresenter.chargeAccount(generatePayload(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid(anyString())).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).displayInternetBankingPage(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef());

    }

    @Test
    public void chargeAccount_onSuccess_validUrl_validateAccountChargeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(null);
        chargeResponse.getData().getValidateInstructions().setInstruction(null);
        String responseAsJsonString = generateRandomString();
        when(urlValidator.isUrlValid(anyString())).thenReturn(true);

        //act
        accountUiPresenter.chargeAccount(payload, generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());


        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), null);

    }

    @Test
    public void chargeAccount_onSuccess_noAuthUrl__validInstruction_onChargeAccountFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        boolean isInternetBanking = generateRandomBoolean();
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(generateRandomString());
        String responseAsJsonString = generateRandomString();

        //act
        accountUiPresenter.chargeAccount(payload, encryptionKey);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid(anyString())).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstruction());

    }

    @Test
    public void validateAccountCharge_onSuccess_onValidationSuccessfulCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponse();
        String responseAsJsonString = generateRandomString();
        String otp = generateRandomString();
        ravePayInitializer.setPublicKey(generateRandomString());

        //act
        accountUiPresenter.authenticateAccountCharge(chargeResponse.getData().getFlwRef(), otp, ravePayInitializer.getPublicKey());

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).onDataValidationSuccessful(chargeResponse.getData().getFlwRef(), responseAsJsonString);
    }

    @Test
    public void validateAccountCharge_onSuccess_inValidResponse_onValidationSuccessfulCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.setStatus(null);
        String responseAsJsonString = generateRandomString();
        String otp = generateRandomString();

        //act
        accountUiPresenter.authenticateAccountCharge(chargeResponse.getData().getFlwRef(), otp, ravePayInitializer.getPublicKey());

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).onPaymentError(invalidCharge);
    }

    @Test
    public void validateAccountCharge_onError_onValidationSuccessfulCalledWithcorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponse();
        String message = generateRandomString();
        String responseAsJsonString = generateRandomString();
        String otp = generateRandomString();
        ravePayInitializer.setPublicKey(generateRandomString());

        //act
        accountUiPresenter.authenticateAccountCharge(chargeResponse.getData().getFlwRef(), otp, ravePayInitializer.getPublicKey());

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message, responseAsJsonString);

        //assert
        verify(view).onPaymentError(message);
    }

    @Test
    public void validateAccountCharge_onSuccess_onValidateErrorCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        String responseAsJsonString = generateRandomString();
        String otp = generateRandomString();
        String message = generateRandomString();
        ravePayInitializer.setPublicKey(generateRandomString());

        //act
        accountUiPresenter.authenticateAccountCharge(chargeResponse.getData().getFlwRef(), otp, ravePayInitializer.getPublicKey());

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        verify(view).onAuthenticationError(chargeResponse.getStatus(), responseAsJsonString);
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        boolean internetBanking = generateRandomBoolean();
        String message = generateRandomString();

        //act
        accountUiPresenter.fetchFee(payload);

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
        boolean internetBanking = generateRandomBoolean();

        //act
        accountUiPresenter.fetchFee(payload);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(view).onTransactionFeeRetrieved(feeCheckResponse.getData().getCharge_amount(), payload);

    }

    @Test
    public void fetchFee_onSuccess_exceptionThrown_showFetchFeeFailedCalledWithCorrectParams() {

        accountUiPresenter.fetchFee(generatePayload());

        doThrow(new NullPointerException()).when(view).onTransactionFeeRetrieved(any(String.class), any(Payload.class));

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view, times(1)).showFetchFeeFailed(transactionError);

    }

    @Test
    public void getBanks_onSuccess_showBanksCalledWithCorrectParams() {

        //arrange
        List<Bank> bankList = generateBankList();

        //act
        accountUiPresenter.getBanksList();
        ArgumentCaptor<Callbacks.OnGetBanksRequestComplete> onGetBanksRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGetBanksRequestComplete.class);
        verify(networkRequest).getBanks(onGetBanksRequestCompleteArgumentCaptor.capture());
        onGetBanksRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(bankList);

        //assert
        verify(view).onBanksListRetrieved(bankList);
    }

    @Test
    public void getBanks_onError_showBanksCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();

        //act
        accountUiPresenter.getBanksList();
        ArgumentCaptor<Callbacks.OnGetBanksRequestComplete> onGetBanksRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGetBanksRequestComplete.class);
        verify(networkRequest).getBanks(onGetBanksRequestCompleteArgumentCaptor.capture());
        onGetBanksRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message);

        //assert
        verify(view).onGetBanksRequestFailed(message);
    }

    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalledWithCorrectParams() {

        String amount = generateRandomString();
        String currency = generateRandomString();
        String responseAsJsonString = generateRandomString();
        RequeryResponse requeryResponse = generateRequerySuccessful();

        when(transactionStatusChecker.getTransactionStatus(responseAsJsonString)).thenReturn(false);
        accountUiPresenter.verifyRequeryResponseStatus(responseAsJsonString);
        verify(view).onPaymentFailed(responseAsJsonString);
    }

    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalledWithCorrectParams() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).onDataValidationSuccessful(map);

    }

    @Test
    public void onDataCollected_phoneInvalid_showFieldErrorCalledWithCorrectParams() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(phoneValidator.isPhoneValid(anyString())).thenReturn(false);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, validPhonePrompt, TextInputLayout.class);

    }

    @Test
    public void onDataCollected_emailInvalid_showFieldErrorCalledWithCorrectParams() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(emailValidator.isEmailValid(anyString())).thenReturn(false);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, validEmailPrompt, TextInputLayout.class);

    }

    @Test
    public void onDataCollected_bankCodeInvalid_showFieldErrorCalledWithCorrectParams() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(bankCodeValidator.isBankCodeValid(anyString())).thenReturn(false);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, invalidBankCodeMessage, TextInputLayout.class);

    }

    @Test
    public void onDataCollected_bankCode057_inValidDateOfBirth_showFieldErrorCalledWithCorrectParams() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, "057");
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(dateOfBirthValidator.isDateValid(anyString())).thenReturn(false);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, invalidDateOfBirthMessage, TextInputLayout.class);

    }

    @Test
    public void onDataCollected_bankCode033_inValidBvn_showFieldErrorCalledWithCorrectParams() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, "033");
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(bvnValidator.isBvnValid(anyString())).thenReturn(false);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, invalidBvnMessage, TextInputLayout.class);

    }

    @Test
    public void onDataCollected_isAmountValid_isBankCodeValid_minimum100ValidationNotPassed_showGTBankAmountIssueCalled() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(amountValidator.isAmountValid(anyString())).thenReturn(true);
        when(bankCodeValidator.isBankCodeValid(anyString())).thenReturn(true);
        when(minimum100AccountPaymentValidator.isPaymentValid(anyString(), anyDouble())).thenReturn(false);

        //act
        accountUiPresenter.onDataCollected(map);

        //assert
        verify(view).showGTBankAmountIssue();

    }

    @Test
    public void onDataCollected_accountNumberInvalid_showFieldErrorCalledWithCorrectParams() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(accountNoValidator.isAccountNumberValid(anyString())).thenReturn(false);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, invalidAccountNoMessage, TextInputLayout.class);

    }

    @Test
    public void onDataCollected_amountInvalid_showFieldErrorCalledWithCorrectParams() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);

        when(amountValidator.isAmountValid(anyString())).thenReturn(false);
        //act
        accountUiPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, validAmountPrompt, TextInputLayout.class);

    }


    @Test
    public void processTransaction_isDisplayFeeFalse_chargeAccountCalledWithCorrectParams() {
        //arrange

        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());

        boolean isDisplayFee = false;
        String encryptionKey = generateRandomString();
        String amount = generateRandomDouble().toString();
        String country = "NG";
        String currency = "NGN";
        String email = map.get(fieldEmail).getData();
        String bankCode = map.get(fieldBankCode).getData();
        String bvn = map.get(fieldBVN).getData();
        String dob = map.get(fieldDOB).getData();
        String phone = map.get(fieldPhone).getData();
        String deviceId = generateRandomString();
        String txRef = generateRandomString();
        String meta = generateRandomString();
        String pubKey = generateRandomString();
        boolean isPreAuth = true;
        String fingerPrint = deviceId;

        accountUiPresenterMock.deviceIdGetter = deviceIdGetter;
        when(deviceIdGetter.getDeviceId()).thenReturn(deviceId);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(isDisplayFee);
        when(ravePayInitializer.getEncryptionKey()).thenReturn(encryptionKey);
        when(ravePayInitializer.getAmount()).thenReturn(Double.parseDouble(amount));
        when(ravePayInitializer.getTxRef()).thenReturn(txRef);
        when(ravePayInitializer.getMeta()).thenReturn(meta);
        when(ravePayInitializer.getIsPreAuth()).thenReturn(isPreAuth);
        when(ravePayInitializer.getPublicKey()).thenReturn(pubKey);

        //act
        doCallRealMethod().when(accountUiPresenterMock).processTransaction(any(HashMap.class), any(RavePayInitializer.class));
        accountUiPresenterMock.processTransaction(map, ravePayInitializer);

        ArgumentCaptor<String> captorEncryptionKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Boolean> captorIsDisplayFee = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Payload> payloadCaptor = ArgumentCaptor.forClass(Payload.class);

        verify(accountUiPresenterMock).chargeAccount(payloadCaptor.capture(),
                captorEncryptionKey.capture()
        );

        //assert
        assertEquals(encryptionKey, captorEncryptionKey.getValue());
        assertEquals(isDisplayFee, captorIsDisplayFee.getValue());

        Payload capturedPayload = payloadCaptor.getValue();
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());
        assertEquals(amount, capturedPayload.getAmount());
        assertEquals(country, capturedPayload.getCountry());
        assertEquals(currency, capturedPayload.getCurrency());
        assertEquals(email, capturedPayload.getEmail());
        assertEquals(fingerPrint, capturedPayload.getIP());
        assertEquals(txRef, capturedPayload.getTxRef());
        assertEquals(pubKey, capturedPayload.getPBFPubKey());
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());

    }

    @Test
    public void processTransaction_isDisplayFeeTrue_fetchFeCalledWithCorrectParams() {
        //arrange

        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());

        boolean isDisplayFee = true;
        boolean isInternetBanking = false;
        String encryptionKey = generateRandomString();
        String amount = generateRandomDouble().toString();
        String country = "NG";
        String currency = "NGN";
        String email = map.get(fieldEmail).getData();
        String bankCode = map.get(fieldBankCode).getData();
        String bvn = map.get(fieldBVN).getData();
        String dob = map.get(fieldDOB).getData();
        String phone = map.get(fieldPhone).getData();
        String account = map.get(fieldAccount).getData();
        String deviceId = generateRandomString();
        String txRef = generateRandomString();
        String meta = generateRandomString();
        String pubKey = generateRandomString();
        boolean isPreAuth = true;
        String fingerPrint = deviceId;

        accountUiPresenterMock.deviceIdGetter = deviceIdGetter;
        when(deviceIdGetter.getDeviceId()).thenReturn(deviceId);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(isDisplayFee);
        when(ravePayInitializer.getEncryptionKey()).thenReturn(encryptionKey);
        when(ravePayInitializer.getAmount()).thenReturn(Double.parseDouble(amount));
        when(ravePayInitializer.getTxRef()).thenReturn(txRef);
        when(ravePayInitializer.getMeta()).thenReturn(meta);
        when(ravePayInitializer.getIsPreAuth()).thenReturn(isPreAuth);
        when(ravePayInitializer.getPublicKey()).thenReturn(pubKey);

        //act
        doCallRealMethod().when(accountUiPresenterMock).processTransaction(any(HashMap.class), any(RavePayInitializer.class));
        accountUiPresenterMock.processTransaction(map, ravePayInitializer);
        ArgumentCaptor<Boolean> captorIsInternetBanking = ArgumentCaptor.forClass(Boolean.class);
        ArgumentCaptor<Payload> payloadCaptor = ArgumentCaptor.forClass(Payload.class);

        verify(accountUiPresenterMock).fetchFee(payloadCaptor.capture()
        );

        //assert
        assertEquals(isInternetBanking, captorIsInternetBanking.getValue());

        Payload capturedPayload = payloadCaptor.getValue();
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());
        assertEquals(amount, capturedPayload.getAmount());
        assertEquals(country, capturedPayload.getCountry());
        assertEquals(currency, capturedPayload.getCurrency());
        assertEquals(email, capturedPayload.getEmail());
        assertEquals(fingerPrint, capturedPayload.getIP());
        assertEquals(txRef, capturedPayload.getTxRef());
        assertEquals(pubKey, capturedPayload.getPBFPubKey());
        assertEquals(deviceId, capturedPayload.getDevice_fingerprint());

    }


    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        accountUiPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(true);

    }


    @Test
    public void processTransaction_getFee_onError_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountUiPresenter.processTransaction(data, ravePayInitializer);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        //assert
        verify(view).showFetchFeeFailed(transactionError);

    }

    @Test(expected = Exception.class)
    public void fetchFee_onSuccess_displayFeeException_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountUiPresenter.fetchFee(payload);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        doThrow(new Exception()).when(view).onTransactionFeeRetrieved(feeCheckResponse.getData().getCharge_amount(), payload);

        //assert
        verify(view).showFetchFeeFailed(transactionError);

    }

    @Test
    public void processTransaction_getFee_onSuccess_displayFeeCalled() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountUiPresenter.processTransaction(data, ravePayInitializer);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(view).onTransactionFeeRetrieved(anyString(), any(Payload.class));

    }

    //
    @Test
    public void chargeAccount_onError_onChargeAccountFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        String responseAsJsonString = generateRandomString();
        String message = generateRandomString();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountUiPresenter.chargeAccount(payload, generateRandomString());

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message, responseAsJsonString);

        //assert
        verify(view).onChargeAccountFailed(message, responseAsJsonString);

    }

    @Test
    public void chargeAccount_onSuccess_noAuthUrl_validateAccountChargeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        String responseAsJsonString = generateRandomString();

        //act
        accountUiPresenter.chargeAccount(payload, generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstructions().getInstruction());

    }

    @Test
    public void chargeAccount_onSuccess_inValidInstruction_validateAccountChargeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(null);
        chargeResponse.getData().getValidateInstructions().setInstruction(null);
        String responseAsJsonString = generateRandomString();

        //act
        accountUiPresenter.chargeAccount(payload, generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), null);

    }


    @Test
    public void chargeAccount_onSuccess_inValidInstruction_invalidAuth_validateAccountChargeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(generateRandomString());
        String responseAsJsonString = generateRandomString();
        when(urlValidator.isUrlValid(anyString())).thenReturn(false);

        //act
        accountUiPresenter.chargeAccount(payload, generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstruction());

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalledWithCorrectParams() {

        //arrange
        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();

        //act
        accountUiPresenter.requeryTx(generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(requeryResponse, responseAsJsonString);

        //assert
        verify(view).onRequerySuccessful(requeryResponse, responseAsJsonString);

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();
        String responseJsonAsString = generateRandomString();

        //act
        accountUiPresenter.requeryTx(generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, responseJsonAsString);

        //assert
        verify(view).onPaymentFailed(responseJsonAsString);

    }


    @Test
    public void onBankSelected_isInternetBanking_showAccountNumberField_Gone_CalledWithCorrectParams() {

        //arrange
        Bank internetBankingBank = generateBank(true, generateRandomString());

        //act
        accountUiPresenter.onBankSelected(internetBankingBank);

        //assert
        verify(view).showAccountNumberField(View.GONE);
    }

    @Test
    public void onBankSelected_isNotInternetBanking_showAccountNumberField_Visible_CalledWithCorrectParams() {

        //arrange
        Bank nonInternetBankingBank = generateBank(false, generateRandomString());

        //act
        accountUiPresenter.onBankSelected(nonInternetBankingBank);

        //assert
        verify(view).showAccountNumberField(View.VISIBLE);

    }


    @Test
    public void onBankSelected_bankCode057_showDateOfBirth_Visible_CalledWithCorrectParams() {

        //arrange
        Bank bank057 = generateBank(generateRandomBoolean(), "057");

        //act
        accountUiPresenter.onBankSelected(bank057);

        //assert
        verify(view).showDateOfBirth(View.VISIBLE);
    }

    @Test
    public void onBankSelected_bankCode033_showDateOfBirth_Visible_CalledWithCorrectParams() {

        //arrange
        Bank bank033 = generateBank(generateRandomBoolean(), "033");

        //act
        accountUiPresenter.onBankSelected(bank033);

        //assert
        verify(view).showDateOfBirth(View.VISIBLE);
    }

    @Test
    public void onBankSelected_isNot033or057_hideAccountNumberFieldCalledWithCorrectParams() {

        //arrange
        Bank bank = generateBank(generateRandomBoolean(), "234");

        //act
        accountUiPresenter.onBankSelected(bank);

        //assert
        verify(view).showDateOfBirth(View.GONE);

    }

    @Test
    public void onBankSelected_bankCode033_showBVN_Visible_CalledWithCorrectParams() {

        //arrange
        Bank bank033 = generateBank(generateRandomBoolean(), "033");

        //act
        accountUiPresenter.onBankSelected(bank033);

        //assert
        verify(view).showBVN(View.VISIBLE);
    }

    @Test
    public void onBankSelected_bankCodeNot033_showBVN_Gone_CalledWithCorrectParams() {

        //arrange
        Bank bankNot033 = generateBank(generateRandomBoolean(), "999");

        //act
        accountUiPresenter.onBankSelected(bankNot033);

        //assert
        verify(view).showBVN(View.GONE);
    }


    @Test
    public void init_validEmail_onEmailValidatedCalledWithCorrectParams() {

        //arrange
        String email = generateEmail(true);
        when(ravePayInitializer.getEmail()).thenReturn(email);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);

        //act
        accountUiPresenter.init(ravePayInitializer);

        //assert
        verify(view).onEmailValidated(email, View.GONE);

    }

    @Test
    public void init_inValidEmail_onEmailValidatedCalledWithEmptyEmailWithCorrectParams() {

        //arrange
        String email = generateRandomString();
        when(emailValidator.isEmailValid(email)).thenReturn(false);

        //act
        accountUiPresenter.init(ravePayInitializer);

        //assert
        verify(view).onEmailValidated("", View.VISIBLE);
    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmountWithCorrectParams() {

        //arrange
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        //act
        accountUiPresenter.init(ravePayInitializer);

        //assert
        verify(view).onAmountValidated(amount.toString(), View.GONE);

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmountWithCorrectParams() {

        //arrange
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        //act
        accountUiPresenter.init(ravePayInitializer);

        //assert
        verify(view).onAmountValidated("", View.VISIBLE);

    }

    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalledWithCorrectParams() {

        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(any(String.class)))
                .thenReturn(true);

        when(ravePayInitializer.getAmount()).thenReturn(generateRandomDouble());
        when(ravePayInitializer.getCurrency()).thenReturn(generateRandomString());

        accountUiPresenter.verifyRequeryResponseStatus(responseAsJsonString);
        verify(view).onPaymentSuccessful(responseAsJsonString);
    }


    private String generateEmail(boolean isValid) {
        if (isValid) {
            return "rave@rave.com";
        } else {
            return generateRandomString();
        }

    }

    private void generateViewValidation(int failedValidations) {

        List<Boolean> falses = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            if (i < failedValidations) {
                falses.add(false);
            } else {
                falses.add(true);
            }
        }

        when(emailValidator.isEmailValid(anyString())).thenReturn(falses.get(0));
        when(amountValidator.isAmountValid(anyString())).thenReturn(falses.get(1));
        when(phoneValidator.isPhoneValid(anyString())).thenReturn(falses.get(2));
        when(dateOfBirthValidator.isDateValid(anyString())).thenReturn(falses.get(3));
        when(bvnValidator.isBvnValid(anyString())).thenReturn(falses.get(4));
        when(accountNoValidator.isAccountNumberValid(anyString())).thenReturn(falses.get(5));
        when(bankCodeValidator.isBankCodeValid(anyString())).thenReturn(falses.get(6));
        when(minimum100AccountPaymentValidator.isPaymentValid(anyString(), anyDouble())).thenReturn(falses.get(7));

    }

    private HashMap<String, ViewObject> generateViewData(int viewID, String data) {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldEmail, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldAmount, new ViewObject(viewID, generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldPhone, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldDOB, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldBVN, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldAccount, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldBankCode, new ViewObject(viewID, data, TextInputLayout.class));

        return viewData;
    }

    private int generateRandomInt() {
        return new Random().nextInt();
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

    private Boolean generateRandomBoolean() {
        return new Random().nextBoolean();
    }

    private Payload generatePayload() {
        List<Meta> metas = new ArrayList<>();
        List<SubAccount> subAccounts = new ArrayList<>();
        return new Payload(generateRandomString(), metas, subAccounts, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
    }

    private FeeCheckResponse generateFeeCheckResponse() {
        FeeCheckResponse feeCheckResponse = new FeeCheckResponse();
        FeeCheckResponse.Data feeCheckResponseData = new FeeCheckResponse.Data();

        feeCheckResponseData.setCharge_amount(generateRandomString());
        feeCheckResponse.setData(feeCheckResponseData);

        return feeCheckResponse;
    }

    private ChargeResponse generateRandomChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        ChargeResponse.Data chargeResponseData = new ChargeResponse.Data();

        chargeResponseData.setChargeResponseCode(generateRandomString());

        chargeResponse.setData(chargeResponseData);

        return chargeResponse;
    }

    private ChargeResponse generateValidChargeResponse() {
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.setStatus(success);
        chargeResponse.getData().setAuthurl("http://www.rave.com");
        chargeResponse.getData().setFlwRef(generateRandomString());
        return chargeResponse;
    }

    private ChargeResponse generateInValidChargeResponse() {
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setChargeResponseCode("00");
        chargeResponse.setStatus("pending");
        ChargeResponse.AccountValidateInstructions instructions = new ChargeResponse.AccountValidateInstructions();
        instructions.setInstruction(generateRandomString());
        chargeResponse.getData().setValidateInstructions(instructions);
        chargeResponse.getData().setFlwRef(generateRandomString());
        return chargeResponse;
    }

    private RequeryResponse generateRequerySuccessful() {
        RequeryResponse requeryResponse = new RequeryResponse();
        RequeryResponse.Data data = new RequeryResponse.Data();
        data.setChargeResponseCode("00");
        requeryResponse.setData(data);
        return requeryResponse;
    }

    private Bank generateBank(boolean internetBanking, String bankCode) {
        Bank bank = new Bank();
        bank.setInternetbanking(internetBanking);
        bank.setBankcode(bankCode);
        return bank;
    }

    private List<Bank> generateBankList() {
        return new ArrayList<>();
    }

}
package com.flutterwave.raveandroid.account;

import com.google.android.material.textfield.TextInputLayout;
import android.view.View;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.di.DaggerTestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
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
import com.google.gson.Gson;

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
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidDateOfBirthMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validAmountPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validEmailPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validPhonePrompt;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountPresenterTest {

    AccountUiPresenter accountUiPresenter;
    @Mock
    AccountUiContract.View view;
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
        accountUiPresenter = new AccountUiPresenter(view);

        TestRaveUiComponent component = DaggerTestRaveUiComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(accountUiPresenter);
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
        String meta = generateRandomMetaString();
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
        ArgumentCaptor<Payload> payloadCaptor = ArgumentCaptor.forClass(Payload.class);

        verify(accountUiPresenterMock).chargeAccount(payloadCaptor.capture(),
                captorEncryptionKey.capture()
        );

        //assert
        assertEquals(encryptionKey, captorEncryptionKey.getValue());

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

    private String generateRandomMetaString() {
        ArrayList<Meta> metas = new ArrayList<Meta>();
        metas.add(new Meta("x", "y"));
        return new Gson().toJson(metas);
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
        String meta = generateRandomMetaString();
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

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        //assert
        verify(view).onFeeFetchError(anyString());

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

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(view).onTransactionFeeRetrieved(anyString(), any(Payload.class), anyString());

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
        feeCheckResponse.getData().setFee(generateRandomString());

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
        Bank bank = new Bank("name", bankCode);
        bank.setInternetbanking(internetBanking);
        return bank;
    }

    private List<Bank> generateBankList() {
        return new ArrayList<>();
    }

}
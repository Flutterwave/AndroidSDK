package com.flutterwave.raveandroid.account;

import android.content.Context;
import android.support.design.widget.TextInputLayout;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.ValidateChargeBody;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestAppComponent;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SubAccount;
import com.flutterwave.raveandroid.validators.AccountNoValidator;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.BankCodeValidator;
import com.flutterwave.raveandroid.validators.BanksMinimum100AccountPaymentValidator;
import com.flutterwave.raveandroid.validators.BvnValidator;
import com.flutterwave.raveandroid.validators.DateOfBirthValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.flutterwave.raveandroid.validators.PhoneValidator;
import com.flutterwave.raveandroid.validators.UrlValidator;

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

import static com.flutterwave.raveandroid.RaveConstants.fieldAccount;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldBVN;
import static com.flutterwave.raveandroid.RaveConstants.fieldBankCode;
import static com.flutterwave.raveandroid.RaveConstants.fieldDOB;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.RaveConstants.success;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountPresenterTest {

    AccountPresenter accountPresenter;
    @Mock
    AccountContract.View view;
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
    NetworkRequestImpl networkRequest;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        accountPresenter = new AccountPresenter(context, view);

        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(accountPresenter);

        accountPresenter.networkRequest = networkRequest;
    }

    @Test
    public void chargeAccount_onSuccess_onDisplayInternetBankingPageCalled() {

        accountPresenter.chargeAccount(generatePayload(), generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid("http://www.rave.com")).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());
        verify(view).onDisplayInternetBankingPage(anyString(), anyString());

    }


    @Test
    public void chargeAccount_onError_onChargeAccountFailedCalled() {

        accountPresenter.chargeAccount(generatePayload(), generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid("http://www.rave.com")).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());
        verify(view).onChargeAccountFailed(anyString(), anyString());

    }

    @Test
    public void chargeAccount_onSuccess_noAuthUrl_onChargeAccountFailedCalled() {

        accountPresenter.chargeAccount(generatePayload(), generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid("http://www.rave.com")).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateInValidChargeResponse(), generateRandomString());
        verify(view).validateAccountCharge(anyString(), anyString(), String.valueOf(anyObject()));

    }

    @Test
    public void validateAccountCharge_onSuccess_onValidationSuccessfulCalled() {
        accountPresenter.validateAccountCharge(generateRandomString(), generateRandomString(), generateRandomString());
        generateValidChargeResponse();

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());

        verify(view).onValidationSuccessful(anyString(), anyString());
    }

    @Test
    public void validateAccountCharge_onError_onValidationSuccessfulCalled() {
        accountPresenter.validateAccountCharge(generateRandomString(), generateRandomString(), generateRandomString());
        generateValidChargeResponse();

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());

        verify(view).onPaymentError(anyString());
    }

    @Test
    public void validateAccountCharge_onError_onValidateErrorCalled() {
        accountPresenter.validateAccountCharge(generateRandomString(), generateRandomString(), generateRandomString());
        generateValidChargeResponse();

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateInValidChargeResponse(), generateRandomString());

        verify(view).onValidateError(anyString(), anyString());
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled() {

        accountPresenter.fetchFee(generatePayload(), generateRandomBoolean());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        verify(view).showFetchFeeFailed(anyString());

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalled() {

        accountPresenter.fetchFee(generatePayload(), generateRandomBoolean());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view).displayFee(anyString(), any(Payload.class), anyBoolean());

    }


    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalled() {

        accountPresenter.requeryTx(generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful(), generateRandomString());
        verify(view).onRequerySuccessful(any(RequeryResponse.class), anyString());

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        accountPresenter.requeryTx(generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());
        verify(view).onPaymentFailed(anyString(), anyString());

    }


    @Test
    public void getBanks_onSuccess_showBanksCalled() {
        accountPresenter.getBanks();
        ArgumentCaptor<Callbacks.OnGetBanksRequestComplete> onGetBanksRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGetBanksRequestComplete.class);
        verify(networkRequest).getBanks(onGetBanksRequestCompleteArgumentCaptor.capture());
        onGetBanksRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(generateBankList());

        verify(view).showBanks(generateBankList());
    }

    @Test
    public void getBanks_onError_showBanksCalled() {
        accountPresenter.getBanks();
        ArgumentCaptor<Callbacks.OnGetBanksRequestComplete> onGetBanksRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGetBanksRequestComplete.class);
        verify(networkRequest).getBanks(onGetBanksRequestCompleteArgumentCaptor.capture());
        onGetBanksRequestCompleteArgumentCaptor.getAllValues().get(0).onError(generateRandomString());

        verify(view).onGetBanksRequestFailed(anyString());
    }

    private List<Bank> generateBankList() {
        return new ArrayList<>();
    }

    @Test
    public void verifyRequeryResponseStatus_onPaymentFailedCalled() {
        //this needs to be reviewed
        accountPresenter.verifyRequeryResponseStatus(generateRequerySuccessful(), generateJSONResponse(), ravePayInitializer);
        verify(view).onPaymentFailed(String.valueOf(anyObject()), anyString());
    }

    @Test
    public void onDataCollected_inValidDataPassed_showFieldErrorCalled() {
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> hashMap = generateViewData(viewID);
        int failedValidations = 1;
        generateViewValidation(failedValidations);

        accountPresenter.onDataCollected(hashMap);

        verify(view, times(failedValidations)).showFieldError(anyInt(), anyString(), (Class<?>) anyObject());

    }


    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID);
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        accountPresenter.onDataCollected(map);
        //assert
        verify(view).onValidationSuccessful(any(HashMap.class));

    }

    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        int viewID = generateRandomInt();
        ArgumentCaptor<Boolean> booleanArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        HashMap<String, ViewObject> data = generateViewData(viewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        accountPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(booleanArgumentCaptor.capture());

        assertEquals(true, booleanArgumentCaptor.getAllValues().get(0));
    }

    @Test
    public void processTransaction_displayFeeIsEnabled_getFeeCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        accountPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), any(Callbacks.OnGetFeeRequestComplete.class));
    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeAccountCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        accountPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest)
                .chargeAccount(any(ChargeRequestBody.class),
                        any(Callbacks.OnChargeRequestComplete.class));
    }


    @Test
    public void onBankSelected_isInternetBanking_bankCode057_showAccountNumberFieldCalled() {
        accountPresenter.onBankSelected(generateBank_057());
        boolean isInternetBanking = generateRandomBoolean();

        if (isInternetBanking) {
            verify(view).showAccountNumberField(anyInt());
        }
    }

    @Test
    public void onBankSelected_isInternetBanking_bankCode033_showAccountNumberFieldCalled() {
        accountPresenter.onBankSelected(generateBank_033());
        boolean isInternetBanking = generateRandomBoolean();

        if (isInternetBanking) {
            verify(view).showAccountNumberField(anyInt());
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

    private HashMap<String, ViewObject> generateViewData(int viewID) {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldEmail, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldAmount, new ViewObject(viewID, generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldPhone, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldDOB, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldBVN, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldAccount, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));
        viewData.put(fieldBankCode, new ViewObject(viewID, generateRandomString(), TextInputLayout.class));

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
        return new RequeryResponse();
    }

    private Bank generateBank_057() {
        Bank bank = new Bank();
        bank.setInternetbanking(true);
        bank.setBankcode("057");
        return bank;
    }

    private Bank generateBank_033() {
        Bank bank = new Bank();
        bank.setInternetbanking(true);
        bank.setBankcode("033");
        return bank;
    }

    private String generateJSONResponse() {
        return "{\"data\":{\"status\": \"success\",\"amount\": \"100\",\"currency\": \"NGN\",\"chargeResponseCode\": \"00\"}}";
    }
}
package com.flutterwave.raveandroid.account;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.TransactionStatusChecker;
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
import static com.flutterwave.raveandroid.RaveConstants.invalidAccountNoMessage;
import static com.flutterwave.raveandroid.RaveConstants.invalidBankCodeMessage;
import static com.flutterwave.raveandroid.RaveConstants.invalidBvnMessage;
import static com.flutterwave.raveandroid.RaveConstants.invalidDateOfBirthMessage;
import static com.flutterwave.raveandroid.RaveConstants.success;
import static com.flutterwave.raveandroid.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.RaveConstants.validAmountPrompt;
import static com.flutterwave.raveandroid.RaveConstants.validEmailPrompt;
import static com.flutterwave.raveandroid.RaveConstants.validPhonePrompt;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
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
    @Inject
    TransactionStatusChecker transactionStatusChecker;

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
    }

    @Test
    public void chargeAccount_onSuccess_onDisplayInternetBankingPageCalledWithCorrectParams() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
        ChargeResponse chargeResponse = generateValidChargeResponse();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountPresenter.processTransaction(data, ravePayInitializer);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid(anyString())).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).onDisplayInternetBankingPage(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef());

    }


    @Test
    public void processTransaction_onError_onChargeAccountFailedCalledWithCorrectParams() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
        String responseAsJsonString = generateRandomString();
        String message = generateRandomString();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountPresenter.processTransaction(data, ravePayInitializer);

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
        accountPresenter.chargeAccount(payload, generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).validateAccountCharge(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstructions().getInstruction());

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
        accountPresenter.chargeAccount(payload, generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).validateAccountCharge(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), null);

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
        accountPresenter.chargeAccount(payload, generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());


        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).validateAccountCharge(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), null);

    }

    @Test
    public void chargeAccount_onSuccess_validInstruction_validateAccountChargeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(generateRandomString());
        String responseAsJsonString = generateRandomString();

        //act
        accountPresenter.chargeAccount(payload, generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);

        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).validateAccountCharge(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstruction());

    }

    @Test
    public void chargeAccount_onSuccess_validUrl_onDisplayInternetBankingPageCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(generateRandomString());
        String responseAsJsonString = generateRandomString();

        //act
        accountPresenter.chargeAccount(payload, generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        when(urlValidator.isUrlValid(anyString())).thenReturn(true);
        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJsonString);

        //assert
        verify(view).onDisplayInternetBankingPage(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef());

    }


    @Test
    public void chargeAccount_onError_onChargeAccountFailedCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        String message = generateRandomString();
        String responseAsJsonString = generateRandomString();

        //act
        accountPresenter.chargeAccount(payload, generateRandomString(), generateRandomBoolean());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        when(urlValidator.isUrlValid(anyString())).thenReturn(true);
        verify(networkRequest).chargeAccount(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message, responseAsJsonString);

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onChargeAccountFailed(message, responseAsJsonString);

    }


    @Test
    public void validateAccountCharge_onSuccess_onValidationSuccessfulCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponse();
        String responseJsonAsString = generateRandomString();

        //act
        accountPresenter.validateAccountCharge(chargeResponse.getData().getFlwRef(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseJsonAsString);

        //assert
        verify(view).onValidationSuccessful(chargeResponse.getData().getFlwRef(), responseJsonAsString);
    }

    @Test
    public void validateAccountCharge_onError_onPaymentErrorCalledWithParams() {

        //arrange
        String message = generateRandomString();
        String responseJsonAsString = generateRandomString();

        //act
        accountPresenter.validateAccountCharge(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message, responseJsonAsString);

        //assert
        verify(view).onPaymentError(message);
    }

    @Test
    public void validateAccountCharge_onError_onValidateErrorCalledWithCorrectParams() {

        //arrange
        String responseJsonAsString = generateRandomString();
        ChargeResponse chargeResponse = generateInValidChargeResponse();

        //act
        accountPresenter.validateAccountCharge(generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);
        verify(networkRequest).validateAccountCard(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse, responseJsonAsString);

        //assert
        verify(view).onValidateError(chargeResponse.getStatus(), responseJsonAsString);
    }

    @Test
    public void processTransaction_getFee_onError_showFetchFeeFailedCalledWithCorrectParams() {

        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        accountPresenter.processTransaction(data, ravePayInitializer);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        verify(view).showFetchFeeFailed(transactionError);

    }

    @Test
    public void processTransaction_getFee_onSuccess_displayFeeCalled() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountPresenter.processTransaction(data, ravePayInitializer);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        //assert
        verify(view).displayFee(anyString(), any(Payload.class), anyBoolean());

    }

    @Test(expected = RuntimeException.class)
    public void processTransaction_getFee_onSuccess_throwsException_showFetchFeeFailedCalledWithCorrectParams() {

        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();
        Payload payload = generatePayload();

        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        accountPresenter.processTransaction(data, ravePayInitializer);

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        doThrow(new Exception()).when(view).displayFee(feeCheckResponse.getData().getCharge_amount(), payload, false);

        //assert
        verify(view).showFetchFeeFailed(transactionError);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalledWithCorrectParams() {

        //arrange
        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();

        //act
        accountPresenter.requeryTx(generateRandomString(), generateRandomString());
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
        accountPresenter.requeryTx(generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, responseJsonAsString);

        //assert
        verify(view).onPaymentFailed(message, responseJsonAsString);

    }


    @Test
    public void getBanks_onSuccess_showBanksCalledWithCorrectParams() {

        //arrange
        List<Bank> bankList = generateBankList();

        //act
        accountPresenter.getBanks();
        ArgumentCaptor<Callbacks.OnGetBanksRequestComplete> onGetBanksRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGetBanksRequestComplete.class);
        verify(networkRequest).getBanks(onGetBanksRequestCompleteArgumentCaptor.capture());
        onGetBanksRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(bankList);

        //assert
        verify(view).showBanks(bankList);
    }

    @Test
    public void getBanks_onError_onGetBanksRequestFailedCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();

        //act
        accountPresenter.getBanks();
        ArgumentCaptor<Callbacks.OnGetBanksRequestComplete> onGetBanksRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(Callbacks.OnGetBanksRequestComplete.class);
        verify(networkRequest).getBanks(onGetBanksRequestCompleteArgumentCaptor.capture());
        onGetBanksRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message);

        //assert
        verify(view).onGetBanksRequestFailed(message);
    }



    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalledWithCorrectParams() {

        //arrange
        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(anyString(), anyString(), anyString())).thenReturn(false);

        //act
        accountPresenter.verifyRequeryResponseStatus(requeryResponse, responseAsJsonString, ravePayInitializer);

        //assert
        verify(view).onPaymentFailed(requeryResponse.getStatus(), responseAsJsonString);
    }

    @Test
    public void onDataCollected_inValidDataPassed_showFieldErrorCalled() {
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> hashMap = generateViewData(viewID, generateRandomString());
        int failedValidations = 1;
        generateViewValidation(failedValidations);

        accountPresenter.onDataCollected(hashMap);

        verify(view, times(failedValidations)).showFieldError(anyInt(), anyString(), (Class<?>) anyObject());

    }


    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(viewID, generateRandomString());
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        accountPresenter.onDataCollected(map);
        //assert
        verify(view).onValidationSuccessful(any(HashMap.class));

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
        accountPresenter.onDataCollected(map);
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
        accountPresenter.onDataCollected(map);
        //assert
        verify(view).showFieldError(viewID, validAmountPrompt, TextInputLayout.class);

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
        accountPresenter.onDataCollected(map);
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
        accountPresenter.onDataCollected(map);
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
        accountPresenter.onDataCollected(map);
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
        accountPresenter.onDataCollected(map);
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
        accountPresenter.onDataCollected(map);
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
        accountPresenter.onDataCollected(map);
        //assert
        verify(view).showGTBankAmountIssue();

    }

    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        accountPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(true);

    }

    @Test
    public void processTransaction_displayFeeIsEnabled_getFeeCalled() {
        //arrange
        int viewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
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
        HashMap<String, ViewObject> data = generateViewData(viewID, generateRandomString());
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
    public void onBankSelected_isInternetBanking_showAccountNumberField_Gone_Called() {
        Bank internetBankingBank = generateBank(true, generateRandomString());
        accountPresenter.onBankSelected(internetBankingBank);
        verify(view).showAccountNumberField(View.GONE);
    }

    @Test
    public void onBankSelected_isNotInternetBanking_showAccountNumberField_Visible_Called() {
        Bank nonInternetBankingBank = generateBank(false, generateRandomString());
        accountPresenter.onBankSelected(nonInternetBankingBank);
        verify(view).showAccountNumberField(View.VISIBLE);

    }


    @Test
    public void onBankSelected_bankCode057_showDateOfBirth_Visible_Called() {
        Bank bank057 = generateBank(generateRandomBoolean(), "057");
        accountPresenter.onBankSelected(bank057);
        verify(view).showDateOfBirth(View.VISIBLE);
    }

    @Test
    public void onBankSelected_bankCode033_showDateOfBirth_Visible_Called() {
        Bank bank033 = generateBank(generateRandomBoolean(), "033");
        accountPresenter.onBankSelected(bank033);
        verify(view).showDateOfBirth(View.VISIBLE);
    }

    @Test
    public void onBankSelected_isNot033or057_hideAccountNumberFieldCalled() {
        Bank bank = generateBank(generateRandomBoolean(), "234");
        accountPresenter.onBankSelected(bank);
        verify(view).showDateOfBirth(View.GONE);

    }

    @Test
    public void onBankSelected_bankCode033_showBVN_Visible_Called() {
        Bank bank033 = generateBank(generateRandomBoolean(), "033");
        accountPresenter.onBankSelected(bank033);
        verify(view).showBVN(View.VISIBLE);
    }

    @Test
    public void onBankSelected_bankCodeNot033_showBVN_Gone_Called() {
        Bank bankNot033 = generateBank(generateRandomBoolean(), "999");
        accountPresenter.onBankSelected(bankNot033);
        verify(view).showBVN(View.GONE);
    }


    @Test
    public void init_validEmail_onEmailValidatedCalledWithValidEmailCorrectParamsPassed() {
        String email = generateEmail(true);
        when(ravePayInitializer.getEmail()).thenReturn(email);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);
        accountPresenter.init(ravePayInitializer);

        verify(view).onEmailValidated(email, View.GONE);

    }

    @Test
    public void init_inValidEmail_onEmailValidatedCalledWithEmptyEmail() {

        String email = generateRandomString();
        when(emailValidator.isEmailValid(email)).thenReturn(false);
        accountPresenter.init(ravePayInitializer);

        verify(view).onEmailValidated("", View.VISIBLE);
    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        accountPresenter.init(ravePayInitializer);

        verify(view).onAmountValidated(amount.toString(), View.GONE);

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        accountPresenter.init(ravePayInitializer);
        verify(view).onAmountValidated("", View.VISIBLE);

    }

    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalled() {

        when(transactionStatusChecker.getTransactionStatus(any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        when(ravePayInitializer.getAmount()).thenReturn(generateRandomDouble());
        when(ravePayInitializer.getCurrency()).thenReturn(generateRandomString());

        accountPresenter.verifyRequeryResponseStatus(generateRequerySuccessful(), generateRandomString(), ravePayInitializer);
        verify(view).onPaymentSuccessful(String.valueOf(anyObject()), anyString());
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

    private List<Bank> generateBankList() {
        return new ArrayList<>();
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

    private Bank generateBank(boolean internetBanking, String bankCode) {
        Bank bank = new Bank();
        bank.setInternetbanking(internetBanking);
        bank.setBankcode(bankCode);
        return bank;
    }

}
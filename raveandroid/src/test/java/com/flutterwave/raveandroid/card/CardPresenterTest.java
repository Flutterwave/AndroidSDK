package com.flutterwave.raveandroid.card;

import android.content.Context;
import com.google.android.material.textfield.TextInputLayout;
import android.view.View;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadEncryptor;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.TransactionStatusChecker;
import com.flutterwave.raveandroid.ViewObject;
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
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CardNoValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;

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

import static com.flutterwave.raveandroid.RaveConstants.ACCESS_OTP;
import static com.flutterwave.raveandroid.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.RaveConstants.GTB_OTP;
import static com.flutterwave.raveandroid.RaveConstants.NOAUTH;
import static com.flutterwave.raveandroid.RaveConstants.NOAUTH_INTERNATIONAL;
import static com.flutterwave.raveandroid.RaveConstants.PIN;
import static com.flutterwave.raveandroid.RaveConstants.VBV;
import static com.flutterwave.raveandroid.RaveConstants.enterOTP;
import static com.flutterwave.raveandroid.RaveConstants.expired;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldcardNoStripped;
import static com.flutterwave.raveandroid.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.RaveConstants.success;
import static com.flutterwave.raveandroid.RaveConstants.tokenExpired;
import static com.flutterwave.raveandroid.RaveConstants.tokenNotFound;
import static com.flutterwave.raveandroid.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.RaveConstants.unknownAuthmsg;
import static com.flutterwave.raveandroid.RaveConstants.validExpiryDatePrompt;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardPresenterTest {

    private CardPresenter cardPresenter;
    @Mock
    CardContract.View view;
    @Inject
    Context context;
    @Inject
    AmountValidator amountValidator;
    @Inject
    EmailValidator emailValidator;
    @Inject
    CvvValidator cvvValidator;
    @Inject
    CardExpiryValidator cardExpiryValidator;
    @Inject
    CardNoValidator cardNoValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    NetworkRequestImpl networkRequest;
    @Inject
    PayloadEncryptor payloadEncryptor;

    @Inject
    TransactionStatusChecker transactionStatusChecker;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cardPresenter = new CardPresenter(context, view);
        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(cardPresenter);
        stubPayloadEncryptor();

    }

    @Test
    public void init_validEmail_onEmailValidatedCalledWithValidEmailCorrectParamsPassed() {

        //arrange
        String email = generateEmail(true);
        when(ravePayInitializer.getEmail()).thenReturn(email);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);

        //act
        cardPresenter.init(ravePayInitializer);

        //assert
        verify(view).onEmailValidated(email, View.GONE);

    }

    @Test
    public void init_inValidEmail_onEmailValidatedCalledWithEmptyEmail() {

        //arrange
        String email = generateEmail(false);
        when(emailValidator.isEmailValid(email)).thenReturn(false);

        //act
        cardPresenter.init(ravePayInitializer);

        //arrange
        verify(view).onEmailValidated("", View.VISIBLE);
    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {

        //arrange
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        //act
        cardPresenter.init(ravePayInitializer);

        //assert
        verify(view).onAmountValidated(amount.toString(), View.GONE);

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmount() {

        //arrange
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        //act
        cardPresenter.init(ravePayInitializer);

        //assert
        verify(view).onAmountValidated("", View.VISIBLE);

    }


    @Test
    public void onDataCollected_InvalidDataPassed_showFieldErrorCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 3;
        generateViewValidation(failedValidations);
        //act
        cardPresenter.onDataCollected(map);
        //assert
        verify(view, times(failedValidations)).showFieldError(anyInt(), anyString(), (Class<?>) anyObject());

    }

    @Test
    public void onDataCollected_isCardExpiryValid_showFieldErrorCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int viewId = map.get(fieldCardExpiry).getViewId();
        Class cardExpiryViewType = map.get(fieldCardExpiry).getViewType();

        //act
        cardPresenter.onDataCollected(map);

        when(cardExpiryValidator.isCardExpiryValid(generateRandomString())).thenReturn(false);
        //assert
        verify(view).showFieldError(viewId, validExpiryDatePrompt, cardExpiryViewType);


    }


    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 0;

        generateViewValidation(failedValidations);
        //act
        cardPresenter.onDataCollected(map);
        //assert
        verify(view, times(failedValidations)).showFieldError(anyInt(), anyString(), (Class<?>) anyObject());

    }

    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        ArgumentCaptor<Boolean> booleanArgumentCaptor = ArgumentCaptor.forClass(Boolean.class);
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        cardPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(booleanArgumentCaptor.capture());

        assertEquals(true, booleanArgumentCaptor.getAllValues().get(0));
    }

    @Test
    public void processTransaction_displayFeeIsEnabled_getFeeCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        cardPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), any(Callbacks.OnGetFeeRequestComplete.class));
    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeCardCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        cardPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest)
                .charge(any(ChargeRequestBody.class),
                        any(Callbacks.OnChargeRequestComplete.class));
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled() {

        cardPresenter.fetchFee(generatePayload(), generateRandomInt());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        verify(view).showFetchFeeFailed(anyString());

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalled() {

        cardPresenter.fetchFee(generatePayload(), generateRandomInt());

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view).displayFee(anyString(), any(Payload.class), anyInt());

    }

    @Test
    public void fetchFee_onSuccess_exceptionThrown_showFetchFeeFailedCalledWithCorrectParams() {

        cardPresenter.fetchFee(generatePayload(), 1);

        doThrow(new NullPointerException()).when(view).displayFee(any(String.class), any(Payload.class), any(Integer.class));

        ArgumentCaptor<Callbacks.OnGetFeeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnGetFeeRequestComplete.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(view, times(1)).showFetchFeeFailed(transactionError);

    }

    @Test
    public void chargeCard_onSuccessWithPIN_onPinAuthModelSuggestedCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(PIN);
        Payload payload = generatePayload();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onPinAuthModelSuggested(payload);

    }

    @Test
    public void chargeCard_onSuccessWithAVS_VBVSECURECODE_onAVS_VBVSECURECODEModelSuggestedCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(AVS_VBVSECURECODE);
        Payload payload = generatePayload();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onAVS_VBVSECURECODEModelSuggested(payload);

    }

    @Test
    public void chargeCard_onSuccess_onNOAUTH_INTERNATIONALSuggested_onNoAuthInternationalSuggestedCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(NOAUTH_INTERNATIONAL);
        Payload payload = generatePayload();

        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onNoAuthInternationalSuggested(payload);

    }

    @Test
    public void chargeCard_onSuccess_unknownSuggestedAuth_onPaymentErrorCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(generateRandomString());
        Payload payload = generatePayload();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(unknownAuthmsg);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_VBVAuthModelUsed_onVBVAuthModelUsedCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        chargeResponse.getData().setAuthModelUsed(VBV);
        String authUrlCrude = chargeResponse.getData().getAuthurl();
        String flwRef = chargeResponse.getData().getFlwRef();

        //act
        cardPresenter.chargeCard(generatePayload(), generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onVBVAuthModelUsed(authUrlCrude, flwRef);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_authModelUsed_enterOTP_showOTPLayoutCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.getData().setAuthModelUsed(ACCESS_OTP);
        chargeResponse.getData().setSuggested_auth(null);
        chargeResponse.getData().setChargeResponseMessage(null);
        String flwRef = chargeResponse.getData().getFlwRef();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).showOTPLayout(flwRef, enterOTP);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_authModelUsedAccess_showOTPLayoutCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.getData().setAuthModelUsed(ACCESS_OTP);
        String flwRef = chargeResponse.getData().getFlwRef();
        chargeResponse.getData().setChargeResponseMessage(generateRandomString());
        String chargeResponseMessage = chargeResponse.getData().getChargeResponseMessage();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).showOTPLayout(flwRef, chargeResponseMessage);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_authModelUsedGtb_showOTPLayoutCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.getData().setAuthModelUsed(GTB_OTP);
        String flwRef = chargeResponse.getData().getFlwRef();
        chargeResponse.getData().setChargeResponseMessage(generateRandomString());
        String chargeResponseMessage = chargeResponse.getData().getChargeResponseMessage();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).showOTPLayout(flwRef, chargeResponseMessage);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_authModelUsedNOAUTH_onNoAuthUsedCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.getData().setAuthModelUsed(NOAUTH);
        String flwRef = chargeResponse.getData().getFlwRef();
        chargeResponse.getData().setChargeResponseMessage(generateRandomString());

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onNoAuthUsed(flwRef, payload.getPBFPubKey());

    }

    @Test
    public void chargeCard_onSuccess_nullData_onPaymentErrorCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.setData(null);

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(noResponse);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_authModelUsed_otp_showOTPLayoutCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.getData().setAuthModelUsed("otp");
        String flwRef = chargeResponse.getData().getFlwRef();
        chargeResponse.getData().setChargeResponseMessage(generateRandomString());
        String chargeResponseMessage = chargeResponse.getData().getChargeResponseMessage();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).showOTPLayout(flwRef, chargeResponseMessage);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_authModelUsedGTB_chargeResponseMessageNotNull_showOTPLayoutCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.getData().setAuthModelUsed(GTB_OTP);
        String flwRef = chargeResponse.getData().getFlwRef();

        //act
        cardPresenter.chargeCard(payload, generateRandomString());
        verify(view).showProgressIndicator(true);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showProgressIndicator(false);
        verify(view).showOTPLayout(flwRef, enterOTP);

    }


    @Test
    public void chargeCard_onError_onPaymentErrorCalled() {

        cardPresenter.chargeCard(generatePayload(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());
        verify(view).onPaymentError(anyString());

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_onChargeCardSuccessfulCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());
        verify(view).onChargeCardSuccessful(any(ChargeResponse.class));

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_AVS_VBVSECURECODE_onChargeCardSuccessfulCalled() {

        String authModel = AVS_VBVSECURECODE;
        Payload payload = generatePayload();
        payload.setBillingzip(generateRandomString());
        String ziporPin = generateRandomString();

        cardPresenter.chargeCardWithSuggestedAuthModel(payload, ziporPin, authModel, generateRandomString());

        payload.setPin(ziporPin);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());
        verify(view).onChargeCardSuccessful(any(ChargeResponse.class));

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_PIN_onChargeCardSuccessfulCalled() {

        String authModel = PIN;
        Payload payload = generatePayload();
        String ziporPin = generateRandomString();

        cardPresenter.chargeCardWithSuggestedAuthModel(payload, ziporPin, authModel, generateRandomString());

        payload.setPin(ziporPin);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());
        verify(view).onChargeCardSuccessful(any(ChargeResponse.class));

    }

    @Test
    public void validateCardCharge_onSuccess_onValidateCardChargeFailedCalled() {

        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.setStatus(null);

        String flwref = generateRandomString();
        String otp = generateRandomString();
        String pbfkey = generateRandomString();

        String responseAsJson = generateRandomString();

        cardPresenter.validateCardCharge(flwref, pbfkey, otp);

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);

        verify(networkRequest).validateChargeCard(any(ValidateChargeBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJson);
        verify(view).onValidateCardChargeFailed(flwref, responseAsJson);

    }


    @Test
    public void validateCardCharge_onSuccess_isSuccess_onChargeCardSuccessfulCalled() {

        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.setStatus(success);

        String flwref = generateRandomString();
        String otp = generateRandomString();
        String pbfkey = generateRandomString();
        String status = chargeResponse.getStatus();

        String responseAsJson = generateRandomString();

        cardPresenter.validateCardCharge(flwref, pbfkey, otp);

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);

        verify(networkRequest).validateChargeCard(any(ValidateChargeBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJson);
        verify(view).onValidateSuccessful(status, responseAsJson);

    }

    @Test
    public void validateCardCharge_onSuccess_isNotSuccess_onValidateErrorCalled() {

        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.setStatus(generateRandomString());

        String flwref = generateRandomString();
        String otp = generateRandomString();
        String pbfkey = generateRandomString();

        String message = chargeResponse.getMessage();

        String responseAsJson = generateRandomString();

        cardPresenter.validateCardCharge(flwref, pbfkey, otp);

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);

        verify(networkRequest).validateChargeCard(any(ValidateChargeBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, responseAsJson);
        verify(view).onValidateError(message);

    }


    @Test
    public void validateCardCharge_onError_isNotSuccess_onPaymentErrorCalled() {

        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.setStatus(generateRandomString());

        String flwref = generateRandomString();
        String otp = generateRandomString();
        String pbfkey = generateRandomString();

        String message = chargeResponse.getMessage();
        String responseAsJson = generateRandomString();

        cardPresenter.validateCardCharge(flwref, pbfkey, otp);

        ArgumentCaptor<Callbacks.OnValidateChargeCardRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnValidateChargeCardRequestComplete.class);

        verify(networkRequest).validateChargeCard(any(ValidateChargeBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, responseAsJson);
        verify(view).onPaymentError(message);

    }

    @Test
    public void chargeToken_onSuccess_onChargeTokenCompleteCalled() {

        Payload payload = generatePayload();
        ChargeResponse chargeResponse = generateValidChargeResponse();

        cardPresenter.chargeToken(payload);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeToken(any(Payload.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onChargeTokenComplete(chargeResponse);

    }

    @Test
    public void chargeToken_onError_tokenNotFound_onPaymentErrorCalled() {

        Payload payload = generatePayload();

        String responseAsString = tokenNotFound;
        String message = generateRandomString();

        cardPresenter.chargeToken(payload);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeToken(any(Payload.class), captor.capture());

        captor.getAllValues().get(0).onError(message, responseAsString);

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(tokenNotFound);

    }

    @Test
    public void verifyRequeryResponse_wasTxSuccessful_onPaymentSuccessfulCalled() {

        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();
        String flwRef = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(any(String.class), any(String.class), any(String.class)))
                .thenReturn(true);

        when(ravePayInitializer.getAmount()).thenReturn(generateRandomDouble());
        when(ravePayInitializer.getCurrency()).thenReturn(generateRandomString());

        cardPresenter.verifyRequeryResponse(requeryResponse, responseAsJsonString, ravePayInitializer, flwRef);
        verify(view).onPaymentSuccessful(requeryResponse.getStatus(), flwRef, responseAsJsonString, ravePayInitializer);

    }


    @Test
    public void verifyRequeryResponse_notWasTxSuccessful_onPaymentFailedCalled() {


        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();
        String flwRef = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(any(String.class), any(String.class), any(String.class)))
                .thenReturn(false);

        when(ravePayInitializer.getAmount()).thenReturn(generateRandomDouble());
        when(ravePayInitializer.getCurrency()).thenReturn(generateRandomString());

        cardPresenter.verifyRequeryResponse(requeryResponse, responseAsJsonString, ravePayInitializer, flwRef);
        verify(view).onPaymentFailed(requeryResponse.getStatus(), responseAsJsonString);

    }

    @Test
    public void chargeToken_onError_expired_onPaymentErrorCalled() {

        Payload payload = generatePayload();
        String responseAsString = expired;
        String message = generateRandomString();

        cardPresenter.chargeToken(payload);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeToken(any(Payload.class), captor.capture());

        captor.getAllValues().get(0).onError(message, responseAsString);

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(tokenExpired);

    }


    @Test
    public void chargeToken_onError_onPaymentErrorCalled() {

        Payload payload = generatePayload();
        String responseAsString = generateRandomString();
        String message = generateRandomString();

        cardPresenter.chargeToken(payload);

        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeToken(any(Payload.class), captor.capture());

        captor.getAllValues().get(0).onError(message, responseAsString);

        verify(view).showProgressIndicator(false);
        verify(view).onPaymentError(message);

    }


    @Test
    public void chargeCardWithSuggestedAuthModel_onError_onPaymentErrorCalled() {

        String message = generateRandomString();

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, generateRandomString());
        verify(view).onPaymentError(message);

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccessWithPIN_showOTPLayoutCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuthModelUsed(PIN), generateRandomString());
        verify(view).showOTPLayout(anyString(), anyString());

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccessWithAVS_VBVSECURECODE_onAVSVBVSecureCodeModelUsedCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuthModelUsed(AVS_VBVSECURECODE), generateRandomString());
        verify(view).onVBVAuthModelUsed(anyString(), anyString());

    }


    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_unknownResCodemsgReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse(), generateRandomString());
        verify(view).onPaymentError(anyString());

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_unknownAuthmsgReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth("unknown Auth"), generateRandomString());
        verify(view).onPaymentError(unknownAuthmsg);

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_invalidChargeCodeReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse(), generateRandomString());
        verify(view).onPaymentError(anyString());

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_onChargeCardSuccessfulCalled() {

        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());
        verify(view).onChargeCardSuccessful(any(ChargeResponse.class));

    }

    @Test
    public void chargeCardWithAVSModel_onError_onPaymentErrorCalled() {

        //arrange
        String message = generateRandomString();

        //act
        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, generateRandomString());

        //assert
        verify(view).onPaymentError(message);

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_02_authModelPIN_showOTPLayoutCalled() {

        //arrange
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setChargeResponseCode("02");
        chargeResponse.getData().setFlwRef(generateRandomString());
        String flwref = chargeResponse.getData().getFlwRef();
        chargeResponse.getData().setAuthModelUsed(PIN);

        chargeResponse.getData().setChargeResponseMessage(generateRandomString());
        String responseMessage = chargeResponse.getData().getChargeResponseMessage();

        //act
        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).showOTPLayout(flwref, responseMessage);

    }


    @Test
    public void chargeCardWithAVSModel_onSuccess_02_authModelVBV_showOTPLayoutCalled() {

        //arrange
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setChargeResponseCode("02");
        chargeResponse.getData().setFlwRef(generateRandomString());
        String flwref = chargeResponse.getData().getFlwRef();
        String authurl = chargeResponse.getData().getAuthurl();
        chargeResponse.getData().setAuthModelUsed(VBV);

        chargeResponse.getData().setChargeResponseMessage(generateRandomString());

        //act
        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse, generateRandomString());

        //assert
        verify(view).onVBVAuthModelUsed(authurl, flwref);

    }


    @Test
    public void chargeCardWithAVSModel_onSuccess_unknownResCodemsgReturned_onPaymentErrorCalled() {

        //arrange
        Payload payload = generatePayload();

        //act
        cardPresenter.chargeCardWithAVSModel(payload, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse(), generateRandomString());

        //assert
        verify(view).onPaymentError(anyString());

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_unknownAuthmsgReturned_onPaymentErrorCalled() {

        //arrange
        Payload payload = generatePayload();

        //act
        cardPresenter.chargeCardWithAVSModel(payload, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth("unknown Auth"), generateRandomString());

        //assert
        verify(view).onPaymentError(unknownAuthmsg);

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_invalidChargeCodeReturned_onPaymentErrorCalled() {

        //arrange
        Payload payload = generatePayload();

        //act
        cardPresenter.chargeCardWithAVSModel(payload, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse(), generateRandomString());

        //assert
        verify(view).onPaymentError(any(String.class));

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalled() {

        //arrange
        String flwRef = generateRandomString();

        //act
        cardPresenter.requeryTx(flwRef, generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful(), generateRandomString());

        //assert
        verify(view).onRequerySuccessful(any(RequeryResponse.class), anyString(), anyString());

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();
        String responseAsString = generateRandomString();

        //act
        cardPresenter.requeryTx(generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, responseAsString);

        //assert
        verify(view).onPaymentFailed(message, responseAsString);

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

    private ChargeResponse generateNullChargeResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setData(null);

        return chargeResponse;
    }

    private ChargeResponse generateValidChargeResponse() {
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setChargeResponseCode("00");
        return chargeResponse;
    }

    private RequeryResponse generateRequerySuccessful() {
        return new RequeryResponse();
    }

    private ChargeResponse generateValidChargeResponseWithAuth(String auth) {
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setAuthModelUsed(auth);
        chargeResponse.getData().setSuggested_auth(auth);
        chargeResponse.getData().setAuthurl(generateRandomString());
        chargeResponse.getData().setFlwRef(generateRandomString());
        chargeResponse.getData().setChargeResponseCode("02");
        return chargeResponse;
    }

    private ChargeResponse generateValidChargeResponseWithAuthModelUsed(String auth) {
        ChargeResponse chargeResponse = generateRandomChargeResponse();
        chargeResponse.getData().setAuthModelUsed(auth);
        chargeResponse.getData().setAuthurl(generateRandomString());
        chargeResponse.getData().setFlwRef(generateRandomString());
        chargeResponse.getData().setChargeResponseCode("02");
        return chargeResponse;
    }

    private Payload generatePayload() {
        List<Meta> metas = new ArrayList<>();
        List<SubAccount> subAccounts = new ArrayList<>();
        return new Payload(generateRandomString(), metas, subAccounts, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
    }

    private void generateViewValidation(int failedValidations) {

        List<Boolean> falses = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            if (i < failedValidations) {
                falses.add(false);
            } else {
                falses.add(true);
            }
        }

        when(amountValidator.isAmountValid(anyString())).thenReturn(falses.get(0));
        when(emailValidator.isEmailValid(anyString())).thenReturn(falses.get(1));
        when(cvvValidator.isCvvValid(anyString())).thenReturn(falses.get(2));
        when(cardExpiryValidator.isCardExpiryValid(anyString())).thenReturn(falses.get(3));
        when(cardNoValidator.isCardNoStrippedValid(anyString())).thenReturn(falses.get(4));

    }

    private HashMap<String, ViewObject> generateViewData() {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(generateRandomInt(), generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldEmail, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        viewData.put(fieldCvv, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        viewData.put(fieldCardExpiry, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        viewData.put(fieldcardNoStripped, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));

        return viewData;
    }

    private String generateEmail(boolean isValid) {
        if (isValid) {
            return "rave@rave.com";
        } else {
            return generateRandomString();
        }

    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    private Boolean generateRandomBoolean() {
        return new Random().nextBoolean();
    }

    private int generateRandomInt() {
        return new Random().nextInt();
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

    private void stubPayloadEncryptor() {
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
    }
}
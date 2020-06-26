package com.flutterwave.raveandroid.card;


import android.view.View;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.di.DaggerTestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.rave_core.models.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.validators.CardNoValidator;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.google.android.material.textfield.TextInputLayout;

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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldcardNoStripped;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validExpiryDatePrompt;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardPresenterTest {

    @Mock
    CardUiContract.View view;
    private CardUiPresenter cardUiPresenter;
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
    RemoteRepository networkRequest;
    @Inject
    PayloadEncryptor payloadEncryptor;

    @Inject
    TransactionStatusChecker transactionStatusChecker;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        cardUiPresenter = new CardUiPresenter(view);
        TestRaveUiComponent component = DaggerTestRaveUiComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(cardUiPresenter);
        stubPayloadEncryptor();

    }

    @Test
    public void init_validEmail_onEmailValidatedCalledWithValidEmailCorrectParamsPassed() {

        //arrange
        String email = generateEmail(true);
        when(ravePayInitializer.getEmail()).thenReturn(email);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);

        //act
        cardUiPresenter.init(ravePayInitializer);

        //assert
        verify(view).onEmailValidated(email, View.GONE);

    }

    @Test
    public void init_inValidEmail_onEmailValidatedCalledWithEmptyEmail() {

        //arrange
        String email = generateEmail(false);
        when(emailValidator.isEmailValid(email)).thenReturn(false);

        //act
        cardUiPresenter.init(ravePayInitializer);

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
        cardUiPresenter.init(ravePayInitializer);

        //assert
        verify(view).onAmountValidated(amount.toString(), View.GONE);

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmount() {

        //arrange
        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        //act
        cardUiPresenter.init(ravePayInitializer);

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
        cardUiPresenter.onDataCollected(map);
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
        cardUiPresenter.onDataCollected(map);

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
        cardUiPresenter.onDataCollected(map);
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
        cardUiPresenter.processTransaction(data, ravePayInitializer);
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
        cardUiPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), any(ResultCallback.class));
    }

    @Test
    public void processTransaction_displayFeeIsDisabled_chargeCardCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        cardUiPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest)
                .charge(any(ChargeRequestBody.class),
                        any(ResultCallback.class));
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
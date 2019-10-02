package com.flutterwave.raveandroid.card;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.view.View;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
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

import static com.flutterwave.raveandroid.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.RaveConstants.PIN;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldcardNoStripped;
import static com.flutterwave.raveandroid.RaveConstants.invalidChargeCode;
import static com.flutterwave.raveandroid.RaveConstants.unknownAuthmsg;
import static com.flutterwave.raveandroid.RaveConstants.unknownResCodemsg;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
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

    }

    @Test
    public void init_validEmail_onEmailValidatedCalledWithValidEmailCorrectParamsPassed() {
        String email = generateEmail(true);
        when(ravePayInitializer.getEmail()).thenReturn(email);
        when(emailValidator.isEmailValid(anyString())).thenReturn(true);
        cardPresenter.init(ravePayInitializer);

        verify(view).onEmailValidated(email, View.GONE);

    }

    @Test
    public void init_inValidEmail_onEmailValidatedCalledWithEmptyEmail() {

        String email = generateEmail(false);
        when(emailValidator.isEmailValid(email)).thenReturn(false);
        cardPresenter.init(ravePayInitializer);

        verify(view).onEmailValidated("", View.VISIBLE);
    }

    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        cardPresenter.init(ravePayInitializer);

        verify(view).onAmountValidated(amount.toString(), View.GONE);

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        cardPresenter.init(ravePayInitializer);
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
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        cardPresenter.onDataCollected(map);
        //assert
        verify(view).onValidationSuccessful(any(HashMap.class));

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
                .chargeCard(any(ChargeRequestBody.class),
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
    public void chargeCard_onSuccessWithPIN_onPinAuthModelSuggestedCalled() {

        cardPresenter.chargeCard(generatePayload(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth(PIN), generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onPinAuthModelSuggested(any(Payload.class));

    }

    @Test
    public void chargeCard_onSuccessWithAVS_VBVSECURECODE_onAVS_VBVSECURECODEModelSuggestedCalled() {

        cardPresenter.chargeCard(generatePayload(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth(AVS_VBVSECURECODE), generateRandomString());

        verify(view).showProgressIndicator(false);
        verify(view).onAVS_VBVSECURECODEModelSuggested(any(Payload.class));

    }

    @Test
    public void chargeCard_onError_onPaymentErrorCalled() {

        cardPresenter.chargeCard(generatePayload(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());
        verify(view).onPaymentError(anyString());

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_onChargeCardSuccessfulCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());
        verify(view).onChargeCardSuccessful(any(ChargeResponse.class));

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccessWithPIN_showOTPLayoutCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth(PIN), generateRandomString());
        verify(view).showOTPLayout(anyString(), anyString());

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccessWithAVS_VBVSECURECODE_onAVSVBVSecureCodeModelUsedCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth(AVS_VBVSECURECODE), generateRandomString());
        verify(view).onAVSVBVSecureCodeModelUsed(anyString(), anyString());

    }


    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_unknownResCodemsgReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse(), generateRandomString());
        verify(view).onPaymentError(unknownResCodemsg);

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_unknownAuthmsgReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth("unknown Auth"), generateRandomString());
        verify(view).onPaymentError(unknownAuthmsg);

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_invalidChargeCodeReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithSuggestedAuthModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse(), generateRandomString());
        verify(view).onPaymentError(invalidChargeCode);

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_onChargeCardSuccessfulCalled() {

        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponse(), generateRandomString());
        verify(view).onChargeCardSuccessful(any(ChargeResponse.class));

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_unknownResCodemsgReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse(), generateRandomString());
        verify(view).onPaymentError(unknownResCodemsg);

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_unknownAuthmsgReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth("unknown Auth"), generateRandomString());
        verify(view).onPaymentError(unknownAuthmsg);

    }

    @Test
    public void chargeCardWithAVSModel_onSuccess_invalidChargeCodeReturned_onPaymentErrorCalled() {

        cardPresenter.chargeCardWithAVSModel(generatePayload(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
        ArgumentCaptor<Callbacks.OnChargeRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnChargeRequestComplete.class);
        verify(networkRequest).chargeCard(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse(), generateRandomString());
        verify(view).onPaymentError(invalidChargeCode);

    }

    @Test
    public void requeryTx_onSuccess_onRequerySuccessfulCalled() {

        cardPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomBoolean());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRequerySuccessful(), generateRandomString());
        verify(view).onRequerySuccessful(any(RequeryResponse.class), anyString(), anyString());

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalled() {

        cardPresenter.requeryTx(generateRandomString(), generateRandomString(), generateRandomBoolean());
        verify(view).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString(), generateRandomString());
        verify(view).onPaymentFailed(anyString(), anyString());

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
}
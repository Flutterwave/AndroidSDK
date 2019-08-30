package com.flutterwave.raveandroid.card;

import android.content.Context;
import android.support.design.widget.TextInputLayout;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.Encrypt;
import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.di.DaggerTestAppComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestAppComponent;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.responses.ChargeResponse;
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
import static com.flutterwave.raveandroid.RaveConstants.NOAUTH_INTERNATIONAL;
import static com.flutterwave.raveandroid.RaveConstants.PIN;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldcardNoStripped;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardPresenterTest {

    CardPresenter presenter;
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
    Encrypt encrypt;
    @Mock
    NetworkRequestImpl networkRequest;
    @Mock
    Callbacks.OnGetFeeRequestComplete onGetFeeRequestComplete;
    @Inject
    Callbacks.OnChargeRequestComplete OnChargeRequestComplete;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        presenter = new CardPresenter(context, view);

        TestAppComponent component = DaggerTestAppComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(presenter);

        presenter.networkRequest = networkRequest;


    }

    @Test
    public void onDataCollected_InvalidDataPassed_showFieldErrorCalled() {
        //arrange
        HashMap<String, ViewObject> map = generateViewData();
        int failedValidations = 3;
        generateViewValidation(failedValidations);
        //act
        presenter.onDataCollected(map);
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
        presenter.onDataCollected(map);
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
        presenter.processTransaction(data, ravePayInitializer);
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
        presenter.processTransaction(data, ravePayInitializer);
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
        presenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest)
                .chargeCard(any(ChargeRequestBody.class),
                        any(Callbacks.OnChargeRequestComplete.class));
    }

    @Test
    public void processTransaction_feeDisplayFlagEnabled_displaysGetFeeLoadingDialog_callsGetFee_returnsFailed() {

        presenter.chargeCard(generatePayload(),
                generateRandomString());

    }

    @Test
    public void processTransaction_feeDisplayFlagEnabled_displaysGetFeeLoadingDialog_callsGetFee_returnsSuccessful() {

        //assert
        presenter.chargeCard(generatePayload(),
                generateRandomString());
    }

    private Payload generatePayload() {
        List<Meta> metas = new ArrayList<>();
        List<SubAccount> subAccounts = new ArrayList<>();
        return new Payload(generateRandomString(), metas, subAccounts, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
    }

//    @Test
//    public void processTransaction_feeDisplayFlagEnabled_displaysGetFeeLoadingDialog_callsGetFee_returnsSuccessful_ifSuggestedAuthIsPin() {
//
//        presenter.chargeCard(generatePayload(),
//                generateRandomString());
//
//        verify(view).showProgressIndicator(true);
//        ChargeRequestBody chargeRequestBody = generateChargeRequestBody();
//        verify(networkRequest).chargeCard(chargeRequestBody, OnChargeRequestComplete);
//        OnChargeRequestComplete.onSuccess(generateValidResponse(), generateRandomString());
//        generateRandomResponse(true);
//
//        verify(view).onPinAuthModelSuggested(generatePayload());
//    }

    @Test
    public void chargeCardWithSuggestedAuthModel() {
        Payload payload = generatePayload();
        presenter.chargeCardWithSuggestedAuthModel(payload, generateRandomString(), generateAuthModel_returnPIN(), generateRandomString());
        verify(view).showProgressIndicator(true);
    }

    private String generateAuthModel_returnAVS_VBVSECURECODE() {

        return AVS_VBVSECURECODE;
    }

    private String generateAuthModel_returnPIN() {

        return PIN;
    }

    private ChargeRequestBody generateChargeRequestBody() {
        return new ChargeRequestBody(generateRandomString(), generateRandomString(), generateRandomString());
    }

    private Callbacks.OnChargeRequestComplete generateCallbacksOnChargeRequestComplete() {
        OnChargeRequestComplete.onSuccess(generateValidResponse(), generateRandomString());
        return OnChargeRequestComplete;
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

    private ChargeResponse generateValidResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setStatus(generateRandomString());
        chargeResponse.setMessage(generateRandomString());
        chargeResponse.setData(new ChargeResponse.Data(generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateAccountValidateInstructions(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString()));
        return chargeResponse;
    }

    private ChargeResponse.AccountValidateInstructions generateAccountValidateInstructions() {

        return new ChargeResponse.AccountValidateInstructions();
    }

    private ChargeResponse generateInvalidResponse() {
        ChargeResponse chargeResponse = new ChargeResponse();
        chargeResponse.setStatus(generateRandomString());
        chargeResponse.setMessage(generateRandomString());
        chargeResponse.setData(null);
        return chargeResponse;
    }

    private String generateRandomResponse(boolean isValid) {

        ChargeResponse chargeResponse;

        if (isValid) {
            chargeResponse = generateValidResponse();
            return getSuggestedAuth();

        } else {
            chargeResponse = generateInvalidResponse();
            return getSuggestedAuth();
        }

    }

    private String getSuggestedAuth() {
        String suggestAuth = "";

        //suggestAuth();
        suggestAuth = PIN;

        if (suggestAuth.equalsIgnoreCase(PIN)) {
            suggestAuth = PIN;
        } else if (suggestAuth.equalsIgnoreCase(AVS_VBVSECURECODE)) {
            suggestAuth = AVS_VBVSECURECODE;
        } else if (suggestAuth.equalsIgnoreCase(NOAUTH_INTERNATIONAL)) {
            suggestAuth = NOAUTH_INTERNATIONAL;
        } else {
            verify(view).onPaymentError(anyString());
        }

        return suggestAuth;
    }

    private String suggestAuth() {
        String[] auth = {PIN, AVS_VBVSECURECODE, NOAUTH_INTERNATIONAL, ""};
        return auth[new Random().nextInt(3)];
    }


    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    private int generateRandomInt() {
        return new Random().nextInt();
    }

    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }
}
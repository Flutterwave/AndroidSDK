package com.flutterwave.raveandroid.ussd;

import android.os.Bundle;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.widget.AppCompatSpinner;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.di.DaggerTestRaveUiComponent;
import com.flutterwave.raveandroid.di.TestAndroidModule;
import com.flutterwave.raveandroid.di.TestNetworkModule;
import com.flutterwave.raveandroid.di.TestRaveUiComponent;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.validators.AmountValidator;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldUssdBank;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UssdPresenterTest {

    @Mock
    UssdUiContract.View view;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RavePayInitializer ravePayInitializer;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    Bundle bundle;
    @Mock
    UssdPresenter ussdPresenterMock;
    @Mock
    PayloadBuilder payloadBuilder;
    private UssdPresenter ussdPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ussdPresenter = new UssdPresenter(view);

        TestRaveUiComponent component = DaggerTestRaveUiComponent.builder()
                .testAndroidModule(new TestAndroidModule())
                .testNetworkModule(new TestNetworkModule())
                .build();

        component.inject(this);
        component.inject(ussdPresenter);
    }


    @Test
    public void init_validAmount_onAmountValidatedCalledWithValidAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(true);
        when(ravePayInitializer.getAmount()).thenReturn(amount);

        ussdPresenter.init(ravePayInitializer);

        verify(view).onAmountValidationSuccessful(amount.toString());

    }

    @Test
    public void init_inValidAmount_onAmountValidatedCalledWithEmptyAmount() {

        Double amount = generateRandomDouble();
        when(amountValidator.isAmountValid(amount)).thenReturn(false);

        ussdPresenter.init(ravePayInitializer);
        verify(view).onAmountValidationFailed();

    }


    @Test
    public void onDataCollected_inValidDataPassed_showFieldErrorCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(amountViewID, banksSpinnerViewID);
        int failedValidations = 1;
        generateViewValidation(failedValidations);

        ussdPresenter.onDataCollected(map);

        verify(view, times(failedValidations)).showFieldError(amountViewID, RaveConstants.validAmountPrompt, map.get(fieldAmount).getViewType());

    }


    @Test
    public void onDataCollected_validDataPassed_onValidationSuccessfulCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(amountViewID, banksSpinnerViewID);
        int failedValidations = 0;
        generateViewValidation(failedValidations);
        //act
        ussdPresenter.onDataCollected(map);
        //assert
        verify(view).onDataValidationSuccessful(map);

    }


    @Test
    public void processTransaction_displayFeeIsEnabled_progressDialogShown() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> map = generateViewData(amountViewID, banksSpinnerViewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        ussdPresenter.processTransaction(map, ravePayInitializer);
        //assert
        verify(view).showProgressIndicator(true);

    }

    @Test
    public void processTransaction_displayFeeIsEnabled_FetchFeeCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(amountViewID, banksSpinnerViewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        ussdPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), any(ResultCallback.class));

    }

    @Test
    public void processTransaction_payloadBuilderCalled() {
        //arrange
        int amountViewID = generateRandomInt();
        int banksSpinnerViewID = generateRandomInt();
        HashMap<String, ViewObject> data = generateViewData(amountViewID, banksSpinnerViewID);
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());
        //act
        ussdPresenter.processTransaction(data, ravePayInitializer);
        payloadBuilder.createBankPayload();
        //assert
        verify(view).showProgressIndicator(true);
        verify(payloadBuilder).createBankPayload();

    }

    @Test
    public void processTransaction_displayFeeIsDisabled_CgargeCalled() {
        //arrange
        HashMap<String, ViewObject> data = generateViewData();
        Payload payload = generatePayload();
        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
        when(ravePayInitializer.getEncryptionKey()).thenReturn(generateRandomString());
        when(payloadToJson.convertChargeRequestPayloadToJson(payload)).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(any(String.class), any(String.class))).thenReturn(generateRandomString());
        when(payloadEncryptor.getEncryptedData(isNull(String.class), any(String.class))).thenReturn(generateRandomString());
        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());


        //act
        ussdPresenter.processTransaction(data, ravePayInitializer);
        //assert
        verify(networkRequest).charge(any(ChargeRequestBody.class), any(ResultCallback.class));
    }


    private Double generateRandomDouble() {
        return new Random().nextDouble();
    }

    private HashMap<String, ViewObject> generateViewData(int amountViewID, int banksSpinnerViewID) {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(amountViewID, generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldUssdBank, new ViewObject(banksSpinnerViewID, RaveConstants.bankNameGtb, AppCompatSpinner.class));

        return viewData;
    }

    private HashMap<String, ViewObject> generateViewData() {

        HashMap<String, ViewObject> viewData = new HashMap<>();
        viewData.put(fieldAmount, new ViewObject(generateRandomInt(), generateRandomDouble().toString(), TextInputLayout.class));
        viewData.put(fieldUssdBank, new ViewObject(generateRandomInt(), generateRandomString(), TextInputLayout.class));
        return viewData;
    }

    private void generateViewValidation(int failedValidations) {

        List<Boolean> falses = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            if (i < failedValidations) {
                falses.add(false);
            } else {
                falses.add(true);
            }
        }

        when(amountValidator.isAmountValid(anyString())).thenReturn(falses.get(0));
    }

    private int generateRandomInt() {
        return new Random().nextInt();
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }

    private Payload generatePayload() {
        List<Meta> metas = new ArrayList<>();
        List<SubAccount> subAccounts = new ArrayList<>();
        return new Payload(generateRandomString(), metas, subAccounts, generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString(), generateRandomString());
    }


}
package com.flutterwave.raveandroid.rave_presentation.account;

import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.DaggerTestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestNetworkModule;
import com.flutterwave.raveandroid.rave_presentation.TestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestUtilsModule;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.FeeCheckRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.RequeryRequestBody;
import com.flutterwave.raveandroid.rave_remote.requests.ValidateChargeBody;
import com.flutterwave.raveandroid.rave_remote.responses.ChargeResponse;
import com.flutterwave.raveandroid.rave_remote.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.rave_remote.responses.RequeryResponse;

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

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidCharge;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.success;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AccountHandlerTest {

    AccountHandler paymentHandler;
    @Mock
    AccountContract.AccountInteractor interactor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    UrlValidator urlValidator;
    @Inject
    TransactionStatusChecker transactionStatusChecker;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new AccountHandler(interactor);

        TestRaveComponent component = DaggerTestRaveComponent.builder()
                .testNetworkModule(new TestNetworkModule())
                .testUtilsModule(new TestUtilsModule())
                .build();

        component.inject(this);
        component.inject(paymentHandler);
    }

    @Test
    public void chargeAccount_onSuccess_displayInternetBankingPageCalledWithCorrectParams() {

        //arrange
        String authurl = generateRandomString();
        String flwRef = generateRandomString();
        ChargeResponse chargeResponse = generateValidChargeResponse();

        //act
        paymentHandler.chargeAccount(generatePayload(), generateRandomString());
        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid(anyString())).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).displayInternetBankingPage(chargeResponse.getData().getAuthurl(), chargeResponse.getData().getFlwRef());

    }

    @Test
    public void chargeAccount_onSuccess_noAuthUrl_noValidationInstruction_collectOtpCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(null);
        chargeResponse.getData().getValidateInstructions().setInstruction(null);
        chargeResponse.getData().setAuthurl(null);
        when(urlValidator.isUrlValid(anyString())).thenReturn(true);

        //act
        paymentHandler.chargeAccount(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());


        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), null);


    }

    @Test
    public void chargeAccount_onSuccess_noAuthUrl__validInstruction_collectOtpCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        String encryptionKey = generateRandomString();
        boolean isInternetBanking = generateRandomBoolean();
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(generateRandomString());
        String responseAsJsonString = generateRandomString();

        //act
        paymentHandler.chargeAccount(payload, encryptionKey);
        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        when(urlValidator.isUrlValid(anyString())).thenReturn(true);
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstruction());

    }

    @Test
    public void validateAccountCharge_onSuccess_inValidResponse_onPaymentErrorCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.setStatus(null);
        String otp = generateRandomString();

        //act
        paymentHandler.authenticateAccountCharge(chargeResponse.getData().getFlwRef(), otp, generateRandomString());

        ArgumentCaptor<ResultCallback> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).validateAccountCharge(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).onPaymentError(invalidCharge);
    }

    @Test
    public void validateAccountCharge_onError_onPaymentErrorCalledWithMessage() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponse();
        String message = generateRandomString();
        String otp = generateRandomString();

        //act
        paymentHandler.authenticateAccountCharge(chargeResponse.getData().getFlwRef(), otp, generateRandomString());

        ArgumentCaptor<ResultCallback> onValidateChargeCardRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).validateAccountCharge(any(ValidateChargeBody.class), onValidateChargeCardRequestCompleteArgumentCaptor.capture());

        onValidateChargeCardRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message);

        //assert
        verify(interactor).onPaymentError(message);
    }


    @Test
    public void fetchFee_onError_showFetchFeeFailedCalledWithMessage() {

        //arrange
        Payload payload = generatePayload();
        boolean internetBanking = generateRandomBoolean();
        String message = generateRandomString();

        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(message);

        //assert
        verify(interactor).onFeeFetchError(message);

    }

    @Test
    public void fetchFee_onSuccess_onTransactionFeeRetrievedCalledWithCorrectParams() {

        //arrange
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();
        Payload payload = generatePayload();
        boolean internetBanking = generateRandomBoolean();

        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        //assert
        verify(interactor).onTransactionFeeRetrieved(feeCheckResponse.getData().getCharge_amount(), payload, feeCheckResponse.getData().getFee());

    }


    @Test
    public void getBanks_onSuccess_onBanksListRetrievedCalledWithCorrectParams() {

        //arrange
        List<Bank> bankList = generateBankList();

        //act
        paymentHandler.getBanksList();
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).getBanks(captor.capture());
        captor.getAllValues().get(0).onSuccess(bankList);

        //assert
        verify(interactor).onBanksListRetrieved(bankList);
    }

    @Test
    public void getBanks_onError_showBanksCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();

        //act
        paymentHandler.getBanksList();
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).getBanks(captor.capture());
        captor.getAllValues().get(0).onError(message);

        //assert
        verify(interactor).onGetBanksRequestFailed(anyString());
    }

    @Test
    public void verifyRequeryResponseStatus_transactionUnsuccessful_onPaymentFailedCalledWithCorrectParams() {

        String responseAsJsonString = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(responseAsJsonString)).thenReturn(false);
        paymentHandler.verifyRequeryResponseStatus(responseAsJsonString);
        verify(interactor).onPaymentFailed(responseAsJsonString);
    }

    @Test(expected = Exception.class)
    public void fetchFee_onSuccess_displayFeeException_onFeeFetchErrorCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        FeeCheckResponse feeCheckResponse = generateFeeCheckResponse();

//        when(ravePayInitializer.getIsDisplayFee()).thenReturn(true);
//        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        paymentHandler.fetchFee(payload);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onSuccess(feeCheckResponse);

        doThrow(new Exception()).when(interactor).onTransactionFeeRetrieved(feeCheckResponse.getData().getCharge_amount(), payload, feeCheckResponse.getData().getFee());

        //assert
        verify(interactor).onFeeFetchError(anyString());

    }

    @Test
    public void chargeAccount_onError_onPaymentErrorCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        String responseAsJsonString = generateRandomString();
        String message = generateRandomString();

//        when(ravePayInitializer.getIsDisplayFee()).thenReturn(false);
//        when(deviceIdGetter.getDeviceId()).thenReturn(generateRandomString());

        //act
        paymentHandler.chargeAccount(payload, generateRandomString());

        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onError(message);

        //assert
        verify(interactor).onPaymentError(message);

    }

    @Test
    public void chargeAccount_onSuccess_noAuthUrl_validateAccountChargeCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        String responseAsJsonString = generateRandomString();

        //act
        paymentHandler.chargeAccount(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());

        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstructions().getInstruction());

    }


    @Test
    public void chargeAccount_onSuccess_inValidInstruction_invalidAuth_collectOtpCalledWithCorrectParams() {

        //arrange
        Payload payload = generatePayload();
        payload.setSuggestedAuth(null);
        ChargeResponse chargeResponse = generateInValidChargeResponse();
        chargeResponse.getData().setValidateInstruction(generateRandomString());
        String responseAsJsonString = generateRandomString();
        when(urlValidator.isUrlValid(anyString())).thenReturn(false);

        //act
        paymentHandler.chargeAccount(payload, generateRandomString());
        ArgumentCaptor<ResultCallback> onChargeRequestCompleteArgumentCaptor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).charge(any(ChargeRequestBody.class), onChargeRequestCompleteArgumentCaptor.capture());
        onChargeRequestCompleteArgumentCaptor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).collectOtp(payload.getPBFPubKey(), chargeResponse.getData().getFlwRef(), chargeResponse.getData().getValidateInstruction());

    }

    @Test
    public void requeryTx_onError_onPaymentFailedCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();
        String responseJsonAsString = generateRandomString();

        //act
        paymentHandler.requeryTx(generateRandomString(), generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, responseJsonAsString);

        //assert
        verify(interactor).onPaymentFailed(responseJsonAsString);

    }


    @Test
    public void verifyRequeryResponseStatus_transactionSuccessful_onPaymentSuccessfulCalledWithCorrectParams() {

        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(any(String.class)))
                .thenReturn(true);

        paymentHandler.verifyRequeryResponseStatus(responseAsJsonString);
        verify(interactor).onPaymentSuccessful(responseAsJsonString);
    }

    private String generateRandomString() {
        return UUID.randomUUID().toString();
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

    private List<Bank> generateBankList() {
        return new ArrayList<>();
    }
}
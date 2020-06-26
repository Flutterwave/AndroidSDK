package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.DaggerTestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestNetworkModule;
import com.flutterwave.raveandroid.rave_presentation.TestRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.TestUtilsModule;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
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
import java.util.UUID;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.ACCESS_OTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.GTB_OTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NOAUTH_INTERNATIONAL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VBV;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.enterOTP;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.noResponse;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.unknownAuthmsg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CardPaymentHandlerTest {

    @Mock
    CardContract.CardInteractor interactor;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Mock
    RequeryRequestBody requeryRequestBody;
    private CardPaymentHandler paymentHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        paymentHandler = new CardPaymentHandler(interactor);


        TestRaveComponent component = DaggerTestRaveComponent.builder()
                .testNetworkModule(new TestNetworkModule())
                .testUtilsModule(new TestUtilsModule())
                .build();

        component.inject(this);
        component.inject(paymentHandler);
    }

    @Test
    public void fetchFee_onError_showFetchFeeFailedCalled() {

        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());

        captor.getAllValues().get(0).onError(generateRandomString());

        verify(interactor).onFetchFeeError(anyString());

    }

    @Test
    public void fetchFee_onSuccess_displayFeeCalled() {

        paymentHandler.fetchFee(generatePayload());

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).getFee(any(FeeCheckRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateFeeCheckResponse());

        verify(interactor).onTransactionFeeFetched(anyString(), any(Payload.class), anyString());

    }

    @Test
    public void chargeCard_onSuccessWithPIN_onPinAuthModelSuggestedCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(PIN);
        Payload payload = generatePayload();

        //act
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectCardPin(payload);

    }

    @Test
    public void chargeCard_onSuccessWithAVS_VBVSECURECODE_onAVS_VBVSECURECODEModelSuggestedCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(AVS_VBVSECURECODE);
        Payload payload = generatePayload();

        //act
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectCardAddressDetails(eq(payload), anyString());

    }

    @Test
    public void chargeCard_onSuccess_onNOAUTH_INTERNATIONALSuggested_onNoAuthInternationalSuggestedCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(NOAUTH_INTERNATIONAL);
        Payload payload = generatePayload();

        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectCardAddressDetails(payload, NOAUTH_INTERNATIONAL);

    }

    @Test
    public void chargeCard_onSuccess_unknownSuggestedAuth_onPaymentErrorCalled() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(generateRandomString());
        Payload payload = generatePayload();

        //act
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).onPaymentError(unknownAuthmsg);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_VBVAuthModelUsed_onVBVAuthModelUsedCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        chargeResponse.getData().setAuthModelUsed(VBV);
        String authUrlCrude = chargeResponse.getData().getAuthurl();
        String flwRef = chargeResponse.getData().getFlwRef();

        //act
        paymentHandler.chargeCard(generatePayload(), generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).showWebPage(authUrlCrude, flwRef);

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
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectOtp(flwRef, enterOTP);

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
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectOtp(flwRef, chargeResponseMessage);

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
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectOtp(flwRef, chargeResponseMessage);

    }


    @Test
    public void chargeCard_onSuccess_nullData_onPaymentErrorCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.setData(null);

        //act
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).onPaymentError(noResponse);

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
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectOtp(flwRef, chargeResponseMessage);

    }

    @Test
    public void chargeCard_onSuccess_nullSuggestedAuth_authModelUsedGTB_chargeResponseMessageNotNull_showOTPLayoutCalledWithCorrectParams() {

        //arrange
        ChargeResponse chargeResponse = generateValidChargeResponseWithAuth(null);
        Payload payload = generatePayload();
        chargeResponse.getData().setAuthModelUsed(GTB_OTP);
        String flwRef = chargeResponse.getData().getFlwRef();

        //act
        paymentHandler.chargeCard(payload, generateRandomString());
        verify(interactor).showProgressIndicator(true);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);

        //assert
        verify(interactor).showProgressIndicator(false);
        verify(interactor).collectOtp(flwRef, enterOTP);

    }


    @Test
    public void chargeCard_onError_onPaymentErrorCalled() {

        paymentHandler.chargeCard(generatePayload(), generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(generateRandomString());
        verify(interactor).onPaymentError(anyString());

    }


    @Test
    public void validateCardCharge_onSuccess_isNotSuccess_onPaymentErrorCalled() {

        ChargeResponse chargeResponse = generateValidChargeResponse();
        chargeResponse.setStatus(generateRandomString());

        String flwref = generateRandomString();
        String otp = generateRandomString();
        String pbfkey = generateRandomString();

        String message = chargeResponse.getMessage();

        String responseAsJson = generateRandomString();

        paymentHandler.validateCardCharge(flwref, pbfkey, otp);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).validateCardCharge(any(ValidateChargeBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(chargeResponse);
        verify(interactor).onPaymentError(message);

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

        paymentHandler.validateCardCharge(flwref, pbfkey, otp);

        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);

        verify(networkRequest).validateCardCharge(any(ValidateChargeBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message);
        verify(interactor).onPaymentError(message);

    }

    @Test
    public void verifyRequeryResponse_wasTxSuccessful_onPaymentSuccessfulCalled() {

        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();
        String flwRef = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(any(String.class)))
                .thenReturn(true);

        paymentHandler.verifyRequeryResponse(requeryResponse, responseAsJsonString, flwRef);
        verify(interactor).onPaymentSuccessful(requeryResponse.getStatus(), flwRef, responseAsJsonString);

    }


    @Test
    public void verifyRequeryResponse_notWasTxSuccessful_onPaymentFailedCalled() {


        RequeryResponse requeryResponse = generateRequerySuccessful();
        String responseAsJsonString = generateRandomString();
        String flwRef = generateRandomString();

        when(transactionStatusChecker.getTransactionStatus(any(String.class)))
                .thenReturn(false);


        paymentHandler.verifyRequeryResponse(requeryResponse, responseAsJsonString, flwRef);
        verify(interactor).onPaymentFailed(requeryResponse.getStatus(), responseAsJsonString);

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onError_onPaymentErrorCalled() {

        String message = generateRandomString();

        paymentHandler.chargeCardWithPinAuthModel(generatePayload(), generateRandomString(), generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message);
        verify(interactor).onPaymentError(message);

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccessWithPIN_showOTPLayoutCalled() {

        paymentHandler.chargeCardWithPinAuthModel(generatePayload(), generateRandomString(), generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuthModelUsed(PIN));
        verify(interactor).collectOtp(anyString(), anyString());

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccessWithAVS_VBVSECURECODE_onAVSVBVSecureCodeModelUsedCalled() {

        paymentHandler.chargeCardWithPinAuthModel(generatePayload(), generateRandomString(), generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuthModelUsed(AVS_VBVSECURECODE));
        verify(interactor).showWebPage(anyString(), anyString());

    }


    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_unknownResCodemsgReturned_onPaymentErrorCalled() {

        paymentHandler.chargeCardWithPinAuthModel(generatePayload(), generateRandomString(), generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateRandomChargeResponse());
        verify(interactor).onPaymentError(anyString());

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_unknownAuthmsgReturned_onPaymentErrorCalled() {

        paymentHandler.chargeCardWithPinAuthModel(generatePayload(), generateRandomString(), generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateValidChargeResponseWithAuth("unknown Auth"));
        verify(interactor).onPaymentError(unknownAuthmsg);

    }

    @Test
    public void chargeCardWithSuggestedAuthModel_onSuccess_invalidChargeCodeReturned_onPaymentErrorCalled() {

        paymentHandler.chargeCardWithPinAuthModel(generatePayload(), generateRandomString(), generateRandomString());
        ArgumentCaptor<ResultCallback> captor = ArgumentCaptor.forClass(ResultCallback.class);
        verify(networkRequest).charge(any(ChargeRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onSuccess(generateNullChargeResponse());
        verify(interactor).onPaymentError(anyString());

    }


    @Test
    public void requeryTx_onError_onPaymentFailedCalledWithCorrectParams() {

        //arrange
        String message = generateRandomString();
        String responseAsString = generateRandomString();

        //act
        paymentHandler.requeryTx(generateRandomString(), generateRandomString());
        verify(interactor).showProgressIndicator(true);
        ArgumentCaptor<Callbacks.OnRequeryRequestComplete> captor = ArgumentCaptor.forClass(Callbacks.OnRequeryRequestComplete.class);
        verify(networkRequest).requeryTx(any(RequeryRequestBody.class), captor.capture());
        captor.getAllValues().get(0).onError(message, responseAsString);

        //assert
        verify(interactor).onPaymentFailed(message, responseAsString);

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


    private String generateRandomString() {
        return UUID.randomUUID().toString();
    }


    private AddressDetails generateRandomAddressDetails() {
        return new AddressDetails(
                "",
                "",
                "",
                "",
                ""
        );
    }
}
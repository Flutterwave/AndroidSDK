package com.flutterwave.raveandroid.ugmobilemoney;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.RequeryRequestBodyv2;
import com.flutterwave.raveandroid.ghmobilemoney.GhMobileMoneyContract;
import com.flutterwave.raveandroid.mpesa.MpesaContract;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.GhChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.RequeryResponsev2;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Jeremiah on 10/12/2018.
 */

public class UgMobileMoneyPresenter implements UgMobileMoneyContract.UserActionsListener {
    private Context context;
    private UgMobileMoneyContract.View mView;

    public UgMobileMoneyPresenter(Context context, UgMobileMoneyContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void fetchFee(final Payload payload) {
        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPtype("3");
        body.setPBFPubKey(payload.getPBFPubKey());

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().getFee(body, new Callbacks.OnGetFeeRequestComplete() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    mView.showFetchFeeFailed(context.getResources().getString(R.string.transactionError));
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RaveConstants.RAVEPAY, message);
                mView.showFetchFeeFailed(context.getResources().getString(R.string.transactionError));
            }
        });
    }

    @Override
    public void chargeUgMobileMoney(final Payload payload, final String encryptionKey) {
        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = Utils.getEncryptedData(cardRequestBodyAsString, encryptionKey).trim().replaceAll("\\n", "");

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().chargeGhanaMobileMoneyWallet(body, new Callbacks.OnGhanaChargeRequestComplete() {
            @Override
            public void onSuccess(GhChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {
                    Log.d("resp", responseAsJSONString);

                    String flwRef = response.getData().getFlwRef();
                    String txRef = response.getData().getTx_ref();
                    requeryTx(flwRef, txRef, payload.getPBFPubKey());
                }
                else {
                    mView.onPaymentError(context.getResources().getString(R.string.noResponse));
                }

            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onPaymentError(message);
            }
        });
    }

    @Override
    public void requeryTx(final String flwRef, final String txRef, final String publicKey) {

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        mView.showPollingIndicator(true);

        new NetworkRequestImpl().requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                if (response.getData() == null) {
                    mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
                }
                else if (response.getData().getChargeResponseCode().equals("02")){
//                    Log.d("Requery response",responseAsJSONString);
                    mView.onPollingRoundComplete(flwRef, txRef, publicKey);
                }
                else if (response.getData().getChargeResponseCode().equals("00")) {
                    mView.showPollingIndicator(false);
                    mView.onPaymentSuccessful(flwRef, txRef, responseAsJSONString);
                }
                else {
                    mView.showProgressIndicator(false);
                    mView.onPaymentFailed(response.getData().getStatus(), responseAsJSONString);
                }
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void validate(HashMap<String, ViewObject> dataHashMap) {

         Boolean valid = true;

        int amountID = dataHashMap.get(context.getResources().getString(R.string.fieldAmount)).getViewId();
        String amount = dataHashMap.get(context.getResources().getString(R.string.fieldAmount)).getData();
        Class amountViewType = dataHashMap.get(context.getResources().getString(R.string.fieldAmount)).getViewType();

        int phoneID = dataHashMap.get(context.getResources().getString(R.string.fieldPhone)).getViewId();
        String phone = dataHashMap.get(context.getResources().getString(R.string.fieldPhone)).getData();
        Class phoneViewType = dataHashMap.get(context.getResources().getString(R.string.fieldPhone)).getViewType();

                try {
                        if (Double.parseDouble(amount) <= 0) {
                        valid = false;
                        mView.showFieldError(amountID, context.getResources().getString(R.string.validAmountPrompt));
                            }
                    } catch (Exception e) {
                    e.printStackTrace();
                    valid = false;
                    mView.showFieldError(amountID, context.getResources().getString(R.string.validAmountPrompt));
                 }

                 if (phone.length() < 1) {
                        valid = false;
                        mView.showFieldError(phoneID, context.getResources().getString(R.string.validPhonePrompt));
                }

                 if (valid) {
                     mView.onValidationSuccessful(dataHashMap);
                 }

    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer, Activity activity) {
        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(ravePayInitializer.getAmount() + "")
//                    .setCountry(ravePayInitializer.getCountry())
                .setCountry("NG") //Country has to be set to NG for UGX payments (as at 10/12/2018)
                .setCurrency(ravePayInitializer.getCurrency())
                .setEmail(ravePayInitializer.getEmail())
                .setFirstname(ravePayInitializer.getfName())
                .setLastname(ravePayInitializer.getlName())
                .setIP(Utils.getDeviceImei(activity))
                .setTxRef(ravePayInitializer.getTxRef())
                .setMeta(ravePayInitializer.getMeta())
                .setSubAccount(ravePayInitializer.getSubAccount())
                .setNetwork("UGX")
                .setPhonenumber(dataHashMap.get(activity.getResources().getString(R.string.fieldPhone)).getData())
                .setPBFPubKey(ravePayInitializer.getPublicKey())
                .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                .setDevice_fingerprint(Utils.getDeviceImei(activity));

        if (ravePayInitializer.getPayment_plan() != null) {
            builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
        }

        Payload body = builder.createUgMobileMoneyPayload();

        if(ravePayInitializer.getIsDisplayFee()){
            fetchFee(body);
        } else {
            chargeUgMobileMoney(body, ravePayInitializer.getEncryptionKey());
        }
    }

}



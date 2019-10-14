package com.flutterwave.raveandroid.ach;

import android.content.Context;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.SharedPrefsRequestImpl;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;


public class AchPresenter implements AchContract.UserActionsListener {

    private Context context;
    private AchContract.View mView;
    SharedPrefsRequestImpl sharedMgr;

    public AchPresenter(Context context, AchContract.View mView) {
        this.context = context;
        this.mView = mView;
        sharedMgr = new SharedPrefsRequestImpl(context);
    }

    @Override
    public void onStartAchPayment(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer.getAmount() > 0) {
            mView.showAmountField(false);
            mView.showRedirectMessage(true);
        }
        else {
            mView.showAmountField(true);
            mView.showRedirectMessage(false);
        }

    }

    @Override
    public void onPayButtonClicked(RavePayInitializer ravePayInitializer, String amount) {

        mView.showAmountError(null);

        if (ravePayInitializer.getAmount() > 0) {
            initiatePayment(ravePayInitializer);
        }
        else {
            try {
                double amnt = Double.parseDouble(amount);

                if (amnt <= 0) {
                    mView.showAmountError("Enter a valid amount");
                }
                else {
                    ravePayInitializer.setAmount(amnt);
                    initiatePayment(ravePayInitializer);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                mView.showAmountError("Enter a valid amount");
            }
        }

    }

    private void initiatePayment(RavePayInitializer ravePayInitializer) {


//        {
//            "PBFPubKey": "FLWPUBK-7adb6177bd71dd43c2efa3f1229e3b7f-X",
//                "currency": "USD",
//                "payment_type": "account",
//                "country": "US",
//                "amount": "20",
//                "email": "user@example.com",
//                "phonenumber": "0000000000",
//                "firstname": "Temi",
//                "lastname": "Tester",
//                "IP": "355426087298442",
//                "txRef": "rave-checkout-" + Date.now(),
//                "is_us_bank_charge": "true",
//                "redirect_url": "https://rave-webhook.herokuapp.com/receivepayment",
//                "device_fingerprint": "69e6b7f0b72037aa8428b70fbe03986c"
//        }

        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(ravePayInitializer.getAmount() + "")
                .setCountry(ravePayInitializer.getCountry())
                .setCurrency(ravePayInitializer.getCurrency())
                .setEmail(ravePayInitializer.getEmail())
                .setFirstname(ravePayInitializer.getfName())
                .setLastname(ravePayInitializer.getlName())
                .setIP(Utils.getDeviceImei(context))
                .setTxRef(ravePayInitializer.getTxRef())
                .setMeta(ravePayInitializer.getMeta())
                .setPBFPubKey(ravePayInitializer.getPublicKey())
                .setIsUsBankCharge(ravePayInitializer.isWithAch())
                .setDevice_fingerprint(Utils.getDeviceImei(context));

        if (ravePayInitializer.getPayment_plan() != null) {
            builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
        }

        Payload body = builder.createBankPayload();

        chargeAccount(body, ravePayInitializer.getEncryptionKey(), ravePayInitializer.getIsDisplayFee());
    }

    @Override
    public void chargeAccount(Payload payload, String encryptionKey, final boolean isDisplayFee) {

        String requestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String accountRequestBody = Utils.getEncryptedData(requestBodyAsString, encryptionKey);

        final ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(accountRequestBody);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().chargeCard(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {

                mView.showProgressIndicator(false);

                if (response.getData() != null) {

                    if (response.getData().getAuthurl() != null) {
                        String authUrl = response.getData().getAuthurl();
                        String flwRef = response.getData().getFlwRef();
                        String chargedAmount = response.getData().getChargedAmount();
                        String currency = response.getData().getCurrency();
                        sharedMgr.saveFlwRef(flwRef);

                        if (isDisplayFee) {
                            mView.showFee(authUrl, flwRef, chargedAmount, currency);
                        }
                        else {
                            mView.showWebView(authUrl, flwRef);
                        }
                    }
                    else {
                        mView.onPaymentError("No authUrl was returned");
                    }

                }
                else {
                    mView.onPaymentError("No response data was returned");
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
    public void onFeeConfirmed(String authUrl, String flwRef) {
        mView.showWebView(authUrl, flwRef);
    }

    public void requeryTx(String publicKey) {
        final String flwRef = sharedMgr.fetchFlwRef();
        //todo call requery

        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onRequerySuccessful(response, responseAsJSONString, flwRef);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void verifyRequeryResponse(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer, String flwRef) {
        boolean wasTxSuccessful = Utils.wasTxSuccessful(ravePayInitializer, responseAsJSONString);

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(response.getStatus(), flwRef, responseAsJSONString);
        }
        else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }
}

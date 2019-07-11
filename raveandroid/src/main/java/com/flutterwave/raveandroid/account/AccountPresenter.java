package com.flutterwave.raveandroid.account;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.URLUtil;

import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.ChargeRequestBody;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.data.RequeryRequestBody;
import com.flutterwave.raveandroid.data.ValidateChargeBody;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.FeeCheckResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;

import java.util.HashMap;
import java.util.List;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class AccountPresenter implements AccountContract.UserActionsListener {

    Context context;
    AccountContract.View mView;

    public AccountPresenter(Context context, AccountContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void getBanks() {

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().getBanks(new Callbacks.OnGetBanksRequestComplete() {
            @Override
            public void onSuccess(List<Bank> banks) {
                mView.showProgressIndicator(false);
                mView.showBanks(banks);
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.onGetBanksRequestFailed(message);
            }
        });

    }

    @Override
    public void chargeAccount(final Payload payload, String encryptionKey, final boolean internetBanking) {

        String cardRequestBodyAsString = Utils.convertChargeRequestPayloadToJson(payload);
        String encryptedCardRequestBody = Utils.getEncryptedData(cardRequestBodyAsString, encryptionKey);

//        Log.d("encrypted", encryptedCardRequestBody);

        ChargeRequestBody body = new ChargeRequestBody();
        body.setAlg("3DES-24");
        body.setPBFPubKey(payload.getPBFPubKey());
        body.setClient(encryptedCardRequestBody);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().chargeAccount(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);

                 if (response.getData() != null) {
                     String authUrlCrude = response.getData().getAuthurl();
                     String flwRef = response.getData().getFlwRef();
                     if (authUrlCrude != null && URLUtil.isValidUrl(authUrlCrude)) {
                         mView.onDisplayInternetBankingPage(authUrlCrude, flwRef);
                     }
                     else {
                         if (response.getData().getValidateInstruction() != null) {
                             mView.validateAccountCharge(payload.getPBFPubKey(), flwRef, response.getData().getValidateInstruction());
                         }
                         else if (response.getData().getValidateInstructions() != null &&
                                 response.getData().getValidateInstructions().getInstruction() != null) {
                             mView.validateAccountCharge(payload.getPBFPubKey(), flwRef, response.getData().getValidateInstructions().getInstruction());
                         }
                         else {
                             mView.validateAccountCharge(payload.getPBFPubKey(), flwRef, null);
                         }
                     }
                 }

            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onChargeAccountFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void validateAccountCharge(final String flwRef, String otp, String PBFPubKey) {

        ValidateChargeBody body = new ValidateChargeBody();
        body.setPBFPubKey(PBFPubKey);
        body.setOtp(otp);
        body.setTransactionreference(flwRef);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().validateAccountCard(body, new Callbacks.OnValidateChargeCardRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);

                if (response.getStatus() != null) {
                    String status = response.getStatus();
                    String message = response.getMessage();

                    if (status.equalsIgnoreCase(context.getResources().getString(R.string.success))) {
                        mView.onValidateSuccessful(flwRef, responseAsJSONString);
                    }
                    else {
                        mView.onValidateError(status, responseAsJSONString);
                    }
                }
                else {
                    mView.onPaymentError(context.getResources().getString(R.string.invalidCharge));
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
    public void fetchFee(final Payload payload, final boolean internetbanking) {


        FeeCheckRequestBody body = new FeeCheckRequestBody();
        body.setAmount(payload.getAmount());
        body.setCurrency(payload.getCurrency());
        body.setPtype("2");
        body.setPBFPubKey(payload.getPBFPubKey());

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().getFee(body, new Callbacks.OnGetFeeRequestComplete() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload, internetbanking);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    mView.showFetchFeeFailed("An error occurred while retrieving transaction fee");
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RaveConstants.RAVEPAY, message);
                mView.showFetchFeeFailed("An error occurred while retrieving transaction fee");
            }
        });
    }


    public void requeryTx(String flwRef, String publicKey) {
        RequeryRequestBody body = new RequeryRequestBody();
        body.setFlw_ref(flwRef);
        body.setPBFPubKey(publicKey);

        mView.showProgressIndicator(true);

        new NetworkRequestImpl().requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
            @Override
            public void onSuccess(RequeryResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onRequerySuccessful(response, responseAsJSONString);
            }

            @Override
            public void onError(String message, String responseAsJSONString) {
                mView.showProgressIndicator(false);
                mView.onPaymentFailed(message, responseAsJSONString);
            }
        });
    }

    @Override
    public void verifyRequeryResponseStatus(RequeryResponse response, String responseAsJSONString, RavePayInitializer ravePayInitializer) {
        mView.showProgressIndicator(true);
        boolean wasTxSuccessful = Utils.wasTxSuccessful(ravePayInitializer, responseAsJSONString);

        mView.showProgressIndicator(false);

        if (wasTxSuccessful) {
            mView.onPaymentSuccessful(response.getStatus(), responseAsJSONString);
        }
        else {
            mView.onPaymentFailed(response.getStatus(), responseAsJSONString);
        }
    }

    @Override
    public void validate(HashMap<String, ViewObject> dataHashMap) {

        Boolean valid = true;

        int amountID = dataHashMap.get(context.getResources().getString(R.string.fieldAmount)).getViewId();
        String amount = dataHashMap.get(context.getResources().getString(R.string.fieldAmount)).getData();
        Class amountViewType = dataHashMap.get(context.getResources().getString(R.string.fieldAmount)).getViewType();

        int emailID = dataHashMap.get(context.getResources().getString(R.string.fieldEmail)).getViewId();
        String email = dataHashMap.get(context.getResources().getString(R.string.fieldEmail)).getData();
        Class emailViewType = dataHashMap.get(context.getResources().getString(R.string.fieldEmail)).getViewType();

        int accountID = dataHashMap.get(context.getResources().getString(R.string.fieldAccount)).getViewId();
        String account = dataHashMap.get(context.getResources().getString(R.string.fieldAccount)).getData();
        Class accountViewType = dataHashMap.get(context.getResources().getString(R.string.fieldAccount)).getViewType();

        int phoneID = dataHashMap.get(context.getResources().getString(R.string.fieldPhone)).getViewId();
        String phone = dataHashMap.get(context.getResources().getString(R.string.fieldPhone)).getData();
        Class phoneViewType = dataHashMap.get(context.getResources().getString(R.string.fieldPhone)).getViewType();


                if (phone.length() < 1) {
                    valid = false;
                    mView.showFieldError(phoneID, context.getResources().getString(R.string.validPhonePrompt), phoneViewType);
                }

                if (!Utils.isEmailValid(email)) {
                    valid = false;
                    mView.showFieldError(emailID, context.getResources().getString(R.string.validPhonePrompt), emailViewType);
                }


                if (account.isEmpty()) {
                        valid = false;
                        mView.showFieldError(accountID, "Enter a valid account number", phoneViewType);
                } else {
                    account = "0000000000";
                }

                try {
                    double amnt = Double.parseDouble(amount);

                    if (amnt <= 0) {
                        valid = false;
                        mView.showToast(context.getResources().getString(R.string.validAmountPrompt));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    valid = false;
                    mView.showToast(context.getResources().getString(R.string.validAmountPrompt));
                }

                if (valid){
                    mView.onValidationSuccessful(dataHashMap);
                }

    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        //make request
        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(ravePayInitializer.getAmount() + "")
                .setEmail(dataHashMap.get(context.getString(R.string.fieldEmail)).getData())
                .setCountry("NG").setCurrency("NGN")
                .setPBFPubKey(ravePayInitializer.getPublicKey())
                .setDevice_fingerprint(Utils.getDeviceImei(context))
                .setIP(Utils.getDeviceImei(context)).setTxRef(ravePayInitializer.getTxRef())
                .setAccountbank(dataHashMap.get(context.getString(R.string.fieldBankCode)).getData())
                .setMeta(ravePayInitializer.getMeta())
                .setSubAccount(ravePayInitializer.getSubAccount())
                .setAccountnumber(dataHashMap.get(context.getString(R.string.fieldAccount)).getData())
                .setBVN(context.getResources().getString(R.string.fieldBVN))
                .setIsPreAuth(ravePayInitializer.getIsPreAuth());

        Payload body = builder.createBankPayload();
        body.setPasscode(context.getResources().getString(R.string.date_of_birth));
        body.setPhonenumber(context.getResources().getString(R.string.fieldPhone));

        if ((dataHashMap.get(context.getString(R.string.fieldBankCode)).getData().equalsIgnoreCase("058") ||
                dataHashMap.get(context.getString(R.string.fieldBankCode)).getData().equalsIgnoreCase("011"))
                && (Double.parseDouble(dataHashMap.get(context.getString(R.string.fieldAmount)).getData()) <= 100)) {
                mView.showGTBankAmountIssue();
        }
        else {
            if(ravePayInitializer.getIsDisplayFee()){
                fetchFee(body, Boolean.valueOf(dataHashMap.get(context.getString(R.string.isInternetBanking)).getData()));
            } else {
                chargeAccount(body, ravePayInitializer.getEncryptionKey(), Boolean.valueOf(dataHashMap.get(context.getString(R.string.isInternetBanking)).getData()));
            }
        }
    }


    @Override
    public void onAttachView(AccountContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullAccountView();
    }
}

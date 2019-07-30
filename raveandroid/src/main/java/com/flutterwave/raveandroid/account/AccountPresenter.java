package com.flutterwave.raveandroid.account;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;

import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
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
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.flutterwave.raveandroid.validators.PhoneValidator;

import java.util.HashMap;
import java.util.List;

import static com.flutterwave.raveandroid.RaveConstants.NG;
import static com.flutterwave.raveandroid.RaveConstants.NGN;
import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.accounNumberPrompt;
import static com.flutterwave.raveandroid.RaveConstants.date_of_birth;
import static com.flutterwave.raveandroid.RaveConstants.fieldAccount;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldBVN;
import static com.flutterwave.raveandroid.RaveConstants.fieldBankCode;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.RaveConstants.invalidCharge;
import static com.flutterwave.raveandroid.RaveConstants.isInternetBanking;
import static com.flutterwave.raveandroid.RaveConstants.success;
import static com.flutterwave.raveandroid.RaveConstants.transactionError;
import static com.flutterwave.raveandroid.RaveConstants.validAmountPrompt;
import static com.flutterwave.raveandroid.RaveConstants.validEmailPrompt;
import static com.flutterwave.raveandroid.RaveConstants.validPhonePrompt;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class AccountPresenter implements AccountContract.UserActionsListener {

    private Context context;
    private AccountContract.View mView;
    private EmailValidator emailValidator = new EmailValidator();
    private AmountValidator amountValidator = new AmountValidator();
    private PhoneValidator phoneValidator = new PhoneValidator();

     AccountPresenter(Context context, AccountContract.View mView) {
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

                    if (status.equalsIgnoreCase(success)) {
                        mView.onValidationSuccessful(flwRef, responseAsJSONString);
                    }
                    else {
                        mView.onValidateError(status, responseAsJSONString);
                    }
                }
                else {
                    mView.onPaymentError(invalidCharge);
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
                    mView.showFetchFeeFailed(transactionError);
                }
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                Log.e(RAVEPAY, message);
                mView.showFetchFeeFailed(transactionError);
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
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

        boolean valid = true;

        int amountID = dataHashMap.get(fieldAmount).getViewId();
        String amount = dataHashMap.get(fieldAmount).getData();
        Class amountViewType = dataHashMap.get(fieldAmount).getViewType();

        int emailID = dataHashMap.get(fieldEmail).getViewId();
        String email = dataHashMap.get(fieldEmail).getData();
        Class emailViewType = dataHashMap.get(fieldEmail).getViewType();

        int accountID = dataHashMap.get(fieldAccount).getViewId();
        String account = dataHashMap.get(fieldAccount).getData();
        Class accountViewType = dataHashMap.get(fieldAccount).getViewType();

        int phoneID = dataHashMap.get(fieldPhone).getViewId();
        String phone = dataHashMap.get(fieldPhone).getData();
        Class phoneViewType = dataHashMap.get(fieldPhone).getViewType();

        boolean isAmountValid = amountValidator.isAmountValid(amount);
        boolean isPhoneValid = phoneValidator.isPhoneValid(phone);
        boolean isEmailValid = emailValidator.isEmailValid(email);

                if (!isAmountValid) {
                    valid = false;
                    mView.showFieldError(amountID, validAmountPrompt, amountViewType);
                }

                if (!isPhoneValid) {
                    valid = false;
                    mView.showFieldError(phoneID, validPhonePrompt, phoneViewType);
                }

                if (!isEmailValid) {
                    valid = false;
                    mView.showFieldError(emailID, validEmailPrompt, emailViewType);
                }

                if (account.isEmpty()) {
                        valid = false;
                        mView.showFieldError(accountID, accounNumberPrompt, phoneViewType);
                }

                if (valid){
                    mView.onValidationSuccessful(dataHashMap);
                }

    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        //make request

        if (ravePayInitializer!=null) {
            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setEmail(dataHashMap.get(fieldEmail).getData())
                    .setCountry(NG).setCurrency(NGN)
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(Utils.getDeviceImei(context))
                    .setIP(Utils.getDeviceImei(context)).setTxRef(ravePayInitializer.getTxRef())
                    .setAccountbank(dataHashMap.get(fieldBankCode).getData())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setAccountnumber(dataHashMap.get(fieldAccount).getData())
                    .setBVN(fieldBVN)
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth());

            Payload body = builder.createBankPayload();
            body.setPasscode(date_of_birth);
            body.setPhonenumber(fieldPhone);

            if ((dataHashMap.get(fieldBankCode).getData().equalsIgnoreCase("058") ||
                    dataHashMap.get(fieldBankCode).getData().equalsIgnoreCase("011"))
                    && (Double.parseDouble(dataHashMap.get(fieldAmount).getData()) <= 100)) {
                mView.showGTBankAmountIssue();
            } else {
                if (ravePayInitializer.getIsDisplayFee()) {
                    fetchFee(body, Boolean.valueOf(dataHashMap.get(isInternetBanking).getData()));
                } else {
                    chargeAccount(body, ravePayInitializer.getEncryptionKey(), Boolean.valueOf(dataHashMap.get(isInternetBanking).getData()));
                }
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

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

         if (ravePayInitializer!=null) {

             boolean isEmailValid = emailValidator.isEmailValid(ravePayInitializer.getEmail());
             boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());

             if (isEmailValid) {
                 mView.onEmailValidated(ravePayInitializer.getEmail(), View.GONE);
             } else {
                 mView.onEmailValidated("", View.VISIBLE);
             }
             if (isAmountValid) {
                 mView.onAmountValidated(String.valueOf(ravePayInitializer.getAmount()), View.GONE);
             } else {
                 mView.onAmountValidated("", View.VISIBLE);
             }
         }
    }

    @Override
    public void onInternetBankingValidated(Bank bank) {
        if (bank.isInternetbanking()) {
            mView.showInternetBankingSelected(View.GONE);
            if (bank.getBankcode().equals("057")  || bank.getBankcode().equals("033")) {
                mView.showDateOfBirth(View.VISIBLE);
            }
            else {
                mView.showDateOfBirth(View.GONE);
            }
            if(bank.getBankcode().equals("033")){
                mView.showBVN(View.VISIBLE);
            }else{
                mView.showBVN(View.GONE);
            }
        }
        else{
            mView.showInternetBankingSelected(View.VISIBLE);
        }

    }
}

package com.flutterwave.raveandroid.account;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;

import com.flutterwave.raveandroid.FeeCheckRequestBody;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
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
    private TextInputEditText amountEt;
    private TextInputLayout amountTil;
    private TextInputEditText emailEt;
    private TextInputLayout emailTil,rave_bvnTil;
    private TextInputEditText phoneEt;
    private TextInputLayout phoneTil;
    TextInputEditText accountNumberEt;
    TextInputLayout accountNumberTil;
    private EditText dateOfBirthEt,bvnEt;
    EditText bankEt;
    Button payButton;
    Bank selectedBank;

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

                    if (status.equalsIgnoreCase("success")) {
                        mView.onValidateSuccessful(flwRef, responseAsJSONString);
                    }
                    else {
                        mView.onValidateError(status, responseAsJSONString);
                    }
                }
                else {
                    mView.onPaymentError("Invalid charge card response");
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
    public void validate(HashMap<String, List<String>> dataHashMap) {

        Boolean valid = true;


        int accountID = Integer.valueOf(dataHashMap.get("account").get(0));
        String account = dataHashMap.get("account").get(1);

        int emailID = Integer.valueOf(dataHashMap.get("email").get(0));
        String email = dataHashMap.get("email").get(1);

        int phoneID = Integer.valueOf(dataHashMap.get("phone").get(0));
        String phone = dataHashMap.get("phone").get(1);

        int amountID = Integer.valueOf(dataHashMap.get("amount").get(0));
        String amount = dataHashMap.get("amount").get(1);

                if (phone.length() < 1) {
                    valid = false;
                    mView.showFieldError(phoneID, "Enter a valid number");
                }

                if (!Utils.isEmailValid(email)) {
                    valid = false;
                    mView.showFieldError(emailID, "Enter a valid email");
                }


                if (account.isEmpty()) {
                        valid = false;
                        mView.showFieldError(accountID, "Enter a valid account number");
                } else {
                    account = "0000000000";
                }

                try {
                    double amnt = Double.parseDouble(amount);

                    if (amnt <= 0) {
                        valid = false;
                        mView.showToast("Enter a valid amount");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    valid = false;
                    mView.showToast("Enter a valid amount");
                }

                mView.onValidate(valid);

    }

    private void clearErrors(){
        bankEt.setError(null);
        accountNumberTil.setError(null);
        accountNumberTil.setErrorEnabled(false);
        emailTil.setError(null);
        emailTil.setErrorEnabled(false);
        phoneTil.setError(null);
        phoneTil.setErrorEnabled(false);
        bankEt.setError(null);
        dateOfBirthEt.setError(null);
        rave_bvnTil.setError(null);
        rave_bvnTil.setErrorEnabled(false);
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

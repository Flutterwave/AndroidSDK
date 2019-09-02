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
import com.flutterwave.raveandroid.validators.AccountNoValidator;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.BankCodeValidator;
import com.flutterwave.raveandroid.validators.BanksMinimum100AccountPaymentValidator;
import com.flutterwave.raveandroid.validators.BvnValidator;
import com.flutterwave.raveandroid.validators.DateOfBirthValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.flutterwave.raveandroid.validators.PhoneValidator;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.RaveConstants.NG;
import static com.flutterwave.raveandroid.RaveConstants.NGN;
import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.fieldAccount;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldBVN;
import static com.flutterwave.raveandroid.RaveConstants.fieldBankCode;
import static com.flutterwave.raveandroid.RaveConstants.fieldDOB;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.RaveConstants.invalidAccountNoMessage;
import static com.flutterwave.raveandroid.RaveConstants.invalidBankCodeMessage;
import static com.flutterwave.raveandroid.RaveConstants.invalidBvnMessage;
import static com.flutterwave.raveandroid.RaveConstants.invalidCharge;
import static com.flutterwave.raveandroid.RaveConstants.invalidDateOfBirthMessage;
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

    @Inject
    EmailValidator emailValidator;
    @Inject
    AmountValidator amountValidator;
    @Inject
    PhoneValidator phoneValidator;
    @Inject
    DateOfBirthValidator dateOfBirthValidator;
    @Inject
    BvnValidator bvnValidator;
    @Inject
    AccountNoValidator accountNoValidator;
    @Inject
    BankCodeValidator bankCodeValidator;
    @Inject
    BanksMinimum100AccountPaymentValidator minimum100AccountPaymentValidator;
    @Inject
    NetworkRequestImpl networkRequest;

    @Inject
    AccountPresenter(Context context, AccountContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void getBanks() {

        mView.showProgressIndicator(true);

        networkRequest.getBanks(new Callbacks.OnGetBanksRequestComplete() {
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

        networkRequest.chargeAccount(body, new Callbacks.OnChargeRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);

                if (response.getData() != null) {
                    String authUrlCrude = response.getData().getAuthurl();
                    String flwRef = response.getData().getFlwRef();
                    if (authUrlCrude != null && URLUtil.isValidUrl(authUrlCrude)) {
                        mView.onDisplayInternetBankingPage(authUrlCrude, flwRef);
                    } else {
                        if (response.getData().getValidateInstruction() != null) {
                            mView.validateAccountCharge(payload.getPBFPubKey(), flwRef, response.getData().getValidateInstruction());
                        } else if (response.getData().getValidateInstructions() != null &&
                                response.getData().getValidateInstructions().getInstruction() != null) {
                            mView.validateAccountCharge(payload.getPBFPubKey(), flwRef, response.getData().getValidateInstructions().getInstruction());
                        } else {
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

        networkRequest.validateAccountCard(body, new Callbacks.OnValidateChargeCardRequestComplete() {
            @Override
            public void onSuccess(ChargeResponse response, String responseAsJSONString) {
                mView.showProgressIndicator(false);

                if (response.getStatus() != null) {
                    String status = response.getStatus();
                    String message = response.getMessage();

                    if (status.equalsIgnoreCase(success)) {
                        mView.onValidationSuccessful(flwRef, responseAsJSONString);
                    } else {
                        mView.onValidateError(status, responseAsJSONString);
                    }
                } else {
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

        networkRequest.getFee(body, new Callbacks.OnGetFeeRequestComplete() {
            @Override
            public void onSuccess(FeeCheckResponse response) {
                mView.showProgressIndicator(false);

                try {
                    mView.displayFee(response.getData().getCharge_amount(), payload, internetbanking);
                } catch (Exception e) {
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

        networkRequest.requeryTx(body, new Callbacks.OnRequeryRequestComplete() {
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
        } else {
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

        ViewObject accountNoObject = dataHashMap.get(fieldAccount);

        if (accountNoObject != null) {
            int accountID = dataHashMap.get(fieldAccount).getViewId();
            String accountNo = dataHashMap.get(fieldAccount).getData();
            Class accountViewType = dataHashMap.get(fieldAccount).getViewType();

            if (!accountNoValidator.isAccountNumberValid(accountNo)) {
                valid = false;
                mView.showFieldError(accountID, invalidAccountNoMessage, accountViewType);
            }
        }

        int phoneID = dataHashMap.get(fieldPhone).getViewId();
        String phone = dataHashMap.get(fieldPhone).getData();
        Class phoneViewType = dataHashMap.get(fieldPhone).getViewType();

        int bvnID = dataHashMap.get(fieldBVN).getViewId();
        String bvn = dataHashMap.get(fieldBVN).getData();
        Class bvnViewType = dataHashMap.get(fieldBVN).getViewType();

        int dateOfBirthID = dataHashMap.get(fieldDOB).getViewId();
        String dateOfBirth = dataHashMap.get(fieldDOB).getData();
        Class dateOfBirthViewType = dataHashMap.get(fieldDOB).getViewType();

        int bankCodeId = dataHashMap.get(fieldBankCode).getViewId();
        String bankCode = dataHashMap.get(fieldBankCode).getData();
        Class bankCodeViewType = dataHashMap.get(fieldBankCode).getViewType();

        boolean isAmountValid = amountValidator.isAmountValid(amount);
        boolean isPhoneValid = phoneValidator.isPhoneValid(phone);
        boolean isEmailValid = emailValidator.isEmailValid(email);
        boolean isDateOfBirthValid = dateOfBirthValidator.isDateValid(dateOfBirth);
        boolean isBvnValid = bvnValidator.isBvnValid(bvn);
        boolean isBankCodeValid = bankCodeValidator.isBankCodeValid(bankCode);

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

        if (!isBankCodeValid) {
            valid = false;
            mView.showFieldError(bankCodeId, invalidBankCodeMessage, bankCodeViewType);
        } else {
            if ((bankCode.equals("057") || bankCode.equals("033")) && !isDateOfBirthValid) {
                valid = false;
                mView.showFieldError(dateOfBirthID, invalidDateOfBirthMessage, dateOfBirthViewType);
            }

            if (bankCode.equals("033") && !isBvnValid) {
                valid = false;
                mView.showFieldError(bvnID, invalidBvnMessage, bvnViewType);
            }

        }

        if (isAmountValid && isBankCodeValid) {
            boolean minimum100ValidationPassed = minimum100AccountPaymentValidator.isPaymentValid(bankCode, Double.valueOf(amount));

            if (!minimum100ValidationPassed) {
                valid = false;
                mView.showGTBankAmountIssue();
            }

        }

        if (valid) {
            mView.onValidationSuccessful(dataHashMap);
        }

    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        //make request

        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(fieldAmount).getData()));

            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setEmail(dataHashMap.get(fieldEmail).getData())
                    .setCountry(NG)
                    .setCurrency(NGN)
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(Utils.getDeviceImei(context))
                    .setIP(Utils.getDeviceImei(context))
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setAccountbank(dataHashMap.get(fieldBankCode).getData())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setBVN(dataHashMap.get(fieldBVN).getData())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth());


            if (dataHashMap.get(fieldAccount) != null && dataHashMap.get(fieldAccount).getData() != null) {
                builder.setAccountnumber(dataHashMap.get(fieldAccount).getData());
            }

            Payload body = builder.createBankPayload();
            body.setPasscode(dataHashMap.get(fieldDOB).getData());
            body.setPhonenumber(dataHashMap.get(fieldPhone).getData());


            boolean isInternetBanking = dataHashMap.get(fieldAccount) == null;
            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body, isInternetBanking);
            } else {
                chargeAccount(body, ravePayInitializer.getEncryptionKey(), isInternetBanking);
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

        if (ravePayInitializer != null) {

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
    public void onBankSelected(Bank bank) {
        if (bank.isInternetbanking()) {
            mView.showAccountNumberField(View.GONE);
        } else {
            mView.showAccountNumberField(View.VISIBLE);
        }

        if (bank.getBankcode().equals("057") || bank.getBankcode().equals("033")) {
            mView.showDateOfBirth(View.VISIBLE);
        } else {
            mView.showDateOfBirth(View.GONE);
        }

        if (bank.getBankcode().equals("033")) {
            mView.showBVN(View.VISIBLE);
        } else {
            mView.showBVN(View.GONE);
        }

    }

}

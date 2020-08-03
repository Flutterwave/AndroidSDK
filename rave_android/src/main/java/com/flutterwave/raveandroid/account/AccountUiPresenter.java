package com.flutterwave.raveandroid.account;

import android.view.View;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_presentation.account.AccountHandler;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.validators.AccountNoValidator;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.BankCodeValidator;
import com.flutterwave.raveandroid.validators.BanksMinimum100AccountPaymentValidator;
import com.flutterwave.raveandroid.validators.BvnValidator;
import com.flutterwave.raveandroid.validators.DateOfBirthValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.flutterwave.raveandroid.validators.PhoneValidator;

import java.util.HashMap;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NG;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NGN;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAccount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldBVN;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldBankCode;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldDOB;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidAccountNoMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidBankCodeMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidBvnMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.invalidDateOfBirthMessage;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validAmountPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validEmailPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validPhonePrompt;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class AccountUiPresenter extends AccountHandler implements AccountUiContract.UserActionsListener {

    private AccountUiContract.View mView;

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
    UrlValidator urlValidator;
    @Inject
    BanksMinimum100AccountPaymentValidator minimum100AccountPaymentValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    EventLogger eventLogger;
    @Inject
    PayloadToJsonConverter payloadToJsonConverter;
    @Inject
    PayloadEncryptor payloadEncryptor;

    @Inject
    public AccountUiPresenter(AccountUiContract.View mView) {
        super(mView);
        this.mView = mView;
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
            mView.onDataValidationSuccessful(dataHashMap);
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
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setDevice_fingerprint(deviceIdGetter.getDeviceId())
                    .setIP(deviceIdGetter.getDeviceId())
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
                fetchFee(body);
            } else {
                chargeAccount(body, ravePayInitializer.getEncryptionKey());
            }

        }
    }


    @Override
    public void onAttachView(AccountUiContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullAccountView();
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("Account Fragment").getEvent(), ravePayInitializer.getPublicKey());

            boolean isEmailValid = emailValidator.isEmailValid(ravePayInitializer.getEmail());
            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            boolean isPhoneValid = phoneValidator.isPhoneValid(ravePayInitializer.getPhoneNumber());

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
            if(isPhoneValid){
                mView.onPhoneNumberValidated(ravePayInitializer.getPhoneNumber(), View.VISIBLE);
            } else {
                mView.onPhoneNumberValidated("", View.VISIBLE);
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

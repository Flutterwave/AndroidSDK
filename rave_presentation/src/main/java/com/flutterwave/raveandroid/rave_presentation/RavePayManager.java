package com.flutterwave.raveandroid.rave_presentation;

import android.content.Context;

import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.data.Utils;

import java.util.ArrayList;
import java.util.List;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_BANK_TRANSFER;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_BARTER;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_CARD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_MPESA;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_SA_BANK_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UG_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UK;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_USSD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY;

abstract public class RavePayManager  {
    protected String email;
    protected double amount = -1;
    protected String publicKey;
    protected String encryptionKey;
    protected String txRef;
    protected String narration = "";
    protected String currency = "NGN";
    protected String country = "NG";
    protected String fName = "";
    protected String lName = "";
    protected boolean allowSaveCard = true;
    protected String meta = "";
    protected String subAccounts = "";
    protected String payment_plan;
    protected boolean isPreAuth = false;
    protected String phoneNumber = "";
    protected boolean showStagingLabel = true;
    protected boolean displayFee = true;
    protected boolean staging = true;
    protected boolean isPermanent = false;
    protected int duration = 0;
    protected int frequency = 0;
    protected ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();

    public RavePayManager() { }

    public RavePayManager onStagingEnv(boolean staging) {
        this.staging = staging;
        return this;
    }

    public RavePayManager allowSaveCardFeature(boolean allowSaveCard) {
        this.allowSaveCard = allowSaveCard;
        return this;
    }

    public RavePayManager acceptAchPayments(boolean withAch) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ACH) && withAch)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ACH);
        return this;
    }

    public RavePayManager shouldDisplayFee(boolean displayFee) {
        this.displayFee = displayFee;
        return this;
    }

    public RavePayManager acceptCardPayments(boolean withCard) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_CARD) && withCard)
            orderedPaymentTypesList.add(PAYMENT_TYPE_CARD);
        return this;
    }

    public RavePayManager acceptMpesaPayments(boolean withMpesa) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_MPESA) && withMpesa)
            orderedPaymentTypesList.add(PAYMENT_TYPE_MPESA);
        return this;
    }

    public RavePayManager acceptAccountPayments(boolean withAccount) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT) && withAccount)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ACCOUNT);
        return this;
    }

    public RavePayManager acceptGHMobileMoneyPayments(boolean withGHMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY) && withGHMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_GH_MOBILE_MONEY);
        return this;
    }

    public RavePayManager acceptUgMobileMoneyPayments(boolean withUgMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY) && withUgMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_UG_MOBILE_MONEY);
        return this;
    }

    public RavePayManager acceptRwfMobileMoneyPayments(boolean withRwfMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY) && withRwfMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_RW_MOBILE_MONEY);
        return this;
    }

    public RavePayManager acceptZmMobileMoneyPayments(boolean withZmMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY) && withZmMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ZM_MOBILE_MONEY);
        return this;
    }

    public RavePayManager acceptUkPayments(boolean withUk) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_UK) && withUk)
            orderedPaymentTypesList.add(PAYMENT_TYPE_UK);
        return this;
    }

    public RavePayManager acceptSaBankPayments(boolean withSaBankAccount) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_SA_BANK_ACCOUNT) && withSaBankAccount)
            orderedPaymentTypesList.add(PAYMENT_TYPE_SA_BANK_ACCOUNT);
        return this;
    }

    public RavePayManager acceptFrancMobileMoneyPayments(boolean withFrancMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY) && withFrancMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_FRANCO_MOBILE_MONEY);
        return this;
    }

    public RavePayManager acceptBankTransferPayments(boolean withBankTransfer) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        return this;
    }


    public RavePayManager acceptBankTransferPayments(boolean withBankTransfer, boolean isPermanent) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        this.isPermanent = isPermanent;
        return this;
    }

    public RavePayManager acceptBankTransferPayments(boolean withBankTransfer, int duration, int frequency) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        this.duration = duration;
        this.frequency = frequency;
        return this;
    }

    public RavePayManager acceptUssdPayments(boolean withUssd) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_USSD) && withUssd)
            orderedPaymentTypesList.add(PAYMENT_TYPE_USSD);
        return this;
    }

    public RavePayManager acceptBarterPayments(boolean withBarter) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BARTER) && withBarter)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BARTER);
        return this;
    }

    public RavePayManager isPreAuth(boolean isPreAuth) {
        this.isPreAuth = isPreAuth;
        return this;
    }

    public RavePayManager setMeta(List<Meta> meta) {
        this.meta = Utils.stringifyMeta(meta);
        return this;
    }

    public RavePayManager setSubAccounts(List<SubAccount> subAccounts) {
        this.subAccounts = Utils.stringifySubaccounts(subAccounts);
        return this;
    }

    public RavePayManager showStagingLabel(boolean showStagingLabel) {
        this.showStagingLabel = showStagingLabel;
        return this;
    }


    public RavePayManager setEmail(String email) {
        this.email = email;
        return this;
    }

    public RavePayManager setAmount(double amount) {
        if (amount != 0) {
            this.amount = amount;
        }
        return this;
    }

    public RavePayManager setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public RavePayManager setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
        return this;
    }

    public RavePayManager setTxRef(String txRef) {
        this.txRef = txRef;
        return this;
    }

    public RavePayManager setNarration(String narration) {
        this.narration = narration;
        return this;
    }

    public RavePayManager setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public RavePayManager setCountry(String country) {
        this.country = country;
        return this;
    }

    public RavePayManager setfName(String fName) {
        this.fName = fName;
        return this;
    }

    public RavePayManager setlName(String lName) {
        this.lName = lName;
        return this;
    }

    public RavePayManager setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public RavePayManager setPaymentPlan(String payment_plan) {
        this.payment_plan = payment_plan;
        return this;
    }


    public abstract void initializeUI();

    public abstract RaveNonUIManager initializeNonUI();
}

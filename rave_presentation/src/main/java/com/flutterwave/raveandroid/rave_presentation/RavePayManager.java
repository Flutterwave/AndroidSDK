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

    public RavePayManager() { }

    public RavePayManager onStagingEnv(boolean staging) {
        this.staging = staging;
        return this;
    }

    public RavePayManager shouldDisplayFee(boolean displayFee) {
        this.displayFee = displayFee;
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

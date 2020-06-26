package com.flutterwave.raveandroid.rave_presentation;

import com.flutterwave.raveandroid.rave_core.di.DeviceIdGetterModule;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.data.Utils;
import com.flutterwave.raveandroid.rave_presentation.di.DaggerRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;

import java.util.List;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.STAGING_URL;

public class RaveNonUIManager extends RavePayManager {
    private RaveComponent raveComponent;
    private String uniqueDeviceID;


    public RaveNonUIManager() {
    }


    public RaveNonUIManager setUniqueDeviceId(String uniqueDeviceID){
        this.uniqueDeviceID = uniqueDeviceID;
        return this;
    }

    public RaveNonUIManager setMeta(List<Meta> meta) {
        this.meta = Utils.stringifyMeta(meta);
        return this;
    }

    public RaveNonUIManager setSubAccounts(List<SubAccount> subAccounts) {
        this.subAccounts = Utils.stringifySubaccounts(subAccounts);
        return this;
    }

    public RaveNonUIManager setEmail(String email) {
        this.email = email;
        return this;
    }

    public RaveNonUIManager setAmount(double amount) {
        if (amount != 0) {
            this.amount = amount;
        }
        return this;
    }

    public RaveNonUIManager setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public RaveNonUIManager setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
        return this;
    }

    public RaveNonUIManager setTxRef(String txRef) {
        this.txRef = txRef;
        return this;
    }

    public RaveNonUIManager setNarration(String narration) {
        this.narration = narration;
        return this;
    }

    public RaveNonUIManager setCurrency(String currency) {
        this.currency = currency;
        switch (currency) {
            case "KES":
                country = "KE";
                break;
            case "GHS":
                country = "GH";
                break;
            case "ZAR":
                country = "ZA";
                break;
            case "TZS":
                country = "TZ";
                break;
            default:
                country = "NG";
                break;
        }
        return this;
    }

    @Deprecated
    public RaveNonUIManager setCountry(String country) {
        this.country = country;
        return this;
    }

    public RaveNonUIManager setfName(String fName) {
        this.fName = fName;
        return this;
    }

    public RaveNonUIManager setlName(String lName) {
        this.lName = lName;
        return this;
    }

    public RaveNonUIManager setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public RaveNonUIManager setPaymentPlan(String payment_plan) {
        this.payment_plan = payment_plan;
        return this;
    }

    public RaveNonUIManager onStagingEnv(boolean isStaging) {
        this.staging = isStaging;
        return this;
    }

    public RaveNonUIManager isPreAuth(boolean isPreAuth) {
        this.isPreAuth = isPreAuth;
        return this;
    }

    public String getUniqueDeviceID() {
        return uniqueDeviceID;
    }


    public RaveComponent getRaveComponent() {
        return raveComponent;
    }

    public RaveNonUIManager initialize() {
        setUpGraph();
        return this;
    }

    private RaveComponent setUpGraph() {
        String baseUrl;

        if (isStaging()) {
            baseUrl = STAGING_URL;
        } else {
            baseUrl = LIVE_URL;
        }
        raveComponent = DaggerRaveComponent.builder()
                .deviceIdGetterModule(new DeviceIdGetterModule(uniqueDeviceID))
                .remoteModule(new RemoteModule(baseUrl))
                .build();
        return raveComponent;
    }
}

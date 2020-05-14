package com.flutterwave.raveandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.flutterwave.raveandroid.di.components.AppComponent;
import com.flutterwave.raveandroid.di.components.DaggerAppComponent;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.responses.SubAccount;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.flutterwave.raveandroid.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ACCOUNT;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ACH;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_BANK_TRANSFER;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_BARTER;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_CARD;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_MPESA;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_SA_BANK_ACCOUNT;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_UG_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_UK;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_USSD;
import static com.flutterwave.raveandroid.RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY;
import static com.flutterwave.raveandroid.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.RaveConstants.RAVE_PARAMS;
import static com.flutterwave.raveandroid.RaveConstants.RAVE_REQUEST_CODE;
import static com.flutterwave.raveandroid.RaveConstants.STAGING_URL;

public class RavePayManager {
    private String email;
    private double amount = -1;
    private String publicKey;
    private String encryptionKey;
    private String txRef;
    private String narration = "";
    private String currency = "NGN";
    private String country = "NG";
    private String fName = "";
    private String lName = "";
    boolean allowSaveCard = true;
    private String meta = "";
    private String subAccounts = "";
    private String payment_plan;
    private Activity activity;
    private Fragment supportFragment;
    private android.app.Fragment fragment;
    private int theme = R.style.DefaultTheme;
    boolean staging = true;
    boolean isPreAuth = false;
    private String phoneNumber = "";
    private Boolean allowEditPhone = true;
    boolean showStagingLabel = true;
    boolean displayFee = true;
    private boolean isPermanent = false;
    private int duration = 0;
    private int frequency = 0;
    private ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();

    public ArrayList<Integer> getOrderedPaymentTypesList() {
        return orderedPaymentTypesList;
    }

    public RavePayManager allowSaveCardFeature(boolean allowSaveCard) {
        this.allowSaveCard = allowSaveCard;
        return this;
    }

    public RavePayManager onStagingEnv(boolean staging) {
        this.staging = staging;
        return this;
    }

    public RavePayManager withTheme(int theme) {
        this.theme = theme;
        return this;
    }

    public RavePayManager(Activity activity) {
        this.activity = activity;
    }

    public RavePayManager(Fragment fragment) {
        this.supportFragment = fragment;
    }

    public RavePayManager(android.app.Fragment fragment) {
        this.fragment = fragment;
    }

    public RavePayManager acceptAchPayments(boolean withAch) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ACH) && withAch)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ACH);
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

    public RavePayManager setPhoneNumber(String phoneNumber, Boolean isEditable) {
        this.phoneNumber = phoneNumber;
        this.allowEditPhone = isEditable;
        return this;
    }

    public RavePayManager setPaymentPlan(String payment_plan) {
        this.payment_plan = payment_plan;
        return this;
    }

    public void initialize() {

        if (activity != null) {

            Intent intent = new Intent(activity, RavePayActivity.class);
            intent.putExtra(RAVE_PARAMS, Parcels.wrap(createRavePayInitializer()));
            activity.startActivityForResult(intent, RAVE_REQUEST_CODE);
        } else if (supportFragment != null && supportFragment.getContext() != null) {
            Intent intent = new Intent(supportFragment.getContext(), RavePayActivity.class);
            intent.putExtra(RAVE_PARAMS, Parcels.wrap(createRavePayInitializer()));
            supportFragment.startActivityForResult(intent, RAVE_REQUEST_CODE);
        } else if (fragment != null && fragment.getActivity() != null) {
            Intent intent = new Intent(fragment.getActivity(), RavePayActivity.class);
            intent.putExtra(RAVE_PARAMS, Parcels.wrap(createRavePayInitializer()));
            fragment.startActivityForResult(intent, RAVE_REQUEST_CODE);
        } else {
            Log.d(RAVEPAY, "Context is required!");
        }

    }

    public Raver initializeNoUi() {

        RavePayInitializer ravePayInitializer = createRavePayInitializer();
        AppComponent component = setUpGraph();

        return new Raver(ravePayInitializer, component);

    }

    public RavePayManager shouldDisplayFee(boolean displayFee) {
        this.displayFee = displayFee;
        return this;
    }

    public RavePayInitializer createRavePayInitializer() {

        return new RavePayInitializer(
                email,
                amount,
                publicKey,
                encryptionKey,
                txRef,
                narration,
                currency,
                country,
                fName,
                lName,
                theme,
                phoneNumber,
                allowEditPhone,
                allowSaveCard,
                isPermanent,
                duration,
                frequency,
                staging,
                meta,
                subAccounts,
                payment_plan,
                isPreAuth,
                showStagingLabel,
                displayFee,
                orderedPaymentTypesList);
    }

    private AppComponent setUpGraph() {
        String baseUrl;

        if (staging) {
            baseUrl = STAGING_URL;
        } else {
            baseUrl = LIVE_URL;
        }

        if (activity != null) {
            return DaggerAppComponent.builder()
                    .androidModule(new AndroidModule(activity))
                    .networkModule(new NetworkModule(baseUrl))
                    .build();
        } else if (supportFragment != null && supportFragment.getContext() != null) {
            return DaggerAppComponent.builder()
                    .androidModule(new AndroidModule(supportFragment.getContext()))
                    .networkModule(new NetworkModule(baseUrl))
                    .build();
        } else if (fragment != null && fragment.getActivity() != null) {
            return DaggerAppComponent.builder()
                    .androidModule(new AndroidModule(fragment.getActivity()))
                    .networkModule(new NetworkModule(baseUrl))
                    .build();
        } else {
            throw new IllegalArgumentException("Context is required");
        }
    }
}
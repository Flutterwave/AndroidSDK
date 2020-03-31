package com.flutterwave.raveandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.RavePayManager;

import org.parceler.Parcels;

import java.util.ArrayList;

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
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_PARAMS;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_REQUEST_CODE;

public class RaveUiManager extends RavePayManager {
    private Activity activity;
    private Fragment supportFragment;
    private android.app.Fragment fragment;
    private int theme = R.style.DefaultTheme;
    private boolean allowSaveCard = true;
    private ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();

    public RaveUiManager(Activity activity) {
        super();
        this.activity = activity;
    }

    public RaveUiManager(Fragment fragment) {
        super();
        this.supportFragment = fragment;
    }

    public RaveUiManager(android.app.Fragment fragment) {
        super();
        this.fragment = fragment;
    }

    public RaveUiManager withTheme(int theme) {
        this.theme = theme;
        return this;
    }

    public RaveUiManager allowSaveCardFeature(boolean allowSaveCard) {
        this.allowSaveCard = allowSaveCard;
        return this;
    }

    public RaveUiManager acceptAchPayments(boolean withAch) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ACH) && withAch)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ACH);
        return this;
    }

    public RaveUiManager acceptCardPayments(boolean withCard) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_CARD) && withCard)
            orderedPaymentTypesList.add(PAYMENT_TYPE_CARD);
        return this;
    }

    public RaveUiManager acceptMpesaPayments(boolean withMpesa) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_MPESA) && withMpesa)
            orderedPaymentTypesList.add(PAYMENT_TYPE_MPESA);
        return this;
    }

    public RaveUiManager acceptAccountPayments(boolean withAccount) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT) && withAccount)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ACCOUNT);
        return this;
    }

    public RaveUiManager acceptGHMobileMoneyPayments(boolean withGHMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY) && withGHMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_GH_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptUgMobileMoneyPayments(boolean withUgMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY) && withUgMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_UG_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptRwfMobileMoneyPayments(boolean withRwfMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY) && withRwfMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_RW_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptZmMobileMoneyPayments(boolean withZmMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY) && withZmMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ZM_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptUkPayments(boolean withUk) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_UK) && withUk)
            orderedPaymentTypesList.add(PAYMENT_TYPE_UK);
        return this;
    }

    public RaveUiManager acceptSaBankPayments(boolean withSaBankAccount) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_SA_BANK_ACCOUNT) && withSaBankAccount)
            orderedPaymentTypesList.add(PAYMENT_TYPE_SA_BANK_ACCOUNT);
        return this;
    }

    public RaveUiManager acceptFrancMobileMoneyPayments(boolean withFrancMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY) && withFrancMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_FRANCO_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptBankTransferPayments(boolean withBankTransfer) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        return this;
    }


    public RaveUiManager acceptBankTransferPayments(boolean withBankTransfer, boolean isPermanent) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        this.isPermanent = isPermanent;
        return this;
    }

    public RaveUiManager acceptBankTransferPayments(boolean withBankTransfer, int duration, int frequency) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        this.duration = duration;
        this.frequency = frequency;
        return this;
    }

    public RaveUiManager acceptUssdPayments(boolean withUssd) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_USSD) && withUssd)
            orderedPaymentTypesList.add(PAYMENT_TYPE_USSD);
        return this;
    }

    public RaveUiManager acceptBarterPayments(boolean withBarter) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BARTER) && withBarter)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BARTER);
        return this;
    }

    public RaveNonUIManager initializeNonUI() {
        throw new IllegalArgumentException("Cannot initialize non Rave UI with RaveUIManager use RaveNonUIManager instead");
    }

    public void initializeUI() {
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

    public RaveUiManager shouldDisplayFee(boolean displayFee) {
        this.displayFee = displayFee;
        return this;
    }

    private RavePayInitializer createRavePayInitializer() {
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
}
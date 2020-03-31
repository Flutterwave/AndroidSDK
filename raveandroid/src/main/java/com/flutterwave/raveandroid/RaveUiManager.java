package com.flutterwave.raveandroid;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.RavePayManager;

import org.parceler.Parcels;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_PARAMS;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_REQUEST_CODE;

public class RaveUiManager extends RavePayManager {
    private Activity activity;
    private Fragment supportFragment;
    private android.app.Fragment fragment;
    private int theme = R.style.DefaultTheme;

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

    public RavePayManager withTheme(int theme) {
        this.theme = theme;
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
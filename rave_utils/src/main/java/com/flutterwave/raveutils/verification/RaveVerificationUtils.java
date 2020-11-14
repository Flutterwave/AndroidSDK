package com.flutterwave.raveutils.verification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveutils.R;
import com.flutterwave.raveutils.verification.web.WebFragment;

import java.util.UUID;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.ADDRESS_DETAILS_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.EMBED_FRAGMENT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.OTP_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VIEW_ID;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.WEB_VERIFICATION_REQUEST_CODE;
import static com.flutterwave.raveutils.verification.VerificationActivity.EXTRA_IS_STAGING;

public class RaveVerificationUtils {

    private final Context context;
    private final boolean isStaging;
    private final String publicKey;
    private int theme = R.style.DefaultTheme;
    private AppCompatActivity activity = null;
    private Fragment fragment = null;
    private boolean embedFragment = false;
    private int viewId;

    public RaveVerificationUtils(AppCompatActivity activity, boolean isStaging, String publicKey, int theme) {
        this.activity = activity;
        this.context = activity;
        this.isStaging = isStaging;
        this.publicKey = publicKey;
        this.theme = theme;
    }

    public RaveVerificationUtils(AppCompatActivity activity, Fragment fragment, boolean isStaging, String publicKey, int theme, boolean embedFragment, int viewId) {
        this.activity = activity;
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.isStaging = isStaging;
        this.publicKey = publicKey;
        this.theme = theme;
        this.embedFragment = embedFragment;
        this.viewId = viewId;
    }

    public RaveVerificationUtils(AppCompatActivity activity, boolean isStaging, String publicKey) {
        this.activity = activity;
        this.context = activity;
        this.isStaging = isStaging;
        this.publicKey = publicKey;
    }

    public RaveVerificationUtils(Fragment fragment, boolean isStaging, String publicKey) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.isStaging = isStaging;
        this.publicKey = publicKey;
    }

    public void showOtpScreen() {
        showOtpScreen(null);
    }

    public void showOtpScreen(String authInstruction) {
        showOtpScreen(authInstruction, false);
    }

    public void showOtpScreenForSavedCard(String authInstruction) {
        showOtpScreen(authInstruction, true);
    }

    private void showOtpScreen(String validateInstruction, boolean forSavedCards) {

        if (embedFragment) {

            VerificationFragment verificationFragment = new VerificationFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_IS_STAGING, isStaging);
            bundle.putBoolean(EMBED_FRAGMENT, embedFragment);
            bundle.putInt(VIEW_ID, viewId);
            bundle.putString(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            bundle.putString(VerificationActivity.ACTIVITY_MOTIVE, "otp");
            bundle.putBoolean(OTPFragment.IS_SAVED_CARD_CHARGE, forSavedCards);
            if (validateInstruction != null) {
                bundle.putString(OTPFragment.EXTRA_CHARGE_MESSAGE, validateInstruction);
            }
            bundle.putInt("theme", theme);

            verificationFragment.setArguments(bundle);

            if (activity != null) {
                String fragmentTag = UUID.randomUUID().toString();
                fragment.getParentFragmentManager().beginTransaction().add(viewId, verificationFragment, fragmentTag).addToBackStack("").commit();
            }

        } else {

            Intent intent = new Intent(context, VerificationActivity.class);
            intent.putExtra(EXTRA_IS_STAGING, isStaging);
            intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "otp");
            intent.putExtra(OTPFragment.IS_SAVED_CARD_CHARGE, forSavedCards);
            if (validateInstruction != null) {
                intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, validateInstruction);
            }
            intent.putExtra("theme", theme);
            if (activity != null && fragment == null)
                activity.startActivityForResult(intent, OTP_REQUEST_CODE);
            else fragment.startActivityForResult(intent, OTP_REQUEST_CODE);

        }
    }

    public void showWebpageVerificationScreen(String authurl) {

        if (embedFragment) {

            VerificationFragment verificationFragment = new VerificationFragment();
            Bundle bundle = new Bundle();
            bundle.putString(WebFragment.EXTRA_AUTH_URL, authurl);
            bundle.putString(VerificationActivity.ACTIVITY_MOTIVE, "web");
            bundle.putInt(VIEW_ID, viewId);
            bundle.putBoolean(EXTRA_IS_STAGING, isStaging);
            bundle.putString(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            bundle.putInt("theme", theme);

            verificationFragment.setArguments(bundle);

            if (activity != null) {
                String fragmentTag = UUID.randomUUID().toString();
                fragment.getParentFragmentManager().beginTransaction().replace(viewId, verificationFragment).addToBackStack("").commit();
            }

        } else {

            Intent intent = new Intent(context, VerificationActivity.class);
            intent.putExtra(WebFragment.EXTRA_AUTH_URL, authurl);
            intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "web");
            intent.putExtra(EXTRA_IS_STAGING, isStaging);
            intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            intent.putExtra("theme", theme);
            if (activity != null && fragment == null)
                activity.startActivityForResult(intent, WEB_VERIFICATION_REQUEST_CODE);
            else fragment.startActivityForResult(intent, WEB_VERIFICATION_REQUEST_CODE);
        }

    }

    public void showBarterCheckoutScreen(String authurl, String flwRef) {

        if (embedFragment) {

            VerificationFragment verificationFragment = new VerificationFragment();
            Bundle bundle = new Bundle();
            bundle.putString(WebFragment.EXTRA_AUTH_URL, authurl);
            bundle.putString(VerificationActivity.ACTIVITY_MOTIVE, "web");
            bundle.putInt(VIEW_ID, viewId);
            bundle.putBoolean(EXTRA_IS_STAGING, isStaging);
            bundle.putString(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            bundle.putInt("theme", theme);

            verificationFragment.setArguments(bundle);

            if (activity != null) {
                String fragmentTag = UUID.randomUUID().toString();
                fragment.getParentFragmentManager().beginTransaction().add(viewId, verificationFragment, fragmentTag).addToBackStack("").commit();
            }

        } else {

            Intent intent = new Intent(context, VerificationActivity.class);
            intent.putExtra(WebFragment.EXTRA_FLW_REF, flwRef);
            intent.putExtra(WebFragment.EXTRA_AUTH_URL, authurl);
            intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, BARTER_CHECKOUT);
            intent.putExtra(EXTRA_IS_STAGING, isStaging);
            intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            intent.putExtra("theme", theme);
            if (activity != null)
                activity.startActivityForResult(intent, BARTER_CHECKOUT_REQUEST_CODE);
            else fragment.startActivityForResult(intent, BARTER_CHECKOUT_REQUEST_CODE);

        }

    }

    public void showPinScreen() {

        if (embedFragment) {

            VerificationFragment verificationFragment = new VerificationFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_IS_STAGING, isStaging);
            bundle.putString(VerificationActivity.ACTIVITY_MOTIVE, "pin");
            bundle.putInt(VIEW_ID, viewId);
            bundle.putString(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            bundle.putInt("theme", theme);
            verificationFragment.setArguments(bundle);

            if (activity != null) {
                String fragmentTag = UUID.randomUUID().toString();
                fragment.getParentFragmentManager().beginTransaction().add(viewId, verificationFragment, fragmentTag).addToBackStack("").commit();
            }

        } else {

            Intent intent = new Intent(context, VerificationActivity.class);
            intent.putExtra(EXTRA_IS_STAGING, isStaging);
            intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "pin");
            intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            intent.putExtra("theme", theme);
            if (activity != null)
                activity.startActivityForResult(intent, PIN_REQUEST_CODE);
            else fragment.startActivityForResult(intent, PIN_REQUEST_CODE);

        }

    }

    public void showAddressScreen() {

        if (embedFragment) {

            VerificationFragment verificationFragment = new VerificationFragment();
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_IS_STAGING, isStaging);
            bundle.putString(VerificationActivity.ACTIVITY_MOTIVE, "avsvbv");
            bundle.putInt(VIEW_ID, viewId);
            bundle.putString(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            bundle.putInt("theme", theme);

            verificationFragment.setArguments(bundle);

            if (activity != null) {
                String fragmentTag = UUID.randomUUID().toString();
                fragment.getParentFragmentManager().beginTransaction().add(viewId, verificationFragment, fragmentTag).addToBackStack("").commit();
            }

        } else {

            Intent intent = new Intent(context, VerificationActivity.class);
            intent.putExtra(EXTRA_IS_STAGING, isStaging);
            intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "avsvbv");
            intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
            intent.putExtra("theme", theme);
            if (activity != null)
                activity.startActivityForResult(intent, ADDRESS_DETAILS_REQUEST_CODE);
            else fragment.startActivityForResult(intent, ADDRESS_DETAILS_REQUEST_CODE);

        }

    }
}

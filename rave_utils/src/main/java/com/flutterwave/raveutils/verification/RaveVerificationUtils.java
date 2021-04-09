package com.flutterwave.raveutils.verification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveutils.R;
import com.flutterwave.raveutils.verification.web.WebFragment;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.ADDRESS_DETAILS_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.OTP_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.WEB_VERIFICATION_REQUEST_CODE;
import static com.flutterwave.raveutils.verification.VerificationActivity.EXTRA_IS_STAGING;

public class RaveVerificationUtils {

    private final Context context;
    private final boolean isStaging;
    private final String publicKey;
    private int theme = R.style.DefaultTheme;
    private Activity activity = null;
    private Fragment fragment = null;

    public RaveVerificationUtils(Activity activity, boolean isStaging, String publicKey, int theme) {
        this.activity = activity;
        this.context = activity;
        this.isStaging = isStaging;
        this.publicKey = publicKey;
        this.theme = theme;
    }

    public RaveVerificationUtils(Fragment fragment, boolean isStaging, String publicKey, int theme) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.isStaging = isStaging;
        this.publicKey = publicKey;
        this.theme = theme;
    }

    public RaveVerificationUtils(Activity activity, boolean isStaging, String publicKey) {
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
        Intent intent = new Intent(context, VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, isStaging);
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "otp");
        intent.putExtra(OTPFragment.IS_SAVED_CARD_CHARGE, forSavedCards);
        if (validateInstruction != null) {
            intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, validateInstruction);
        }
        intent.putExtra("theme", theme);
        if (activity != null) activity.startActivityForResult(intent, OTP_REQUEST_CODE);
        else fragment.startActivityForResult(intent, OTP_REQUEST_CODE);
    }

    public void showWebpageVerificationScreen(String authUrl) {
        showWebpageVerificationScreen(authUrl, null);
    }

    public void showWebpageVerificationScreen(String authUrl, @Nullable String flwRef) {
        Intent intent = new Intent(context, VerificationActivity.class);
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authUrl);
        intent.putExtra(WebFragment.EXTRA_FLW_REF, flwRef);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "web");
        intent.putExtra(EXTRA_IS_STAGING, isStaging);
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
        intent.putExtra("theme", theme);
        if (activity != null)
            activity.startActivityForResult(intent, WEB_VERIFICATION_REQUEST_CODE);
        else fragment.startActivityForResult(intent, WEB_VERIFICATION_REQUEST_CODE);
    }

    public void showBarterCheckoutScreen(String authurl, String flwRef) {
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

    public void showPinScreen() {
        Intent intent = new Intent(context, VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, isStaging);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "pin");
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
        intent.putExtra("theme", theme);
        if (activity != null)
            activity.startActivityForResult(intent, PIN_REQUEST_CODE);
        else fragment.startActivityForResult(intent, PIN_REQUEST_CODE);
    }

    public void showAddressScreen() {
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

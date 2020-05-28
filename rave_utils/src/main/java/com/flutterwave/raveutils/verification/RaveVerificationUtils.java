package com.flutterwave.raveutils.verification;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.flutterwave.raveutils.R;
import com.flutterwave.raveutils.verification.web.WebFragment;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.OTP_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.WEB_VERIFICATION_REQUEST_CODE;
import static com.flutterwave.raveutils.verification.VerificationActivity.EXTRA_IS_STAGING;

public class RaveVerificationUtils {

    private final Context context;
    private final boolean isStaging;
    private final String publicKey;
    private Activity activity = null;
    private Fragment fragment = null;

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

    public void showOtpScreen(int theme) {
        showOtpScreen(null, theme);
    }

    public void showOtpScreen(String validateInstruction) {
        showOtpScreen(validateInstruction, R.style.DefaultTheme);
    }

    public void showOtpScreen(String authInstruction, int theme) {
        showOtpScreen(authInstruction, theme, false);
    }

    public void showOtpScreenForSavedCard(String authInstruction, int theme) {
        showOtpScreen(authInstruction, theme, true);
    }

    private void showOtpScreen(String validateInstruction, int theme, boolean forSavedCards) {
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

    public void showWebpageVerificationScreen(String authurl) {
        showWebpageVerificationScreen(authurl, R.style.DefaultTheme);
    }

    public void showWebpageVerificationScreen(String authurl, int theme) {
        Intent intent = new Intent(context, VerificationActivity.class);
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authurl);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "web");
        intent.putExtra(EXTRA_IS_STAGING, isStaging);
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, publicKey);
        intent.putExtra("theme", theme);
        if (activity != null)
            activity.startActivityForResult(intent, WEB_VERIFICATION_REQUEST_CODE);
        else fragment.startActivityForResult(intent, WEB_VERIFICATION_REQUEST_CODE);
    }

    public void showBarterCheckoutScreen(String authurl, String flwRef) {
        showBarterCheckoutScreen(authurl, flwRef, R.style.DefaultTheme);
    }

    public void showBarterCheckoutScreen(String authurl, String flwRef, int theme) {
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

package com.flutterwave.raveutils.verification;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.VERIFICATION_REQUEST_KEY;

public class Utils {

    public static Bundle bundle = new Bundle();

    public static final String RESULT_CODE = "resultCode";
    public static final String REQUEST_CODE = "requestCode";

    public static int RESULT_SUCCESS = RaveConstants.RESULT_SUCCESS;
    public static int RESULT_ERROR = RaveConstants.RESULT_ERROR;
    public static int RESULT_CANCELLED = RaveConstants.RESULT_CANCELLED;

    public static void onBackPressed(Boolean embedFragment, final Fragment fragment, final AppCompatActivity activity) {

        if (embedFragment && activity != null) {

            bundle.clear();

            new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {

                    if (fragment != null) {
                        activity.getOnBackPressedDispatcher().addCallback((AppCompatActivity) activity, this);

                        if (bundle.isEmpty()) {
                            bundle.putInt("resultCode", RESULT_CANCELLED);
                        }
                        activity.getSupportFragmentManager().setFragmentResult(VERIFICATION_REQUEST_KEY, bundle);
                    }
                }
            };
        }

    }

    public static void hideKeyboard(FragmentActivity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}

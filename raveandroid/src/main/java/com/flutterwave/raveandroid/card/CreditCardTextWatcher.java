package com.flutterwave.raveandroid.card;

import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.SparseArray;


import com.flutterwave.raveandroid.R;

import java.util.regex.Pattern;

/**
 * Created by hamzafetuga on 24/07/2017.
 */

class CreditCardTextWatcher implements TextWatcher {

    private static final char space = ' ';
    private final int mDefaultDrawableResId = R.drawable.creditcard;
    private int mCurrentDrawableResId = 0;
    private Drawable mCurrentDrawable;
    String lastFormattedText;
    private SparseArray<Pattern> mCCPatterns = null;

    CreditCardTextWatcher(){
        init();
    }

    private void init() {
        if (mCCPatterns == null) {
            mCCPatterns = new SparseArray<>();
            // With spaces for credit card masking
            mCCPatterns.put(R.drawable.visa_logo_new, Pattern.compile(
                    "^4[0-9]{2,12}(?:[0-9]{3})?$"));
            mCCPatterns.put(R.drawable.master_card_logo_svg, Pattern.compile(
                    "^5[1-5][0-9]{1,14}$"));
            mCCPatterns.put(R.drawable.amex, Pattern.compile(
                    "^3[47][0-9]{1,13}$"));
            ///^([506]{3})([0-9]{1,16})$/
            mCCPatterns.put(R.drawable.verve, Pattern.compile(
                    "^([506]{3})([0-9]{1,16})$"
            ));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {


        if (s.length() > 0 && (s.length() % 5) == 0) {
            final char c = s.charAt(s.length() - 1);
            if (space == c) {
                s.delete(s.length() - 1, s.length());
            }
        }
        // Insert char where needed.
        if (s.length() > 0 && (s.length() % 5) == 0) {
            char c = s.charAt(s.length() - 1);
            // Only if its a digit where there should be a space we insert a space
            if (Character.isDigit(c) && TextUtils.split(s.toString(), String.valueOf(space)).length <= 3) {
                s.insert(s.length() - 1, String.valueOf(space));
            }
        }
    }


}

package com.flutterwave.raveandroid.barter;


import android.content.Context;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.PayloadEncryptor;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.validators.AmountValidator;

import javax.inject.Inject;

public class BarterPresenter implements BarterContract.UserActionsListener {

    @Inject
    NetworkRequestImpl networkRequest;
    @Inject
    AmountValidator amountValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private Context context;
    private BarterContract.View mView;

    @Inject
    public BarterPresenter(Context context, BarterContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()));
            }
        }
    }

}

package com.flutterwave.raveandroid.ussd;


import android.content.Context;

import javax.inject.Inject;

class UssdPresenter implements UssdContract.UserActionsListener {
    private Context context;
    private UssdContract.View mView;

    @Inject
    UssdPresenter(Context context, UssdContract.View mView) {
        this.context = context;
        this.mView = mView;
    }

}
package com.flutterwave.raveandroid;


import android.view.View;

public class PaymentTile {

    View view;
    boolean isTop;
    int paymentType;

    PaymentTile(View view, int paymentType) {
        this.view = view;
        this.isTop = false;
        this.paymentType = paymentType;
    }

    PaymentTile(View view, int paymentType, boolean isTop) {
        this.view = view;
        this.isTop = isTop;
        this.paymentType = paymentType;
    }
}

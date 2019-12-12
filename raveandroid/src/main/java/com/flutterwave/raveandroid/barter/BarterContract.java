package com.flutterwave.raveandroid.barter;


import com.flutterwave.raveandroid.RavePayInitializer;

public interface BarterContract {

    interface View {


        void onAmountValidationSuccessful(String amountToPay);

    }

    interface UserActionsListener {

        void init(RavePayInitializer ravePayInitializer);
    }
}

package com.flutterwave.raveandroid.validators;

import javax.inject.Inject;

public class NetworkValidator {

    @Inject
    public NetworkValidator() {
    }

    public boolean isNetworkValid(int position) {
        return position != 0;
    }
}

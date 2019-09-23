package com.flutterwave.raveandroid.validators;

import com.flutterwave.raveandroid.RaveConstants;

import javax.inject.Inject;

public class NetworkValidator {

    @Inject
    public NetworkValidator() {
    }

    public boolean isNetworkValid(String network) {
        return network != null && network.equalsIgnoreCase(RaveConstants.mtn) && !network.isEmpty();
    }
}

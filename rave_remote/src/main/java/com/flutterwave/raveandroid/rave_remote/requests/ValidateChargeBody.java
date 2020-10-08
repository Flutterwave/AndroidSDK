package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by hamzafetuga on 19/07/2017.
 */

public class ValidateChargeBody {
    String flw_ref;
    String otp;
    String type;

    public ValidateChargeBody(String flw_ref,
                              String otp,
                              String type) {

        this.flw_ref = flw_ref;
        this.otp = otp;
        this.type = type;
    }
}

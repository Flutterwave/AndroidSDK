package com.flutterwave.raveandroid.data;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class RequeryRequestBody {

    String SECKEY;

    public String getSECKEY() {
        return SECKEY;
    }

    public void setSECKEY(String SECKEY) {
        this.SECKEY = SECKEY;
    }

    public String getFlw_ref() {
        return flw_ref;
    }

    public void setFlw_ref(String flw_ref) {
        this.flw_ref = flw_ref;
    }

    String flw_ref;


}

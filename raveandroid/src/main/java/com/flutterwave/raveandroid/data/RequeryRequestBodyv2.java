package com.flutterwave.raveandroid.data;

/**
 * Created by hfetuga on 28/06/2018.
 */

public class RequeryRequestBodyv2 {

    String SECKEY;
    String txref;

    public String getSECKEY() {
        return SECKEY;
    }

    public void setSECKEY(String SECKEY) {
        this.SECKEY = SECKEY;
    }

    public String getTxref() {
        return txref;
    }

    public void setTxref(String txref) {
        this.txref = txref;
    }
}

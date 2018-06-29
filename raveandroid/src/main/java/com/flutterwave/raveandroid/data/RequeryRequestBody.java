package com.flutterwave.raveandroid.data;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class RequeryRequestBody {

    String SECKEY;

    public String getTx_Ref() {
        return tx_ref;
    }

    public void setTx_Ref(String tx_ref) {
        this.tx_ref = tx_ref;
    }

    String tx_ref;

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

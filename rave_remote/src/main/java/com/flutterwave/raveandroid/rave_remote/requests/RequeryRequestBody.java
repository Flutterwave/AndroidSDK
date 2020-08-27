package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class RequeryRequestBody {

    String PBFPubKey;
    String flw_ref;
    private String order_ref;
    private String tx_ref;

    public void setTx_ref(String tx_ref) {
        this.tx_ref = tx_ref;
    }

    public void setPBFPubKey(String PBFPubKey) {
        this.PBFPubKey = PBFPubKey;
    }

    public void setFlw_ref(String flw_ref) {
        this.flw_ref = flw_ref;
    }

    public void setOrder_ref(String order_ref) {
        this.order_ref = order_ref;
    }
}

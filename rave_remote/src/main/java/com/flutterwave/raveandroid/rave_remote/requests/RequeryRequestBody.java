package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class RequeryRequestBody {

    private String order_ref;

    private String tx_ref;

    public void setTx_ref(String tx_ref) {
        this.tx_ref = tx_ref;
    }

    public String getPBFPubKey() {
        return PBFPubKey;
    }

    public void setPBFPubKey(String PBFPubKey) {
        this.PBFPubKey = PBFPubKey;
    }

    String PBFPubKey;

    public String getFlw_ref() {
        return flw_ref;
    }

    public void setFlw_ref(String flw_ref) {
        this.flw_ref = flw_ref;
    }

    String flw_ref;

    public String getOrder_ref() {
        return order_ref;
    }

    public void setOrder_ref(String order_ref) {
        this.order_ref = order_ref;
    }
}

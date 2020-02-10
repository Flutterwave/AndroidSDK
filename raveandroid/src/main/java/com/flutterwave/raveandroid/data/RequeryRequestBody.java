package com.flutterwave.raveandroid.data;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class RequeryRequestBody {

    String PBFPubKey;
    String flw_ref;
    private String order_ref;

    public String getPBFPubKey() {
        return PBFPubKey;
    }

    public void setPBFPubKey(String PBFPubKey) {
        this.PBFPubKey = PBFPubKey;
    }

    public String getFlw_ref() {
        return flw_ref;
    }

    public void setFlw_ref(String flw_ref) {
        this.flw_ref = flw_ref;
    }

    public String getOrder_ref() {
        return order_ref;
    }

    public void setOrder_ref(String order_ref) {
        this.order_ref = order_ref;
    }
}

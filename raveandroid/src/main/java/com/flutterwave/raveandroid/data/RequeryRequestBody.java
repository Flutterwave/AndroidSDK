package com.flutterwave.raveandroid.data;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class RequeryRequestBody {

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


}

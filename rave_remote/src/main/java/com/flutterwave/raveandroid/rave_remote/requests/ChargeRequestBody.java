package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by hamzafetuga on 16/07/2017.
 */

public class ChargeRequestBody {
    String PBFPubKey;
    String client;
    String alg;

    public String getPBFPubKey() {
        return PBFPubKey;
    }

    public void setPBFPubKey(String PBFPubKey) {
        this.PBFPubKey = PBFPubKey;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getAlg() {
        return alg;
    }

    public void setAlg(String alg) {
        this.alg = alg;
    }
}
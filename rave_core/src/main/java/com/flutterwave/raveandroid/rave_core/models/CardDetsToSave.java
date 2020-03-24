package com.flutterwave.raveandroid.rave_core.models;

/**
 * Created by hamzafetuga on 25/07/2017.
 */

public class CardDetsToSave {
    String first6;

    public String getFirst6() {
        return first6;
    }

    public String getLast4() {
        return last4;
    }

    String last4;

    public CardDetsToSave(String first6, String last4) {
        this.first6 = first6;
        this.last4 = last4;
    }



}

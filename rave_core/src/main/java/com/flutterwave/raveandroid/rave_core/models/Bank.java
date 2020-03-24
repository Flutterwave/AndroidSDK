package com.flutterwave.raveandroid.rave_core.models;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class Bank {

    String bankname;
    String bankcode;
    boolean internetbanking;

    public String getBankname() {
        return bankname;
    }

    public String getBankcode() {
        return bankcode;
    }

    public boolean isInternetbanking() {
        return internetbanking;
    }

    public void setInternetbanking(boolean internetbanking) {
        this.internetbanking = internetbanking;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }
}
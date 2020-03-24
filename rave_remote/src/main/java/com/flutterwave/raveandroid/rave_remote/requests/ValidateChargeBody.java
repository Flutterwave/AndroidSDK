package com.flutterwave.raveandroid.rave_remote.requests;

/**
 * Created by hamzafetuga on 19/07/2017.
 */

public class ValidateChargeBody {
    String transaction_reference;

    public String getTransactionreference() {
        return transactionreference;
    }

    public void setTransactionreference(String transactionreference) {
        this.transactionreference = transactionreference;
    }

    String transactionreference;
    String PBFPubKey;
    String otp;

    public String getTransaction_reference() {
        return transaction_reference;
    }

    public void setTransaction_reference(String transaction_reference) {
        this.transaction_reference = transaction_reference;
    }

    public String getPBFPubKey() {
        return PBFPubKey;
    }

    public void setPBFPubKey(String PBFPubKey) {
        this.PBFPubKey = PBFPubKey;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}

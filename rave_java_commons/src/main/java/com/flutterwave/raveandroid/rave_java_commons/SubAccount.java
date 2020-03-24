package com.flutterwave.raveandroid.rave_java_commons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubAccount {
    public static final String FLAT="flat";
    public static final String PERCENTAGE="percentage";

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("transaction_split_ratio")
    @Expose
    private String transactionSplitRatio;
    @SerializedName("transaction_charge_type")
    @Expose
    private String transactionChargeType;
    @SerializedName("transaction_charge")
    @Expose
    private String transactionCharge;

    public SubAccount(){

    }

    public SubAccount(String id, String transactionSplitRatio){
        this.id = id;
        this.transactionSplitRatio = transactionSplitRatio;
    }

    public SubAccount(String id, String transactionSplitRatio, String transactionChargeType, String transactionCharge){
        this.id=id;
        this.transactionSplitRatio = transactionSplitRatio;
        this.transactionChargeType = transactionChargeType;
        this.transactionCharge = transactionCharge;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransactionSplitRatio() {
        return transactionSplitRatio;
    }

    public void setTransactionSplitRatio(String transactionSplitRatio) {
        this.transactionSplitRatio = transactionSplitRatio;
    }

    public String getTransactionChargeType() {
        return transactionChargeType;
    }

    public void setTransactionChargeType(String transactionChargeType) {
        this.transactionChargeType = transactionChargeType;
    }

    public String getTransactionCharge() {
        return transactionCharge;
    }

    public void setTransactionCharge(String transactionCharge) {
        this.transactionCharge = transactionCharge;
    }

}

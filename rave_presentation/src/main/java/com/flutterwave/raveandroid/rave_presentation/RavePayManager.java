package com.flutterwave.raveandroid.rave_presentation;

abstract public class RavePayManager  {
    protected String email;
    protected double amount = -1;
    protected String publicKey;
    protected String encryptionKey;
    protected String txRef;
    protected String narration = "";
    protected String currency = "NGN";
    protected String country = "NG";
    protected String fName = "";
    protected String lName = "";
    protected String fullName = null;
    protected String meta = "";
    protected String subAccounts = "";
    protected String payment_plan;
    protected boolean isPreAuth = false;
    protected String phoneNumber = "";
    protected boolean showStagingLabel = true;
    protected boolean displayFee = true;
    protected boolean staging = true;
    protected boolean isPermanent = false;
    protected int duration = 0;
    protected int frequency = 0;

    public abstract RavePayManager initialize();

    public String getEmail() {
        return email;
    }

    public double getAmount() {
        return amount;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public String getTxRef() {
        return txRef;
    }

    public String getNarration() {
        return narration;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountry() {
        return country;
    }

    public String getFullName() {
        return fullName != null ? fullName : fName + " " + lName;
    }

    public String getMeta() {
        return meta;
    }

    public String getSubAccounts() {
        return subAccounts;
    }

    public String getPayment_plan() {
        return payment_plan;
    }

    public boolean isPreAuth() {
        return isPreAuth;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean isDisplayFee() {
        return displayFee;
    }

    public boolean isStaging() {
        return staging;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public int getDuration() {
        return duration;
    }

    public int getFrequency() {
        return frequency;
    }
}

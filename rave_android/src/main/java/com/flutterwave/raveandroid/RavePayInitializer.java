package com.flutterwave.raveandroid;

import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;

import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by hamzafetuga on 21/07/2017.
 */

@Parcel
public class RavePayInitializer {
    private final ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();
    private boolean isPhoneEditable = true;
    String phoneNumber = "";
    private boolean saveCardFeatureAllowed = true;
    private boolean usePhoneAndEmailSuppliedToSaveCards = true;
    String email;
    double amount;
    String publicKey;
    String encryptionKey;
    String txRef;
    String narration;
    String currency;
    String country;
    String barterCountry;
    String fName;
    String lName;
    String meta;
    String subAccounts;
    String payment_plan;
    int theme;
    boolean staging = true;
    boolean isPreAuth = false;
    boolean displayFee = true;
    boolean showStagingLabel;
    boolean isPermanent;
    int frequency;
    int duration;

    public RavePayInitializer(String email, double amount, String publicKey,
                              String encryptionKey, String txRef, String narration,
                              String currency, String country, String barterCountry, String fName,
                              String lName, int theme, String phoneNumber,
                              boolean isPhoneEditable, boolean saveCardFeatureAllowed, boolean usePhoneAndEmailSuppliedToSaveCards,
                              boolean isPermanent, int duration, int frequency,
                              boolean staging, String meta, String subAccounts, String payment_plan, boolean isPreAuth,
                              boolean showStagingLabel, boolean displayFee, ArrayList<Integer> orderedPaymentTypesList) {
        this.email = email;
        this.amount = amount;
        this.publicKey = publicKey;
        this.encryptionKey = encryptionKey;
        this.txRef = txRef;
        this.narration = narration;
        this.currency = currency;
        this.country = country;
        this.barterCountry = barterCountry;
        this.fName = fName;
        this.lName = lName;
        this.isPermanent = isPermanent;
        this.duration = duration;
        this.frequency = frequency;
        this.theme = theme;
        this.staging = staging;
        this.meta = meta;
        this.subAccounts = subAccounts;
        this.payment_plan = payment_plan;
        this.isPreAuth = isPreAuth;
        this.phoneNumber = phoneNumber;
        this.isPhoneEditable = isPhoneEditable;
        this.saveCardFeatureAllowed = saveCardFeatureAllowed;
        this.usePhoneAndEmailSuppliedToSaveCards = usePhoneAndEmailSuppliedToSaveCards;
        this.showStagingLabel = showStagingLabel;
        this.displayFee = displayFee;
        if (!orderedPaymentTypesList.isEmpty())
            this.orderedPaymentTypesList.addAll(orderedPaymentTypesList);
        else this.orderedPaymentTypesList.add(RaveConstants.PAYMENT_TYPE_CARD);
    }

    public RavePayInitializer() {
    }

    public boolean getShowStagingLabel() {
        return showStagingLabel;
    }

    public void showStagingLabel(boolean showStagingLabel) {
        this.showStagingLabel = showStagingLabel;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getSubAccount(){return subAccounts;}

    public void setSubAccount(String subAccounts){this.subAccounts=subAccounts;}

    public boolean isStaging() {
        return staging;
    }

    public ArrayList<Integer> getOrderedPaymentTypesList() {
        return orderedPaymentTypesList;
    }

    public void setStaging(boolean staging) {
        this.staging = staging;
    }

    public int getTheme() {
        return theme;
    }

    public void setTheme(int theme) {
        this.theme = theme;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public String getTxRef() {
        return txRef;
    }

    public void setTxRef(String txRef) {
        this.txRef = txRef;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBarterCountry() {
        return barterCountry;
    }

    public void setBarterCountry(String barterCountry) {
        this.barterCountry = barterCountry;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getPayment_plan() {
        return payment_plan;
    }

    public void setPayment_plan(String payment_plan) {
        this.payment_plan = payment_plan;
    }

    public void setPreAuth(boolean isPreAuth) {
        this.isPreAuth = isPreAuth;
    }

    public boolean getIsPreAuth() { return  isPreAuth; }

    public boolean getIsDisplayFee() {
        return displayFee;
    }

    public boolean getIsPhoneEditable() {
        return isPhoneEditable;
    }

    public void setIsDisplayFee(boolean displayFee) {
        this.displayFee = displayFee;
    }

    public boolean getIsPermanent() {
        return isPermanent;
    }

    public void setPermanent(boolean permanent) {
        this.isPermanent = permanent;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isSaveCardFeatureAllowed() {
        return saveCardFeatureAllowed;
    }

    public boolean isUsePhoneAndEmailSuppliedToSaveCards() {
        return usePhoneAndEmailSuppliedToSaveCards;
    }
}

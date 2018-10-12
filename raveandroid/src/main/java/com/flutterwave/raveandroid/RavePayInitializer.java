package com.flutterwave.raveandroid;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by hamzafetuga on 21/07/2017.
 */

@Parcel
public class RavePayInitializer {
    String email;
    double amount;
    String publicKey;
    String secretKey;
    String txRef;
    String narration;
    String currency;
    String country;
    String fName;
    String lName;
    String meta;
    String subAccounts;
    String payment_plan;
    boolean withMpesa = false;
    boolean withCard = true;
    boolean withAccount = true;
    boolean withGHMobileMoney = false;
    int theme;
    boolean allowSaveCard;
    boolean staging = true;

    public RavePayInitializer(String email, double amount, String publicKey,
                              String secretKey, String txRef, String narration,
                              String currency, String country, String fName,
                              String lName, boolean withCard,
                              boolean withAccount, boolean withMpesa, boolean withGHMobileMoney, int theme,
                              boolean staging, boolean allowSaveCard, String meta, String subAccounts, String payment_plan) {
        this.email = email;
        this.amount = amount;
        this.publicKey = publicKey;
        this.secretKey = secretKey;
        this.txRef = txRef;
        this.narration = narration;
        this.currency = currency;
        this.country = country;
        this.fName = fName;
        this.lName = lName;
        this.withAccount = withAccount;
        this.withGHMobileMoney = withGHMobileMoney;
        this.withMpesa = withMpesa;
        this.withCard = withCard;
        this.theme = theme;
        this.staging = staging;
        this.allowSaveCard = allowSaveCard;
        this.meta = meta;
        this.subAccounts = subAccounts;
        this.payment_plan = payment_plan;
    }

    public RavePayInitializer() {
    }

    public boolean isWithMpesa() {
        return withMpesa;
    }

    public void setWithMpesa(boolean withMpesa) {
        this.withMpesa = withMpesa;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

    public String getSubAccount(){return subAccounts;}

    public void setSubAccount(String subAccounts){this.subAccounts=subAccounts;}

    public boolean isAllowSaveCard() {
        return allowSaveCard;
    }

    public void setAllowSaveCard(boolean allowSaveCard) {
        this.allowSaveCard = allowSaveCard;
    }

    public boolean isStaging() {
        return staging;
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

    public boolean isWithAccount() {
        return withAccount;
    }

    public void setWithAccount(boolean withAccount) {
        this.withAccount = withAccount;
    }

    public boolean isWithGHMobileMoney() {
        return withGHMobileMoney;
    }

    public void setWithGHMobileMoney(boolean withGHMobileMoney) {
        this.withGHMobileMoney = withGHMobileMoney;
    }

    public boolean isWithCard() {
        return withCard;
    }

    public void setWithCard(boolean withCard) {
        this.withCard = withCard;
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

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
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
}

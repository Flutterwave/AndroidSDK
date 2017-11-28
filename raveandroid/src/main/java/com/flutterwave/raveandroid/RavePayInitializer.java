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
    boolean withCard = true;
    boolean withAccount = true;
    int theme;
    boolean allowSaveCard;
    boolean staging = true;

    public RavePayInitializer(String email, double amount, String publicKey,
                              String secretKey, String txRef, String narration,
                              String currency, String country, String fName,
                              String lName, boolean withCard,
                              boolean withAccount, int theme,
                              boolean staging, boolean allowSaveCard, String meta) {
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
        this.withCard = withCard;
        this.theme = theme;
        this.staging = staging;
        this.allowSaveCard = allowSaveCard;
        this.meta = meta;
    }

    public RavePayInitializer() {
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }

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


}

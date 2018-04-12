package com.flutterwave.raveandroid;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public class Payload
{
    public String getCardBIN() {
        return cardBIN;
    }

    private String cardBIN;

    public Payload(List<Meta> meta, String narration,
                   String expirymonth, String PBFPubKey, String IP, String lastname,
                   String firstname, String currency, String country, String amount,
                   String email, String expiryyear, String cvv, String device_fingerprint,
                   String cardno, String txRef) {
        this.meta = meta;
        this.narration = narration;
        this.expirymonth = expirymonth;
        this.PBFPubKey = PBFPubKey;
        this.IP = IP;
        this.lastname = lastname;
        this.firstname = firstname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.expiryyear = expiryyear;
        this.cvv = cvv;
        this.device_fingerprint = device_fingerprint;
        this.cardno = cardno;
        this.txRef = txRef;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
    }


    public String getBillingcity() {
        return billingcity;
    }

    public void setBillingcity(String billingcity) {
        this.billingcity = billingcity;
    }

    public String getBillingaddress() {
        return billingaddress;
    }

    public void setBillingaddress(String billingaddress) {
        this.billingaddress = billingaddress;
    }

    public String getBillingstate() {
        return billingstate;
    }

    public void setBillingstate(String billingstate) {
        this.billingstate = billingstate;
    }

    public String getBillingcountry() {
        return billingcountry;
    }

    public void setBillingcountry(String billingcountry) {
        this.billingcountry = billingcountry;
    }

    private String billingcity;
    private String billingaddress;
    private String billingstate;
    private String billingcountry;

    public Payload(List<Meta> meta, String narration, String IP, String accountnumber, String accountbank,
                   String lastname, String firstname, String currency, String country, String amount,
                   String email, String device_fingerprint, String txRef, String PBFPubKey,
                   String billingaddress, String billingcity, String billingstate, String billingzip, String billingcountry) {
        this.meta = meta;
        this.narration = narration;
        this.IP = IP;
        this.accountnumber = accountnumber;
        this.accountbank = accountbank;
        this.lastname = lastname;
        this.firstname = firstname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.device_fingerprint = device_fingerprint;
        this.txRef = txRef;
        this.PBFPubKey = PBFPubKey;
        this.billingaddress = billingaddress;
        this.billingstate = billingstate;
        this.billingcity = billingcity;
        this.billingcountry = billingcountry;
        this.billingzip = billingzip;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
    }


    public Payload(List<Meta> meta, String narration, String IP, String accountnumber, String accountbank,
                   String lastname, String firstname, String currency, String country, String amount,
                   String email, String device_fingerprint, String txRef, String PBFPubKey) {
        this.meta = meta;
        this.narration = narration;
        this.IP = IP;
        this.accountnumber = accountnumber;
        this.accountbank = accountbank;
        this.lastname = lastname;
        this.firstname = firstname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.device_fingerprint = device_fingerprint;
        this.txRef = txRef;
        this.PBFPubKey = PBFPubKey;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
    }

    public String getToken() {
        return token;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    private String passcode;



    public void setToken(String token) {
        this.token = token;
    }

    String token;

    public List<Meta> getMeta() {
        return meta;
    }

    public void setMeta(List<Meta> meta) {
        this.meta = meta;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    private String pin;

    private String redirect_url = "https://rave-webhook.herokuapp.com/receivepayment";

    public String getPBFSecKey() {
        return PBFSecKey;
    }

    public void setPBFSecKey(String PBFSecKey) {
        this.PBFSecKey = PBFSecKey;
    }

    public void setSECKEY(String SECKEY) {
        this.SECKEY = SECKEY;
    }

    String SECKEY;

    private String PBFSecKey;

    @SerializedName("suggested_auth")
    private String suggestedAuth;

    private List<Meta> meta;

    public String getBillingzip() {
        return billingzip;
    }

    public void setBillingzip(String billingzip) {
        this.billingzip = billingzip;
    }

    private String billingzip;

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    private String payment_type;

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    private String phonenumber;

    private String narration;

    public void setIs_internet_banking(String is_internet_banking) {
        this.is_internet_banking = is_internet_banking;
    }

    private String is_internet_banking;

    private String expirymonth;

    private String PBFPubKey;

    private String IP;

    private String accountnumber;

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getAccountbank() {
        return accountbank;
    }

    public void setAccountbank(String accountbank) {
        this.accountbank = accountbank;
    }

    private String accountbank;

    private String lastname;

    private String firstname;

    private String currency;

    private String country;

    private String amount;

    private String email;

    private String expiryyear;

    private String cvv;

    private String device_fingerprint;

    private String cardno;

    private String txRef;

    public String getExpirymonth ()
    {
        return expirymonth;
    }

    public void setExpirymonth (String expirymonth)
    {
        this.expirymonth = expirymonth;
    }

    public String getPBFPubKey ()
    {
        return PBFPubKey;
    }

    public void setPBFPubKey (String PBFPubKey)
    {
        this.PBFPubKey = PBFPubKey;
    }

    public String getIP ()
    {
        return IP;
    }

    public void setIP (String IP)
    {
        this.IP = IP;
    }

    public String getLastname ()
    {
        return lastname;
    }

    public void setLastname (String lastname)
    {
        this.lastname = lastname;
    }

    public String getFirstname ()
    {
        return firstname;
    }

    public void setFirstname (String firstname)
    {
        this.firstname = firstname;
    }

    public String getCurrency ()
    {
        return currency;
    }

    public void setCurrency (String currency)
    {
        this.currency = currency;
    }

    public String getCountry ()
    {
        return country;
    }

    public void setCountry (String country)
    {
        this.country = country;
    }

    public String getAmount ()
    {
        return amount;
    }

    public void setAmount (String amount)
    {
        this.amount = amount;
    }

    public String getEmail ()
    {
        return email;
    }

    public void setEmail (String email)
    {
        this.email = email;
    }

    public String getExpiryyear ()
    {
        return expiryyear;
    }

    public void setExpiryyear (String expiryyear)
    {
        this.expiryyear = expiryyear;
    }

    public String getCvv ()
    {
        return cvv;
    }

    public void setCvv (String cvv)
    {
        this.cvv = cvv;
    }

    public String getDevice_fingerprint ()
    {
        return device_fingerprint;
    }

    public void setDevice_fingerprint (String device_fingerprint)
    {
        this.device_fingerprint = device_fingerprint;
    }

    public String getCardno ()
    {
        return cardno;
    }

    public void setCardno (String cardno)
    {
        this.cardno = cardno;
    }

    public String getTxRef ()
    {
        return txRef;
    }

    public void setTxRef (String txRef)
    {
        this.txRef = txRef;
    }

    public void setSuggestedAuth(String suggestedAuth) {
        this.suggestedAuth = suggestedAuth;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [expirymonth = "+expirymonth+", PBFPubKey = "+PBFPubKey+", IP = "+IP+", lastname = "+lastname+", firstname = "+firstname+", currency = "+currency+", country = "+country+", amount = "+amount+", email = "+email+", expiryyear = "+expiryyear+", cvv = "+cvv+", device_fingerprint = "+device_fingerprint+", cardno = "+cardno+", txRef = "+txRef+"]";
    }

    public void setCardBIN(String cardBIN) {
        this.cardBIN = cardBIN;
    }
}


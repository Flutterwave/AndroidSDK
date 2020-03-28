package com.flutterwave.raveandroid.rave_java_commons;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public class Payload
{
    private Boolean is_permanent;
    private Integer duration;
    private Integer frequency;

    private boolean is_ussd;
    private String orderRef;
    private String is_barter;
    private String card_hash;

    public String getCardBIN() {
        return cardBIN;
    }

    private String cardBIN;

    private boolean is_us_bank_charge;
    private boolean is_saved_card_charge;

    private boolean is_uk_bank_charge2;

    private String remember_device_mobile_key;

    private String device_key;
    private String otp;

    // Constructor for saved card charge
    public Payload(List<Meta> meta,
                   List<SubAccount> subaccounts, String narration, String PBFPubKey, String IP,
                   String lastname, String firstname, String currency, String country, String amount,
                   String email, String device_fingerprint, String txRef, Boolean
                           is_saved_card_charge, String phonenumber) {
        this.narration = narration;
        this.PBFPubKey = PBFPubKey;
        this.IP = IP;
        this.subaccounts = subaccounts;
        this.lastname = lastname;
        this.firstname = firstname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.device_fingerprint = device_fingerprint;
        this.txRef = txRef;
        this.is_saved_card_charge = is_saved_card_charge;
        this.phonenumber = phonenumber;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;
    }

    public String getOtp() {
        return otp;
    }

    private String remember_device_email;

    private String charge_type;

    public String getRemember_device_mobile_key() {
        return remember_device_mobile_key;
    }

    private String bvn;

    public void setRemember_device_mobile_key(String remember_device_mobile_key) {
        this.remember_device_mobile_key = remember_device_mobile_key;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getDevice_key() {
        return device_key;
    }


    public String getRemember_device_email() {
        return remember_device_email;
    }

    public void setRemember_device_email(String remember_device_email) {
        this.remember_device_email = remember_device_email;
    }

    public String getIs_remembered() {
        return is_remembered;
    }

    public void setIs_remembered(String is_remembered) {
        this.is_remembered = is_remembered;
    }

    private String is_remembered;

    private String payment_plan;

    public String getIs_mpesa() {
        return is_mpesa;
    }

    public void setIs_mpesa(String is_mpesa) {
        this.is_mpesa = is_mpesa;
    }

    public String getIs_mpesa_lipa() {
        return is_mpesa_lipa;
    }

    public void setIs_mpesa_lipa(String is_mpesa_lipa) {
        this.is_mpesa_lipa = is_mpesa_lipa;
    }

    private String is_mpesa;

    private String is_mpesa_lipa;

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
    }

    public Payload(List<Meta> meta, List<SubAccount> subaccounts, String narration,
                   String expirymonth, String PBFPubKey, String IP, String lastname,
                   String firstname, String currency, String country, String amount,
                   String email, String expiryyear, String cvv, String device_fingerprint,
                   String cardno, String txRef) {
        this.narration = narration;
        this.expirymonth = expirymonth;
        this.PBFPubKey = PBFPubKey;
        this.IP = IP;
        this.subaccounts = subaccounts;
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
        this.meta = meta;

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

    public String getBVN() {
        return bvn;
    }

    public void setBVN(String bvn){
        this.bvn = bvn;
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

    public Payload(List<Meta> meta, List<SubAccount> subaccounts, String narration, String IP, String accountnumber, String accountbank,
                   String lastname, String firstname, String currency, String country, String amount,
                   String email, String device_fingerprint, String txRef, String PBFPubKey,
                   String billingaddress, String billingcity, String billingstate, String billingzip, String billingcountry) {
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
        this.subaccounts = subaccounts;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;

    }

    public Payload(String phonenumber,
                   List<Meta> meta,
                   List<SubAccount> subaccounts,
                   String narration,
                   String IP,
                   String lastname,
                   String firstname,
                   String currency,
                   String country,
                   String amount,
                   String email,
                   String device_fingerprint,
                   String txRef,
                   String PBFPubKey) {
        this.meta = meta;
        this.subaccounts = subaccounts;
        this.narration = narration;
        this.IP = IP;
        this.phonenumber = phonenumber;
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
        this.meta = meta;

    }

    public Payload(List<Meta> meta, List<SubAccount> subaccounts, String narration, String IP, String accountnumber, String accountbank,
                   String lastname, String firstname, String currency, String country, String amount,
                   String email, String device_fingerprint, String txRef, String PBFPubKey, String bvn, boolean is_us_bank_charge) {
        this.meta = meta;
        this.subaccounts = subaccounts;
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
        this.bvn = bvn;
        this.is_us_bank_charge = is_us_bank_charge;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;

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

    public String getPayment_plan() {
        return payment_plan;
    }

    public void setPayment_plan(String payment_plan) {
        this.payment_plan = payment_plan;
    }

    public String getCharge_type() {
        return charge_type;
    }

    public void setCharge_type(String charge_type){
        this.charge_type = charge_type;
    }

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

    private String redirect_url = RaveConstants.RAVE_3DS_CALLBACK;

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

    private List<SubAccount> subaccounts;

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

    private boolean is_mobile_money_franco;

    private String payment_type;

    public boolean getIs_mobile_money_franco() {
        return is_mobile_money_franco;
    }

    public void setIs_mobile_money_franco(boolean is_mobile_money_franco) {
        this.is_mobile_money_franco = is_mobile_money_franco;
    }

    private String is_sa_call_pay;

    public void setIs_sa_call_pay(String is_sa_call_pay){
        this.is_sa_call_pay = is_sa_call_pay;
    }

    private String getIs_sa_call_pay(){
        return is_sa_call_pay;
    }

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

    private String accountname;

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

    public String is_mobile_money_gh;

    public String is_mobile_money_ug;

    public void setNetwork(String network) {
        this.network = network;
    }

    public String network;

    public String voucher;

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public void setIs_mobile_money_gh(String is_mobile_money_gh) {
        this.is_mobile_money_gh = is_mobile_money_gh;
    }

    public void setIs_mobile_money_ug(String is_mobile_money_ug) {
        this.is_mobile_money_ug = is_mobile_money_ug;
    }

    public boolean isIs_us_bank_charge() {
        return is_us_bank_charge;
    }

    public void setIs_us_bank_charge(boolean is_us_bank_charge) {
        this.is_us_bank_charge = is_us_bank_charge;
    }

    public boolean is_bank_transfer;

    public String getCard_hash() {
        return card_hash;
    }

    public void setCard_hash(String card_hash) {
        this.card_hash = card_hash;
    }

    public void setIs_bank_transfer(boolean is_bank_transfer) {
        this.is_bank_transfer = is_bank_transfer;
    }

    public boolean getIs_permanent() {
        return is_permanent;
    }

    public void setIs_permanent(boolean is_permanent) {
        this.is_permanent = is_permanent;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public boolean getIs_ussd() {
        return is_ussd;
    }

    public void setIs_ussd(boolean is_ussd) {
        this.is_ussd = is_ussd;
    }

    public String getOrderRef() {
        return orderRef;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;

    }

    public boolean getIs_uk_bank_charge() {
        return is_uk_bank_charge2;
    }

    public void setIs_uk_bank_charge2(boolean is_uk_bank_charge2) {
        this.is_uk_bank_charge2 = is_uk_bank_charge2;
    }

    public String getAccountname() {
        return accountname;
    }

    public void setAccountname(String accountname) {
        this.accountname = accountname;
    }

    public String getIs_barter() {
        return is_barter;
    }

    public void setIs_barter(String is_barter) {
        this.is_barter = is_barter;
    }

    public void setSavedCardDetails(SavedCard savedCard) {
        is_saved_card_charge = true;
        this.setCardBIN(savedCard.getMasked_pan().substring(0, 6));
        this.setCard_hash(savedCard.getCardHash());
        this.setDevice_key(phonenumber);
    }
}


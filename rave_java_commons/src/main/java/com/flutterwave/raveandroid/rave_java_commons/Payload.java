package com.flutterwave.raveandroid.rave_java_commons;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.AVS_NOAUTH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public class Payload {
    //Todo: Complete payment fields harmonising
    public String is_mobile_money_gh;
    public String is_mobile_money_ug;
    public String network;
    public String voucher;
    public String bvn;
    public boolean is_bank_transfer;
    String token;
    String SECKEY;
    private Boolean is_permanent;
    private Integer duration;
    private Integer frequency;
    private String billingcity;
    private String billingaddress;
    private String billingstate;
    private String billingcountry;
    private boolean is_ussd;
    private String orderRef;
    private String order_id;
    private String is_barter;
    private String card_hash;
    private String cardBIN;
    private boolean is_us_bank_charge;
    private boolean is_saved_card_charge;
    private boolean preauthorize;//Todo: test preauth compatibility
    private boolean is_uk_bank_charge2;
    private String remember_device_mobile_key;
    private String device_key;
    private String otp;
    private String is_internet_banking;
    private String expiry_month;
    private String PBFPubKey;
    private String client_ip;
    private String account_number;
    private String account_name;
    private String phone_number;
    private String narration;
    private String account_bank;
    private String fullname;
    private String currency;
    private String country;
    private String amount;
    private String email;
    private String expiry_year;
    private String cvv;
    private String device_fingerprint;
    private String card_number;
    private String tx_ref;
    private String is_sa_call_pay;
    private boolean is_mobile_money_franco;
    private String payment_type;
    private String is_remembered;
    private String payment_plan;
    private String remember_device_email;
    private String charge_type;
    private String is_mpesa;
    private String is_mpesa_lipa;
    private String passcode;
    private String PBFSecKey;
    private String redirect_url = RaveConstants.RAVE_3DS_CALLBACK;
    @SerializedName("suggested_auth")
    private String suggestedAuth;
    private List<Meta> meta;
    private List<SubAccount> subaccounts;
    private String billingzip;
    private Authorization authorization;

    // Constructor for saved card charge
    public Payload(List<Meta> meta,
                   List<SubAccount> subaccounts, String narration, String PBFPubKey, String IP,
                   String fullname, String currency, String country, String amount,
                   String email, String device_fingerprint, String tx_ref, Boolean
                           is_saved_card_charge, String phone_number) {
        this.narration = narration;
        this.PBFPubKey = PBFPubKey;
        this.client_ip = IP;
        this.subaccounts = subaccounts;
        this.fullname = fullname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.device_fingerprint = device_fingerprint;
        this.tx_ref = tx_ref;
        this.is_saved_card_charge = is_saved_card_charge;
        this.phone_number = phone_number;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;
    }


    public Payload(List<Meta> meta, List<SubAccount> subaccounts, String narration,
                   String expiry_month, String PBFPubKey, String IP,
                   String fullname, String currency, String country, String amount,
                   String email, String expiry_year, String cvv, String device_fingerprint,
                   String card_number, String tx_ref) {
        this.narration = narration;
        this.expiry_month = expiry_month;
        this.PBFPubKey = PBFPubKey;
        this.client_ip = IP;
        this.subaccounts = subaccounts;
        this.fullname = fullname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.expiry_year = expiry_year;
        this.cvv = cvv;
        this.device_fingerprint = device_fingerprint;
        this.card_number = card_number;
        this.tx_ref = tx_ref;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;

    }

    public Payload(List<Meta> meta, List<SubAccount> subaccounts, String narration, String IP, String accountnumber, String accountbank,
                   String fullname, String currency, String country, String amount,
                   String email, String device_fingerprint, String tx_ref, String PBFPubKey,
                   String billingaddress, String billingcity, String billingstate, String billingzip, String billingcountry) {
        this.narration = narration;
        this.client_ip = IP;
        this.account_number = accountnumber;
        this.account_bank = accountbank;
        this.fullname = fullname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.device_fingerprint = device_fingerprint;
        this.tx_ref = tx_ref;
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

    public Payload(String phone_number,
                   List<Meta> meta,
                   List<SubAccount> subaccounts,
                   String narration,
                   String IP,
                   String fullname,
                   String currency,
                   String country,
                   String amount,
                   String email,
                   String device_fingerprint,
                   String tx_ref,
                   String PBFPubKey) {
        this.meta = meta;
        this.subaccounts = subaccounts;
        this.narration = narration;
        this.client_ip = IP;
        this.phone_number = phone_number;
        this.fullname = fullname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.device_fingerprint = device_fingerprint;
        this.tx_ref = tx_ref;
        this.PBFPubKey = PBFPubKey;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;

    }

    public Payload(List<Meta> meta, List<SubAccount> subaccounts, String narration, String IP, String accountnumber, String accountbank,
                   String fullname, String currency, String country, String amount,
                   String email, String device_fingerprint, String tx_ref, String PBFPubKey, String bvn, boolean is_us_bank_charge) {
        this.meta = meta;
        this.subaccounts = subaccounts;
        this.narration = narration;
        this.client_ip = IP;
        this.account_number = accountnumber;
        this.account_bank = accountbank;
        this.fullname = fullname;
        this.currency = currency;
        this.country = country;
        this.amount = amount;
        this.email = email;
        this.device_fingerprint = device_fingerprint;
        this.tx_ref = tx_ref;
        this.PBFPubKey = PBFPubKey;
        this.bvn = bvn;
        this.is_us_bank_charge = is_us_bank_charge;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;

    }

    public String getCardBIN() {
        return cardBIN;
    }

    public void setCardBIN(String cardBIN) {
        this.cardBIN = cardBIN;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getRemember_device_mobile_key() {
        return remember_device_mobile_key;
    }

    public void setRemember_device_mobile_key(String remember_device_mobile_key) {
        this.remember_device_mobile_key = remember_device_mobile_key;
    }

    public String getDevice_key() {
        return device_key;
    }

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
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

    public void setBVN(String bvn) {
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPasscode() {
        return passcode;
    }

    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    public String getPayment_plan() {
        return payment_plan;
    }

    public void setPayment_plan(String payment_plan) {
        this.payment_plan = payment_plan;
    }

    public String getCharge_type() {
        return charge_type;
    }

    public void setCharge_type(String charge_type) {
        this.charge_type = charge_type;
    }

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

    public String getPBFSecKey() {
        return PBFSecKey;
    }

    public void setPBFSecKey(String PBFSecKey) {
        this.PBFSecKey = PBFSecKey;
    }

    public void setSECKEY(String SECKEY) {
        this.SECKEY = SECKEY;
    }

    public String getBillingzip() {
        return billingzip;
    }

    public void setBillingzip(String billingzip) {
        this.billingzip = billingzip;
    }

    public String getPayment_type() {
        return payment_type;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public boolean getIs_mobile_money_franco() {
        return is_mobile_money_franco;
    }

    public void setIs_mobile_money_franco(boolean is_mobile_money_franco) {
        this.is_mobile_money_franco = is_mobile_money_franco;
    }

    private String getIs_sa_call_pay() {
        return is_sa_call_pay;
    }

    public void setIs_sa_call_pay(String is_sa_call_pay) {
        this.is_sa_call_pay = is_sa_call_pay;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setIs_internet_banking(String is_internet_banking) {
        this.is_internet_banking = is_internet_banking;
    }

    public String getAccountnumber() {
        return account_number;
    }

    public void setAccountnumber(String accountnumber) {
        this.account_number = accountnumber;
    }

    public String getAccount_bank() {
        return account_bank;
    }

    public void setAccount_bank(String account_bank) {
        this.account_bank = account_bank;
    }

    public String getExpiry_month() {
        return expiry_month;
    }

    public void setExpiry_month(String expiry_month) {
        this.expiry_month = expiry_month;
    }

    public String getPBFPubKey() {
        return PBFPubKey;
    }

    public void setPBFPubKey(String PBFPubKey) {
        this.PBFPubKey = PBFPubKey;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getExpiry_year() {
        return expiry_year;
    }

    public void setExpiry_year(String expiry_year) {
        this.expiry_year = expiry_year;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public String getDevice_fingerprint() {
        return device_fingerprint;
    }

    public void setDevice_fingerprint(String device_fingerprint) {
        this.device_fingerprint = device_fingerprint;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getTx_ref() {
        return tx_ref;
    }

    public void setTx_ref(String tx_ref) {
        this.tx_ref = tx_ref;
    }

    public void setSuggestedAuth(String suggestedAuth) {
        this.suggestedAuth = suggestedAuth;
    }

    @Override
    public String toString() {
        return "ClassPojo [expirymonth = " + expiry_month + ", PBFPubKey = " + PBFPubKey + ", IP = " + client_ip + ", fullname = " + fullname + ", currency = " + currency + ", country = " + country + ", amount = " + amount + ", email = " + email + ", expiryyear = " + expiry_year + ", cvv = " + cvv + ", device_fingerprint = " + device_fingerprint + ", cardno = " + card_number + ", txRef = " + tx_ref + "]";
    }

    public void setNetwork(String network) {
        this.network = network;
    }

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
        return account_name;
    }

    public void setAccountname(String accountname) {
        this.account_name = accountname;
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
        this.setDevice_key(phone_number);
    }

    public void setPreauthorize(boolean preauthorize) {
        this.preauthorize = preauthorize;
    }

    public void setPin(String pin) {
        this.authorization = new Authorization(PIN, pin);
    }

    public void setAddressDetails(AddressDetails address) {
        this.authorization = new Authorization(AVS_NOAUTH, address);
    }

    private class Authorization {
        String mode;
        String pin;
        String address;
        String city;
        String state;
        String zipcode;
        String country;

        Authorization(String mode, String pin) {
            this.mode = mode;
            this.pin = pin;
        }

        Authorization(String mode, AddressDetails addressDetails) {
            this.mode = mode;
            if (addressDetails != null) {
                address = addressDetails.getStreetAddress();
                city = addressDetails.getCity();
                state = addressDetails.getState();
                zipcode = addressDetails.getZipCode();
                country = addressDetails.getCountry();
            }
        }
    }
}


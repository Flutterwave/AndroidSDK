package com.flutterwave.raveandroid.rave_java_commons;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;

import java.util.ArrayList;
import java.util.List;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.AVS_NOAUTH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN;

/**
 * Created by hamzafetuga on 05/07/2017.
 */

public class Payload {
    public String network;
    public String voucher;
    public String bvn;
    private Boolean is_permanent;
    private Integer duration;
    private Integer frequency;
    private String orderRef;
    private String order_id;
    private String is_barter;
    private String card_hash;
    private String cardBIN;
    private Boolean is_saved_card_charge;
    private Boolean preauthorize;//Todo: test preauth compatibility
    private String device_key;
    private String otp;
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
    private String txRef;// Required for v2 charges
    private String payment_type;
    private String payment_plan;
    private String passcode;
    private String redirect_url = RaveConstants.RAVE_3DS_CALLBACK;
    private List<Meta> meta;// Todo: match meta to v3 standard
    private List<SubAccount> subaccounts;
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
        this.txRef = tx_ref;
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
        this.txRef = tx_ref;

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
        this.txRef = tx_ref;
        this.PBFPubKey = PBFPubKey;

        if (meta == null) {
            meta = new ArrayList<>();
        }

        meta.add(new Meta("sdk", "android"));
        this.meta = meta;

    }

    public Payload(List<Meta> meta, List<SubAccount> subaccounts, String narration, String IP, String accountnumber, String accountbank,
                   String fullname, String currency, String country, String amount,
                   String email, String device_fingerprint, String tx_ref, String PBFPubKey, String bvn) {
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
        this.txRef = tx_ref;
        this.PBFPubKey = PBFPubKey;
        this.bvn = bvn;

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

    public void setDevice_key(String device_key) {
        this.device_key = device_key;
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

    public List<Meta> getMeta() {
        return meta;
    }

    public void setMeta(List<Meta> meta) {
        this.meta = meta;
    }

    public void setPayment_type(String payment_type) {
        this.payment_type = payment_type;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getAccountnumber() {
        return account_number;
    }

    public void setAccountnumber(String accountnumber) {
        this.account_number = accountnumber;
    }

    public void setAccount_bank(String account_bank) {
        this.account_bank = account_bank;
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

    public String getDevice_fingerprint() {
        return device_fingerprint;
    }

    public void setDevice_fingerprint(String device_fingerprint) {
        this.device_fingerprint = device_fingerprint;
    }

    public String getCard_number() {
        return card_number;
    }

    public String getTx_ref() {
        return tx_ref;
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

    public String getCard_hash() {
        return card_hash;
    }

    public void setCard_hash(String card_hash) {
        this.card_hash = card_hash;
    }

    public void setIs_permanent(Boolean is_permanent) {
        this.is_permanent = is_permanent;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public void setOrderRef(String orderRef) {
        this.orderRef = orderRef;

    }

    public void setAccountname(String accountname) {
        this.account_name = accountname;
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

    public void setPreauthorize(Boolean preauthorize) {
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


package com.flutterwave.raveandroid;

import com.flutterwave.raveandroid.responses.SubAccount;

import java.util.List;

public class PayloadBuilder {
    private String expirymonth;
    private String pbfPubKey;
    private String ip;
    private String lastname;
    private String firstname;
    private String currency = "NGN";
    private String country = "NG";
    private String amount;
    private String email;
    private String expiryyear;
    private String cvv;
    private String device_fingerprint;
    private String cardno;
    private String payment_plan;
    private String network;
    private String bvn;
    private String voucher;
    private boolean isPreAuth = false;
    private boolean is_us_bank_charge = false;

    public PayloadBuilder setIs_mobile_money_gh(String is_mobile_money_gh) {
        this.is_mobile_money_gh = is_mobile_money_gh;
        return this;
    }

    public PayloadBuilder setIs_mobile_money_ug(String is_mobile_money_ug) {
        this.is_mobile_money_ug = is_mobile_money_ug;
        return this;
    }

    private String is_mobile_money_gh;
    private String is_mobile_money_ug;

    private String phonenumber;

    private String txRef;
    private String meta = "";
    private String subAccounts = "";

    public PayloadBuilder setCustomer_phone(String customer_phone) {
        this.customer_phone = customer_phone;
        return this;
    }

    private String customer_phone;
    private String narration;
    private String pin;
    private String accountbank;
    private String accountnumber;

    public PayloadBuilder setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
        return this;
    }

    public PayloadBuilder setAccountbank(String accountbank) {
        this.accountbank = accountbank;
        return this;
    }

    public PayloadBuilder setPin(String pin) {
        this.pin = pin;
        return this;
    }

    public PayloadBuilder setExpirymonth(String expirymonth) {
        this.expirymonth = expirymonth;
        return this;
    }

    public PayloadBuilder setVoucher(String voucher) {
        this.voucher = voucher;
        return this;
    }

    public PayloadBuilder setPBFPubKey(String pbfPubKey) {
        this.pbfPubKey = pbfPubKey;
        return this;
    }

    public PayloadBuilder setIP(String ip) {
        this.ip = ip;
        return this;
    }

    public PayloadBuilder setBVN(String bvn){
        this.bvn = bvn;
        return this;
    }

    public PayloadBuilder setLastname(String lastname) {
        this.lastname = lastname;
        return this;
    }

    public PayloadBuilder setFirstname(String firstname) {
        this.firstname = firstname;
        return this;
    }

    public PayloadBuilder setIsPreAuth(boolean isPreAuth){
        this.isPreAuth = isPreAuth;
        return this;
    }

    public PayloadBuilder setIsUsBankCharge(boolean is_us_bank_charge){
        this.is_us_bank_charge = is_us_bank_charge;
        return this;
    }

    public PayloadBuilder setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public PayloadBuilder setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
        return this;
    }

    public PayloadBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public PayloadBuilder setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public PayloadBuilder setEmail(String email) {
        this.email = email;
        return this;
    }

    public PayloadBuilder setExpiryyear(String expiryyear) {
        this.expiryyear = expiryyear;
        return this;
    }

    public PayloadBuilder setCvv(String cvv) {
        this.cvv = cvv;
        return this;
    }

    public PayloadBuilder setPaymentPlan(String payment_plan) {
        this.payment_plan = payment_plan;
        return this;
    }

    public PayloadBuilder setDevice_fingerprint(String device_fingerprint) {
        this.device_fingerprint = device_fingerprint;
        return this;
    }

    public PayloadBuilder setCardno(String cardno) {
        this.cardno = cardno;
        return this;
    }

    public PayloadBuilder setTxRef(String txRef) {
        this.txRef = txRef;
        return this;
    }

    public Payload createPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);

        Payload payload = new Payload(metaObj,subaccountsObj, narration, expirymonth,
                pbfPubKey, ip, lastname,
                firstname, currency, country,
                amount, email, expiryyear,
                cvv, device_fingerprint,
                cardno, txRef);

        if (payment_plan != null) {
            payload.setPayment_plan(payment_plan);
        }

        if(isPreAuth) {
            payload.setCharge_type("preauth");
        }

        return payload;
    }

    public Payload createBankPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(metaObj, subaccountsObj,narration, ip, accountnumber, accountbank, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey,bvn, is_us_bank_charge);
        payload.setPayment_type("account");

        return payload;
    }

    public Payload createMpesaPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj,subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("mpesa");
        payload.setIs_mpesa("1");
        payload.setIs_mpesa_lipa("1");
        return payload;
    }

    public Payload createGhMobileMoneyPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setIs_mobile_money_gh("1");
        payload.setPayment_type("mobilemoneygh");
        payload.setVoucher(voucher);
        payload.setNetwork(network);
        return payload;
    }

    public Payload createUgMobileMoneyPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setIs_mobile_money_ug("1");
        payload.setPayment_type("mobilemoneyuganda");
        payload.setNetwork(network);
        return payload;
    }

    public PayloadBuilder setMeta(String meta) {
        this.meta = meta;
        return this;
    }

    public PayloadBuilder setSubAccount(String subAccounts) {
        this.subAccounts = subAccounts;
        return this;
    }

    public PayloadBuilder setNarration(String narration) {
        this.narration = narration;
        return this;
    }

    public PayloadBuilder setNetwork(String network) {
        this.network = network;
        return this;
    }
}
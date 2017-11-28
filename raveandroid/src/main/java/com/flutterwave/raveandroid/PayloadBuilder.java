package com.flutterwave.raveandroid;

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

    private String txRef;
    private String meta = "";

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

    public PayloadBuilder setPBFPubKey(String pbfPubKey) {
        this.pbfPubKey = pbfPubKey;
        return this;
    }

    public PayloadBuilder setIP(String ip) {
        this.ip = ip;
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

    public PayloadBuilder setCurrency(String currency) {
        this.currency = currency;
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

        return new Payload(metaObj, narration, expirymonth,
                pbfPubKey, ip, lastname,
                firstname, currency, country,
                amount, email, expiryyear,
                cvv, device_fingerprint,
                cardno, txRef);
    }

    public Payload createBankPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        Payload payload = new Payload(metaObj, narration, ip, accountnumber, accountbank, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("account");
        return payload;
    }

    public PayloadBuilder setMeta(String meta) {
        this.meta = meta;
        return this;
    }

    public PayloadBuilder setNarration(String narration) {
        this.narration = narration;
        return this;
    }

}
package com.flutterwave.raveandroid.rave_presentation.data;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;

import java.util.List;

public class PayloadBuilder {

    private String expirymonth;
    private String pbfPubKey;
    private String ip;
    private String fullname;
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
    private String otp;
    private Boolean isPreAuth = null;
    private Boolean is_saved_card_charge = null;
    private SavedCard savedCard;
    private Boolean isPermanent;
    private int frequency;
    private int duration;
    private String phonenumber;
    private String txRef;
    private String meta = null;
    private String subAccounts = null;
    private String narration;
    private String accountbank;
    private String accountnumber;

    public PayloadBuilder setPBFPubKey(String pbfPubKey) {
        this.pbfPubKey = pbfPubKey;
        return this;
    }

    public PayloadBuilder setIP(String ip) {
        this.ip = ip;
        return this;
    }

    public PayloadBuilder setBVN(String bvn) {
        this.bvn = bvn;
        return this;
    }

    public PayloadBuilder setIsPreAuth(boolean isPreAuth) {
        this.isPreAuth = isPreAuth;
        return this;
    }

    public PayloadBuilder setPaymentPlan(String payment_plan) {
        this.payment_plan = payment_plan;
        return this;
    }

    public Payload createPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);

        Payload payload = new Payload(subaccountsObj, narration, expirymonth,
                pbfPubKey, ip,
                fullname, currency, country,
                amount, email, expiryyear,
                cvv, device_fingerprint,
                cardno, txRef);

        if (payment_plan != null) {
            payload.setPayment_plan(payment_plan);
        }

        payload.setPreauthorize(isPreAuth);
        payload.setMeta(Utils.pojofyMetaString(meta));

        return payload;
    }

    public Payload createBankPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(subaccountsObj, narration, ip, accountnumber, accountbank,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey, bvn);
        payload.setPayment_type("account");
        payload.setMeta(Utils.pojofyMetaString(meta));

        return payload;
    }

    public Payload createBankTransferPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);

        // Setup account expiry details
        if (isPermanent) {
            payload.setIs_permanent(isPermanent);
        } else {
            if (duration > 0) payload.setDuration(duration);
            if (frequency > 0) payload.setFrequency(frequency);
        }
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createUssdPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setAccount_bank(accountbank);
        payload.setOrderRef(txRef);
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createMpesaPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createFrancPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("mobilemoneyfranco");
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createUKPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("account");
        payload.setAccountnumber("00000");
        payload.setAccountname("account rave mobile");
        payload.setAccount_bank("093");
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createSaBankAccountPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("account");
        payload.setAccountnumber("00000");
        payload.setAccount_bank("093");
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createBarterPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("barter");
        payload.setIs_barter("1");
        payload.setMetaForV2(Utils.pojofyMetaStringForV2(meta));
        return payload;
    }

    public Payload createGhMobileMoneyPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setVoucher(voucher);
        payload.setNetwork(network);
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createZmMobileMoneyPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setVoucher(voucher);
        payload.setNetwork(network);
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createUgMobileMoneyPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setNetwork(network);
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createRwfMobileMoneyPayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, subaccountsObj, narration, ip,
                fullname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setNetwork(network);
        payload.setMeta(Utils.pojofyMetaString(meta));
        return payload;
    }

    public Payload createSavedCardChargePayload() {
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);

        Payload payload = new Payload(subaccountsObj, narration,
                pbfPubKey, ip,
                fullname, currency, country,
                amount, email, device_fingerprint,
                txRef, is_saved_card_charge, phonenumber);

        if (payment_plan != null) {
            payload.setPayment_plan(payment_plan);
        }

        payload.setPreauthorize(isPreAuth);

        payload.setCardBIN(savedCard.getMasked_pan().substring(0, 6));
        payload.setCard_hash(savedCard.getCardHash());
        payload.setDevice_key(phonenumber);
        payload.setMetaForV2(Utils.pojofyMetaStringForV2(meta));
        return payload;

    }

    public PayloadBuilder setSubAccount(String subAccounts) {
        this.subAccounts = subAccounts;
        return this;
    }

    public String getOtp() {
        return otp;
    }

    public PayloadBuilder setOtp(String otp) {
        this.otp = otp;
        return this;
    }

    public PayloadBuilder setIs_saved_card_charge(boolean is_saved_card_charge) {
        this.is_saved_card_charge = is_saved_card_charge;
        return this;
    }

    public PayloadBuilder setSavedCard(SavedCard savedCard) {
        this.savedCard = savedCard;
        return this;
    }

    public PayloadBuilder setExpirymonth(String expirymonth) {
        this.expirymonth = expirymonth;
        return this;
    }

    public PayloadBuilder setFullname(String fullname) {
        this.fullname = fullname;
        return this;
    }

    public String getCurrency() {
        return currency;
    }

    public PayloadBuilder setCurrency(String currency) {
        this.currency = currency;
        return this;
    }

    public String getCountry() {
        return country;
    }

    public PayloadBuilder setCountry(String country) {
        this.country = country;
        return this;
    }

    public String getAmount() {
        return amount;
    }

    public PayloadBuilder setAmount(String amount) {
        this.amount = amount;
        return this;
    }

    public String getEmail() {
        return email;
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

    public String getPayment_plan() {
        return payment_plan;
    }

    public String getNetwork() {
        return network;
    }

    public PayloadBuilder setNetwork(String network) {
        this.network = network;
        return this;
    }

    public PayloadBuilder setVoucher(String voucher) {
        this.voucher = voucher;
        return this;
    }

    public boolean isPreAuth() {
        return isPreAuth;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public PayloadBuilder setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
        return this;
    }

    public String getTxRef() {
        return txRef;
    }

    public PayloadBuilder setTxRef(String txRef) {
        this.txRef = txRef;
        return this;
    }

    public String getMeta() {
        return meta;
    }

    public PayloadBuilder setMeta(String meta) {
        this.meta = meta;
        return this;
    }

    public String getSubAccounts() {
        return subAccounts;// TOdo: Test subaccount functionality
    }

    public PayloadBuilder setNarration(String narration) {
        this.narration = narration;
        return this;
    }

    public PayloadBuilder setAccountbank(String accountbank) {
        this.accountbank = accountbank;
        return this;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public PayloadBuilder setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
        return this;
    }

    public PayloadBuilder setIsPermanent(boolean permanent) {
        this.isPermanent = permanent;
        return this;
    }

    public PayloadBuilder setfrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    public PayloadBuilder setDuration(int duration) {
        this.duration = duration;
        return this;
    }
}
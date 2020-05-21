package com.flutterwave.raveandroid.rave_presentation.data;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;

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
    private String otp;
    private boolean isPreAuth = false;
    private boolean is_us_bank_charge = false;
    private boolean is_bank_transfer = false;
    private boolean is_saved_card_charge = false;
    private SavedCard savedCard;
    private boolean isPermanent;
    private int frequency;
    private int duration;

    public PayloadBuilder setIs_mobile_money_gh(String is_mobile_money_gh) {
        this.is_mobile_money_gh = is_mobile_money_gh;
        return this;
    }

    public PayloadBuilder setIs_mobile_money_ug(String is_mobile_money_ug) {
        this.is_mobile_money_ug = is_mobile_money_ug;
        return this;
    }

    private String is_mobile_money_rwf;

    public PayloadBuilder setIs_bank_transfer(boolean is_bank_transfer) {
        this.is_bank_transfer = is_bank_transfer;
        return this;
    }


    private String is_mobile_money_gh;
    private String is_mobile_money_ug;

    public PayloadBuilder setIs_mobile_money_rwf(String is_mobile_money_rwf) {
        this.is_mobile_money_rwf = is_mobile_money_rwf;
        return this;
    }

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
    private String accountname;

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

    public PayloadBuilder setBVN(String bvn) {
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

    public PayloadBuilder setIsPreAuth(boolean isPreAuth) {
        this.isPreAuth = isPreAuth;
        return this;
    }

    public PayloadBuilder setIsUsBankCharge(boolean is_us_bank_charge) {
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

    public String getAccountname() {
        return accountname;
    }

    public PayloadBuilder setAccountname(String accountname) {
        this.accountname = accountname;
        return this;
    }

    public Payload createPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);

        Payload payload = new Payload(metaObj, subaccountsObj, narration, expirymonth,
                pbfPubKey, ip, lastname,
                firstname, currency, country,
                amount, email, expiryyear,
                cvv, device_fingerprint,
                cardno, txRef);

        if (payment_plan != null) {
            payload.setPayment_plan(payment_plan);
        }

        if (isPreAuth) {
            payload.setCharge_type("preauth");
        }

        return payload;
    }

    public Payload createBankPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(metaObj, subaccountsObj, narration, ip, accountnumber, accountbank, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey, bvn, is_us_bank_charge);
        payload.setPayment_type("account");

        return payload;
    }

    public Payload createBankTransferPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setIs_bank_transfer(true);
        payload.setPayment_type("banktransfer");
        payload.setNetwork(network);

        // Setup account expiry details
        if (isPermanent) {
            payload.setIs_permanent(isPermanent);
        } else {
            if (duration > 0) payload.setDuration(duration);
            if (frequency > 0) payload.setFrequency(frequency);
        }
        return payload;
    }


    public Payload createUssdPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setAccountbank(accountbank);
        payload.setIs_ussd(true);
        payload.setPayment_type("USSD");
        payload.setOrderRef(txRef);
        return payload;
    }

    public Payload createMpesaPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("mpesa");
        payload.setIs_mpesa("1");
        payload.setIs_mpesa_lipa("1");
        return payload;
    }

    public Payload createFrancPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("mobilemoneyfranco");
        payload.setIs_mobile_money_franco(true);
        return payload;
    }

    public Payload createUKPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("account");
        payload.setAccountnumber("00000");
        payload.setAccountname("account rave mobile");
        payload.setAccountbank("093");
        payload.setIs_uk_bank_charge2(true);
        return payload;
    }


    public Payload createSaBankAccountPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("account");
        payload.setIs_sa_call_pay("1");
        payload.setAccountnumber("00000");
        payload.setAccountbank("093");
        return payload;
    }

    public Payload createBarterPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setPayment_type("barter");
        payload.setIs_barter("1");
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

    public Payload createZmMobileMoneyPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setIs_mobile_money_ug("1");
        payload.setPayment_type("mobilemoneyzambia");
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

    public Payload createRwfMobileMoneyPayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);
        Payload payload = new Payload(phonenumber, metaObj, subaccountsObj, narration, ip, lastname,
                firstname, currency, country, amount, email, device_fingerprint, txRef, pbfPubKey);
        payload.setIs_mobile_money_gh("1");
        payload.setPayment_type("mobilemoneygh");
        payload.setNetwork(network);
        return payload;
    }

    public Payload createSavedCardChargePayload() {
        List<Meta> metaObj = Utils.pojofyMetaString(meta);
        List<SubAccount> subaccountsObj = Utils.pojofySubaccountString(subAccounts);

        Payload payload = new Payload(metaObj, subaccountsObj, narration,
                pbfPubKey, ip, lastname,
                firstname, currency, country,
                amount, email, device_fingerprint,
                txRef, is_saved_card_charge, phonenumber);

        if (payment_plan != null) {
            payload.setPayment_plan(payment_plan);
        }

        if (isPreAuth) {
            payload.setCharge_type("preauth");
        }

        payload.setCardBIN(savedCard.getMasked_pan().substring(0, 6));
        payload.setCard_hash(savedCard.getCardHash());
        payload.setDevice_key(phonenumber);
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

    public String getOtp() {
        return otp;
    }

    public PayloadBuilder setOtp(String otp) {
        this.otp = otp;
        return this;
    }

    public boolean isIs_saved_card_charge() {
        return is_saved_card_charge;
    }

    public PayloadBuilder setIs_saved_card_charge(boolean is_saved_card_charge) {
        this.is_saved_card_charge = is_saved_card_charge;
        return this;
    }

    public SavedCard getSavedCard() {
        return savedCard;
    }

    public String getExpirymonth() {
        return expirymonth;
    }

    public String getPbfPubKey() {
        return pbfPubKey;
    }

    public String getIp() {
        return ip;
    }

    public String getLastname() {
        return lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getCurrency() {
        return currency;
    }

    public String getCountry() {
        return country;
    }

    public String getAmount() {
        return amount;
    }

    public String getEmail() {
        return email;
    }

    public String getExpiryyear() {
        return expiryyear;
    }

    public String getCvv() {
        return cvv;
    }

    public String getDevice_fingerprint() {
        return device_fingerprint;
    }

    public String getCardno() {
        return cardno;
    }

    public String getPayment_plan() {
        return payment_plan;
    }

    public String getNetwork() {
        return network;
    }

    public String getBvn() {
        return bvn;
    }

    public String getVoucher() {
        return voucher;
    }

    public boolean isPreAuth() {
        return isPreAuth;
    }

    public boolean isIs_us_bank_charge() {
        return is_us_bank_charge;
    }

    public boolean isIs_bank_transfer() {
        return is_bank_transfer;
    }

    public String getIs_mobile_money_gh() {
        return is_mobile_money_gh;
    }

    public String getIs_mobile_money_ug() {
        return is_mobile_money_ug;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public String getTxRef() {
        return txRef;
    }

    public String getMeta() {
        return meta;
    }

    public String getSubAccounts() {
        return subAccounts;
    }

    public String getCustomer_phone() {
        return customer_phone;
    }

    public String getNarration() {
        return narration;
    }

    public String getPin() {
        return pin;
    }

    public String getAccountbank() {
        return accountbank;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public PayloadBuilder setIsPermanent(boolean permanent) {
        this.isPermanent = permanent;
        return this;
    }

    public boolean getPermanent() {
        return isPermanent;
    }


    public int getFrequency() {
        return frequency;
    }

    public PayloadBuilder setfrequency(int frequency) {
        this.frequency = frequency;
        return this;
    }

    public int getDuration() {
        return duration;
    }

    public PayloadBuilder setDuration(int duration) {
        this.duration = duration;
        return this;
    }

    public PayloadBuilder setSavedCard(SavedCard savedCard) {
        this.savedCard = savedCard;
        return this;
    }
}
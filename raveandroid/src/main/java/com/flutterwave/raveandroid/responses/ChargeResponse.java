package com.flutterwave.raveandroid.responses;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class ChargeResponse {

    String status;
    String message;
    Data data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public static class AccountValidateInstructions {
        String instruction;

        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

    }

    public static class Data {

        Data data;
        String suggested_auth;
        String chargeResponseCode;
        String authModelUsed;
        String flwRef;

        // Pay with bank (extra) response fields
        String flw_reference;
        String response_code;
        String response_message;
        String accountnumber;
        String bankname;
        String note;
        String amount;
        String validateInstruction;
        String transaction_reference;
        String payment_code;
        AccountValidateInstructions validateInstructions;
        String txRef;
        String chargeResponseMessage;
        String authurl;
        String appFee;
        String currency;
        String charged_amount;
        String redirectUrl;
        private String redirect_url;
        private String requery_url;

        public String getReference_code() {
            return payment_code;
        }

        public void setReference_code(String reference_code) {
            this.payment_code = reference_code;
        }

        public Data getUssdData() {
            return data;
        }

        public void setUssdData(Data data) {
            this.data = data;
        }

        public String getTxRef() {
            return txRef;
        }

        public void setTxRef(String txRef) {
            this.txRef = txRef;
        }

        public String getCharged_amount() {
            return charged_amount;
        }

        public void setCharged_amount(String charged_amount) {
            this.charged_amount = charged_amount;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public String getPayment_code() {
            return payment_code;
        }

        public String getTransaction_reference() {
            return transaction_reference;
        }

        public String getFlw_reference() {
            return flw_reference;
        }

        public void setFlw_reference(String flw_reference) {
            this.flw_reference = flw_reference;
        }

        public String getResponse_code() {
            return response_code;
        }

        public void setResponse_code(String response_code) {
            this.response_code = response_code;
        }

        public String getResponse_message() {
            return response_message;
        }

        public void setResponse_message(String response_message) {
            this.response_message = response_message;
        }

        public String getAccountnumber() {
            return accountnumber;
        }

        public void setAccountnumber(String accountnumber) {
            this.accountnumber = accountnumber;
        }

        public String getBankname() {
            return bankname;
        }

        public void setBankname(String bankname) {
            this.bankname = bankname;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public AccountValidateInstructions getValidateInstructions() {
            return validateInstructions;
        }

        public void setValidateInstructions(AccountValidateInstructions validateInstructions) {
            this.validateInstructions = validateInstructions;
        }

        public String getValidateInstruction() {
            return validateInstruction;
        }

        public void setValidateInstruction(String validateInstruction) {
            this.validateInstruction = validateInstruction;
        }

        public String getTx_ref() {
            return txRef;
        }

        public void setTx_ref(String txRef) {
            this.txRef = txRef;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public String getAuthurl() {
            return authurl;
        }

        public void setAuthurl(String authurl) {
            this.authurl = authurl;
        }

        public String getFlwRef() {
            return flwRef;
        }

        public void setFlwRef(String flwRef) {
            this.flwRef = flwRef;
        }

        public String getChargeResponseMessage() {
            return chargeResponseMessage;
        }

        public void setChargeResponseMessage(String chargeResponseMessage) {
            this.chargeResponseMessage = chargeResponseMessage;
        }

        public String getAuthModelUsed() {
            return authModelUsed;
        }

        public void setAuthModelUsed(String authModelUsed) {
            this.authModelUsed = authModelUsed;
        }

        public String getChargeResponseCode() {
            return chargeResponseCode;
        }

        public void setChargeResponseCode(String chargeResponseCode) {
            this.chargeResponseCode = chargeResponseCode;
        }

        public String getSuggested_auth() {
            return suggested_auth;
        }

        public void setSuggested_auth(String suggested_auth) {
            this.suggested_auth = suggested_auth;
        }

        public String getAppFee() {
            return appFee;
        }

        public void setAppFee(String appFee) {
            this.appFee = appFee;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getChargedAmount() {
            return charged_amount;
        }

        public void setChargedAmount(String charged_amount) {
            this.charged_amount = charged_amount;
        }

        public void setBankName(String bankName) {
            this.bankname = bankName;
        }

        public String getRedirect_url() {
            return redirect_url;
        }

        public void setRedirect_url(String redirect_url) {
            this.redirect_url = redirect_url;
        }

        public String getRequery_url() {
            return requery_url;
        }

        public void setRequery_url(String requery_url) {
            this.requery_url = requery_url;
        }
    }
}

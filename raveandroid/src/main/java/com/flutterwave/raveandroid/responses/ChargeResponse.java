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
        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        String instruction;

    }

    public static class Data {
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

        public void setValidateInstruction(String validateInstruction) {
            this.validateInstruction = validateInstruction;
        }

        public String getFlw_reference() {
            return flw_reference;
        }

        public String getResponse_code() {
            return response_code;
        }

        public String getResponse_message() {
            return response_message;
        }

        public String getAccountnumber() {
            return accountnumber;
        }

        public String getBankname() {
            return bankname;
        }

        public String getNote() {
            return note;
        }

        public String getAmount() {
            return amount;
        }

        public AccountValidateInstructions getValidateInstructions() {
            return validateInstructions;
        }

        AccountValidateInstructions validateInstructions;

        public String getValidateInstruction() {
            return validateInstruction;
        }

        public String getTx_ref() {
            return txRef;
        }

        public void setTx_ref(String txRef) {
            this.txRef = txRef;
        }

        String txRef;
        String chargeResponseMessage;
        String authurl;
        String appFee;
        String currency;
        String charged_amount;

        public String getRedirectUrl() {
            return redirectUrl;
        }

        String redirectUrl;

        public String getAuthurl() {
            return authurl;
        }

        public String getFlwRef() {
            return flwRef;
        }

        public void setValidateInstructions(AccountValidateInstructions validateInstructions) {
            this.validateInstructions = validateInstructions;
        }

        public String getChargeResponseMessage() {
            return chargeResponseMessage;
        }

        public String getAuthModelUsed() {
            return authModelUsed;
        }

        public String getChargeResponseCode() {
            return chargeResponseCode;
        }


        public String getSuggested_auth() {
            return suggested_auth;
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

        public void setChargeResponseCode(String chargeResponseCode) {
            this.chargeResponseCode = chargeResponseCode;
        }

        public void setAuthModelUsed(String authModelUsed) {
            this.authModelUsed = authModelUsed;
        }

        public void setSuggested_auth(String suggested_auth) {
            this.suggested_auth = suggested_auth;
        }

        public void setAuthurl(String authurl) {
            this.authurl = authurl;
        }

        public void setFlwRef(String flwRef) {
            this.flwRef = flwRef;
        }
    }
}

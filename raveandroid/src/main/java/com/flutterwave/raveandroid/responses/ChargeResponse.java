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

        String instruction;

    }

    public ChargeResponse() {
    }

    public ChargeResponse(String status, String message, Data data) {
        this.status = status;
        this.message = message;
        this.data = data;
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

        public Data() {
        }

        public Data(String suggested_auth, String chargeResponseCode, String authModelUsed, String flwRef, String flw_reference, String response_code, String response_message, String accountnumber, String bankname, String note, String amount, AccountValidateInstructions validateInstructions, String validateInstruction, String txRef, String chargeResponseMessage, String authurl, String appFee, String currency, String charged_amount, String redirectUrl) {
            this.suggested_auth = suggested_auth;
            this.chargeResponseCode = chargeResponseCode;
            this.authModelUsed = authModelUsed;
            this.flwRef = flwRef;
            this.flw_reference = flw_reference;
            this.response_code = response_code;
            this.response_message = response_message;
            this.accountnumber = accountnumber;
            this.bankname = bankname;
            this.note = note;
            this.amount = amount;
            this.validateInstructions = validateInstructions;
            this.validateInstruction = validateInstruction;
            this.txRef = txRef;
            this.chargeResponseMessage = chargeResponseMessage;
            this.authurl = authurl;
            this.appFee = appFee;
            this.currency = currency;
            this.charged_amount = charged_amount;
            this.redirectUrl = redirectUrl;
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

        String validateInstruction;

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
    }
}

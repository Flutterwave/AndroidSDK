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


    public static class Data {
        Data data;
        String suggested_auth;
        String chargeResponseCode;
        String authModelUsed;
        String flwRef;
        AccountValidateInstructions validateInstructions;
        String validateInstruction;
        String txRef;
        String chargeResponseMessage;
        String authurl;
        String appFee;
        String currency;
        String charged_amount;

        public String getFlw_reference() {
            return flw_reference;
        }

        public void setFlw_reference(String flw_reference) {
            this.flw_reference = flw_reference;
        }

        String flw_reference;
        String amount;
        String transaction_reference;
        String payment_code;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTransaction_reference() {
            return transaction_reference;
        }

        public void setTransaction_reference(String transaction_reference) {
            this.transaction_reference = transaction_reference;
        }

        public String getPayment_code() {
            return payment_code;
        }

        public void setPayment_code(String payment_code) {
            this.payment_code = payment_code;
        }

        public AccountValidateInstructions getValidateInstructions() {
            return validateInstructions;
        }

        public String getValidateInstruction() {
            return validateInstruction;
        }

        public String getTx_ref() {
            return txRef;
        }

        public void setTx_ref(String txRef) {
            this.txRef = txRef;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

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

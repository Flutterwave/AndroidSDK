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
        String suggested_auth;
        String chargeResponseCode;
        String authModelUsed;
        String flwRef;

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

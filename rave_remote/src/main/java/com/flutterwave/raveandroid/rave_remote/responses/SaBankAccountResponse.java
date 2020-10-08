package com.flutterwave.raveandroid.rave_remote.responses;

import com.google.gson.annotations.SerializedName;

public class SaBankAccountResponse {
    private String status;
    private String message;
    private Data data;
    ChargeMeta meta;

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

    public static class Data_ {

        @SerializedName("amount")
        private String amount;
        @SerializedName("type")
        private String type;
        @SerializedName("redirect")
        private Boolean redirect;
        @SerializedName("transaction_date")
        private String transactionDate;
        @SerializedName("transaction_reference")
        private String transactionReference;
        @SerializedName("flw_reference")
        private String flwReference;
        @SerializedName("redirect_url")
        private String redirectUrl;
        @SerializedName("payment_code")
        private Object paymentCode;
        @SerializedName("type_data")
        private String typeData;

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getRedirect() {
            return redirect;
        }

        public void setRedirect(Boolean redirect) {
            this.redirect = redirect;
        }

        public String getTransactionDate() {
            return transactionDate;
        }

        public void setTransactionDate(String transactionDate) {
            this.transactionDate = transactionDate;
        }

        public String getTransactionReference() {
            return transactionReference;
        }

        public void setTransactionReference(String transactionReference) {
            this.transactionReference = transactionReference;
        }

        public String getFlwReference() {
            return flwReference;
        }

        public void setFlwReference(String flwReference) {
            this.flwReference = flwReference;
        }

        public String getRedirectUrl() {
            return redirectUrl;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public Object getPaymentCode() {
            return paymentCode;
        }

        public void setPaymentCode(Object paymentCode) {
            this.paymentCode = paymentCode;
        }

        public String getTypeData() {
            return typeData;
        }

        public void setTypeData(String typeData) {
            this.typeData = typeData;
        }
    }

    public ChargeMeta getChargeMeta() {
        return (meta != null) ? meta : data.meta;
    }

    public String getAuthUrl() {
        ChargeMeta meta = getChargeMeta();
        return (data == null) ? null
                : (data.authUrl != null) ? data.authUrl
                : (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.redirect;
    }

    public static class Data {
        ChargeMeta meta;
        @SerializedName("auth_url")
        String authUrl;
        @SerializedName("flw_ref")
        String flwRef;
        private Data_ data;
        @SerializedName("response_code")
        private String responseCode;
        @SerializedName("response_message")
        private String responseMessage;

        public String getFlwRef() {
            return flwRef;
        }

        public Data_ getData() {
            return data;
        }

        public void setData(Data_ data) {
            this.data = data;
        }

        public String getResponseCode() {
            return responseCode;
        }

        public void setResponseCode(String responseCode) {
            this.responseCode = responseCode;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public void setResponseMessage(String responseMessage) {
            this.responseMessage = responseMessage;
        }
    }

    private class ChargeMeta {
        Authorization authorization;

        private class Authorization {
            String mode;
            String redirect;
            String note;
            String validate_instructions;

            //Pay with bank transfer
            String transfer_reference;
            String transfer_account;
            String transfer_bank;
            String account_expiration;
            String transfer_note;
            String transfer_amount;

        }
    }
}





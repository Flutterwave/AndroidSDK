package com.flutterwave.raveandroid.rave_remote.responses;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class ChargeResponse {

    String status;
    String message;
    Data data;
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

    public String getAmount() {
        return data == null ? null : data.amount;
    }

    public String getPaymentCode() {
        return data == null ? null : data.payment_code;
    }

    public String getAuthMode() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.mode;
    }

    public ChargeMeta getChargeMeta() {
        return (meta != null) ? meta : data.meta;
    }

    public String getAuthUrl() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.redirect;
    }

    public String getUssdCode() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.note;
    }

    public String getTransferNote() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.transfer_note;
    }

    public String getTransferAmount() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.transfer_amount;
    }

    public String getTransferBankName() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.transfer_bank;
    }

    public String getTransferAccountNumber() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.transfer_account;
    }

    public String getValidateInstructions() {
        ChargeMeta meta = getChargeMeta();
        return (meta == null) ? null
                : (meta.authorization == null) ? null
                : meta.authorization.validate_instructions;
    }

    public String getFlwRef() {
        return (data == null) ? null : data.getFlwRef();
    }

    public String getTxRef() {
        return (data == null) ? null : data.getTx_ref();
    }

    public static class Data {

        ChargeMeta meta;// For account charge

        Data data;
        String suggested_auth;
        String chargeResponseCode;
        String authModelUsed;
        String flwRef;
        String flw_ref;

        // Pay with bank (extra) response fields
        String flw_reference;
        String response_code;
        String response_message;
        String accountnumber;
        String bankname;
        private String redirect_url;
        private String requery_url;
        private String orderRef;

        String txRef;

        String appFee;
        String currency;
        String charged_amount;
        String note;
        String amount;
        String validateInstruction;
        String transaction_reference;
        String payment_code;

        String chargeResponseMessage;
        String processor_response;
        String authurl;
        String redirectUrl;
        @SerializedName("link")
        String captchaLink;
        String code;


        public void setFlw_reference(String flw_reference) {
            this.flw_reference = flw_reference;
        }

        public String getReference_code() {
            return payment_code;
        }

        public String getProcessorResponse() {
            return processor_response;
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

        public void setResponse_code(String response_code) {
            this.response_code = response_code;
        }

        public void setResponse_message(String response_message) {
            this.response_message = response_message;
        }

        public void setBankname(String bankname) {
            this.bankname = bankname;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getTxRef() {
            return txRef;
        }

        public void setTxRef(String txRef) {
            this.txRef = txRef;
        }

        public void setChargeResponseMessage(String chargeResponseMessage) {
            this.chargeResponseMessage = chargeResponseMessage;
        }

        public String getCharged_amount() {
            return charged_amount;
        }

        public void setCharged_amount(String charged_amount) {
            this.charged_amount = charged_amount;
        }

        public void setRedirectUrl(String redirectUrl) {
            this.redirectUrl = redirectUrl;
        }

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        public String getTransaction_reference() {
            return transaction_reference;
        }


        public void setValidateInstruction(String validateInstruction) {
            this.validateInstruction = validateInstruction;
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

        public String getValidateInstruction() {
            return validateInstruction;
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

        public String getAuthurl() {
            return authurl;
        }

        public String getFlwRef() {

            return flwRef != null ? flwRef :
                    flw_ref != null ? flw_ref :
                            flw_reference;
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

        public void setNote(String note) {
            this.note = note;
        }

        public void setBankName(String bankName) {
            this.bankname = bankName;
        }

        public void setAccountnumber(String accountnumber) {
            this.accountnumber = accountnumber;
        }

        public String getRedirect_url() {
            return redirect_url;
        }

        public String getRequery_url() {
            return requery_url;
        }

        public String getCode() {
            return code;
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

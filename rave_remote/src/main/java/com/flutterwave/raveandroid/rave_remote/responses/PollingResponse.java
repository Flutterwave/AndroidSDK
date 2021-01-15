package com.flutterwave.raveandroid.rave_remote.responses;

public class PollingResponse {

    public Data data;

    public ChargeResponse getResponse() {
        return data == null ? null : data.response_parsed;
    }

    public String getAmount() {
        return getResponse() == null ? null : getResponse().getAmount();
    }

    public String getPaymentCode() {
        return getResponse() == null ? null : getResponse().getPaymentCode();
    }

    public String getFlwRef() {
        return (getResponse() == null) ? null : getResponse().getFlwRef();
    }

    public String getAccountNumber() {
        return (getResponse() == null) ? null : getResponse().getAccountNumber();
    }

    public String getSortCode() {
        return (getResponse() == null) ? null : getResponse().getSortCode();
    }


    class Data {
        ChargeResponse response_parsed;
    }

}
/*
{
  "status": "success",
  "message": "REQSPONSE",
  "data": {
    "id": 288709,
    "reqid": "RCORE_CHREQ_19D59CBC397138B33690",
    "status": "completed",
    "response": "{\"data\":{\"amount\":\"1.02\",\"type\":\"paymentcode\",\"redirect\":false,\"transaction_date\":\"2020-10-14T12:02:15.218\",\"transaction_reference\":\"URF_1602673332764_7102735\",\"flw_reference\":\"ACH639111602673335221\",\"redirect_url\":null,\"payment_code\":\"GBP693A02C\",\"type_data\":\"GBP693A02C\",\"meta_data\":{\"account_number\":\"43271228\",\"sort_code\":\"040053\"}},\"response_code\":\"02\",\"response_message\":\"Transaction in progress\"}",
    "createdAt": "2020-10-14T11:02:14.000Z",
    "updatedAt": "2020-10-14T11:02:16.000Z",
    "deletedAt": null,
    "getResponse()": {
      "data": {
        "amount": "1.02",
        "type": "paymentcode",
        "redirect": false,
        "transaction_date": "2020-10-14T12:02:15.218",
        "transaction_reference": "URF_1602673332764_7102735",
        "flw_reference": "ACH639111602673335221",
        "redirect_url": null,
        "payment_code": "GBP693A02C",
        "type_data": "GBP693A02C",
        "meta_data": {
          "account_number": "43271228",
          "sort_code": "040053"
        }
      },
      "response_code": "02",
      "response_message": "Transaction in progress"
    }
  }
}
 */
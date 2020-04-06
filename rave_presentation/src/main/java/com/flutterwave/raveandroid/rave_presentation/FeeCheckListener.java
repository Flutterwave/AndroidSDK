package com.flutterwave.raveandroid.rave_presentation;

public interface FeeCheckListener {

    /**
     * @param chargeAmount The total charge amount (fee inclusive)
     * @param fee          The applicable fees
     */
    void onTransactionFeeFetched(String chargeAmount, String fee);

    /**
     * Called when there is an error while fetching the transaction Fee.
     *
     * @param errorMessage The error message that can be displayed to the user
     */
    void onFetchFeeError(String errorMessage);
}

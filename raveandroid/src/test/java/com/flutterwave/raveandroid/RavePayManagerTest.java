package com.flutterwave.raveandroid;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertNotEquals;

public class RavePayManagerTest {

    @Test
    public void ravePayManagerSetup_PaymentTypesNotAddedMoreThanOnce() {
        // Attempt to add same payment types multiple times
        RavePayManager manager = new RavePayManager(null);
        manager
                .acceptCardPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptBankTransferPayments(true)
                .acceptCardPayments(true)
                .acceptFrancMobileMoneyPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptMpesaPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptUssdPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptCardPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptBankTransferPayments(true)
                .acceptCardPayments(true)
                .acceptFrancMobileMoneyPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptMpesaPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptUssdPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptCardPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptBankTransferPayments(true)
                .acceptCardPayments(true)
                .acceptFrancMobileMoneyPayments(true)
                .acceptGHMobileMoneyPayments(true)
                .acceptUkPayments(true)
                .acceptMpesaPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptUssdPayments(true)
                .acceptZmMobileMoneyPayments(true);

        ArrayList<Integer> paymentTypesList = manager.getOrderedPaymentTypesList();

        for (int firstLoopIndex = 0; firstLoopIndex < paymentTypesList.size(); firstLoopIndex++) {
            for (int secondLoopIndex = 0; secondLoopIndex < paymentTypesList.size(); secondLoopIndex++) {
                if (firstLoopIndex != secondLoopIndex) {
                    assertNotEquals(
                            paymentTypesList.get(firstLoopIndex),
                            paymentTypesList.get(secondLoopIndex));
                }
            }
        }
    }

}
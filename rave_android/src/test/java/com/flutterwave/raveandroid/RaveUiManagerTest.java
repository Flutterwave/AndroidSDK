package com.flutterwave.raveandroid;

import android.app.Activity;

import org.junit.Test;

import java.util.ArrayList;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_BANK_TRANSFER;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_CARD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_MPESA;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UG_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UK;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_USSD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY;
import static org.junit.Assert.assertNotEquals;

public class RaveUiManagerTest {

    @Test
    public void ravePayManagerSetup_PaymentTypesNotAddedMoreThanOnce() {
        // Attempt to add same payment types multiple times
        RaveUiManager manager = new RaveUiManager((Activity) null);
        manager
                .acceptCardPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptBankTransferPayments(true)
                .acceptCardPayments(true)
                .acceptFrancMobileMoneyPayments(true,"NG")
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
                .acceptFrancMobileMoneyPayments(true,"NG")
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
                .acceptFrancMobileMoneyPayments(true,"NG")
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


    @Test
    public void ravePayManagerSetup_AllPaymentTypesAddedAreInPaymentTypesList() {
        RaveUiManager manager = new RaveUiManager((Activity) null);
        manager.acceptCardPayments(true)
                .acceptAccountPayments(true)
                .acceptAchPayments(true)
                .acceptBankTransferPayments(true)
                .acceptFrancMobileMoneyPayments(true,"")
                .acceptGHMobileMoneyPayments(true)
                .acceptMpesaPayments(true)
                .acceptRwfMobileMoneyPayments(true)
                .acceptUgMobileMoneyPayments(true)
                .acceptUssdPayments(true)
                .acceptZmMobileMoneyPayments(true)
                .acceptUkPayments(true);

        ArrayList<Integer> expectedPaymentTypesList = new ArrayList<Integer>() {{
            add(PAYMENT_TYPE_CARD);
            add(PAYMENT_TYPE_ACCOUNT);
            add(PAYMENT_TYPE_ACH);
            add(PAYMENT_TYPE_BANK_TRANSFER);
            add(PAYMENT_TYPE_FRANCO_MOBILE_MONEY);
            add(PAYMENT_TYPE_GH_MOBILE_MONEY);
            add(PAYMENT_TYPE_MPESA);
            add(PAYMENT_TYPE_RW_MOBILE_MONEY);
            add(PAYMENT_TYPE_UG_MOBILE_MONEY);
            add(PAYMENT_TYPE_USSD);
            add(PAYMENT_TYPE_ZM_MOBILE_MONEY);
            add(PAYMENT_TYPE_UK);
        }};

        ArrayList<Integer> actualPaymentTypesList = manager.getOrderedPaymentTypesList();

        for (int index = 0; index < expectedPaymentTypesList.size(); index++) {
            assert (actualPaymentTypesList.contains(expectedPaymentTypesList.get(index)));
        }
    }

}
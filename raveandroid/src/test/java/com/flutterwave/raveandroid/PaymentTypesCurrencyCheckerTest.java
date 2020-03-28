package com.flutterwave.raveandroid;

import com.flutterwave.raveandroid.data.PaymentTypesCurrencyChecker;

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
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.paymentTypesNamesList;
import static org.junit.Assert.assertEquals;


public class PaymentTypesCurrencyCheckerTest {

    ArrayList<Integer> fullPaymentTypesList = new ArrayList<Integer>() {{
        addAll(paymentTypesNamesList.keySet());
    }};

    PaymentTypesCurrencyChecker currencyChecker = new PaymentTypesCurrencyChecker();


    @Test
    public void applyCurrencyChecks_NgnPassed_ReturnsAllAndOnlyNigerianPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "NGN");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }

    @Test
    public void applyCurrencyChecks_GhsPassed_ReturnsAllAndOnlyGhanaianPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "GHS");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }

    @Test
    public void applyCurrencyChecks_KesPassed_ReturnsAllAndOnlyKenyanPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList =
                currencyChecker.applyCurrencyChecks(
                        fullPaymentTypesList,
                        "KES");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }


    @Test
    public void applyCurrencyChecks_UgxPassed_ReturnsAllAndOnlyUgandanPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "UGX");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }


    @Test
    public void applyCurrencyChecks_ZmwPassed_ReturnsAllAndOnlyZambianPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "ZMW");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }


    @Test
    public void applyCurrencyChecks_RwfPassed_ReturnsAllAndOnlyRwandanPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "RWF");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }


    @Test
    public void applyCurrencyChecks_XofPassed_ReturnsAllAndOnlyFrancoPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "XOF");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }

    @Test
    public void applyCurrencyChecks_XafPassed_ReturnsAllAndOnlyFrancoPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "XAF");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }


    @Test
    public void applyCurrencyChecks_UsdPassed_ReturnsAllAndOnlyUsPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "USD");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }

    @Test
    public void applyCurrencyChecks_GbpPassed_ReturnsAllAndOnlyUkPaymentMethods() {
        ArrayList<Integer> checkedPaymentTypesList = currencyChecker.applyCurrencyChecks(fullPaymentTypesList, "GBP");

        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_CARD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_USSD));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_ACH));
        assert (checkedPaymentTypesList.contains(PAYMENT_TYPE_UK));
        assert (!checkedPaymentTypesList.contains(PAYMENT_TYPE_MPESA));
    }

    @Test
    public void applyCurrencyChecks_paymentTypesOrderIsMaintainedAfterChecks() {
        ArrayList<Integer> checkedPaymentTypesList =
                currencyChecker.applyCurrencyChecks(
                        new ArrayList<Integer>() {{
                            add(PAYMENT_TYPE_CARD);
                            add(PAYMENT_TYPE_BANK_TRANSFER);
                            add(PAYMENT_TYPE_USSD);
                            add(PAYMENT_TYPE_ACH);// Non-NGN method
                            add(PAYMENT_TYPE_ACCOUNT);
                        }},
                        "NGN");

        assertEquals(PAYMENT_TYPE_CARD, (int) checkedPaymentTypesList.get(0));
        assertEquals(PAYMENT_TYPE_BANK_TRANSFER, (int) checkedPaymentTypesList.get(1));
        assertEquals(PAYMENT_TYPE_USSD, (int) checkedPaymentTypesList.get(2));
        assertEquals(PAYMENT_TYPE_ACCOUNT, (int) checkedPaymentTypesList.get(3));

    }

}
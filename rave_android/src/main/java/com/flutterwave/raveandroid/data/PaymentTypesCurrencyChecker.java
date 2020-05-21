package com.flutterwave.raveandroid.data;

import java.util.ArrayList;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_BANK_TRANSFER;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_MPESA;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_SA_BANK_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UG_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UK;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_USSD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY;

public class PaymentTypesCurrencyChecker {


    public PaymentTypesCurrencyChecker() {
    }

    public ArrayList<Integer> applyCurrencyChecks(
            ArrayList<Integer> orderedPaymentTypesList,
            String acceptedCurrency) {
        ArrayList<Integer> currencyCheckedPaymentTypesList = new ArrayList<>();

        for (int index = 0; index < orderedPaymentTypesList.size(); index++) {
            switch (orderedPaymentTypesList.get(index)) {
                case PAYMENT_TYPE_ACCOUNT:
                case PAYMENT_TYPE_BANK_TRANSFER:
                case PAYMENT_TYPE_USSD:
                    if (acceptedCurrency.equalsIgnoreCase("NGN"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_ACH:
                    if (acceptedCurrency.equalsIgnoreCase("USD"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_GH_MOBILE_MONEY:
                    if (acceptedCurrency.equalsIgnoreCase("GHS"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_RW_MOBILE_MONEY:
                    if (acceptedCurrency.equalsIgnoreCase("RWF"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_ZM_MOBILE_MONEY:
                    if (acceptedCurrency.equalsIgnoreCase("ZMW"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_FRANCO_MOBILE_MONEY:
                    if (acceptedCurrency.equalsIgnoreCase("XAF") || acceptedCurrency.equalsIgnoreCase("XOF"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_MPESA:
                    if (acceptedCurrency.equalsIgnoreCase("KES"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_UG_MOBILE_MONEY:
                    if (acceptedCurrency.equalsIgnoreCase("UGX"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_UK:
                    if (acceptedCurrency.equalsIgnoreCase("GBP"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                case PAYMENT_TYPE_SA_BANK_ACCOUNT:
                    if (acceptedCurrency.equalsIgnoreCase("ZAR"))
                        currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
                    break;
                default:
                    currencyCheckedPaymentTypesList.add(orderedPaymentTypesList.get(index));
            }
        }

        return currencyCheckedPaymentTypesList;
    }
}

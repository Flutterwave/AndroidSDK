package com.flutterwave.raveandroid;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.data.PaymentTypesCurrencyChecker;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.RavePayManager;
import com.flutterwave.raveandroid.rave_presentation.data.Utils;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ACH;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_BANK_TRANSFER;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_BARTER;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_CARD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_FRANCO_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_GH_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_MPESA;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_RW_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_SA_BANK_ACCOUNT;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UG_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_UK;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_USSD;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PAYMENT_TYPE_ZM_MOBILE_MONEY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_PARAMS;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVE_REQUEST_CODE;

public class RaveUiManager extends RavePayManager {
    private Activity activity;
    private Fragment supportFragment;
    private android.app.Fragment fragment;
    private int theme = R.style.DefaultTheme;
    private boolean allowSaveCard = true;
    private boolean usePhoneAndEmailSuppliedToSaveCards = true;
    protected boolean showStagingLabel = true;
    private Boolean allowEditPhone = true;

    private ArrayList<Integer> orderedPaymentTypesList = new ArrayList<>();

    public RaveUiManager(Activity activity) {
        super();
        this.activity = activity;
    }

    public RaveUiManager(Fragment fragment) {
        super();
        this.supportFragment = fragment;
    }

    public RaveUiManager(android.app.Fragment fragment) {
        super();
        this.fragment = fragment;
    }


    public RaveUiManager setMeta(List<Meta> meta) {
        this.meta = Utils.stringifyMeta(meta);
        return this;
    }

    public RaveUiManager setSubAccounts(List<SubAccount> subAccounts) {
        this.subAccounts = Utils.stringifySubaccounts(subAccounts);
        return this;
    }

    public RaveUiManager setEmail(String email) {
        this.email = email;
        return this;
    }

    public RaveUiManager setAmount(double amount) {
        if (amount != 0) {
            this.amount = amount;
        }
        return this;
    }

    public RaveUiManager setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        return this;
    }

    public RaveUiManager setEncryptionKey(String encryptionKey) {
        this.encryptionKey = encryptionKey;
        return this;
    }

    public RaveUiManager setTxRef(String txRef) {
        this.txRef = txRef;
        return this;
    }

    public RaveUiManager setNarration(String narration) {
        this.narration = narration;
        return this;
    }

    public RaveUiManager setCurrency(String currency) {
        this.currency = currency;
        if (country==null || country.isEmpty()) { // Set the country based on Rave defaults if the country is not already set
            switch (currency) {
                case "KES":
                    country = "KE";
                    break;
                case "GHS":
                    country = "GH";
                    break;
                case "ZAR":
                    country = "ZA";
                    break;
                case "TZS":
                    country = "TZ";
                    break;
                default:
                    country = "NG";
                    break;
            }
        }
        return this;
    }

    public RaveUiManager setCountry(String country) {
        this.country = country;
        return this;
    }

    public RaveUiManager setBarterCountry(String barterCountry) {
        this.barterCountry = barterCountry;
        return this;
    }

    public RaveUiManager setfName(String fName) {
        this.fName = fName;
        return this;
    }

    public RaveUiManager setlName(String lName) {
        this.lName = lName;
        return this;
    }

    public RaveUiManager setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public RaveUiManager setPhoneNumber(String phoneNumber, Boolean isEditable) {
        this.phoneNumber = phoneNumber;
        this.allowEditPhone = isEditable;
        return this;
    }

    public RaveUiManager setPaymentPlan(String payment_plan) {
        this.payment_plan = payment_plan;
        return this;
    }

    public RaveUiManager onStagingEnv(boolean isStaging) {
        this.staging = isStaging;
        return this;
    }

    public RaveUiManager isPreAuth(boolean isPreAuth) {
        this.isPreAuth = isPreAuth;
        return this;
    }

    public RaveUiManager shouldDisplayFee(boolean displayFee) {
        this.displayFee = displayFee;
        return this;
    }

    public RaveUiManager showStagingLabel(boolean showStagingLabel) {
        this.showStagingLabel = showStagingLabel;
        return this;
    }

    public RaveUiManager withTheme(int theme) {
        this.theme = theme;
        return this;
    }

    public RaveUiManager allowSaveCardFeature(boolean allowSaveCard) {
        this.allowSaveCard = allowSaveCard;
        return this;
    }

    public RaveUiManager allowSaveCardFeature(boolean allowSaveCard, boolean usePhoneAndEmailSuppliedToSaveCards) {
        this.allowSaveCard = allowSaveCard;
        this.usePhoneAndEmailSuppliedToSaveCards = usePhoneAndEmailSuppliedToSaveCards;
        return this;
    }

    public RaveUiManager acceptAchPayments(boolean withAch) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ACH) && withAch)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ACH);
        return this;
    }

    public RaveUiManager acceptCardPayments(boolean withCard) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_CARD) && withCard)
            orderedPaymentTypesList.add(PAYMENT_TYPE_CARD);
        return this;
    }

    public RaveUiManager acceptMpesaPayments(boolean withMpesa) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_MPESA) && withMpesa)
            orderedPaymentTypesList.add(PAYMENT_TYPE_MPESA);
        return this;
    }

    public RaveUiManager acceptAccountPayments(boolean withAccount) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ACCOUNT) && withAccount)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ACCOUNT);
        return this;
    }

    public RaveUiManager acceptGHMobileMoneyPayments(boolean withGHMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_GH_MOBILE_MONEY) && withGHMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_GH_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptUgMobileMoneyPayments(boolean withUgMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_UG_MOBILE_MONEY) && withUgMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_UG_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptRwfMobileMoneyPayments(boolean withRwfMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_RW_MOBILE_MONEY) && withRwfMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_RW_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptZmMobileMoneyPayments(boolean withZmMobileMoney) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_ZM_MOBILE_MONEY) && withZmMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_ZM_MOBILE_MONEY);
        return this;
    }

    public RaveUiManager acceptUkPayments(boolean withUk) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_UK) && withUk)
            orderedPaymentTypesList.add(PAYMENT_TYPE_UK);
        return this;
    }

    public RaveUiManager acceptSaBankPayments(boolean withSaBankAccount) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_SA_BANK_ACCOUNT) && withSaBankAccount)
            orderedPaymentTypesList.add(PAYMENT_TYPE_SA_BANK_ACCOUNT);
        return this;
    }

    public RaveUiManager acceptFrancMobileMoneyPayments(boolean withFrancMobileMoney, String country) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_FRANCO_MOBILE_MONEY) && withFrancMobileMoney)
            orderedPaymentTypesList.add(PAYMENT_TYPE_FRANCO_MOBILE_MONEY);
        this.country = country;
        return this;
    }

    public RaveUiManager acceptBankTransferPayments(boolean withBankTransfer) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        return this;
    }


    public RaveUiManager acceptBankTransferPayments(boolean withBankTransfer, boolean isPermanent) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        this.isPermanent = isPermanent;
        return this;
    }

    public RaveUiManager acceptBankTransferPayments(boolean withBankTransfer, int duration, int frequency) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BANK_TRANSFER) && withBankTransfer)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BANK_TRANSFER);
        this.duration = duration;
        this.frequency = frequency;
        return this;
    }

    public RaveUiManager acceptUssdPayments(boolean withUssd) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_USSD) && withUssd)
            orderedPaymentTypesList.add(PAYMENT_TYPE_USSD);
        return this;
    }

    public RaveUiManager acceptBarterPayments(boolean withBarter) {
        if (!orderedPaymentTypesList.contains(PAYMENT_TYPE_BARTER) && withBarter)
            orderedPaymentTypesList.add(PAYMENT_TYPE_BARTER);
        return this;
    }

    public RaveUiManager initialize() {
        filterPaymentTypes();

        if (orderedPaymentTypesList.size() == 0) {
            if (activity != null) {
                Toast.makeText(activity, "No valid payment types for the selected currency.", Toast.LENGTH_LONG).show();
            } else if (supportFragment != null && supportFragment.getContext() != null) {
                Toast.makeText(supportFragment.getContext(), "No valid payment types for the selected currency.", Toast.LENGTH_LONG).show();
            } else if (fragment != null && fragment.getActivity() != null) {
                Toast.makeText(fragment.getActivity(), "No valid payment types for the selected currency.", Toast.LENGTH_LONG).show();
            } else {
                Log.d(RAVEPAY, "No valid payment types for the selected currency.");
            }
            return this;
        }

        if (activity != null) {
            Intent intent = new Intent(activity, RavePayActivity.class);
            intent.putExtra(RAVE_PARAMS, Parcels.wrap(createRavePayInitializer()));
            activity.startActivityForResult(intent, RAVE_REQUEST_CODE);
        } else if (supportFragment != null && supportFragment.getContext() != null) {
            Intent intent = new Intent(supportFragment.getContext(), RavePayActivity.class);
            intent.putExtra(RAVE_PARAMS, Parcels.wrap(createRavePayInitializer()));
            supportFragment.startActivityForResult(intent, RAVE_REQUEST_CODE);
        } else if (fragment != null && fragment.getActivity() != null) {
            Intent intent = new Intent(fragment.getActivity(), RavePayActivity.class);
            intent.putExtra(RAVE_PARAMS, Parcels.wrap(createRavePayInitializer()));
            fragment.startActivityForResult(intent, RAVE_REQUEST_CODE);
        } else {
            Log.d(RAVEPAY, "Context is required!");
        }
        return this;
    }

    private void filterPaymentTypes() {
        orderedPaymentTypesList =
                new PaymentTypesCurrencyChecker().applyCurrencyChecks(
                        orderedPaymentTypesList,
                        currency
                );
    }

    private RavePayInitializer createRavePayInitializer() {
        return new RavePayInitializer(
                getEmail(),
                getAmount(),
                getPublicKey(),
                getEncryptionKey(),
                getTxRef(),
                getNarration(),
                getCurrency(),
                getCountry(),
                getBarterCountry(),
                getfName(),
                getlName(),
                theme,
                getPhoneNumber(),
                allowEditPhone,
                allowSaveCard,
                usePhoneAndEmailSuppliedToSaveCards,
                isPermanent(),
                getDuration(),
                getFrequency(),
                isStaging(),
                getMeta(),
                getSubAccounts(),
                getPayment_plan(),
                isPreAuth(),
                showStagingLabel,
                isDisplayFee(),
                orderedPaymentTypesList);
    }

    public ArrayList<Integer> getOrderedPaymentTypesList() {
        return orderedPaymentTypesList;
    }
}
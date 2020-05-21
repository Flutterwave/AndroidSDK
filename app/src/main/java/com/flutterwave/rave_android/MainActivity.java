package com.flutterwave.rave_android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RaveUiManager;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Meta;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_java_commons.SubAccount;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.RavePayManager;
import com.flutterwave.raveandroid.rave_presentation.card.Card;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentManager;
import com.flutterwave.raveandroid.rave_presentation.card.SavedCardsListener;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;

import java.util.ArrayList;
import java.util.List;


public class MainActivity
        extends AppCompatActivity
        // Interfaces to implement for custom UI or no-UI usage
        implements
        FeeCheckListener, // Implement if you want to be able to check fees beforehand
        SavedCardsListener, // Implement if you want to be able to save cards and charge saved cards
        CardPaymentCallback {// Must be implemented to charge cards with custom UI or no-UI

    EditText emailEt;
    EditText amountEt;
    EditText publicKeyEt;
    EditText encryptionKeyEt;
    EditText txRefEt;
    EditText narrationEt;
    EditText currencyEt;
    EditText countryEt;
    EditText fNameEt;
    EditText lNameEt;
    EditText durationEt;
    EditText frequencyEt;
    EditText phoneNumberEt;
    Button startPayBtn;
    Button addVendorBtn;
    Button clearVendorBtn;
    SwitchCompat cardSwitch;
    SwitchCompat accountSwitch;
    SwitchCompat ghMobileMoneySwitch;
    SwitchCompat ugMobileMoneySwitch;
    SwitchCompat ukbankSwitch;
    SwitchCompat saBankSwitch;
    SwitchCompat francMobileMoneySwitch;
    SwitchCompat rwfMobileMoneySwitch;
    SwitchCompat zmMobileMoneySwitch;
    SwitchCompat bankTransferSwitch;
    SwitchCompat isPermanentAccountSwitch;
    SwitchCompat setExpirySwitch;
    SwitchCompat ussdSwitch;
    SwitchCompat barterSwitch;
    SwitchCompat isLiveSwitch;
    SwitchCompat isMpesaSwitch;
    SwitchCompat accountAchSwitch;
    SwitchCompat addSubAccountsSwitch;
    SwitchCompat useRaveUISwitch;
    SwitchCompat isPreAuthSwitch;
    SwitchCompat allowSavedCardsSwitch;
    SwitchCompat shouldDisplayFeeSwitch;
    SwitchCompat shouldShowStagingLabelSwitch;
    List<Meta> meta = new ArrayList<>();
    List<SubAccount> subAccounts = new ArrayList<>();
    LinearLayout addSubaccountsLayout;
    LinearLayout expiryDetailsLayout;
    TextView vendorListTXT;

    ProgressDialog progressDialog;
    private CardPaymentManager cardPayManager;
    private Card card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEt = findViewById(R.id.emailEt);
        amountEt = findViewById(R.id.amountEt);
        publicKeyEt = findViewById(R.id.publicKeyEt);
        encryptionKeyEt = findViewById(R.id.encryptionEt);
        txRefEt = findViewById(R.id.txRefEt);
        narrationEt = findViewById(R.id.narrationTV);
        currencyEt = findViewById(R.id.currencyEt);
        countryEt = findViewById(R.id.countryEt);
        fNameEt = findViewById(R.id.fNameEt);
        lNameEt = findViewById(R.id.lnameEt);
        phoneNumberEt = findViewById(R.id.phoneNumberEt);
        durationEt = findViewById(R.id.expiryDaysEt);
        frequencyEt = findViewById(R.id.frequencyEt);
        startPayBtn = findViewById(R.id.startPaymentBtn);
        cardSwitch = findViewById(R.id.cardPaymentSwitch);
        allowSavedCardsSwitch = findViewById(R.id.allowSavedCardsSwitch);
        accountSwitch = findViewById(R.id.accountPaymentSwitch);
        accountAchSwitch = findViewById(R.id.accountAchSwitch);
        isMpesaSwitch = findViewById(R.id.accountMpesaSwitch);
        isPreAuthSwitch = findViewById(R.id.isPreAuthSwitch);
        shouldDisplayFeeSwitch = findViewById(R.id.isDisplayFeeSwitch);
        ghMobileMoneySwitch = findViewById(R.id.accountGHMobileMoneySwitch);
        ugMobileMoneySwitch = findViewById(R.id.accountUgMobileMoneySwitch);
        ukbankSwitch = findViewById(R.id.accountUkbankSwitch);
        saBankSwitch = findViewById(R.id.accountSaBankSwitch);
        francMobileMoneySwitch = findViewById(R.id.accountfrancMobileMoneySwitch);
        zmMobileMoneySwitch = findViewById(R.id.accountZmMobileMoneySwitch);
        rwfMobileMoneySwitch = findViewById(R.id.accountRwfMobileMoneySwitch);
        bankTransferSwitch = findViewById(R.id.bankTransferSwitch);
        isPermanentAccountSwitch = findViewById(R.id.isPermanentSwitch);
        setExpirySwitch = findViewById(R.id.setExpirySwitch);
        bankTransferSwitch = findViewById(R.id.bankTransferSwitch);
        expiryDetailsLayout = findViewById(R.id.expiry_layout);
        ussdSwitch = findViewById(R.id.ussd_switch);
        barterSwitch = findViewById(R.id.barter_switch);
        isLiveSwitch = findViewById(R.id.isLiveSwitch);
        addSubAccountsSwitch = findViewById(R.id.addSubAccountsSwitch);
        useRaveUISwitch = findViewById(R.id.useRaveUISwitch);
        shouldShowStagingLabelSwitch = findViewById(R.id.shouldShowStagingLabelSwitch);
        addVendorBtn = findViewById(R.id.addVendorBtn);
        clearVendorBtn = findViewById(R.id.clearVendorsBtn);
        vendorListTXT = findViewById(R.id.refIdsTV);
        vendorListTXT.setText("Your current vendor refs are: ");

        bankTransferSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    isPermanentAccountSwitch.setVisibility(View.VISIBLE);
                    setExpirySwitch.setVisibility(View.VISIBLE);
                } else {
                    isPermanentAccountSwitch.setVisibility(View.GONE);
                    setExpirySwitch.setVisibility(View.GONE);
                    expiryDetailsLayout.setVisibility(View.GONE);
                }
            }
        });

        isPermanentAccountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setExpirySwitch.setVisibility(View.GONE);
                    expiryDetailsLayout.setVisibility(View.GONE);
                } else setExpirySwitch.setVisibility(View.VISIBLE);
            }
        });
        setExpirySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    expiryDetailsLayout.setVisibility(View.VISIBLE);
                    isPermanentAccountSwitch.setVisibility(View.GONE);
                } else {
                    expiryDetailsLayout.setVisibility(View.GONE);
                    isPermanentAccountSwitch.setVisibility(View.VISIBLE);
                }
            }
        });

        publicKeyEt.setText(RaveConstants.PUBLIC_KEY);
        encryptionKeyEt.setText(RaveConstants.ENCRYPTION_KEY);

        addSubaccountsLayout = findViewById(R.id.addSubAccountsLayout);

        meta.add(new Meta("test key 1", "test value 1"));
        meta.add(new Meta("test key 2", "test value 2"));

        startPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEntries();
            }
        });

        addSubAccountsSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    addSubaccountsLayout.setVisibility(View.VISIBLE);
                } else {
                    clear();
                }
            }
        });

        addVendorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVendorDialog();
            }
        });
        clearVendorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

    }

    private void clear() {
        subAccounts.clear();
        vendorListTXT.setText("Your current vendor refs are: ");
        addSubaccountsLayout.setVisibility(View.GONE);
        addSubAccountsSwitch.setChecked(false);
    }

    private void validateEntries() {
        clearErrors();
        String email = emailEt.getText().toString();
        String amount = amountEt.getText().toString();
        String publicKey = publicKeyEt.getText().toString();
        String encryptionKey = encryptionKeyEt.getText().toString();
        String txRef = txRefEt.getText().toString();
        String narration = narrationEt.getText().toString();
        String currency = currencyEt.getText().toString();
        String country = countryEt.getText().toString();
        String fName = fNameEt.getText().toString();
        String lName = lNameEt.getText().toString();
        String phoneNumber = phoneNumberEt.getText().toString();
        String accountDuration = durationEt.getText().toString();
        String accountPaymentFrequency = frequencyEt.getText().toString();

        boolean valid = true;

        if (amount.length() == 0) {
            amount = "0";
        }

        //isAmountValid for compulsory fields
        if (!Utils.isEmailValid(email)) {
            valid = false;
            emailEt.setError("A valid email is required");
        }

        if (publicKey.length() < 1) {
            valid = false;
            publicKeyEt.setError("A valid public key is required");
        }

        if (encryptionKey.length() < 1) {
            valid = false;
            encryptionKeyEt.setError("A valid encryption key is required");
        }

        if (txRef.length() < 1) {
            valid = false;
            txRefEt.setError("A valid txRef key is required");
        }

        if (currency.length() < 1) {
            valid = false;
            currencyEt.setError("A valid currency code is required");
        }

        if (country.length() < 1) {
            valid = false;
            countryEt.setError("A valid country code is required");
        }

        if (setExpirySwitch.isChecked()) {
            if (accountDuration.isEmpty()) {
                valid = false;
                durationEt.setError("Please enter expiry duration (in days)");
            }
            if (accountPaymentFrequency.isEmpty()) {
                valid = false;
                frequencyEt.setError("Please enter payment frequency");
            }
        }

        if (valid) {
            RavePayManager raveManager;

            boolean shouldUseRaveUi = useRaveUISwitch.isChecked();

            if (shouldUseRaveUi) {
                raveManager = new RaveUiManager(this)
                        .acceptMpesaPayments(isMpesaSwitch.isChecked())
                        .acceptAccountPayments(accountSwitch.isChecked())
                        .acceptCardPayments(cardSwitch.isChecked())
                        .allowSaveCardFeature(allowSavedCardsSwitch.isChecked())
                        .acceptAchPayments(accountAchSwitch.isChecked())
                        .acceptGHMobileMoneyPayments(ghMobileMoneySwitch.isChecked())
                        .acceptUgMobileMoneyPayments(ugMobileMoneySwitch.isChecked())
                        .acceptZmMobileMoneyPayments(zmMobileMoneySwitch.isChecked())
                        .acceptRwfMobileMoneyPayments(rwfMobileMoneySwitch.isChecked())
                        .acceptUkPayments(ukbankSwitch.isChecked())
                        .acceptSaBankPayments(saBankSwitch.isChecked())
                        .acceptFrancMobileMoneyPayments(francMobileMoneySwitch.isChecked())
                        .acceptBankTransferPayments(bankTransferSwitch.isChecked())
                        .acceptUssdPayments(ussdSwitch.isChecked())
                        .acceptBarterPayments(barterSwitch.isChecked())
                        //                    .withTheme(R.style.TestNewTheme)
                        .showStagingLabel(shouldShowStagingLabelSwitch.isChecked())
                        .setAmount(Double.parseDouble(amount))
                        .setCountry(country)
                        .setCurrency(currency)
                        .setEmail(email)
                        .setfName(fName)
                        .setlName(lName)
                        .setPhoneNumber(phoneNumber)
                        .setNarration(narration)
                        .setPublicKey(publicKey)
                        .setEncryptionKey(encryptionKey)
                        .setTxRef(txRef)
                        .onStagingEnv(!isLiveSwitch.isChecked())
                        .setSubAccounts(subAccounts)
                        .isPreAuth(isPreAuthSwitch.isChecked())
                        .setMeta(meta)
                        .shouldDisplayFee(shouldDisplayFeeSwitch.isChecked());

                // Customize pay with bank transfer options (optional)
                if (isPermanentAccountSwitch.isChecked())
                    ((RaveUiManager) raveManager).acceptBankTransferPayments(true, true);
                else {
                    if (setExpirySwitch.isChecked()) {
                        int duration = 0, frequency = 0;
                        try {
                            duration = Integer.parseInt(durationEt.getText().toString());
                            frequency = Integer.parseInt(frequencyEt.getText().toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        ((RaveUiManager) raveManager).acceptBankTransferPayments(true, duration, frequency);
                    }
                }

                raveManager.initialize();

            } else {
                raveManager = new RaveNonUIManager().setAmount(Double.parseDouble(amount))
                        .setCountry(country)
                        .setCurrency(currency)
                        .setEmail(email)
                        .setfName(fName)
                        .setlName(lName)
                        .setPhoneNumber(phoneNumber)
                        .setNarration(narration)
                        .setPublicKey(publicKey)
                        .setEncryptionKey(encryptionKey)
                        .setTxRef(txRef)
                        .onStagingEnv(!isLiveSwitch.isChecked())
                        .setSubAccounts(subAccounts)
                        .setMeta(meta)
                        .isPreAuth(isPreAuthSwitch.isChecked())
                        .initialize();

                cardPayManager = new CardPaymentManager(((RaveNonUIManager) raveManager), this, this);
                card = new Card(
                        "5531886652142950", // Test MasterCard PIN authentication
//                        "4242424242424242", // Test VisaCard 3D-Secure Authentication
//                        "4556052704172643", // Test VisaCard (Address Verification)
                        "12",
                        "30",
                        "123"
                );

//                cardPayManager.fetchSavedCards();
//                cardPayManager.fetchTransactionFee(card,this);
                cardPayManager.chargeCard(card);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {

            String message = data.getStringExtra("response");

            if (message != null) {
                Log.d("rave response", message);
            }

            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void clearErrors() {
        emailEt.setError(null);
        amountEt.setError(null);
        publicKeyEt.setError(null);
        encryptionKeyEt.setError(null);
        txRefEt.setError(null);
        narrationEt.setError(null);
        currencyEt.setError(null);
        countryEt.setError(null);
        fNameEt.setError(null);
        lNameEt.setError(null);
        durationEt.setError(null);
        frequencyEt.setError(null);
    }

    private void addVendorDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.rave_sdk_add_vendor_layout, null);
        dialogBuilder.setView(dialogView);
        final EditText vendorReferenceET = dialogView.findViewById(R.id.vendorReferecnceET);
        final EditText vendorRatioET = dialogView.findViewById(R.id.vendorRatioET);
        Button addVendorBtn = dialogView.findViewById(R.id.doneDialogBtn);
        Button cancelDialogBtn = dialogView.findViewById(R.id.cancelDialogBtn);
        final AlertDialog alertDialog = dialogBuilder.create();
        cancelDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        addVendorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = true;
                String vendorRef = vendorReferenceET.getText().toString().trim();
                String vendorRatio = vendorRatioET.getText().toString().trim();

                if (vendorRef.length() < 1) {
                    vendorReferenceET.setError("Vendor reference is required");
                    valid = false;
                }
                if (vendorRatioET.length() < 1) {
                    vendorRatioET.setError("Vendor ratio is required");
                    valid = false;
                }
                if (!valid) {
                    return;
                }
                if (subAccounts.size() != 0) {
                    vendorListTXT.setText(vendorListTXT.getText().toString() + ", " + vendorRef + "(" + vendorRatio + ")");
                } else {
                    vendorListTXT.setText(vendorListTXT.getText().toString() + vendorRef + "(" + vendorRatio + ")");
                }
                subAccounts.add(new SubAccount(vendorRef, vendorRatio));
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void showProgressIndicator(boolean active) {
        try {
            if (isFinishing()) {
                return;
            }

            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Please wait...");
            }

            if (active && !progressDialog.isShowing()) {
                progressDialog.show();
            } else {
                progressDialog.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void collectCardPin() {
        Toast.makeText(this, "Submitting PIN", Toast.LENGTH_SHORT).show();
        cardPayManager.submitPin("3310");
    }

    @Override
    public void collectOtp(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Submitting OTP", Toast.LENGTH_SHORT).show();
        cardPayManager.submitOtp("12345");
    }

    @Override
    public void collectAddress() {
        Toast.makeText(this, "Submitting address details", Toast.LENGTH_SHORT).show();
        cardPayManager.submitAddress(new AddressDetails(
                "8, Providence Street",
                "Lekki Phase 1",
                "Lagos",
                "102102",
                "NG")
        );
    }

    @Override
    public void showAuthenticationWebPage(String authenticationUrl) {
        Toast.makeText(this, "Called to load web page", Toast.LENGTH_SHORT).show();

        // Load webpage
//        cardPayManager.onWebpageAuthenticationComplete();
    }

    @Override
    public void onError(String errorMessage, @Nullable String flwRef) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccessful(String flwRef) {
        Toast.makeText(this, "Transaction Successful", Toast.LENGTH_LONG).show();
//        cardPayManager.saveCard(); // Save card if needed
    }

    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {
        // Check that the list is not empty, show the user to select which they'd like to charge, then proceed to chargeSavedCard()
        if (cards.size() != 0) cardPayManager.chargeSavedCard(cards.get(0));
        else
            Toast.makeText(this, "No saved cards found for " + phoneNumber, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSavedCardsLookupFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void collectOtpForSaveCardCharge() {
        collectOtp("Otp for saved card");
    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber) {

    }

    @Override
    public void onCardSaveFailed(String message) {

    }

    @Override
    public void onTransactionFeeFetched(String chargeAmount, String fee) {
        // Display the fee to the customer
        Toast.makeText(this, "The transaction fee is " + fee, Toast.LENGTH_SHORT).show();
//        cardPayManager.chargeCard(card);
    }

    @Override
    public void onFetchFeeError(String errorMessage) {

    }
}

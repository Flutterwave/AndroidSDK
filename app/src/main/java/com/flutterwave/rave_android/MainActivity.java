package com.flutterwave.rave_android;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.responses.SubAccount;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

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
    Button startPayBtn;
    Button addVendorBtn;
    Button clearVendorBtn;
    SwitchCompat cardSwitch;
    SwitchCompat accountSwitch;
    SwitchCompat ghMobileMoneySwitch;
    SwitchCompat isLiveSwitch;
    SwitchCompat isMpesaSwitch;
    SwitchCompat addSubAccountsSwitch;
    SwitchCompat isPreAuthSwitch;
    List<Meta> meta = new ArrayList<>();
    List<SubAccount> subAccounts = new ArrayList<>();
    LinearLayout addSubaccountsLayout;
    TextView vendorListTXT;

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
        startPayBtn = findViewById(R.id.startPaymentBtn);
        cardSwitch = findViewById(R.id.cardPaymentSwitch);
        accountSwitch = findViewById(R.id.accountPaymentSwitch);
        isMpesaSwitch = findViewById(R.id.accountMpesaSwitch);
        isPreAuthSwitch = findViewById(R.id.isPreAuthSwitch);
        ghMobileMoneySwitch = findViewById(R.id.accountGHMobileMoneySwitch);
        isLiveSwitch = findViewById(R.id.isLiveSwitch);
        addSubAccountsSwitch = findViewById(R.id.addSubAccountsSwitch);
        addVendorBtn = findViewById(R.id.addVendorBtn);
        clearVendorBtn = findViewById(R.id.clearVendorsBtn);
        vendorListTXT = findViewById(R.id.refIdsTV);
        vendorListTXT.setText("Your current vendor refs are: ");

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

        addSubAccountsSwitch.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked){
                    addSubaccountsLayout.setVisibility(View.VISIBLE);
                }else {
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

    private void clear(){
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

        boolean valid = true;

        if (amount.length() == 0) {
            amount = "0";
        }

        //check for compulsory fields
        if (!Utils.isEmailValid(email)) {
            valid = false;
            emailEt.setError("A valid email is required");
        }

        if (publicKey.length() < 1){
            valid = false;
            publicKeyEt.setError("A valid public key is required");
        }

        if (encryptionKey.length() < 1){
            valid = false;
            encryptionKeyEt.setError("A valid encryption key is required");
        }

        if (txRef.length() < 1){
            valid = false;
            txRefEt.setError("A valid txRef key is required");
        }

        if (currency.length() < 1){
            valid = false;
            currencyEt.setError("A valid currency code is required");
        }

        if (country.length() < 1){
            valid = false;
            countryEt.setError("A valid country code is required");
        }

        if (valid) {
            new RavePayManager(this).setAmount(Double.parseDouble(amount))
                    .setCountry(country)
                    .setCurrency(currency)
                    .setEmail(email)
                    .setfName(fName)
                    .setlName(lName)
                    .setNarration(narration)
                    .setPublicKey(publicKey)
                    .setEncryptionKey(encryptionKey)
                    .setTxRef(txRef)
                    .acceptMpesaPayments(isMpesaSwitch.isChecked())
                    .acceptAccountPayments(accountSwitch.isChecked())
                    .acceptCardPayments(cardSwitch.isChecked())
                    .acceptGHMobileMoneyPayments(ghMobileMoneySwitch.isChecked())
                    .onStagingEnv(!isLiveSwitch.isChecked())
                    .setSubAccounts(subAccounts)
                    .isPreAuth(isPreAuthSwitch.isChecked())
//                    .setMeta(meta)
//                    .withTheme(R.style.TestNewTheme)
                    .initialize();

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
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
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
    }

    private void addVendorDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        // ...Irrelevant code for customizing the buttons and title
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_vendor_layout, null);
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
        addVendorBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Boolean valid = true;
                String vendorRef = vendorReferenceET.getText().toString().trim();
                String vendorRatio = vendorRatioET.getText().toString().trim();

                if(vendorRef.length()<1){
                    vendorReferenceET.setError("Vendor reference is required");
                    valid =  false;
                }
                if(vendorRatioET.length()<1){
                    vendorRatioET.setError("Vendor ratio is required");
                    valid =  false;
                }
                if(!valid){
                    return;
                }
                if(subAccounts.size()!=0){
                    vendorListTXT.setText(vendorListTXT.getText().toString()+", "+ vendorRef +"("+ vendorRatio+")");
                }else{
                    vendorListTXT.setText(vendorListTXT.getText().toString()+ vendorRef +"("+vendorRatio+")");
                }
                subAccounts.add(new SubAccount(vendorRef,vendorRatio));
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

}

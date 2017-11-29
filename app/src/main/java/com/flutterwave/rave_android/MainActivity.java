package com.flutterwave.rave_android;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flutterwave.raveandroid.Meta;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayManager;
import com.flutterwave.raveandroid.Utils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    EditText emailEt;
    EditText amountEt;
    EditText publicKeyEt;
    EditText secretKeyEt;
    EditText txRefEt;
    EditText narrationEt;
    EditText currencyEt;
    EditText countryEt;
    EditText fNameEt;
    EditText lNameEt;
    Button startPayBtn;
    SwitchCompat cardSwitch;
    SwitchCompat accountSwitch;
    SwitchCompat isLiveSwitch;
    List<Meta> meta = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailEt = (EditText) findViewById(R.id.emailEt);
        amountEt = (EditText) findViewById(R.id.amountEt);
        publicKeyEt = (EditText) findViewById(R.id.publicKeyEt);
        secretKeyEt = (EditText) findViewById(R.id.secretKeyEt);
        txRefEt = (EditText) findViewById(R.id.txRefEt);
        narrationEt = (EditText) findViewById(R.id.narrationTV);
        currencyEt = (EditText) findViewById(R.id.currencyEt);
        countryEt = (EditText) findViewById(R.id.countryEt);
        fNameEt = (EditText) findViewById(R.id.fNameEt);
        lNameEt = (EditText) findViewById(R.id.lnameEt);
        startPayBtn = (Button) findViewById(R.id.startPaymentBtn);
        cardSwitch = (SwitchCompat) findViewById(R.id.cardPaymentSwitch);
        accountSwitch = (SwitchCompat) findViewById(R.id.accountPaymentSwitch);
        isLiveSwitch = (SwitchCompat) findViewById(R.id.isLiveSwitch);

        publicKeyEt.setText(RaveConstants.PUBLIC_KEY);
        secretKeyEt.setText(RaveConstants.PRIVATE_KEY);

        meta.add(new Meta("test key 1", "test value 1"));
        meta.add(new Meta("test key 2", "test value 2"));

        startPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateEntries();
            }
        });


    }

    private void validateEntries() {
        clearErrors();
        String email = emailEt.getText().toString();
        String amount = amountEt.getText().toString();
        String publicKey = publicKeyEt.getText().toString();
        String secretKey = secretKeyEt.getText().toString();
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

        if (secretKey.length() < 1){
            valid = false;
            secretKeyEt.setError("A valid secret key is required");
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
                    .setSecretKey(secretKey)
                    .setTxRef(txRef)
                    .acceptAccountPayments(accountSwitch.isChecked())
                    .acceptCardPayments(cardSwitch.isChecked())
                    .onStagingEnv(!isLiveSwitch.isChecked())
                    .setMeta(meta)
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
        secretKeyEt.setError(null);
        txRefEt.setError(null);
        narrationEt.setError(null);
        currencyEt.setError(null);
        countryEt.setError(null);
        fNameEt.setError(null);
        lNameEt.setError(null);
    }

}

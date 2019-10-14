package com.flutterwave.raveandroid.ghmobilemoney;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class GhMobileMoneyFragment extends Fragment implements GhMobileMoneyContract.View {

    View v;
    TextInputEditText amountEt;
    TextInputLayout amountTil;
    TextInputEditText phoneEt;
    TextInputLayout phoneTil;
    RavePayInitializer ravePayInitializer;
    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog ;
    GhMobileMoneyPresenter presenter;
    Spinner networkSpinner;
    TextView instructionsTv;
    TextInputEditText voucherEt;
    TextInputLayout voucherTil;
    String validateInstructions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_gh_mobile_money, container, false);

        presenter = new GhMobileMoneyPresenter(getActivity(), this);
        amountEt = (TextInputEditText) v.findViewById(R.id.rave_amountTV);
        amountTil = (TextInputLayout) v.findViewById(R.id.rave_amountTil);
        phoneEt = (TextInputEditText) v.findViewById(R.id.rave_phoneEt);
        phoneTil = (TextInputLayout) v.findViewById(R.id.rave_phoneTil);
        networkSpinner = (Spinner) v.findViewById(R.id.rave_networkSpinner);
        voucherEt = (TextInputEditText) v.findViewById(R.id.rave_voucherEt);
        voucherTil = (TextInputLayout) v.findViewById(R.id.rave_voucherTil);
        instructionsTv = (TextView) v.findViewById(R.id.instructionsTv);

        Button payButton = (Button) v.findViewById(R.id.rave_payButton);

        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        double amountToPay = ravePayInitializer.getAmount();

        if (amountToPay > 0) {
            amountTil.setVisibility(GONE);
            amountEt.setText(String.valueOf(amountToPay));
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.gh_mobile_money_networks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkSpinner.setAdapter(adapter);

        final String vodafoneInstruction = getResources().getString(R.string.vodafone_msg);
        final String[] networks = getResources().getStringArray(R.array.gh_mobile_money_networks);
        final String mtnValidateInstruction = getResources().getString(R.string.mtn_validate_instructions);
        final String tigoValidateInstruction = getResources().getString(R.string.tigo_validate_instructions);

        networkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < networks.length) {
                    String network = networks[position];

                    if (position == 0) {
                        showInstructionsAndVoucher(false);
                        validateInstructions = "Checking transaction status. \nPlease wait";
                    }

                    if (network.equalsIgnoreCase("mtn")) {
                        validateInstructions = mtnValidateInstruction;
                        showInstructionsAndVoucher(false);
                    }
                    else if (network.equalsIgnoreCase("tigo")) {
                        validateInstructions =  tigoValidateInstruction;
                        showInstructionsAndVoucher(false);
                    }
                    else if (network.equalsIgnoreCase("vodafone")) {
                        validateInstructions = "Checking transaction status. \nPlease wait";
                        showInstructionsAndVoucher(true);
                        instructionsTv.setText(Html.fromHtml(vodafoneInstruction));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showInstructionsAndVoucher(false);
            }
        });

        return v;
    }

    private void showInstructionsAndVoucher(boolean show) {

        if (show) {
            voucherTil.setVisibility(View.VISIBLE);
            instructionsTv.setVisibility(View.VISIBLE);
        }
        else {
            voucherTil.setVisibility(View.GONE);
            instructionsTv.setVisibility(View.GONE);
        }
    }

    private void clearErrors() {
        amountTil.setError(null);
        phoneTil.setError(null);
        voucherTil.setError(null);

        amountTil.setErrorEnabled(false);
        phoneTil.setErrorEnabled(false);
        voucherTil.setErrorEnabled(false);

    }

    private void validate() {
        clearErrors();
        Utils.hide_keyboard(getActivity());

        boolean valid = true;

        String amount = amountEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String voucher = voucherEt.getText().toString();

        try {
            double amnt = Double.parseDouble(amount);

            if (amnt <= 0) {
                valid = false;
                amountTil.setError("Enter a valid amount");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            valid = false;
            amountTil.setError("Enter a valid amount");
        }

        if (phone.length() < 1) {
            valid = false;
            phoneTil.setError("Enter a valid number");
        }

        String network = networkSpinner.getSelectedItem().toString();

        if (networkSpinner.getSelectedItemPosition() == 0) {
            valid = false;
            showToast("Select a network");
        }

        if (voucherTil.getVisibility() == View.VISIBLE && voucher.length() == 0) {
            valid = false;
            voucherTil.setError("Enter a valid voucher code");
        }

        if (valid) {

            ravePayInitializer.setAmount(Double.parseDouble(amount));

            String txRef = ravePayInitializer.getTxRef();
            Log.d("txRef", txRef);
            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(Utils.getDeviceImei(getActivity()))
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setNetwork(network)
                    .setVoucher(voucher)
                    .setPhonenumber(phone)
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setDevice_fingerprint(Utils.getDeviceImei(getActivity()));

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createGhMobileMoneyPayload();

            if(ravePayInitializer.getIsDisplayFee()){
                presenter.fetchFee(body);
            } else {
                presenter.chargeGhMobileMoney(body, ravePayInitializer.getEncryptionKey());
            }
        }

    }

    @Override
    public void showProgressIndicator(boolean active) {

        if (getActivity().isFinishing()) { return; }
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait...");
        }

        if (active && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showPollingIndicator(boolean active) {
        if (getActivity().isFinishing()) { return; }

        if(pollingProgressDialog == null) {
            pollingProgressDialog = new ProgressDialog(getActivity());
            pollingProgressDialog.setCanceledOnTouchOutside(false);
            pollingProgressDialog.setMessage(Html.fromHtml(validateInstructions));
        }

        if (active && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL PAYMENT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    pollingProgressDialog.dismiss();
                }
            });

            pollingProgressDialog.show();
        }
        else if (active && pollingProgressDialog.isShowing()) {
            //pass
        }
        else {
            pollingProgressDialog.dismiss();
        }
    }

    @Override
    public void onPollingRoundComplete(String flwRef, String txRef, String publicKey) {
        if (pollingProgressDialog != null && pollingProgressDialog.isShowing()) {
            presenter.requeryTx(flwRef, txRef, publicKey);
        }
    }

    @Override
    public void onPaymentError(String message) {
//        dismissDialog();
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You will be charged a total of " + charge_amount + ravePayInitializer.getCurrency() + ". Do you want to continue?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                presenter.chargeGhMobileMoney(payload, ravePayInitializer.getEncryptionKey());


            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    @Override
    public void showFetchFeeFailed(String s) {
        showToast(s);
    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {

        if (pollingProgressDialog != null && !pollingProgressDialog.isShowing()) { pollingProgressDialog.dismiss(); }
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }
}


package com.flutterwave.raveandroid.ghmobilemoney;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
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

        return v;
    }

    private void clearErrors() {
        amountTil.setError(null);
        phoneTil.setError(null);

        amountTil.setErrorEnabled(false);
        phoneTil.setErrorEnabled(false);

    }

    private void validate() {
        clearErrors();
        Utils.hide_keyboard(getActivity());

        boolean valid = true;

        String amount = amountEt.getText().toString();
        String phone = phoneEt.getText().toString();

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
                    .setPhonenumber(phone)
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(Utils.getDeviceImei(getActivity()));

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createGhMobileMoneyPayload();

            presenter.fetchFee(body);
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
            pollingProgressDialog.setMessage("Checking transaction status. \nPlease wait");
        }

        if (active && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
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
    public void onPollingRoundComplete(String flwRef, String txRef, String secretKey) {
        if (pollingProgressDialog != null && pollingProgressDialog.isShowing()) {
            presenter.requeryTxv2(flwRef, txRef, secretKey);
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

                presenter.chargeGhMobileMoney(payload, ravePayInitializer.getSecretKey());


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
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }
}


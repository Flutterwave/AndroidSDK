package com.flutterwave.raveandroid.ugmobilemoney;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;

import java.util.HashMap;

import static android.view.View.GONE;

/**
 * Created by Jeremiah on 10/12/2018.
 */
public class UgMobileMoneyFragment extends Fragment implements UgMobileMoneyContract.View {

    View v;
    TextInputEditText amountEt;
    TextInputLayout amountTil;
    TextInputEditText phoneEt;
    TextInputLayout phoneTil;
    RavePayInitializer ravePayInitializer;
    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog ;
    UgMobileMoneyPresenter presenter;
    TextView instructionsTv;
    Button payButton;
    String validateInstructions;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this v
        v = inflater.inflate(R.layout.fragment_ug_mobile_money, container, false);

        initializeViews();

        if (ravePayInitializer.getAmount() > 0) {
            amountTil.setVisibility(GONE);
            amountEt.setText(String.valueOf(ravePayInitializer.getAmount()));
        }

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearErrors();
                formValidate();
            }
        });

        validateInstructions = getResources().getString(R.string.ugx_validate_instructions);

        return v;
    }

    private void initializeViews() {
        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
        presenter = new UgMobileMoneyPresenter(getActivity(), this);
        instructionsTv =  v.findViewById(R.id.instructionsTv);
        amountTil =  v.findViewById(R.id.rave_amountTil);
        phoneTil =  v.findViewById(R.id.rave_phoneTil);
        amountEt =  v.findViewById(R.id.rave_amountTV);
        phoneEt =  v.findViewById(R.id.rave_phoneEt);
        payButton = v.findViewById(R.id.rave_payButton);
    }

    private void showInstructionsAndVoucher(boolean show) {

        if (show) {
            instructionsTv.setVisibility(View.VISIBLE);
        }
        else {
            instructionsTv.setVisibility(View.GONE);
        }
    }

    private void clearErrors() {
        amountTil.setError(null);
        phoneTil.setError(null);
        amountTil.setErrorEnabled(false);
        phoneTil.setErrorEnabled(false);

    }

    private void formValidate() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(getResources().getString(R.string.fieldAmount), new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(getResources().getString(R.string.fieldPhone), new ViewObject(phoneTil.getId(), phoneEt.getText().toString(), TextInputLayout.class));
        presenter.validate(dataHashMap);
    }

    @Override
    public void showProgressIndicator(boolean active) {

        if (getActivity().isFinishing()) { return; }
        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getResources().getString(R.string.wait));
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
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancelPayment), new DialogInterface.OnClickListener() {
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
        intent.putExtra(getResources().getString(R.string.response), responseAsString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.charge) + charge_amount + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                presenter.chargeUgMobileMoney(payload, ravePayInitializer.getEncryptionKey());


            }
        }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
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
        intent.putExtra(getResources().getString(R.string.response), responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

        ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(getResources().getString(R.string.fieldAmount)).getData()));

        presenter.processTransaction(dataHashMap, ravePayInitializer, getActivity());

    }

    @Override
    public void showFieldError(int viewID, String message) {
        TextInputLayout amountView  = (TextInputLayout) v.findViewById(viewID);
        amountView.setError(message);
    }
}


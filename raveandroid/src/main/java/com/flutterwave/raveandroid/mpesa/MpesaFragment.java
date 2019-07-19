package com.flutterwave.raveandroid.mpesa;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;

import java.util.HashMap;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MpesaFragment extends Fragment implements MpesaContract.View, View.OnClickListener {

    View v;
    TextInputEditText amountEt;
    TextInputLayout amountTil;
    TextInputEditText phoneEt;
    TextInputLayout phoneTil;
    RavePayInitializer ravePayInitializer;
    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog ;
    MpesaPresenter presenter;
    Button payButton;
    int rave_phoneEtInt;

    public MpesaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this v
        v = inflater.inflate(R.layout.fragment_mpesa, container, false);

        initializeViews();

        setListeners();

        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();

        presenter.init(ravePayInitializer);

        return v;
    }

    private void setListeners() {
        payButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            clearErrors();
            Utils.hide_keyboard(getActivity());
            collectData();
        }
    }


    @Override
    public void onAmountValidationSuccessful(String amountToPay) {
        amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
    }

    private void collectData() {
        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(RaveConstants.fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(RaveConstants.fieldPhone, new ViewObject(phoneTil.getId(), phoneEt.getText().toString(), TextInputLayout.class));
        presenter.onDataCollected(dataHashMap);
    }

    private void initializeViews() {
        rave_phoneEtInt = v.findViewById(R.id.rave_amountTV).getId();
        presenter = new MpesaPresenter(getActivity(), this);
        payButton =  v.findViewById(R.id.rave_payButton);
        amountTil = v.findViewById(R.id.rave_amountTil);
        amountEt =  v.findViewById(R.id.rave_amountTV);
        phoneTil = v.findViewById(R.id.rave_phoneTil);
        phoneEt =  v.findViewById(R.id.rave_phoneEt);
    }

    @Override
    public void onPollingRoundComplete(String flwRef, String txRef, String publicKey) {

        if (pollingProgressDialog != null && pollingProgressDialog.isShowing()) {
            presenter.requeryTx(flwRef, txRef, publicKey);
        }

    }

    @Override
    public void showPollingIndicator(boolean active) {
        if (getActivity().isFinishing()) { return; }

        if(pollingProgressDialog == null) {
            pollingProgressDialog = new ProgressDialog(getActivity());
            pollingProgressDialog.setMessage(RaveConstants.checkStatus);
        }

        if (active && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, RaveConstants.cancel, new DialogInterface.OnClickListener() {
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
    public void showProgressIndicator(boolean active) {

        if (getActivity().isFinishing()) { return; }

        if(progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(RaveConstants.wait);
        }

        if (active && !progressDialog.isShowing()) {
            progressDialog.show();
        }
        else if (active && progressDialog.isShowing()) {
            //pass
        }
        else {
            progressDialog.dismiss();
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
        intent.putExtra(RaveConstants.response, responseAsString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage((RaveConstants.charge) + charge_amount + ravePayInitializer.getCurrency() + RaveConstants.askToContinue);
        builder.setPositiveButton(RaveConstants.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
        presenter.chargeMpesa(payload, ravePayInitializer.getEncryptionKey());

            }
        }).setNegativeButton(RaveConstants.no, new DialogInterface.OnClickListener() {
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
        intent.putExtra(RaveConstants.response, responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

        presenter.processTransaction(dataHashMap, ravePayInitializer);
        ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(RaveConstants.fieldAmount).getData()));

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

        if (viewType == TextInputLayout.class){
            TextInputLayout view  =  v.findViewById(viewID);
            view.setError(message);
        }
        else if (viewType == EditText.class){
            EditText view  =  v.findViewById(viewID);
            view.setError(message);
        }

    }

    private void clearErrors() {
        amountTil.setError(null);
        phoneTil.setError(null);
        amountTil.setErrorEnabled(false);
        phoneTil.setErrorEnabled(false);

    }
}

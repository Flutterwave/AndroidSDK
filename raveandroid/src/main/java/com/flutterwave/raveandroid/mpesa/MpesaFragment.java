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
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;

import java.util.HashMap;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class MpesaFragment extends Fragment implements MpesaContract.View {

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
    int amountID;

    public MpesaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this v
        v = inflater.inflate(R.layout.fragment_mpesa, container, false);

        initializeViews();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearErrors();
                Utils.hide_keyboard(getActivity());
                formValidate();
            }
        });

        if (ravePayInitializer.getAmount() > 0) {
            amountTil.setVisibility(GONE);
            amountEt.setText(String.valueOf(ravePayInitializer.getAmount()));
        }

        return v;
    }

    private void formValidate() {
        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(getResources().getString(R.string.fieldAmount), new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(getResources().getString(R.string.fieldPhone), new ViewObject(phoneTil.getId(), phoneEt.getText().toString(), TextInputLayout.class));
        presenter.validate(dataHashMap);
    }

    private void initializeViews() {
        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
        rave_phoneEtInt = v.findViewById(R.id.rave_amountTV).getId();
        presenter = new MpesaPresenter(getActivity(), this);
        amountTil = v.findViewById(R.id.rave_amountTil);
        amountEt =  v.findViewById(R.id.rave_amountTV);
        phoneTil = v.findViewById(R.id.rave_phoneTil);
        phoneEt =  v.findViewById(R.id.rave_phoneEt);
        payButton =  v.findViewById(R.id.rave_payButton);
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
            pollingProgressDialog.setMessage(getResources().getString(R.string.checkStatus));
        }

        if (active && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
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
            progressDialog.setMessage(getResources().getString(R.string.wait));
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
        presenter.chargeMpesa(payload, ravePayInitializer.getEncryptionKey());

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
                presenter.processTransaction(dataHashMap, ravePayInitializer);

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

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveApp;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.di.modules.GhanaModule;

import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class GhMobileMoneyFragment extends Fragment implements GhMobileMoneyContract.View, View.OnClickListener {


    @Inject
    GhMobileMoneyPresenter presenter;

    private View v;
    private Button payButton;
    private Spinner networkSpinner;
    private TextView instructionsTv;
    private TextInputLayout phoneTil;
    private TextInputLayout amountTil;
    private TextInputEditText phoneEt;
    private TextInputEditText amountEt;
    private TextInputLayout voucherTil;
    private TextInputEditText voucherEt;
    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog ;

    private String network;
    private String validateInstructions;

    private RavePayInitializer ravePayInitializer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        injectComponents();

        v = inflater.inflate(R.layout.fragment_gh_mobile_money, container, false);

        initializeViews();

        setUpNetworks();

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RaveApp) getActivity().getApplication()).getAppComponent()
                    .plus(new GhanaModule(this))
                    .inject(this);
        }
    }

    private void initializePresenter() {
        if (getActivity() != null) {
            ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
            presenter.init(ravePayInitializer);
        }
    }

    private void setListeners() {
        payButton.setOnClickListener(this);
    }

    private void initializeViews() {
        networkSpinner = v.findViewById(R.id.rave_networkSpinner);
        instructionsTv = v.findViewById(R.id.instructionsTv);
        voucherTil = v.findViewById(R.id.rave_voucherTil);
        voucherEt = v.findViewById(R.id.rave_voucherEt);
        payButton = v.findViewById(R.id.rave_payButton);
        amountTil = v.findViewById(R.id.rave_amountTil);
        phoneTil = v.findViewById(R.id.rave_phoneTil);
        amountEt = v.findViewById(R.id.rave_amountTV);
        phoneEt = v.findViewById(R.id.rave_phoneEt);
    }
    private void setUpNetworks() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.gh_mobile_money_networks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        networkSpinner.setAdapter(adapter);

        networkSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < getResources().getStringArray(R.array.gh_mobile_money_networks).length) {
                    network = getResources().getStringArray(R.array.gh_mobile_money_networks)[position];

                    if (position == 0) {
                        showInstructionsAndVoucher(false);
                        validateInstructions = getResources().getString(R.string.checkStatus);
                    }

                    if (network.equalsIgnoreCase(RaveConstants.mtn)) {
                        validateInstructions = getResources().getString(R.string.mtn_validate_instructions);
                        showInstructionsAndVoucher(false);
                    }
                    else if (network.equalsIgnoreCase(RaveConstants.tigo)) {
                        validateInstructions =  getResources().getString(R.string.tigo_validate_instructions);
                        showInstructionsAndVoucher(false);
                    }
                    else if (network.equalsIgnoreCase(RaveConstants.vodafone)) {
                        validateInstructions = getResources().getString(R.string.checkStatus);
                        showInstructionsAndVoucher(true);
                        instructionsTv.setText(Html.fromHtml(getResources().getString(R.string.vodafone_msg)));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                showInstructionsAndVoucher(false);
            }
        });
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            clearErrors();
            collectData();
        }
    }

    private void clearErrors() {
        amountTil.setError(null);
        phoneTil.setError(null);
        voucherTil.setError(null);
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(RaveConstants.fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(RaveConstants.fieldPhone, new ViewObject(phoneTil.getId(), phoneEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(RaveConstants.fieldNetwork, new ViewObject(networkSpinner.getId(), String.valueOf(networkSpinner.getSelectedItem()), Spinner.class));

        if (voucherTil.getVisibility() == View.VISIBLE) {
            dataHashMap.put(RaveConstants.fieldVoucher, new ViewObject(voucherTil.getId(), voucherEt.getText().toString(), TextInputLayout.class));
        }

        presenter.onDataCollected(dataHashMap);
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

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {
        amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
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

    @Override
    public void showProgressIndicator(boolean active) {

        if (getActivity() != null) {
            if (getActivity().isFinishing()) {
                return;
            }
        }

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
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
        presenter.processTransaction(dataHashMap, ravePayInitializer);
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.charge) + charge_amount + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                presenter.chargeGhMobileMoney(payload, ravePayInitializer.getEncryptionKey());


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
    public void onPaymentFailed(String message, String responseAsJSONString) {

        if (pollingProgressDialog != null && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.dismiss();
        }
        Intent intent = new Intent();
        intent.putExtra(RaveConstants.response, responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentError(String message) {
        showToast(message);
    }

    @Override
    public void showPollingIndicator(boolean active) {
        if (getActivity().isFinishing()) {
            return;
        }

        if (pollingProgressDialog == null) {
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
        } else if (active && pollingProgressDialog.isShowing()) {
            //pass
        } else {
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
    public void onResume() {
        super.onResume();
        if (presenter == null) {
            presenter = new GhMobileMoneyPresenter(getActivity(), this);
        }
        presenter.onAttachView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.onDetachView();
        }
    }
}


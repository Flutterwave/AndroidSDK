package com.flutterwave.raveandroid.ghmobilemoney;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.data.events.InstructionsDisplayedEvent;
import com.flutterwave.raveandroid.data.events.ListItemSelectedEvent;
import com.flutterwave.raveandroid.data.events.RequeryCancelledEvent;
import com.flutterwave.raveandroid.di.modules.GhanaModule;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.events.StartTypingEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.ErrorEvent;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.WEB_VERIFICATION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class GhMobileMoneyFragment extends Fragment implements GhMobileMoneyUiContract.View, View.OnClickListener, View.OnFocusChangeListener {


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

        v = inflater.inflate(R.layout.rave_sdk_fragment_gh_mobile_money, container, false);

        initializeViews();

        setUpNetworks();

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getRaveUiComponent()
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

        amountEt.setOnFocusChangeListener(this);
        phoneEt.setOnFocusChangeListener(this);
        voucherEt.setOnFocusChangeListener(this);
    }

    private void initializeViews() {
        networkSpinner = v.findViewById(R.id.rave_networkSpinner);
        instructionsTv = v.findViewById(R.id.instructionsTv);
        voucherTil = v.findViewById(R.id.rave_voucherTil);
        voucherEt = v.findViewById(R.id.rave_voucherEt);
        payButton = v.findViewById(R.id.rave_payButton);
        amountTil = v.findViewById(R.id.rave_amountTil);
        phoneTil = v.findViewById(R.id.rave_phoneTil);
        amountEt = v.findViewById(R.id.rave_amountEt);
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
                        presenter.logEvent(new ListItemSelectedEvent("Network").getEvent(), ravePayInitializer.getPublicKey());
                        validateInstructions = getResources().getString(R.string.mtn_validate_instructions);
                        showInstructionsAndVoucher(false);
                    }
                    else if (network.equalsIgnoreCase(RaveConstants.tigo)) {
                        presenter.logEvent(new ListItemSelectedEvent("Network").getEvent(), ravePayInitializer.getPublicKey());
                        validateInstructions =  getResources().getString(R.string.tigo_validate_instructions);
                        showInstructionsAndVoucher(false);
                    }
                    else if (network.equalsIgnoreCase(RaveConstants.vodafone)) {
                        presenter.logEvent(new ListItemSelectedEvent("Network").getEvent(), ravePayInitializer.getPublicKey());
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
        dataHashMap.put(RaveConstants.networkPosition, new ViewObject(networkSpinner.getId(), String.valueOf(networkSpinner.getSelectedItemPosition()), Spinner.class));

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

    @Override
    public void onPhoneValidated(String phoneToSet, boolean isEditable) {
        phoneEt.setText(phoneToSet);
        phoneEt.setEnabled(isEditable);
    }

    @Override
    public void showWebPage(String authenticationUrl
//            , String flwRef
    ) {

//        this.flwRef = flwRef;
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showWebpageVerificationScreen(authenticationUrl);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RavePayActivity.RESULT_SUCCESS) {
            //just to be sure this v sent the receiving intent
            if (requestCode == WEB_VERIFICATION_REQUEST_CODE) {
                presenter.requeryTx(ravePayInitializer.getPublicKey());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void showInstructionsAndVoucher(boolean show) {

        if (show) {
            presenter.logEvent(new InstructionsDisplayedEvent("Gh Momo").getEvent(), ravePayInitializer.getPublicKey());
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
    public void onTransactionFeeRetrieved(String chargeAmount, final Payload payload, String fee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.charge) + " " + chargeAmount + " " + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), ravePayInitializer.getPublicKey());

                presenter.chargeGhMobileMoney(payload, ravePayInitializer.getEncryptionKey());


            }
        }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                presenter.logEvent(new FeeDisplayResponseEvent(false).getEvent(), ravePayInitializer.getPublicKey());
            }
        });

        builder.show();
    }

    @Override
    public void showFetchFeeFailed(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(requireContext(), message+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra(RaveConstants.response, responseAsString);

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
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
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentError(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }

    @Override
    public void showPollingIndicator(boolean active, String validateInstruction) {
        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        if (pollingProgressDialog == null) {
            pollingProgressDialog = new ProgressDialog(getActivity());
            pollingProgressDialog.setCanceledOnTouchOutside(false);

            if (validateInstruction.isEmpty()) {
                pollingProgressDialog.setMessage(Html.fromHtml(validateInstructions));
            }else {
                pollingProgressDialog.setMessage(Html.fromHtml(validateInstruction));
            }
        }

        if (active && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancelPayment), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.logEvent(new RequeryCancelledEvent().getEvent(), ravePayInitializer.getPublicKey());
                    presenter.cancelPolling();
                    pollingProgressDialog.dismiss();
                }
            });

            pollingProgressDialog.show();
            presenter.logEvent(new InstructionsDisplayedEvent("GH Momo").getEvent(), ravePayInitializer.getPublicKey());
        } else if (active && pollingProgressDialog.isShowing()) {
            //pass
        } else {
            pollingProgressDialog.dismiss();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null) {
            presenter = new GhMobileMoneyPresenter(this);
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

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        int i = view.getId();

        String fieldName = "";

        if (i == R.id.rave_amountEt) {
            fieldName = "Amount";
        } else if (i == R.id.rave_phoneEt) {
            fieldName = "Phone Number";
        } else if (i == R.id.rave_voucherEt) {
            fieldName = "Voucher";
        }

        if (hasFocus) {
            presenter.logEvent(new StartTypingEvent(fieldName).getEvent(), ravePayInitializer.getPublicKey());
        }
    }
}


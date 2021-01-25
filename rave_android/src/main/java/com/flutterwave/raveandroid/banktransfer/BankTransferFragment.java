package com.flutterwave.raveandroid.banktransfer;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.data.events.InstructionsDisplayedEvent;
import com.flutterwave.raveandroid.data.events.RequeryCancelledEvent;
import com.flutterwave.raveandroid.di.modules.BankTransferModule;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.events.StartTypingEvent;
import com.flutterwave.raveandroid.rave_presentation.data.events.ErrorEvent;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankTransferFragment extends Fragment implements BankTransferUiContract.View, View.OnClickListener, View.OnFocusChangeListener {

    @Inject
    BankTransferPresenter presenter;

    View v;
    TextInputEditText amountEt;
    TextInputLayout amountTil;
    TextView amountTv;
    TextView accountNumberTv;
    TextView bankNameTv;
    TextView beneficiaryNameTv;
    TextView transferInstructionTv;
    TextView transferStatusTv;
    Button verifyPaymentButton;
    Button payButton;
    ConstraintLayout initiateChargeLayout;
    ConstraintLayout transferDetailsLayout;
    RavePayInitializer ravePayInitializer;
    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog;

    public BankTransferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        injectComponents();

        if (savedInstanceState != null) {
            presenter.restoreState(savedInstanceState);
        }

        v = inflater.inflate(R.layout.rave_sdk_fragment_bank_transfer, container, false);

        initializeViews();

        setListeners();

        initializePresenter();

        return v;
    }


    private void injectComponents() {

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getRaveUiComponent()
                    .plus(new BankTransferModule(this))
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
        verifyPaymentButton.setOnClickListener(this);

        amountEt.setOnFocusChangeListener(this);

    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == payButton.getId()) {
            clearErrors();
            Utils.hide_keyboard(getActivity());
            collectData();
        } else if (viewId == verifyPaymentButton.getId()) {
            verifyPayment();
        }
    }

    private void collectData() {
        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(RaveConstants.fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        presenter.onDataCollected(dataHashMap);
    }

    private void initializeViews() {
        amountEt = v.findViewById(R.id.rave_amountEt);
        amountTil = v.findViewById(R.id.rave_amountTil);
        initiateChargeLayout = v.findViewById(R.id.rave_initiate_payment_layout);
        transferDetailsLayout = v.findViewById(R.id.rave_transfer_details_layout);
        transferInstructionTv = v.findViewById(R.id.rave_bank_transfer_instruction);
        transferStatusTv = v.findViewById(R.id.rave_transfer_status_tv);
        amountTv = v.findViewById(R.id.rave_amount_tv);
        beneficiaryNameTv = v.findViewById(R.id.rave_beneficiary_name_tv);
        bankNameTv = v.findViewById(R.id.rave_bank_name_tv);
        accountNumberTv = v.findViewById(R.id.rave_account_number_tv);
        payButton = v.findViewById(R.id.rave_payButton);
        verifyPaymentButton = v.findViewById(R.id.rave_verify_payment_button);
    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {
        amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
    }


    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

        if (viewType == TextInputLayout.class) {
            TextInputLayout view = v.findViewById(viewID);
            view.setError(message);
        } else if (viewType == EditText.class) {
            EditText view = v.findViewById(viewID);
            view.setError(message);
        }

    }


    private void verifyPayment() {
        presenter.startPaymentVerification(300);
    }


    @Override
    public void showPollingIndicator(boolean active) {
        if (getActivity() != null) {
            if (getActivity().isFinishing())
                return;
        }

        if (pollingProgressDialog == null) {
            pollingProgressDialog = new ProgressDialog(getActivity());
            pollingProgressDialog.setMessage("Checking transaction status. \nPlease wait");
        }

        if (active && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.logEvent(new RequeryCancelledEvent().getEvent(), ravePayInitializer.getPublicKey());
                    pollingProgressDialog.dismiss();
                    presenter.cancelPolling();
                }
            });
            pollingProgressDialog.show();
        } else if (active && pollingProgressDialog.isShowing()) {
            //pass
        } else {
            pollingProgressDialog.dismiss();
        }

    }

    private void clearErrors() {
        amountTil.setError(null);

        amountTil.setErrorEnabled(false);

    }


    @Override
    public void onPollingCanceled(String flwRef, String txRef, final String responseAsJSONString) {
        showBackToApp(getString(R.string.bant_transfer_polling_cancelled_message), responseAsJSONString);
    }

    @Override
    public void onPollingTimeout(String flwRef, String txRef, final String responseAsJSONString) {
        showBackToApp(getString(R.string.pay_with_bank_timeout_notification), responseAsJSONString);
    }

    private void showBackToApp(String transferStatusMessage, final String responseAsJSONString) {
        transferStatusTv.setText(transferStatusMessage);
        transferStatusTv.setVisibility(View.VISIBLE);

        verifyPaymentButton.setText(getString(R.string.back_to_app));

        verifyPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("response", responseAsJSONString);
                if (getActivity() != null) {
                    ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_ERROR, intent);
                    getActivity().finish();
                }
            }
        });
    }

    @Override
    public void showProgressIndicator(boolean active) {

        if (getActivity().isFinishing()) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage("Please wait...");
        }

        if (active && !progressDialog.isShowing()) {
            progressDialog.show();
        } else if (active && progressDialog.isShowing()) {
            //pass
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPaymentError(String message) {
//        dismissDialog();
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(requireContext(), message+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, final String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsString);

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentFailed(String message, final String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }

    }

    @Override
    public void onTransferDetailsReceived(String amount, String accountNumber, String bankName, String beneficiaryName) {
        presenter.logEvent(new InstructionsDisplayedEvent("Bank Transfer").getEvent(), ravePayInitializer.getPublicKey());
        showTransferDetails(amount, accountNumber, bankName, beneficiaryName);
    }

    private void showTransferDetails(String amount, String accountNumber, String bankName, String beneficiaryName) {
        amountTv.setText(amount);
        beneficiaryNameTv.setText(beneficiaryName);
        bankNameTv.setText(bankName);
        accountNumberTv.setText(accountNumber);
        transferInstructionTv.setText(
                String.format("%s %s", getString(R.string.bank_transfer_instructions_placeholder), beneficiaryName != null ? beneficiaryName : "this account")
        );

        initiateChargeLayout.setVisibility(GONE);
        transferDetailsLayout.setVisibility(View.VISIBLE);


    }

    @Override
    public void onTransactionFeeFetched(String charge_amount, final Payload payload, String fee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You will be charged a total of " + charge_amount + ravePayInitializer.getCurrency() + ". Do you want to continue?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), ravePayInitializer.getPublicKey());

                presenter.payWithBankTransfer(payload, ravePayInitializer.getEncryptionKey());


            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                presenter.logEvent(new FeeDisplayResponseEvent(false).getEvent(), ravePayInitializer.getPublicKey());
            }
        });

        builder.show();
    }

    @Override
    public void onFetchFeeError(String s) {
        presenter.logEvent(new ErrorEvent(s).getEvent(), ravePayInitializer.getPublicKey());
        showToast(s);
    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

        presenter.processTransaction(dataHashMap, ravePayInitializer);

    }

    @Override
    public void onAmountValidationFailed() {
        amountTil.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
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
        }

        if (hasFocus) {
            presenter.logEvent(new StartTypingEvent(fieldName).getEvent(), ravePayInitializer.getPublicKey());
        }
    }
}

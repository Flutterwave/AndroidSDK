package com.flutterwave.raveandroid.ussd;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.data.events.InstructionsDisplayedEvent;
import com.flutterwave.raveandroid.data.events.ListItemSelectedEvent;
import com.flutterwave.raveandroid.data.events.RequeryCancelledEvent;
import com.flutterwave.raveandroid.di.modules.UssdModule;
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
public class UssdFragment extends Fragment implements UssdUiContract.View, View.OnClickListener, View.OnFocusChangeListener {

    @Inject
    UssdPresenter presenter;


    View rootView;
    View copyReferenceCodeImageView;
    View chooseAnotherBankView;
    ConstraintLayout validatePaymentLayout;
    ConstraintLayout initiatePaymentLayout;
    LinearLayout referenceCodeLayout;
    AppCompatSpinner banksSpinner;
    TextInputEditText amountEt;
    TextInputLayout amountTil;
    TextView ussdCodeTv;
    TextView referenceCodeTv;
    TextView copyReferenceCodeTv;
    TextView paymentStatusMessageTv;
    Button payButton;
    Button verifyUssdPaymentButton;
    String bank;
    RavePayInitializer ravePayInitializer;
    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.rave_sdk_fragment_ussd, container, false);

        injectComponents();
        initializeViews();
        setUpBanks();
        setOnClickListeners();
        initializePresenter();
        return rootView;
    }

    private void initializePresenter() {
        if (getActivity() != null) {
            ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
            presenter.init(ravePayInitializer);
        }
    }


    @Override
    public void onAmountValidationFailed() {
        amountTil.setVisibility(View.VISIBLE);
    }

    @Override
    public void onUssdDetailsReceived(String ussdCode, String referenceCode) {
        presenter.logEvent(new InstructionsDisplayedEvent("USSD").getEvent(), ravePayInitializer.getPublicKey());
        setValidationInstructions(ussdCode, referenceCode);
        showValidationLayout(true);
    }

    private void setValidationInstructions(String ussdCode, String referenceCode) {
        ussdCodeTv.setText(ussdCode);
        referenceCodeTv.setText(referenceCode);
    }

    private void showValidationLayout(boolean show) {
        if (show) {
            initiatePaymentLayout.setVisibility(GONE);
            validatePaymentLayout.setVisibility(View.VISIBLE);
            if (bank.equals(RaveConstants.bankNameGtb)) {
                referenceCodeLayout.setVisibility(View.VISIBLE);
                copyReferenceCodeTv.setVisibility(GONE);
            } else {
                referenceCodeLayout.setVisibility(GONE);
                copyReferenceCodeTv.setVisibility(View.VISIBLE);
            }
        } else {
            initiatePaymentLayout.setVisibility(View.VISIBLE);
            validatePaymentLayout.setVisibility(GONE);
        }
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getRaveUiComponent()
                    .plus(new UssdModule(this))
                    .inject(this);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == payButton.getId()) {
            Utils.hide_keyboard(getActivity());
            clearErrors();
            collectData();
        }
        if (viewId == verifyUssdPaymentButton.getId()) {
            presenter.startPaymentVerification(300);
        }
        if (viewId == chooseAnotherBankView.getId()) {
            showValidationLayout(false);
        }
        if (viewId == copyReferenceCodeImageView.getId()) {
            copyReferenceCodeToClipboard();
        }
        if (viewId == copyReferenceCodeTv.getId()) {
            copyUssdCodeToClipboard();
        }
    }

    private void copyUssdCodeToClipboard() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Copied Text", ussdCodeTv.getText().toString());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            showToast("Copied!");
        }
    }

    private void clearErrors() {
        amountTil.setError(null);
    }

    private void copyReferenceCodeToClipboard() {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", referenceCodeTv.getText().toString());
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            showToast("Copied!");
        }
    }

    private void collectData() {
        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(RaveConstants.fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(RaveConstants.fieldUssdBank, new ViewObject(banksSpinner.getId(), bank, AppCompatSpinner.class));
        presenter.onDataCollected(dataHashMap);
    }

    private void setOnClickListeners() {
        payButton.setOnClickListener(this);
        verifyUssdPaymentButton.setOnClickListener(this);
        chooseAnotherBankView.setOnClickListener(this);
        copyReferenceCodeImageView.setOnClickListener(this);
        copyReferenceCodeTv.setOnClickListener(this);

        amountEt.setOnFocusChangeListener(this);
    }

    private void initializeViews() {
        banksSpinner = rootView.findViewById(R.id.banks_spinner);
        payButton = rootView.findViewById(R.id.pay_button);
        amountTil = rootView.findViewById(R.id.rave_amountTil);
        amountEt = rootView.findViewById(R.id.rave_amountEt);
        ussdCodeTv = rootView.findViewById(R.id.ussd_code_tv);
        referenceCodeTv = rootView.findViewById(R.id.reference_code_tv);
        copyReferenceCodeImageView = rootView.findViewById(R.id.copy_reference_code_view);
        copyReferenceCodeTv = rootView.findViewById(R.id.copy_reference_code_tv);
        chooseAnotherBankView = rootView.findViewById(R.id.choose_another_bank_tv);
        verifyUssdPaymentButton = rootView.findViewById(R.id.verify_ussd_payment_button);
        initiatePaymentLayout = rootView.findViewById(R.id.rave_initiate_payment_layout);
        validatePaymentLayout = rootView.findViewById(R.id.rave_validate_payment_layout);
        referenceCodeLayout = rootView.findViewById(R.id.reference_code_layout);
        paymentStatusMessageTv = rootView.findViewById(R.id.payment_status_Tv);
    }

    private void setUpBanks() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.ussd_banks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        banksSpinner.setAdapter(adapter);

        banksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < getResources().getStringArray(R.array.ussd_banks).length) {
                    if (position != 0) {
                        presenter.logEvent(new ListItemSelectedEvent("Bank").getEvent(), ravePayInitializer.getPublicKey());
                    }
                    bank = getResources().getStringArray(R.array.ussd_banks)[position];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {
        amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
    }

    public void showToast(String message) {
        Toast.makeText(requireContext(), message+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {
        if (viewType == TextInputLayout.class) {
            TextInputLayout view = rootView.findViewById(viewID);
            view.setError(message);
        } else if (viewType == EditText.class) {
            EditText view = rootView.findViewById(viewID);
            view.setError(message);
        } else {
            showToast(message);
        }
    }

    @Override
    public void onDataValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
        presenter.processTransaction(dataHashMap, ravePayInitializer);
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
    public void onTransactionFeeFetched(String charge_amount, final Payload payload, String fee) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You will be charged a total of " + charge_amount + " " + ravePayInitializer.getCurrency() + ". Do you want to continue?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), ravePayInitializer.getPublicKey());

                presenter.payWithUssd(payload, ravePayInitializer.getEncryptionKey());


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
    public void showFetchFeeFailed(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }

    @Override
    public void onPaymentError(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showPollingIndicator(boolean active) {
        if (getActivity() == null || getActivity().isFinishing()) {
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
    public void onPollingCanceled(String flwRef, final String responseAsJSONString) {
        showBackToApp(getString(R.string.bant_transfer_polling_cancelled_message), responseAsJSONString);
    }

    @Override
    public void onPollingTimeout(String flwRef, final String responseAsJSONString) {
        showBackToApp(getString(R.string.pay_with_bank_timeout_notification), responseAsJSONString);
    }

    private void showBackToApp(String transferStatusMessage, final String responseAsJSONString) {
        paymentStatusMessageTv.setText(transferStatusMessage);
        paymentStatusMessageTv.setVisibility(View.VISIBLE);

        chooseAnotherBankView.setVisibility(GONE);

        verifyUssdPaymentButton.setText(getString(R.string.back_to_app));
        verifyUssdPaymentButton.setOnClickListener(new View.OnClickListener() {
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
    public void onPaymentSuccessful(String status, final String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsString);

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
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

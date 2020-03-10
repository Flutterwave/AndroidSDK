package com.flutterwave.raveandroid.uk;


import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.events.ErrorEvent;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.data.events.InstructionsDisplayedEvent;
import com.flutterwave.raveandroid.data.events.RequeryCancelledEvent;
import com.flutterwave.raveandroid.data.events.StartTypingEvent;
import com.flutterwave.raveandroid.di.modules.UkModule;
import com.flutterwave.raveandroid.responses.ChargeResponse;

import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.response;


/**
 * A simple {@link Fragment} subclass.
 */
public class UkFragment extends Fragment implements UkContract.View, View.OnClickListener, View.OnFocusChangeListener {


    @Inject
    UkPresenter presenter;

    private View v;
    private Button payButton;
    private TextInputLayout amountTil;
    private TextInputEditText amountEt;

    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog;

    private int rave_phoneEtInt;
    private RavePayInitializer ravePayInitializer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        injectComponents();

        v = inflater.inflate(R.layout.rave_fragment_uk, container, false);

        initializeViews();

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getAppComponent()
                    .plus(new UkModule(this))
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
    }

    private void initializeViews() {
        payButton = v.findViewById(R.id.rave_payButton);
        amountTil = v.findViewById(R.id.rave_amountTil);
        amountEt = v.findViewById(R.id.rave_amountEt);
        rave_phoneEtInt = amountEt.getId();
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            if (getActivity() != null) {
                Utils.hide_keyboard(getActivity());
                clearErrors();
                collectData();
            }
        }
    }

    private void clearErrors() {
        amountTil.setErrorEnabled(false);
        amountTil.setError(null);
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));

        presenter.onDataCollected(dataHashMap);
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

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {
        amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
    }

    @Override
    public void showProgressIndicator(boolean active) {

        if (getActivity().isFinishing()) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setMessage(getResources().getString(R.string.wait));
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
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
        presenter.processTransaction(dataHashMap, ravePayInitializer);
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload) {
        if (getActivity() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.charge) + charge_amount + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), ravePayInitializer.getPublicKey());
                    presenter.chargeUk(payload, ravePayInitializer.getEncryptionKey());

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
    }

    @Override
    public void showFetchFeeFailed(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showTransactionPage(final ChargeResponse response) {

        if (getContext() != null) {
            final Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.rave_ukinstruction_layout);
            dialog.setTitle("Flutterwave");
            presenter.logEvent(new InstructionsDisplayedEvent("UK").getEvent(), ravePayInitializer.getPublicKey());

            ((TextView) dialog.findViewById(R.id.amount)).setText(String.format("%s %s", "GBP", response.getData().getData().getAmount()));
            ((TextView) dialog.findViewById(R.id.accountNumber)).setText(getString(R.string.flutterwave_ukaccount));
            ((TextView) dialog.findViewById(R.id.sortCode)).setText(getString(R.string.flutterwave_sortcode));
            ((TextView) dialog.findViewById(R.id.reference)).setText(response.getData().getData().getPayment_code());

            dialog.findViewById(R.id.ukPaymentButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    presenter.requeryTx(response.getData().getData().getFlw_reference(), response.getData().getData().getTransaction_reference(), ravePayInitializer.getPublicKey());
                }
            });
            dialog.show();
        }

    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra(response, responseAsString);

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra(response, responseAsJSONString);
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
    public void showPollingIndicator(boolean active) {
        if (getActivity().isFinishing()) {
            return;
        }

        if (pollingProgressDialog == null) {
            pollingProgressDialog = new ProgressDialog(getActivity());
            pollingProgressDialog.setMessage(getResources().getString(R.string.checkStatus));
        }

        if (active && !pollingProgressDialog.isShowing()) {
            pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.logEvent(new RequeryCancelledEvent().getEvent(), ravePayInitializer.getPublicKey());
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

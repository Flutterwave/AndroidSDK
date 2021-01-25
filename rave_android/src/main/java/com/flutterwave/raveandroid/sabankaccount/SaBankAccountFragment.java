package com.flutterwave.raveandroid.sabankaccount;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.di.modules.SaBankModule;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_presentation.data.events.ErrorEvent;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.WEB_VERIFICATION_REQUEST_CODE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SaBankAccountFragment extends Fragment implements SaBankAccountUiContract.View, View.OnClickListener {

    @Inject
    SaBankAccountPresenter presenter;

    private View v;
    private Button payButton;
    private TextInputLayout amountTil;
    private TextInputEditText amountEt;
    private RavePayInitializer ravePayInitializer;

    private ProgressDialog progressDialog;
    private String flwRef;

    private void injectComponents() {
        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getRaveUiComponent()
                    .plus(new SaBankModule(this))
                    .inject(this);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        injectComponents();

        v = inflater.inflate(R.layout.rave_sdk_fragment_sa_bank_account, container, false);

        initializeViews();

        setListeners();

        initializePresenter();


        return v;
    }

    private void initializeViews() {
        payButton = v.findViewById(R.id.rave_payButton);
        amountTil = v.findViewById(R.id.rave_amountTil);
        amountEt = v.findViewById(R.id.rave_amountEt);
    }

    private void setListeners() {
        payButton.setOnClickListener(this);
    }

    private void initializePresenter() {
        if (getActivity() != null) {
            ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
            presenter.init(ravePayInitializer);
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            if (getActivity() != null) {
                Utils.hide_keyboard(getActivity());
                presenter.processTransaction(ravePayInitializer);
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void showToast(String message) {
        Toast.makeText(requireContext(), message+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showFetchFeeFailed(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }

    @Override
    public void onPaymentError(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }

    @Override
    public void showPollingIndicator(boolean active) {

    }

    @Override
    public void showWebView(String authUrl, String flwRef){
        this.flwRef = flwRef;
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showWebpageVerificationScreen(authUrl);
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
    public void onTransactionFeeRetrieved(String chargeAmount, final Payload payload, String fee) {
        if (getActivity() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.charge) + chargeAmount + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), ravePayInitializer.getPublicKey());
                    presenter.chargeSaBankAccount(payload, ravePayInitializer.getEncryptionKey());

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
    public void onAmountValidationSuccessful(String amountToPay, String currency) {
        //amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
        payButton.setText("Pay " + currency + " " + amountToPay);
    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

    }

    @Override
    public void onPaymentFailed(String message, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra(RaveConstants.response, responseAsJSONString);
        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
        presenter.processTransaction(ravePayInitializer);
    }

    @Override
    public void onPaymentSuccessful(String status, String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra(RaveConstants.response, responseAsString);

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RavePayActivity.RESULT_SUCCESS) {
            //just to be sure this fragment sent the receiving intent
            if (requestCode == WEB_VERIFICATION_REQUEST_CODE) {
                presenter.requeryTx(ravePayInitializer.getPublicKey(), flwRef);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}

package com.flutterwave.raveandroid.barter;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.di.modules.BarterModule;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.BARTER_CHECKOUT_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.response;


public class BarterFragment extends Fragment implements BarterUiContract.View {


    @Inject
    BarterPresenter presenter;

    private View v;
    private Button payButton;
    private TextInputLayout amountTil;
    private TextInputEditText amountEt;

    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog;

    private RavePayInitializer ravePayInitializer;
    private String flwRef = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        injectComponents();

        v = inflater.inflate(R.layout.rave_sdk_fragment_barter, container, false);

        initializeViews();

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {
        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getRaveUiComponent()
                    .plus(new BarterModule(this))
                    .inject(this);
        }
    }

    private void initializeViews() {
        payButton = v.findViewById(R.id.rave_payButton);
        amountTil = v.findViewById(R.id.rave_amountTil);
        amountEt = v.findViewById(R.id.rave_amountEt);
    }

    private void setListeners() {
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() != null) {
                    Utils.hide_keyboard(getActivity());
                    clearErrors();
                    collectData();
                }
            }
        });
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));

        presenter.onDataCollected(dataHashMap);
    }

    private void initializePresenter() {
        if (getActivity() != null) {
            ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
            presenter.init(ravePayInitializer);
        }
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
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
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
    public void showFetchFeeFailed(String message) {
        showToast(message);
    }

    @Override
    public void loadBarterCheckout(String authUrlCrude, String flwRef) {

        this.flwRef = flwRef;
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showBarterCheckoutScreen(authUrlCrude, flwRef);

    }


    @Override
    public void onPaymentError(String message) {
        showToast(message);
    }

    public void showToast(String message) {
        Toast.makeText(requireContext(), message+"", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTransactionFeeFetched(String charge_amount, final Payload payload, String fee) {
        if (getActivity() != null) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.charge) + " " + charge_amount + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    presenter.chargeBarter(payload, ravePayInitializer.getEncryptionKey());

                }
            }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //just to be sure this v sent the receiving intent
        if (requestCode == BARTER_CHECKOUT_REQUEST_CODE) {
            if (data != null && data.hasExtra(response)) {
                if (resultCode == RavePayActivity.RESULT_SUCCESS)
                    onPaymentSuccessful(flwRef, data.getStringExtra(response));
                else if (resultCode == RavePayActivity.RESULT_ERROR)
                    onPaymentFailed(flwRef, data.getStringExtra(response));
            } else presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void showPollingIndicator(boolean active) {
        if (getActivity() == null || getActivity().isFinishing()) {
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
    public void onPaymentSuccessful(String flwRef, String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra(response, responseAsString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentFailed(String flwRef, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra(response, responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPollingCanceled(String flwRef, String responseAsJSONString) {
        //
    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {
        amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
    }

    private void clearErrors() {
        amountTil.setErrorEnabled(false);
        amountTil.setError(null);
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
}

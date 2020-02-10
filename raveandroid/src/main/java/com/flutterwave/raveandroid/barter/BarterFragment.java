package com.flutterwave.raveandroid.barter;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.flutterwave.raveandroid.di.modules.BarterModule;
import com.flutterwave.raveandroid.verification.VerificationActivity;
import com.flutterwave.raveandroid.verification.web.WebFragment;

import java.util.HashMap;

import javax.inject.Inject;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.BARTER_CHECKOUT;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.response;
import static com.flutterwave.raveandroid.verification.VerificationActivity.EXTRA_IS_STAGING;


public class BarterFragment extends Fragment implements BarterContract.View {


    public static final int FOR_BARTER_CHECKOUT = 5555;
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

    public BarterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        injectComponents();

        v = inflater.inflate(R.layout.fragment_barter, container, false);

        initializeViews();

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {
        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getAppComponent()
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
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authUrlCrude);
        intent.putExtra(WebFragment.EXTRA_FLW_REF, flwRef);
        intent.putExtra(WebFragment.EXTRA_PUBLIC_KEY, ravePayInitializer.getPublicKey());
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, BARTER_CHECKOUT);
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_BARTER_CHECKOUT);

    }


    @Override
    public void onPaymentError(String message) {
        showToast(message);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload) {
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
        if (requestCode == FOR_BARTER_CHECKOUT) {
            if (data != null && data.hasExtra(response)) {
                if (resultCode == RavePayActivity.RESULT_SUCCESS)
                    onPaymentSuccessful(data.getStringExtra(response));
                else if (resultCode == RavePayActivity.RESULT_ERROR)
                    onPaymentFailed(data.getStringExtra(response));
            } else presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
    public void onPaymentSuccessful(String responseAsString) {
        Intent intent = new Intent();
        intent.putExtra(response, responseAsString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentFailed(String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra(response, responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPollingRoundComplete(final String flwRef, final String publicKey) {
        if (pollingProgressDialog != null && pollingProgressDialog.isShowing()) {

            Handler handler = new Handler();
            Runnable r = new Runnable() {
                public void run() {
                    presenter.requeryTx(flwRef, publicKey);
                }
            };
            handler.postDelayed(r, 1000);

        }
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

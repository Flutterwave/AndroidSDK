package com.flutterwave.raveandroid.banktransfer;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.responses.ChargeResponse;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankTransferFragment extends Fragment implements BankTransferContract.View {

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
    ConstraintLayout initiateChargeLayout;
    ConstraintLayout transferDetailsLayout;
    RavePayInitializer ravePayInitializer;
    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog;
    BankTransferPresenter presenter;
    boolean canShowPollingIndicator = false;

    public BankTransferFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_bank_transfer, container, false);

        presenter = new BankTransferPresenter(getActivity(), this);
        amountEt = (TextInputEditText) v.findViewById(R.id.rave_amountTV);
        amountTil = (TextInputLayout) v.findViewById(R.id.rave_amountTil);
        initiateChargeLayout = (ConstraintLayout) v.findViewById(R.id.rave_initiate_payment_layout);
        transferDetailsLayout = (ConstraintLayout) v.findViewById(R.id.rave_transfer_details_layout);
        transferInstructionTv = (TextView) v.findViewById(R.id.rave_bank_transfer_instruction);
        transferStatusTv = (TextView) v.findViewById(R.id.rave_transfer_status_tv);
        amountTv = (TextView) v.findViewById(R.id.rave_amount_tv);
        beneficiaryNameTv = (TextView) v.findViewById(R.id.rave_beneficiary_name_tv);
        bankNameTv = (TextView) v.findViewById(R.id.rave_bank_name_tv);
        accountNumberTv = (TextView) v.findViewById(R.id.rave_account_number_tv);

        Button payButton = (Button) v.findViewById(R.id.rave_payButton);
        verifyPaymentButton = (Button) v.findViewById(R.id.rave_verify_payment_button);

        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }

        });
        verifyPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyPayment();
            }
        });

        double amountToPay = ravePayInitializer.getAmount();

        if (amountToPay > 0) {
            amountTil.setVisibility(GONE);
            amountEt.setText(String.valueOf(amountToPay));
        }

        return v;
    }

    private void verifyPayment() {
        canShowPollingIndicator = true;
        showPollingIndicator(true);
        presenter.setRequeryCountdownTime(System.currentTimeMillis());

    }

    @Override
    public void onPollingRoundComplete(String flwRef, String txRef, String publicKey) {

        if (canShowPollingIndicator) {
            if (pollingProgressDialog != null && pollingProgressDialog.isShowing()) {
                presenter.requeryTx(flwRef, txRef, publicKey);
            }
        } else presenter.requeryTx(flwRef, txRef, publicKey);


    }

    @Override
    public void showPollingIndicator(boolean active) {
        if (getActivity().isFinishing()) {
            return;
        }

        if (canShowPollingIndicator) {
            if (pollingProgressDialog == null) {
                pollingProgressDialog = new ProgressDialog(getActivity());
                pollingProgressDialog.setMessage("Checking transaction status. \nPlease wait");
            }

            if (active && !pollingProgressDialog.isShowing()) {
                pollingProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
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
    }

    private void clearErrors() {
        amountTil.setError(null);

        amountTil.setErrorEnabled(false);

    }

    @Override
    public void onTransferDetailsReceived(ChargeResponse response) {
        showTransferDetails(response);
    }

    @Override
    public void onPollingTimeout(String flwRef, String txRef, final String responseAsJSONString) {
        transferStatusTv.setText(getString(R.string.pay_with_bank_timeout_notification));
        transferStatusTv.setVisibility(View.VISIBLE);

        verifyPaymentButton.setText(getString(R.string.back_to_app));
        verifyPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("response", responseAsJSONString);
                if (getActivity() != null) {
                    getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
                    getActivity().finish();
                }
            }
        });
    }

    private void showTransferDetails(ChargeResponse response) {
        String beneficiaryName = response.getData().getNote().substring(
                response.getData().getNote().indexOf("to ") + 3
        );

        amountTv.setText(response.getData().getAmount());
        beneficiaryNameTv.setText(beneficiaryName);
        bankNameTv.setText(response.getData().getBankname());
        accountNumberTv.setText(response.getData().getAccountnumber());
        transferInstructionTv.setText(
                transferInstructionTv.getText() + " " + beneficiaryName
        );

        initiateChargeLayout.setVisibility(GONE);
        transferDetailsLayout.setVisibility(View.VISIBLE);


    }

    private void validate() {
        clearErrors();
        Utils.hide_keyboard(getActivity());

        boolean valid = true;

        String amount = amountEt.getText().toString();

        try {
            double amnt = Double.parseDouble(amount);

            if (amnt <= 0) {
                valid = false;
                amountTil.setError("Enter a valid amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
            valid = false;
            amountTil.setError("Enter a valid amount");
        }

        if (valid) {

            ravePayInitializer.setAmount(Double.parseDouble(amount));

            String txRef = ravePayInitializer.getTxRef();
            Log.d("txRef", txRef);
            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(Utils.getDeviceImei(getActivity()))
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setDevice_fingerprint(Utils.getDeviceImei(getActivity()));


            Payload body = builder.createBankTransferPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                presenter.fetchFee(body);
            } else {
                presenter.payWithBankTransfer(body, ravePayInitializer.getEncryptionKey());
            }
        }

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
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccessful(String status, String flwRef, final String responseAsString) {

        if (canShowPollingIndicator) { //Verify payment button has been clicked previously
            Intent intent = new Intent();
            intent.putExtra("response", responseAsString);

            if (getActivity() != null) {
                getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        } else {
            verifyPaymentButton.setText(getString(R.string.proceed));
            transferStatusTv.setText(getString(R.string.transfer_received_successfully));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                transferStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_circle_black_24dp, 0, 0, 0);
            } else transferStatusTv.setTextColor(Color.parseColor("#4BB543"));
            transferStatusTv.setVisibility(View.VISIBLE);

            verifyPaymentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("response", responseAsString);

                    if (getActivity() != null) {
                        getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
                        getActivity().finish();
                    }
                }
            });
        }
    }

    @Override
    public void onPaymentFailed(String message, final String responseAsJSONString) {
        if (canShowPollingIndicator) {// Verify Payment button has been clicked
            Intent intent = new Intent();
            intent.putExtra("response", responseAsJSONString);
            if (getActivity() != null) {
                getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
                getActivity().finish();
            }
        } else {
            transferStatusTv.setText(getString(R.string.payment_failed));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                transferStatusTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_error_black_24dp, 0, 0, 0);
            } else transferStatusTv.setTextColor(Color.parseColor("#FC100D"));
            transferStatusTv.setVisibility(View.VISIBLE);
            verifyPaymentButton.setText(getString(R.string.back_to_app));
            verifyPaymentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.putExtra("response", responseAsJSONString);
                    if (getActivity() != null) {
                        getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
                        getActivity().finish();
                    }
                }
            });
        }
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You will be charged a total of " + charge_amount + ravePayInitializer.getCurrency() + ". Do you want to continue?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                presenter.payWithBankTransfer(payload, ravePayInitializer.getEncryptionKey());


            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
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


}

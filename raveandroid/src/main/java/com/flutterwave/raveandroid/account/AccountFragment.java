package com.flutterwave.raveandroid.account;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.events.ErrorEvent;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.data.events.ListItemSelectedEvent;
import com.flutterwave.raveandroid.data.events.StartTypingEvent;
import com.flutterwave.raveandroid.di.modules.AccountModule;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.verification.OTPFragment;
import com.flutterwave.raveandroid.verification.VerificationActivity;
import com.flutterwave.raveandroid.verification.web.WebFragment;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.RaveConstants.fieldAccount;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldBVN;
import static com.flutterwave.raveandroid.RaveConstants.fieldBankCode;
import static com.flutterwave.raveandroid.RaveConstants.fieldDOB;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.verification.VerificationActivity.EXTRA_IS_STAGING;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements AccountContract.View, DatePickerDialog.OnDateSetListener, View.OnClickListener, View.OnFocusChangeListener {

    @Inject
    AccountPresenter presenter;

    public static final int FOR_0TP = 222;
    public static final int FOR_INTERNET_BANKING = 111;
    private View v;
    private String flwRef;
    private EditText bankEt;
    private TextInputLayout amountTil;
    private TextInputEditText emailEt;
    private Button payButton;
    private TextView pcidss_tv;
    private TextInputEditText phoneEt;
    private TextInputLayout phoneTil;
    private TextInputEditText amountEt;
    private EditText dateOfBirthEt, bvnEt;
    private ProgressDialog progessDialog;
    private TextInputEditText accountNumberEt;
    private TextInputLayout accountNumberTil;
    private RavePayInitializer ravePayInitializer;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputLayout emailTil, rave_bvnTil;
    private Calendar calendar = Calendar.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        injectComponents();

        v = inflater.inflate(R.layout.fragment_account, container, false);

        initializeViews();

        pcidss_tv.setMovementMethod(LinkMovementMethod.getInstance());

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getAppComponent()
                    .plus(new AccountModule(this))
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
        bankEt.setOnClickListener(this);
        payButton.setOnClickListener(this);
        dateOfBirthEt.setOnClickListener(this);

        accountNumberEt.setOnFocusChangeListener(this);
        amountEt.setOnFocusChangeListener(this);
        emailEt.setOnFocusChangeListener(this);
        phoneEt.setOnFocusChangeListener(this);
        rave_bvnTil.getEditText().setOnFocusChangeListener(this);


    }

    private void initializeViews() {
        accountNumberTil = v.findViewById(R.id.rave_accountNumberTil);
        accountNumberEt = v.findViewById(R.id.rave_accountNumberEt);
        pcidss_tv = v.findViewById(R.id.rave_pcidss_compliant_tv);
        dateOfBirthEt = v.findViewById(R.id.rave_dobEditText);
        bankEt = v.findViewById(R.id.rave_bankEditText);
        amountTil = v.findViewById(R.id.rave_amountTil);
        payButton = v.findViewById(R.id.rave_payButton);
        rave_bvnTil = v.findViewById(R.id.rave_bvnTil);
        amountEt = v.findViewById(R.id.rave_amountEt);
        emailTil = v.findViewById(R.id.rave_emailTil);
        phoneTil = v.findViewById(R.id.rave_phoneTil);
        phoneEt = v.findViewById(R.id.rave_phoneEt);
        emailEt = v.findViewById(R.id.rave_emailEt);
        bvnEt = v.findViewById(R.id.rave_bvnEt);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            Utils.hide_keyboard(requireActivity());
            clearErrors();
            collectData();
        } else if (i == R.id.rave_bankEditText) {
            presenter.getBanks();
        } else if (i == R.id.rave_dobEditText) {
            if (getActivity() != null) {
                new DatePickerDialog(getActivity(), AccountFragment.this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }
    }

    private void clearErrors() {
        accountNumberTil.setErrorEnabled(false);
        rave_bvnTil.setErrorEnabled(false);
        emailTil.setErrorEnabled(false);
        phoneTil.setErrorEnabled(false);
        accountNumberTil.setError(null);
        dateOfBirthEt.setError(null);
        rave_bvnTil.setError(null);
        phoneTil.setError(null);
        emailTil.setError(null);
        bankEt.setError(null);
        bankEt.setError(null);
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(fieldEmail, new ViewObject(emailTil.getId(), emailEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldPhone, new ViewObject(phoneTil.getId(), phoneEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldDOB, new ViewObject(dateOfBirthEt.getId(), dateOfBirthEt.getText().toString(), EditText.class));
        dataHashMap.put(fieldBVN, new ViewObject(rave_bvnTil.getId(), bvnEt.getText().toString(), TextInputLayout.class));

        if (accountNumberTil.getVisibility() == View.VISIBLE) {
            dataHashMap.put(fieldAccount, new ViewObject(accountNumberTil.getId(), accountNumberEt.getText().toString(), TextInputLayout.class));
        }

        String selectedBankCode = null;

        if (bankEt.getTag() != null) {
            selectedBankCode = ((Bank) bankEt.getTag()).getBankcode();
        }
        dataHashMap.put(fieldBankCode, new ViewObject(bankEt.getId(), selectedBankCode, EditText.class));

        presenter.onDataCollected(dataHashMap);
    }

    @Override
    public void showGTBankAmountIssue() {

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.payWithBankAmountLimitPrompt));
            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    }

    @Override
    public void onEmailValidated(String emailToSet, int visibility) {
        emailTil.setVisibility(visibility);
        emailEt.setText(emailToSet);
    }

    @Override
    public void onAmountValidated(String amountToSet, int visibility) {
        amountTil.setVisibility(visibility);
        amountEt.setText(amountToSet);
    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
        presenter.processTransaction(dataHashMap, ravePayInitializer);
    }

    @Override
    public void onValidateError(String message, String responseAsJSonString) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
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
    public void onValidationSuccessful(String flwRef, String responseAsJsonString) {
        presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload, final boolean internetbanking) {

        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.charge) + " " + charge_amount + " " + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), payload.getPBFPubKey());
                    dialog.dismiss();
                    presenter.chargeAccount(payload, ravePayInitializer.getEncryptionKey(), internetbanking);
                }
            }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    presenter.logEvent(new FeeDisplayResponseEvent(false).getEvent(), payload.getPBFPubKey());
                    dialog.dismiss();
                }
            });
            builder.show();
        }

    }

    @Override
    public void showFetchFeeFailed(String s) {
        presenter.logEvent(new ErrorEvent(s).getEvent(), ravePayInitializer.getPublicKey());
        showToast(s);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentSuccessful(String status, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentFailed(String status, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
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
    public void showAccountNumberField(int isVisible) {
        accountNumberTil.setVisibility(isVisible);
    }

    @Override
    public void showDateOfBirth(int isVisible) {
        dateOfBirthEt.setVisibility(isVisible);
    }

    @Override
    public void showBVN(int isVisible) {
        rave_bvnTil.setVisibility(isVisible);
    }

    @Override
    public void showBanks(List<Bank> banks) {

        if (getActivity() != null) {
            bottomSheetDialog = new BottomSheetDialog(getActivity());

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.add_exisiting_bank, null, false);
            RecyclerView recyclerView = v.findViewById(R.id.rave_recycler);

            BanksRecyclerAdapter adapter = new BanksRecyclerAdapter();
            adapter.set(banks);

            adapter.setBankSelectedListener(new Callbacks.BankSelectedListener() {
                @Override
                public void onBankSelected(Bank b) {
                    bottomSheetDialog.dismiss();
                    bankEt.setError(null);
                    bankEt.setText(b.getBankname());
                    bankEt.setTag(b);
                    presenter.logEvent(new ListItemSelectedEvent("Bank").getEvent(), ravePayInitializer.getPublicKey());
                    presenter.onBankSelected(b);

                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            bottomSheetDialog.setContentView(v);
            bottomSheetDialog.show();
        }
    }

    @Override
    public void showProgressIndicator(boolean active) {

        try {
            if (getActivity().isFinishing()) {
                return;
            }

            if (progessDialog == null) {
                progessDialog = new ProgressDialog(getActivity());
                progessDialog.setCanceledOnTouchOutside(false);
                progessDialog.setMessage("Please wait...");
            }

            if (active && !progessDialog.isShowing()) {
                progessDialog.show();
            } else {
                progessDialog.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetBanksRequestFailed(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }

    @Override
    public void validateAccountCharge(String pbfPubKey, String flwRef, String validateInstruction) {
        this.flwRef = flwRef;

        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "otp");
        if (validateInstruction != null) {
            intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, validateInstruction);
        }
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_0TP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RavePayActivity.RESULT_SUCCESS) {

            if (requestCode == FOR_0TP) {
                String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                presenter.validateAccountCharge(flwRef, otp, ravePayInitializer.getPublicKey());
            } else if (requestCode == FOR_INTERNET_BANKING) {
                presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDisplayInternetBankingPage(String authurl, String flwRef) {
        this.flwRef = flwRef;
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authurl);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "web");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_INTERNET_BANKING);
    }

    @Override
    public void onChargeAccountFailed(String message, String responseAsJSONString) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
        showToast(message);
    }


    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString) {
        presenter.verifyRequeryResponseStatus(response, responseAsJSONString, ravePayInitializer);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null) {
            presenter = new AccountPresenter(getActivity(), this);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String formattedDay;
        String formattedMonth;

        dateOfBirthEt.setError(null);

        if (String.valueOf(dayOfMonth).length() != 2) {
            formattedDay = "0" + dayOfMonth;
        } else {
            formattedDay = dayOfMonth + "";
        }

        if (String.valueOf(month + 1).length() != 2) {
            formattedMonth = "0" + (month + 1);
        } else {
            formattedMonth = (month + 1) + "";
        }


        dateOfBirthEt.setText(formattedDay + "/" + formattedMonth + "/" + year);
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        int i = view.getId();

        String fieldName = "";

        if (i == R.id.rave_accountNumberEt) {
            fieldName = "Account Number";
        } else if (i == R.id.rave_amountEt) {
            fieldName = "Amount";
        } else if (i == R.id.rave_emailEt) {
            fieldName = "Email";
        } else if (i == R.id.rave_phoneEt) {
            fieldName = "Phone Number";
        } else if (i == R.id.rave_bvnEt) {
            fieldName = "BVN";
        }

        if (hasFocus) {
            presenter.logEvent(new StartTypingEvent(fieldName).getEvent(), ravePayInitializer.getPublicKey());
        }
//        presenter.logEvent(new StopTypingEvent(fieldName).getEvent(),ravePayInitializer.getPublicKey());
    }
}

package com.flutterwave.raveandroid.account;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.VerificationActivity;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.OTPFragment;
import com.flutterwave.raveandroid.WebFragment;
import com.flutterwave.raveandroid.responses.RequeryResponse;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements AccountContract.View, DatePickerDialog.OnDateSetListener {
    public static final int FOR_INTERNET_BANKING = 111;
    public static final int FOR_0TP = 222;
    TextInputEditText accountNumberEt;
    TextInputLayout accountNumberTil;
    EditText bankEt;
    Button payButton;
    AccountPresenter presenter;
    Bank selectedBank;
    TextView pcidss_tv;
    private ProgressDialog progessDialog;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputEditText amountEt;
    private TextInputLayout amountTil;
    private TextInputEditText emailEt;
    private TextInputLayout emailTil,rave_bvnTil;
    private TextInputEditText phoneEt;
    private TextInputLayout phoneTil;
    private String flwRef;
    private RavePayInitializer ravePayInitializer;
    private EditText dateOfBirthEt,bvnEt;
    Calendar calendar = Calendar.getInstance();

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new AccountPresenter(getActivity(), this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account, container, false);

        bankEt = (EditText) v.findViewById(R.id.rave_bankEditText);
        amountEt = (TextInputEditText) v.findViewById(R.id.rave_amountTV);
        amountTil = (TextInputLayout) v.findViewById(R.id.rave_amountTil);
        phoneEt = (TextInputEditText) v.findViewById(R.id.rave_phoneEt);
        phoneTil = (TextInputLayout) v.findViewById(R.id.rave_phoneTil);
        emailEt = (TextInputEditText) v.findViewById(R.id.rave_emailEt);
        emailTil = (TextInputLayout) v.findViewById(R.id.rave_emailTil);
        rave_bvnTil = (TextInputLayout) v.findViewById(R.id.rave_bvnTil);
        accountNumberEt = (TextInputEditText) v.findViewById(R.id.rave_accountNumberEt);
        accountNumberTil = (TextInputLayout) v.findViewById(R.id.rave_accountNumberTil);
        payButton = (Button) v.findViewById(R.id.rave_payButton);
        pcidss_tv = (TextView) v.findViewById(R.id.rave_pcidss_compliant_tv);
        dateOfBirthEt = (EditText) v.findViewById(R.id.rave_dobEditText);
        bvnEt = (EditText) v.findViewById(R.id.rave_bvnEt);

        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern = Pattern.compile("()PCI-DSS COMPLIANT");
        Linkify.addLinks(pcidss_tv, pattern, "https://www.pcisecuritystandards.org/pci_security/", null, filter);

        FrameLayout internetBankingLayout = (FrameLayout) v.findViewById(R.id.rave_internetBankingBottomSheet);

        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();

        bankEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.getBanks();
            }
        });

        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateDetails();
            }
        });

        dateOfBirthEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), AccountFragment.this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


        if (Utils.isEmailValid(ravePayInitializer.getEmail())) {
            emailTil.setVisibility(GONE);
            emailEt.setText(ravePayInitializer.getEmail());
        }

        double amountToPay = ravePayInitializer.getAmount();

        if (amountToPay != -1) {
            amountTil.setVisibility(GONE);
            amountEt.setText(String.valueOf(amountToPay));
        }


        return v;
    }

    private void validateDetails() {
        bankEt.setError(null);
        Utils.hide_keyboard(getActivity());

        accountNumberTil.setError(null);
        accountNumberTil.setErrorEnabled(false);
        emailTil.setError(null);
        emailTil.setErrorEnabled(false);
        phoneTil.setError(null);
        phoneTil.setErrorEnabled(false);
        bankEt.setError(null);
        dateOfBirthEt.setError(null);
        rave_bvnTil.setError(null);
        rave_bvnTil.setErrorEnabled(false);

        boolean valid = true;

        String accountNo = accountNumberEt.getText().toString();
        String amount = amountEt.getText().toString();
        String email = emailEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String dob = dateOfBirthEt.getText().toString();
        String bvn = bvnEt.getText().toString().trim();

        if (phone.length() < 1) {
            valid = false;
            phoneTil.setError("Enter a valid number");
        }

        if (!Utils.isEmailValid(email)) {
            valid = false;
            emailTil.setError("Enter a valid email");
        }


        if (accountNumberTil.getVisibility() == View.VISIBLE) {
            if (accountNo.length() != 10) {
                valid = false;
                accountNumberTil.setError("Enter a valid account number");
            }
        } else {
            accountNo = "0000000000";
        }

        try {
            double amnt = Double.parseDouble(amount);

            if (amnt <= 0) {
                valid = false;
                showToast("Enter a valid amount");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            valid = false;
            showToast("Enter a valid amount");
        }

        if (selectedBank == null) {
            valid = false;
            bankEt.setError("Select a bank");
        }
        else {
            //for Zenith Bank
            if (selectedBank.getBankcode().equals("057") || selectedBank.getBankcode().equals("033")){
                if (dob.length() != 10) {
                    valid = false;
                    dateOfBirthEt.setError("Enter a valid date of birth");
                }
                else {
                    dob = dob.replace("/", "");
                }
            }
        }

        if(selectedBank.getBankcode().equals("033")){
            if(bvn.length() != 11){
                rave_bvnTil.setError("Enter a valid BVN");
                valid = false;
            }
        }

        if (valid) {
            String txRef = ravePayInitializer.getTxRef();

            ravePayInitializer.setAmount(Double.parseDouble(amount));

            //make request
            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
                    .setEmail(email)
                    .setCountry("NG").setCurrency("NGN")
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(Utils.getDeviceImei(getActivity()))
                    .setIP(Utils.getDeviceImei(getActivity())).setTxRef(txRef)
                    .setAccountbank(selectedBank.getBankcode())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setAccountnumber(accountNo)
                    .setBVN(bvn)
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth());

            Payload body = builder.createBankPayload();
            body.setPasscode(dob);
            body.setPhonenumber(phone);

            if ((selectedBank.getBankcode().equalsIgnoreCase("058") ||
                    selectedBank.getBankcode().equalsIgnoreCase("011"))
                            && (Double.parseDouble(amount) <= 100)) {
                showGTBankAmountIssue();
            }
            else {
                if(ravePayInitializer.getIsDisplayFee()){
                    presenter.fetchFee(body, selectedBank.isInternetbanking());
                } else {
                    presenter.chargeAccount(body, ravePayInitializer.getEncryptionKey(), selectedBank.isInternetbanking());
                }
            }

        }
    }

    private void showGTBankAmountIssue() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Bank payments made with this bank must be greater than 100 naira. Please select another bank or increase the amount you're paying and try again");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showBanks(List<Bank> banks) {
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.add_exisiting_bank, null, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rave_recycler);

        BanksRecyclerAdapter adapter = new BanksRecyclerAdapter();
        adapter.set(banks);

        adapter.setBankSelectedListener(new Callbacks.BankSelectedListener() {
            @Override
            public void onBankSelected(Bank b) {
                bottomSheetDialog.dismiss();
                bankEt.setError(null);
                bankEt.setText(b.getBankname());
                selectedBank = b;
                if (selectedBank.isInternetbanking()) {
                    accountNumberTil.setVisibility(View.GONE);
                } else {
                    accountNumberTil.setVisibility(View.VISIBLE);
                }

                if (selectedBank.getBankcode().equals("057")  || selectedBank.getBankcode().equals("033")) {
                    dateOfBirthEt.setVisibility(View.VISIBLE);
                }
                else {
                    dateOfBirthEt.setVisibility(View.GONE);
                }
                if(selectedBank.getBankcode().equals("033")){
                    rave_bvnTil.setVisibility(View.VISIBLE);
                }else{
                    rave_bvnTil.setVisibility(View.GONE);
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.show();

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
        }
        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetBanksRequestFailed(String message) {
        showToast(message);
    }

    @Override
    public void validateAccountCharge(String pbfPubKey, String flwRef, String validateInstruction) {
        this.flwRef = flwRef;
        Intent intent = new Intent(getContext(),VerificationActivity.class);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE,"otp");
        if (validateInstruction != null) {
            intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, validateInstruction);
        }
        intent.putExtra("theme",ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_0TP);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RavePayActivity.RESULT_SUCCESS){
            if(requestCode==FOR_0TP){
                String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                presenter.validateAccountCharge(flwRef, otp, ravePayInitializer.getPublicKey());
            }else if(requestCode==FOR_INTERNET_BANKING){
                presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onDisplayInternetBankingPage(String authurl, String flwRef) {
        this.flwRef = flwRef;
        Intent intent = new Intent(getContext(),VerificationActivity.class);
        intent.putExtra(WebFragment.EXTRA_AUTH_URL,authurl);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE,"web");
        intent.putExtra("theme",ravePayInitializer.getTheme());
        startActivityForResult(intent,FOR_INTERNET_BANKING);
    }

    @Override
    public void onChargeAccountFailed(String message, String responseAsJSONString) {
        showToast(message);
        //// TODO: 25/07/2017 ask Rd
//        showToast(responseAsJSONString);
    }


    @Override
    public void onPaymentSuccessful(String status, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentFailed(String status, String responseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onValidateSuccessful(String flwRef, String responseAsJsonString) {
        presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
    }

    @Override
    public void onValidateError(String message, String responseAsJSonString) {
        showToast(message);
    }

    @Override
    public void onPaymentError(String message) {
        showToast(message);
    }

    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString) {
        presenter.verifyRequeryResponseStatus(response, responseAsJSONString, ravePayInitializer);
    }

    @Override
    public void displayFee(String charge_amount, final Payload payload, final boolean internetbanking) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You will be charged a total of " + charge_amount + ravePayInitializer.getCurrency() + ". Do you want to continue?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                presenter.chargeAccount(payload, ravePayInitializer.getEncryptionKey(), internetbanking);
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

    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter = new AccountPresenter(getActivity(), this);
        }
        assert presenter != null;
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
        }
        else {
            formattedDay = dayOfMonth + "";
        }

        if (String.valueOf(month + 1).length() != 2) {
               formattedMonth = "0" + (month + 1);
        }
        else {
            formattedMonth = (month + 1) + "";
        }


        dateOfBirthEt.setText(formattedDay + "/" + formattedMonth + "/" + year);
    }

}

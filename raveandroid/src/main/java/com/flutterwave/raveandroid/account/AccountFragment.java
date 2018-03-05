package com.flutterwave.raveandroid.account;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
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
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.card.CardPresenter;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.Callbacks;
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


    TextInputEditText accountNumberEt;
    TextInputLayout accountNumberTil;
    EditText bankEt;
    Button payButton;
    AccountPresenter presenter;
    Bank selectedBank;
    BottomSheetBehavior bottomSheetBehaviorOTP;
    TextView pcidss_tv;
    private ProgressDialog progessDialog;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputEditText amountEt;
    private TextInputLayout amountTil;
    private TextInputEditText emailEt;
    private TextInputLayout emailTil;
    private TextInputEditText phoneEt;
    private TextInputLayout phoneTil;
    private String flwRef;
    private WebView webView;
    private BottomSheetBehavior bottomSheetBehaviorInternetBanking;
    private Button otpButton;
    private TextInputEditText otpEt;
    private TextInputLayout otpTil;
    private LinearLayout otpLayout;
    private RavePayInitializer ravePayInitializer;
    private EditText dateOfBirthEt;
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

        otpTil = (TextInputLayout) v.findViewById(R.id.rave_otpTil);
        otpEt = (TextInputEditText) v.findViewById(R.id.rave_otpEv);
        otpButton = (Button) v.findViewById(R.id.rave_otpButton);
        bankEt = (EditText) v.findViewById(R.id.rave_bankEditText);
        amountEt = (TextInputEditText) v.findViewById(R.id.rave_amountTV);
        amountTil = (TextInputLayout) v.findViewById(R.id.rave_amountTil);
        phoneEt = (TextInputEditText) v.findViewById(R.id.rave_phoneEt);
        phoneTil = (TextInputLayout) v.findViewById(R.id.rave_phoneTil);
        emailEt = (TextInputEditText) v.findViewById(R.id.rave_emailEt);
        emailTil = (TextInputLayout) v.findViewById(R.id.rave_emailTil);
        accountNumberEt = (TextInputEditText) v.findViewById(R.id.rave_accountNumberEt);
        accountNumberTil = (TextInputLayout) v.findViewById(R.id.rave_accountNumberTil);
        payButton = (Button) v.findViewById(R.id.rave_payButton);
        webView = (WebView) v.findViewById(R.id.rave_webview);
        pcidss_tv = (TextView) v.findViewById(R.id.rave_pcidss_compliant_tv);
        dateOfBirthEt = (EditText) v.findViewById(R.id.rave_dobEditText);

        Linkify.TransformFilter filter = new Linkify.TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern = Pattern.compile("()PCI-DSS COMPLIANT");
        Linkify.addLinks(pcidss_tv, pattern, "https://www.pcisecuritystandards.org/pci_security/", null, filter);

        FrameLayout internetBankingLayout = (FrameLayout) v.findViewById(R.id.rave_internetBankingBottomSheet);
        bottomSheetBehaviorInternetBanking = BottomSheetBehavior.from(internetBankingLayout);

        otpLayout = (LinearLayout) v.findViewById(R.id.rave_OTPBottomSheet);
        bottomSheetBehaviorOTP = BottomSheetBehavior.from(otpLayout);

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

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpEt.getText().toString();

                otpTil.setError(null);
                otpTil.setErrorEnabled(false);

                if (otp.length() < 1) {
                    otpTil.setError("Enter a valid one time password");
                } else {
                    presenter.validateAccountCharge(flwRef, otp, ravePayInitializer.getPublicKey());
                }
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

        boolean valid = true;

        String accountNo = accountNumberEt.getText().toString();
        String amount = amountEt.getText().toString();
        String email = emailEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String dob = dateOfBirthEt.getText().toString();

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
            if (selectedBank.getBankcode().equals("057")){
                if (dob.length() != 10) {
                    valid = false;
                    dateOfBirthEt.setError("Enter a valid date of birth");
                }
                else {
                    dob = dob.replace("/", "");
                }
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
                    .setAccountnumber(accountNo);

            Payload body = builder.createBankPayload();
            body.setPasscode(dob);
            body.setPhonenumber(phone);
            body.setPBFSecKey(ravePayInitializer.getSecretKey());
            body.setSECKEY(ravePayInitializer.getSecretKey());

            if (selectedBank.isInternetbanking()) {
                body.setIs_internet_banking("1");
            } else {
                body.setIs_internet_banking(null);
            }

            presenter.fetchFee(body, selectedBank.isInternetbanking());

        }
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

                if (selectedBank.getBankcode().equals("057")) {
                    dateOfBirthEt.setVisibility(View.VISIBLE);
                }
                else {
                    dateOfBirthEt.setVisibility(View.GONE);
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

        if (getActivity().isFinishing()) { return; }

        if (progessDialog == null) {
            progessDialog = new ProgressDialog(getActivity());
            progessDialog.setMessage("Please wait...");
        }

        if (active && !progessDialog.isShowing()) {
            progessDialog.show();
        } else {
            progessDialog.dismiss();
        }
    }

    @Override
    public void onGetBanksRequestFailed(String message) {
        showToast(message);
    }

    @Override
    public void validateAccountCharge(String pbfPubKey, String flwRef) {
        this.flwRef = flwRef;

        bottomSheetBehaviorOTP.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    public boolean closeBottomSheetsIfOpen() {
        boolean showing = false;
        if (bottomSheetDialog != null) {
            if (bottomSheetBehaviorOTP.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                showing = true;
                bottomSheetBehaviorOTP.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            if (bottomSheetBehaviorInternetBanking.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                showing = true;
                bottomSheetBehaviorInternetBanking.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }
        return showing;
    }

    @Override
    public void onDisplayInternetBankingPage(String authurl, String flwRef) {
        this.flwRef = flwRef;
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        webView.setWebViewClient(new MyBrowser());
        // Load the initial URL
        webView.loadUrl(authurl);
        bottomSheetBehaviorInternetBanking.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void onChargeAccountFailed(String message, String responseAsJSONString) {
        showToast(message);
        //// TODO: 25/07/2017 ask Rd
//        showToast(responseAsJSONString);
    }

    private void dismissSheets() {
        if (bottomSheetBehaviorInternetBanking != null) {
            bottomSheetBehaviorInternetBanking.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        if (bottomSheetBehaviorOTP != null) {
            bottomSheetBehaviorOTP.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onPaymentSuccessful(String status, String responseAsJSONString) {

        dismissSheets();

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onPaymentFailed(String status, String responseAsJSONString) {
        dismissSheets();
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onValidateSuccessful(String flwRef, String responseAsJsonString) {
        dismissSheets();
        presenter.requeryTx(flwRef, ravePayInitializer.getSecretKey());
    }

    @Override
    public void onValidateError(String message, String responseAsJSonString) {
        dismissSheets();
        showToast(message);
    }

    @Override
    public void onPaymentError(String message) {
        dismissSheets();
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
                presenter.chargeAccount(payload, internetbanking);
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

        if (String.valueOf(month).length() != 2) {
               formattedMonth = "0" + (month + 1);
        }
        else {
            formattedMonth = (month + 1) + "";
        }


        dateOfBirthEt.setText(formattedDay + "/" + formattedMonth + "/" + year);
    }

    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgressIndicator(true);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.contains("/complete") || url.contains("submitting_mock_form")) {
                presenter.requeryTx(flwRef, ravePayInitializer.getSecretKey());
            }

//            Log.d("URLS", url);
            showProgressIndicator(false);

        }
    }
}

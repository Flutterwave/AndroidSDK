package com.flutterwave.raveandroid.account;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.flutterwave.raveandroid.OTPFragment;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.VerificationActivity;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.WebFragment;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.responses.RequeryResponse;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment implements AccountContract.View, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    private View v;
    private String flwRef;
    private EditText bankEt;
    private Button payButton;
    private Bank selectedBank;
    private TextView pcidss_tv;
    private TextInputLayout amountTil;
    private TextInputEditText emailEt;
    private AccountPresenter presenter;
    private TextInputEditText amountEt;
    private TextInputEditText phoneEt;
    private TextInputLayout phoneTil;
    private EditText dateOfBirthEt,bvnEt;
    private ProgressDialog progessDialog;
    private TextInputEditText accountNumberEt;
    private TextInputLayout accountNumberTil;
    private BottomSheetDialog bottomSheetDialog;
    private TextInputLayout emailTil,rave_bvnTil;
    private RavePayInitializer ravePayInitializer;

    private Calendar calendar = Calendar.getInstance();

    public static final int FOR_0TP = 222;
    public static final int FOR_INTERNET_BANKING = 111;


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

        v = inflater.inflate(R.layout.fragment_account, container, false);

        initializeViews();

        setListeners();

        initializePresenter();

        pcidss_tv.setMovementMethod(LinkMovementMethod.getInstance());

        return v;
    }

    private void initializePresenter() {
        if (getActivity() != null){
            ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
            presenter.init(ravePayInitializer);
        }
    }

    private void setListeners() {
        bankEt.setOnClickListener(this);
        payButton.setOnClickListener(this);
        dateOfBirthEt.setOnClickListener(this);
    }

    private void initializeViews() {
        bvnEt =  v.findViewById(R.id.rave_bvnEt);
        phoneEt =  v.findViewById(R.id.rave_phoneEt);
        emailEt =  v.findViewById(R.id.rave_emailEt);
        amountEt =  v.findViewById(R.id.rave_amountTV);
        emailTil =  v.findViewById(R.id.rave_emailTil);
        phoneTil =  v.findViewById(R.id.rave_phoneTil);
        rave_bvnTil =  v.findViewById(R.id.rave_bvnTil);
        payButton =  v.findViewById(R.id.rave_payButton);
        bankEt =  v.findViewById(R.id.rave_bankEditText);
        amountTil =  v.findViewById(R.id.rave_amountTil);
        dateOfBirthEt =  v.findViewById(R.id.rave_dobEditText);
        pcidss_tv =  v.findViewById(R.id.rave_pcidss_compliant_tv);
        accountNumberEt =  v.findViewById(R.id.rave_accountNumberEt);
        accountNumberTil =  v.findViewById(R.id.rave_accountNumberTil);
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            clearErrors();
            collectData();
        }
        else if (i == R.id.rave_bankEditText){
            presenter.getBanks();
        }
        else if (i == R.id.rave_dobEditText){
            if (getActivity() != null) {
                new DatePickerDialog(getActivity(), AccountFragment.this, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        }
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


    private void clearErrors(){
        bankEt.setError(null);
        bankEt.setError(null);
        phoneTil.setError(null);
        emailTil.setError(null);
        rave_bvnTil.setError(null);
        dateOfBirthEt.setError(null);
        accountNumberTil.setError(null);
        emailTil.setErrorEnabled(false);
        phoneTil.setErrorEnabled(false);
        rave_bvnTil.setErrorEnabled(false);
        accountNumberTil.setErrorEnabled(false);
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(fieldEmail, new ViewObject(emailTil.getId(), emailEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldPhone, new ViewObject(phoneTil.getId(), phoneEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));

        if (accountNumberTil.getVisibility() == View.VISIBLE) {
            if (accountNumberEt.getText().toString().length() != 10) {
                dataHashMap.put(fieldAccount, new ViewObject(accountNumberTil.getId(), "", TextInputLayout.class));
            }
            else{
                dataHashMap.put(fieldAccount, new ViewObject(accountNumberTil.getId(), accountNumberEt.getText().toString(), TextInputLayout.class));
            }
         }

        presenter.onDataCollected(dataHashMap);
    }


    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showInternetBankingSelected(int whatToShow) {
        accountNumberTil.setVisibility(whatToShow);
    }

    @Override
    public void showDateOfBirth(int whatToShow) {
        dateOfBirthEt.setVisibility(whatToShow);
    }

    @Override
    public void showBVN(int whatToShow) {
        rave_bvnTil.setVisibility(whatToShow);
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
                    selectedBank = b;
                    presenter.onInternetBankingValidated(b);

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
        }

        else{
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

        if (getActivity() != null){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.charge) + charge_amount + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    presenter.chargeAccount(payload, ravePayInitializer.getEncryptionKey(), internetbanking);
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



    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {

        ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(fieldAmount).getData()));

        dataHashMap.put(fieldBVN, new ViewObject(bvnEt.getId(), bvnEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldDOB, new ViewObject(dateOfBirthEt.getId(), dateOfBirthEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldBankCode, new ViewObject(bankEt.getId(), selectedBank.getBankcode(), TextInputLayout.class));
        dataHashMap.put(isInternetBanking, new ViewObject(bankEt.getId(), selectedBank.isInternetbanking()+"", TextInputLayout.class));

        presenter.processTransaction(dataHashMap, ravePayInitializer);

    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {
        if (viewType == TextInputLayout.class){
            TextInputLayout view  =  v.findViewById(viewID);
            view.setError(message);
        }
        else if (viewType == EditText.class){
            EditText view  =  v.findViewById(viewID);
            view.setError(message);
        }
    }


}

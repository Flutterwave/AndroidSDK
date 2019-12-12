package com.flutterwave.raveandroid.barter;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.di.modules.BarterModule;

import javax.inject.Inject;

import static android.view.View.GONE;


public class BarterFragment extends Fragment implements BarterContract.View {


    @Inject
    BarterPresenter presenter;

    private View v;
    private Button payButton;
    private TextInputLayout amountTil;
    private TextInputEditText amountEt;

    private ProgressDialog progressDialog;
    private ProgressDialog pollingProgressDialog;

    private RavePayInitializer ravePayInitializer;

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

        v = inflater.inflate(R.layout.fragment_uk, container, false);

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
        amountEt = v.findViewById(R.id.rave_amountTV);
    }

    private void setListeners() {
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void initializePresenter() {
        if (getActivity() != null) {
            ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
            presenter.init(ravePayInitializer);
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

}

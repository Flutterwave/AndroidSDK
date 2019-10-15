package com.flutterwave.raveandroid.ussd;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveApp;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.di.modules.UssdModule;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class UssdFragment extends Fragment implements UssdContract.View, View.OnClickListener {

    @Inject
    UssdPresenter presenter;


    View rootView;
    AppCompatSpinner banksSpinner;
    Button payButton;
    String bank;

    public UssdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_ussd, container, false);

        injectComponents();
        initializeViews();
        setUpBanks();
        setOnClickListeners();
        return rootView;
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RaveApp) getActivity().getApplication()).getAppComponent()
                    .plus(new UssdModule(this))
                    .inject(this);
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == payButton.getId()) {
            Utils.hide_keyboard(getActivity());
//            collectData();
        }
    }

    private void setOnClickListeners() {

    }

    private void initializeViews() {
        banksSpinner = rootView.findViewById(R.id.banks_spinner);
        payButton = rootView.findViewById(R.id.pay_button);
    }

    private void setUpBanks() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireActivity(),
                R.array.ussd_banks, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        banksSpinner.setAdapter(adapter);

        banksSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position < getResources().getStringArray(R.array.ussd_banks).length) {
                    bank = getResources().getStringArray(R.array.ussd_banks)[position];

//                    if (position == 0) {
//                        showInstructionsAndVoucher(false);
//                        validateInstructions = getResources().getString(R.string.checkStatus);
//                    }
//
//                    if (bank.equalsIgnoreCase(RaveConstants.mtn)) {
//                        validateInstructions = getResources().getString(R.string.mtn_validate_instructions);
//                        showInstructionsAndVoucher(false);
//                    }
//                    else if (bank.equalsIgnoreCase(RaveConstants.tigo)) {
//                        validateInstructions =  getResources().getString(R.string.tigo_validate_instructions);
//                        showInstructionsAndVoucher(false);
//                    }
//                    else if (bank.equalsIgnoreCase(RaveConstants.vodafone)) {
//                        validateInstructions = getResources().getString(R.string.checkStatus);
//                        showInstructionsAndVoucher(true);
//                        instructionsTv.setText(Html.fromHtml(getResources().getString(R.string.vodafone_msg)));
//                    }
                }
            }

            //
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
//                showInstructionsAndVoucher(false);
            }
        });
    }

}

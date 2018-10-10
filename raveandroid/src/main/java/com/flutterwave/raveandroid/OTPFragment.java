package com.flutterwave.raveandroid;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 */
public class OTPFragment extends Fragment {
    public static final String EXTRA_OTP = "extraOTP";
    public static final String EXTRA_CHARGE_MESSAGE = "extraChargeMessage";
    String otp;
    Button otpButton;


    public OTPFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_ot, container, false);
        final TextInputLayout otpTil = (TextInputLayout) v.findViewById(R.id.otpTil);
        final TextInputEditText otpEt = (TextInputEditText) v.findViewById(R.id.otpEv);
        otpButton = (Button) v.findViewById(R.id.otpButton);
        TextView chargeMessage = (TextView) v.findViewById(R.id.otpChargeMessage);
        if(getArguments().containsKey(EXTRA_CHARGE_MESSAGE)){
            chargeMessage.setText(getArguments().getString(EXTRA_CHARGE_MESSAGE));
        }
        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = otpEt.getText().toString();

                otpTil.setError(null);
                otpTil.setErrorEnabled(false);

                if (otp.length() < 1) {
                    otpTil.setError("Enter a valid one time password");
                } else {
                    goBack();
                }
            }
        });

        return v;
    }

    public void goBack(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OTP, otp);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }



}

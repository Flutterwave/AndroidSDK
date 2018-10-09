package com.flutterwave.raveandroid.otp_pin_avsvbv_webview;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flutterwave.raveandroid.FutherVerificationActivity;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends Fragment {
    public static final String EXTRA_PIN = "extraPin";
    private String pin;

    public PinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pin, container, false);
        Button pinBtn = (Button) v.findViewById(R.id.rave_pinButton);
        final TextInputEditText pinEv = (TextInputEditText) v.findViewById(R.id.rave_pinEv);
        final TextInputLayout pinTil = (TextInputLayout) v.findViewById(R.id.rave_pinTil);

        pinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEv.getText().toString();

                pinTil.setError(null);
                pinTil.setErrorEnabled(false);

                if (pin.length() != 4) {
                    pinTil.setError("Enter a valid pin");
                }
                else {
                    goBack();
                    //presenter.chargeCardWithSuggestedAuthModel(payload, pin, PIN, ravePayInitializer.getSecretKey());
                }
            }
        });

        return v;
    }

    public void goBack(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PIN,pin);
        intent.putExtra(FutherVerificationActivity.INTENT_SENDER,getArguments().getString(FutherVerificationActivity.INTENT_SENDER));
        intent.putExtra(FutherVerificationActivity.ACTIVITY_MOTIVE,getArguments().getString(FutherVerificationActivity.ACTIVITY_MOTIVE));

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

}

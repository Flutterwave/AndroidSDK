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

import com.flutterwave.raveandroid.data.EventLogger;
import com.flutterwave.raveandroid.data.LaunchEvent;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.VerificationActivity.PUBLIC_KEY_EXTRA;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends Fragment {
    public static final String EXTRA_PIN = "extraPin";
    private String pin;
    @Inject
    EventLogger logger;

    public PinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pin, container, false);
        Button pinBtn = v.findViewById(R.id.rave_pinButton);
        final TextInputEditText pinEv = v.findViewById(R.id.rave_pinEv);
        final TextInputLayout pinTil = v.findViewById(R.id.rave_pinTil);

        injectComponents();
        logLaunch();

        pinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin = pinEv.getText().toString();

                pinTil.setError(null);
                pinTil.setErrorEnabled(false);

                if (pin.length() != 4) {
                    pinTil.setError("Enter a valid pin");
                }
                else {
                    goBack();
                }
            }
        });

        return v;
    }

    private void injectComponents() {
        if (getActivity() != null) {
            ((VerificationActivity) getActivity()).getAppComponent()
                    .inject(this);
        }
    }

    private void logLaunch() {
        if (getArguments() != null
                & getArguments().getString(PUBLIC_KEY_EXTRA) != null
                & logger != null) {
            String publicKey = getArguments().getString(PUBLIC_KEY_EXTRA);
            logger.logEvent(new LaunchEvent("PIN Fragment").getEvent(),
                    publicKey);
        }
    }

    public void goBack(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PIN,pin);
        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

}

package com.flutterwave.raveandroid.verification;


import android.content.Intent;
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
import com.flutterwave.raveandroid.data.EventLogger;
import com.flutterwave.raveandroid.data.events.Event;
import com.flutterwave.raveandroid.data.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.data.events.StartTypingEvent;
import com.flutterwave.raveandroid.data.events.SubmitEvent;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.verification.VerificationActivity.PUBLIC_KEY_EXTRA;

/**
 * A simple {@link Fragment} subclass.
 */
public class PinFragment extends Fragment {
    public static final String EXTRA_PIN = "extraPin";
    @Inject
    EventLogger logger;
    private String pin;

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
        final TextInputEditText pinEt = v.findViewById(R.id.rave_pinEv);

        injectComponents();
        logEvent(new ScreenLaunchEvent("PIN Fragment").getEvent());

        pinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pin = pinEt.getText().toString();

                pinTil.setError(null);
                pinTil.setErrorEnabled(false);

                if (pin.length() != 4) {
                    pinTil.setError("Enter a valid pin");
                } else {
                    goBack();
                }
            }
        });

        pinEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    logEvent(new StartTypingEvent("PIN").getEvent());
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

    private void logEvent(Event event) {
        if (getArguments() != null
                & getArguments().getString(PUBLIC_KEY_EXTRA) != null
                & logger != null) {
            String publicKey = getArguments().getString(PUBLIC_KEY_EXTRA);
            logger.logEvent(event,
                    publicKey);
        }
    }

    public void goBack() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PIN, pin);
        logEvent(new SubmitEvent("PIN").getEvent());
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

}

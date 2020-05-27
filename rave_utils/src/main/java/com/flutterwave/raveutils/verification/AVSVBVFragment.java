package com.flutterwave.raveutils.verification;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.flutterwave.raveandroid.rave_logger.Event;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_logger.events.StartTypingEvent;
import com.flutterwave.raveandroid.rave_logger.events.SubmitEvent;
import com.flutterwave.raveutils.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RESULT_SUCCESS;
import static com.flutterwave.raveutils.verification.VerificationActivity.PUBLIC_KEY_EXTRA;


/**
 * A simple {@link Fragment} subclass.
 */
public class AVSVBVFragment extends Fragment implements View.OnFocusChangeListener {
    public static final String EXTRA_ADDRESS = "extraAddress";
    public static final String EXTRA_CITY = "extraCity";
    public static final String EXTRA_ZIPCODE = "extraZipCode";
    public static final String EXTRA_COUNTRY = "extraCountry";
    public static final String EXTRA_STATE = "extraState";
    String address;
    String state;
    String city;
    String zipCode;
    String country;

    @Inject
    EventLogger logger;

    public AVSVBVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.rave_sdk_fragment_avsvbv, container, false);
        injectComponents();
        logEvent(new ScreenLaunchEvent("OTP Fragment").getEvent());

        final TextInputEditText addressEt = v.findViewById(R.id.rave_billAddressEt);
        final TextInputEditText stateEt = v.findViewById(R.id.rave_billStateEt);
        final TextInputEditText cityEt = v.findViewById(R.id.rave_billCityEt);
        final TextInputEditText zipCodeEt = v.findViewById(R.id.rave_zipEt);
        final TextInputEditText countryEt = v.findViewById(R.id.rave_countryEt);
        final TextInputLayout addressTil = v.findViewById(R.id.rave_billAddressTil);
        final TextInputLayout stateTil = v.findViewById(R.id.rave_billStateTil);
        final TextInputLayout cityTil = v.findViewById(R.id.rave_billCityTil);
        final TextInputLayout zipCodeTil = v.findViewById(R.id.rave_zipTil);
        final TextInputLayout countryTil = v.findViewById(R.id.rave_countryTil);

        addressEt.setOnFocusChangeListener(this);
        stateEt.setOnFocusChangeListener(this);
        cityEt.setOnFocusChangeListener(this);
        zipCodeEt.setOnFocusChangeListener(this);
        countryEt.setOnFocusChangeListener(this);

        Button zipBtn = v.findViewById(R.id.rave_zipButton);

        zipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                address = addressEt.getText().toString();
                state = stateEt.getText().toString();
                city = cityEt.getText().toString();
                zipCode = zipCodeEt.getText().toString();
                country = countryEt.getText().toString();

                addressTil.setError(null);
                stateTil.setError(null);
                cityTil.setError(null);
                zipCodeTil.setError(null);
                countryTil.setError(null);

                if (address.length() == 0) {
                    valid = false;
                    addressTil.setError("Enter a valid address");
                }

                if (state.length() == 0) {
                    valid = false;
                    stateTil.setError("Enter a valid state");
                }

                if (city.length() == 0) {
                    valid = false;
                    cityTil.setError("Enter a valid city");
                }

                if (zipCode.length() == 0) {
                    valid = false;
                    zipCodeTil.setError("Enter a valid zip code");
                }

                if (country.length() == 0) {
                    valid = false;
                    countryTil.setError("Enter a valid country");
                }

                if (valid) {
                    goBack();
                }

            }
        });
        return v;
    }

    private void injectComponents() {
        if (getActivity() != null) {
            ((VerificationActivity) getActivity()).getVerificationComponent()
                    .inject(this);
        }
    }

    private void logEvent(Event event) {
        if (getArguments() != null
                & getArguments().getString(PUBLIC_KEY_EXTRA) != null
                & logger != null) {
            String publicKey = getArguments().getString(PUBLIC_KEY_EXTRA);
            event.setPublicKey(publicKey);
            logger.logEvent(event);
        }
    }

    public void goBack() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ADDRESS, address);
        intent.putExtra(EXTRA_CITY, city);
        intent.putExtra(EXTRA_ZIPCODE, zipCode);
        intent.putExtra(EXTRA_COUNTRY, country);
        intent.putExtra(EXTRA_STATE, state);
        logEvent(new SubmitEvent("Address Details").getEvent());
        if (getActivity() != null) {
            getActivity().setResult(RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        int i = view.getId();

        String fieldName = "";

        if (i == R.id.rave_billAddressEt) {
            fieldName = "Address";
        } else if (i == R.id.rave_billStateEt) {
            fieldName = "State";
        } else if (i == R.id.rave_billCityEt) {
            fieldName = "City";
        } else if (i == R.id.rave_zipEt) {
            fieldName = "Zip Code";
        } else if (i == R.id.rave_countryEt) {
            fieldName = "Country";
        }

        if (hasFocus) {
            logEvent(new StartTypingEvent(fieldName).getEvent());
        }
    }

}

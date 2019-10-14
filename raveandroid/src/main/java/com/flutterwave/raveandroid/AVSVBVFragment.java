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

/**
 * A simple {@link Fragment} subclass.
 */
public class AVSVBVFragment extends Fragment {
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

    public AVSVBVFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_avsvbv, container, false);

        final TextInputEditText addressEt = (TextInputEditText) v.findViewById(R.id.rave_billAddressEt);
        final TextInputEditText stateEt = (TextInputEditText) v.findViewById(R.id.rave_billStateEt);
        final TextInputEditText cityEt = (TextInputEditText) v.findViewById(R.id.rave_billCityEt);
        final TextInputEditText zipCodeEt = (TextInputEditText) v.findViewById(R.id.rave_zipEt);
        final TextInputEditText countryEt = (TextInputEditText) v.findViewById(R.id.rave_countryEt);
        final TextInputLayout addressTil = (TextInputLayout) v.findViewById(R.id.rave_billAddressTil);
        final TextInputLayout stateTil = (TextInputLayout) v.findViewById(R.id.rave_billStateTil);
        final TextInputLayout cityTil = (TextInputLayout) v.findViewById(R.id.rave_billCityTil);
        final TextInputLayout zipCodeTil = (TextInputLayout) v.findViewById(R.id.rave_zipTil);
        final TextInputLayout countryTil = (TextInputLayout) v.findViewById(R.id.rave_countryTil);

        Button zipBtn = (Button) v.findViewById(R.id.rave_zipButton);

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

    public void goBack(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ADDRESS,address);
        intent.putExtra(EXTRA_CITY,city);
        intent.putExtra(EXTRA_ZIPCODE,zipCode);
        intent.putExtra(EXTRA_COUNTRY,country);
        intent.putExtra(EXTRA_STATE,state);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

}

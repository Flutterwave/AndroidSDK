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

    public void goBack(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_PIN,pin);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

}

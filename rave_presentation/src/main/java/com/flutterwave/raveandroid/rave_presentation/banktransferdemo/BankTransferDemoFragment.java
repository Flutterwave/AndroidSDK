package com.flutterwave.raveandroid.rave_presentation.banktransferdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.flutterwave.raveandroid.rave_presentation.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankTransferDemoFragment extends Fragment implements BankTransferDemoContract.View {

    public BankTransferDemoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_bank_transfer_demo, container, false);
        v.findViewById(R.id.mainButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }

    @Override
    public void showProgressIndicator(boolean active) {
        Toast.makeText(requireContext(),
                active ? "loading" : "done loading",
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }
}

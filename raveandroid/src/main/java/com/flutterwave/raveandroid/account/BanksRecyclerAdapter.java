package com.flutterwave.raveandroid.account;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.data.Bank;
import com.flutterwave.raveandroid.data.Callbacks;

import java.util.List;

/**
 * Created by hamzafetuga on 20/07/2017.
 */

public class BanksRecyclerAdapter extends RecyclerView.Adapter<BanksRecyclerAdapter.ViewHolder> {

    private List<Bank> banks;
    private Callbacks.BankSelectedListener bankSelectedListener;

    public void set(List<Bank> banks) {
        this.banks = banks;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View v = inflater.inflate(R.layout.select_bank_list_item, parent, false);
        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(banks.get(position));
    }

    @Override
    public int getItemCount() {
        return banks.size();
    }

    public void setBankSelectedListener(Callbacks.BankSelectedListener bankSelectedListener) {
        this.bankSelectedListener = bankSelectedListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Bank bank;
        private TextView bankNameTv;


        ViewHolder(View v) {
            super(v);
            bankNameTv = v.findViewById(R.id.bankNameTv);
            v.setOnClickListener(this);
        }

        public void bind(Bank bank) {
            this.bank = bank;
            bankNameTv.setText(bank.getBankname());
        }


        @Override
        public void onClick(View v) {
            bankSelectedListener.onBankSelected(bank);
        }
    }
}

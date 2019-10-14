package com.flutterwave.raveandroid.card;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.SavedCard;

import java.util.List;

/**
 * Created by hamzafetuga on 25/07/2017.
 */

public class SavedCardRecyclerAdapter extends RecyclerView.Adapter<SavedCardRecyclerAdapter.ViewHolder>{

    private List<SavedCard> cards;
    private Callbacks.SavedCardSelectedListener savedCardSelectedListener;

    public void set(List<SavedCard> cards) {
        this.cards = cards;
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
        holder.bind(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    public void setSavedCardSelectedListener(Callbacks.SavedCardSelectedListener savedCardSelectedListener) {
        this.savedCardSelectedListener = savedCardSelectedListener;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView itemTv;
        SavedCard card;


        ViewHolder(View v) {
            super(v);
            itemTv = (TextView) v.findViewById(R.id.bankNameTv);
            v.setOnClickListener(this);
        }

        public void bind(SavedCard card) {
            this.card = card;
            itemTv.setText(Utils.spacifyCardNumber(Utils.obfuscateCardNumber(card.getFirst6(), card.getLast4())));
        }


        @Override
        public void onClick(View v) {
            savedCardSelectedListener.onCardSelected(card);
        }
    }
}

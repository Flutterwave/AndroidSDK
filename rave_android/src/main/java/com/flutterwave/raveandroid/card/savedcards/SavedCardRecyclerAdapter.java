package com.flutterwave.raveandroid.card.savedcards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_remote.Callbacks;

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

        View v = inflater.inflate(R.layout.rave_sdk_card_preview_item, parent, false);
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
        private TextView cardTypeTv;
        private TextView maskedPanTv;
        private ImageView cardBrandIv;
        SavedCard card;


        ViewHolder(View v) {
            super(v);
            cardTypeTv = v.findViewById(R.id.card_type_tv);
            maskedPanTv = v.findViewById(R.id.masked_pan_tv);
            cardBrandIv = v.findViewById(R.id.rave_card_brand_imageview);

            v.setOnClickListener(this);
        }

        public void bind(SavedCard card) {
            this.card = card;
            cardTypeTv.setText(card.getCard_brand());
            maskedPanTv.setText(card.getMasked_pan());
            if (card.getCard_brand().equalsIgnoreCase("mastercard")) {
                cardBrandIv
                        .setImageResource(R.drawable.ic_master_card);
            } else if (card.getCard_brand().equalsIgnoreCase("visa")) {
                cardBrandIv.setImageResource(R.drawable.ic_visa);
            } else if (card.getCard_brand().equalsIgnoreCase("ic_verve_logo")) {
                cardBrandIv.setImageResource(R.drawable.ic_verve_logo);
            } else if (card.getCard_brand().equalsIgnoreCase("ic_american_express")) {
                cardBrandIv.setImageResource(R.drawable.ic_american_express);
            }
        }


        @Override
        public void onClick(View v) {
            savedCardSelectedListener.onCardSelected(card);
        }
    }
}

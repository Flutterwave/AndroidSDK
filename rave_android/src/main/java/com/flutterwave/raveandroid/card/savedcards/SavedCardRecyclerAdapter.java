package com.flutterwave.raveandroid.card.savedcards;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public List<SavedCard> getCards(){
        return cards;
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
            cardTypeTv = (TextView) v.findViewById(R.id.card_type_tv);
            maskedPanTv = (TextView) v.findViewById(R.id.masked_pan_tv);
            cardBrandIv = (ImageView) v.findViewById(R.id.rave_card_brand_imageview);

            v.setOnClickListener(this);
        }

        public void bind(SavedCard card) {
            this.card = card;
            cardTypeTv.setText("YOUR " + card.getCard_brand());
            maskedPanTv.setText(card.getMasked_pan());
            if (card.getCard_brand().equalsIgnoreCase("mastercard")) {
                cardBrandIv
                        .setImageResource(R.drawable.master_card_logo_svg);
            } else if (card.getCard_brand().equalsIgnoreCase("visa")) {
                cardBrandIv.setImageResource(R.drawable.visa_logo_new);
            } else if (card.getCard_brand().equalsIgnoreCase("verve")) {
                cardBrandIv.setImageResource(R.drawable.verve);
            } else if (card.getCard_brand().equalsIgnoreCase("amex")) {
                cardBrandIv.setImageResource(R.drawable.amex);
            }
        }


        @Override
        public void onClick(View v) {
            savedCardSelectedListener.onCardSelected(card);
        }
    }
}

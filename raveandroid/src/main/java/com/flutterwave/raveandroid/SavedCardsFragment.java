package com.flutterwave.raveandroid;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.flutterwave.raveandroid.card.SavedCardRecyclerAdapter;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.SavedCard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedCardsFragment extends Fragment {

    public static final String EXTRA_SAVED_CARDS = "saved_cards";
    public static final String SAVED_CARD_MOTIVE = "for_saved_card";
    private SavedCard savedCardToCharge=null;
    private List<SavedCard> savedCards;
    TextView useAnotherCardTv;



    public SavedCardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_saved_cards, container, false);

        useAnotherCardTv = (TextView) v.findViewById(R.id.rave_use_new_card_tv);

        String s = useAnotherCardTv.getText().toString();
        Spannable spannable = new SpannableString(s);
        spannable.setSpan(new UnderlineSpan(),0,s.length(),0);
        useAnotherCardTv.setText(spannable);

        useAnotherCardTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        if (getArguments().containsKey(EXTRA_SAVED_CARDS)){
            Type savedCardsListType = new TypeToken<List<SavedCard>>() {}.getType();
            String savedCardsJson = getArguments().getString(EXTRA_SAVED_CARDS);
            savedCards= new Gson().fromJson(savedCardsJson, savedCardsListType);
        }

        SavedCardRecyclerAdapter adapter = new SavedCardRecyclerAdapter();
        adapter.set(savedCards);
        adapter.setSavedCardSelectedListener(new Callbacks.SavedCardSelectedListener() {
            @Override
            public void onCardSelected(SavedCard savedCard) {
                savedCardToCharge=savedCard;
                goBack();
            }
        });
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rave_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void goBack(){
        Intent intent = new Intent();
        if(savedCardToCharge!=null){
            intent.putExtra(EXTRA_SAVED_CARDS, (new Gson()).toJson(savedCardToCharge));
        }

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

}


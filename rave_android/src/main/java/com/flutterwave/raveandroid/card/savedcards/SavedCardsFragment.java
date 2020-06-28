package com.flutterwave.raveandroid.card.savedcards;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SavedCardsFragment extends Fragment {

    public static final String EXTRA_SAVED_CARDS = "saved_cards";
    public static final String SAVED_CARD_MOTIVE = "for_saved_card";
    private SavedCard savedCardToCharge = null;
    private List<SavedCard> savedCards;


    public SavedCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.rave_sdk_fragment_saved_cards, container, false);

        if (getArguments() != null) {
            if (getArguments().containsKey(EXTRA_SAVED_CARDS)) {
                Type savedCardsListType = new TypeToken<List<SavedCard>>() {
                }.getType();
                String savedCardsJson = getArguments().getString(EXTRA_SAVED_CARDS);
                savedCards = new Gson().fromJson(savedCardsJson, savedCardsListType);
            }
        }

        if (savedCards == null) savedCards = new ArrayList<>();
        SavedCardRecyclerAdapter adapter = new SavedCardRecyclerAdapter();
        adapter.set(savedCards);
        adapter.setSavedCardSelectedListener(new Callbacks.SavedCardSelectedListener() {
            @Override
            public void onCardSelected(SavedCard savedCard) {
                savedCardToCharge = savedCard;
                goBack();
            }
        });
        RecyclerView recyclerView = v.findViewById(R.id.rave_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return v;
    }

    public void goBack() {
        Intent intent = new Intent();
        if (savedCardToCharge != null) {
            intent.putExtra(EXTRA_SAVED_CARDS, (new Gson()).toJson(savedCardToCharge));
        }

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

}


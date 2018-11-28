package com.flutterwave.raveandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.Utils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamzafetuga on 25/07/2017.
 */

public class SharedPrefsRequestImpl implements DataRequest.SharedPrefsRequest {

    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String FLW_REF_KEY = "flw_ref_key";

    public SharedPrefsRequestImpl(Context context) {
        this.context = context;
    }

    @Override
    public void saveCardDetsToSave(CardDetsToSave cardDetsToSave) {
        init();
        editor.putString("first6", cardDetsToSave.getFirst6());
        editor.putString("last4", cardDetsToSave.getLast4());
        editor.apply();
    }

    @Override
    public CardDetsToSave retrieveCardDetsToSave() {
        init();
        return new CardDetsToSave(sharedPreferences.getString("first6", ""), sharedPreferences.getString("last4", ""));
    }

    @Override
    public void saveACard(SavedCard card, String SECKEY, String email) {

        List<SavedCard> savedCards = getSavedCards(email);

        for (SavedCard s : savedCards) {
            if ((s.getFirst6() + s.getLast4())
                    .equalsIgnoreCase(card.getFirst6() + card.getLast4())){
                savedCards.remove(s);
                break;
            }
        }

        card.setToken(Utils.encryptRef(SECKEY, card.getFlwRef())); //encrypt Ref
        savedCards.add(card);

        init();
        Gson gson = new Gson();
        Type type = new TypeToken<List<SavedCard>>() {}.getType();
        String json = gson.toJson(savedCards, type);

        Log.d("cards", json);

        editor.putString("SAVED_CARDS" +  email, json).apply();
    }

    @Override
    public List<SavedCard> getSavedCards(String email) {
        init();
        String json = sharedPreferences.getString("SAVED_CARDS" + email, "[]");

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<SavedCard>>() {
            }.getType();
            return gson.fromJson(json, type);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void init() {

        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(
                    RaveConstants.RAVEPAY, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
        }
    }

    @Override
    public void saveFlwRef(String flwRef) {
        init();
        editor.putString(FLW_REF_KEY, flwRef).apply();
    }

    @Override
    public String fetchFlwRef() {
        init();
        return sharedPreferences.getString(FLW_REF_KEY, "");
    }
}

package com.flutterwave.raveandroid.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.flutterwave.raveandroid.RaveConstants;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hamzafetuga on 25/07/2017.
 */

public class SharedPrefsRequestImpl implements DataRequest.SharedPrefsRequest {

    private static final String SAVED_CARDS_PREFIX = "SAVED_CARDS";
    private static final String PHONE_NUMBER = "phone_number";
    Context context;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String FLW_REF_KEY = "flw_ref_key";

    public SharedPrefsRequestImpl(Context context) {
        this.context = context;
    }

//    @Override
//    public void saveCardDetsToSave(CardDetsToSave cardDetsToSave) {
//        init();
//        editor.putString("email", cardDetsToSave.getEmail());
//        editor.putString("last4", cardDetsToSave.getLast4());
//        editor.apply();
//    }
//
//    @Override
//    public CardDetsToSave retrieveCardDetsToSave() {
//        init();
//        return new CardDetsToSave(sharedPreferences.getString("email", ""), sharedPreferences.getString("last4", ""));
//    }

    @Override
    public void saveCardToSharedPreference(List<SavedCard> cardsToSave, String phoneNumber) {

        savePhoneNumber(phoneNumber);

        List<SavedCard> savedCards = getSavedCards(phoneNumber);

        for (SavedCard s : savedCards) {
            for (SavedCard c : cardsToSave){
                if (s.getCardHash().equalsIgnoreCase(c.getCardHash())){
                    savedCards.remove(s);
                    break;
                }
            }
        }

        savedCards.addAll(cardsToSave);

        init();
        Gson gson = new Gson();
        Type type = new TypeToken<List<SavedCard>>() {}.getType();
        String savedCardsJson = gson.toJson(savedCards, type);

        editor.putString(SAVED_CARDS_PREFIX +  phoneNumber, savedCardsJson).apply();
    }

    @Override
    public List<SavedCard> getSavedCards(String phoneNumber) {
        init();
        String savedCardsJson = sharedPreferences.getString(SAVED_CARDS_PREFIX + phoneNumber, "[]");

        try {
            Gson gson = new Gson();
            Type type = new TypeToken<List<SavedCard>>() {
            }.getType();
            return gson.fromJson(savedCardsJson, type);
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

    @Override
    public void savePhoneNumber(String phoneNumber){
        init();
        editor.putString(PHONE_NUMBER,phoneNumber).apply();
    }

    @Override
    public String fetchPhoneNumber() {
        init();
        return sharedPreferences.getString(PHONE_NUMBER, "");
    }
}

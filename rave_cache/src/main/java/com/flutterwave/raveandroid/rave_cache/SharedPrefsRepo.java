package com.flutterwave.raveandroid.rave_cache;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.RAVEPAY;

/**
 * Created by hamzafetuga on 25/07/2017.
 */

public class SharedPrefsRepo {

    private static final String SAVED_CARDS_PREFIX = "EXTRA_SAVED_CARDS";
    private static final String PHONE_NUMBER = "phone_number";
    public Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String FLW_REF_KEY = "flw_ref_key";
    private Gson gson;

    @SuppressLint("CommitPrefEdits")
    @Inject
    public SharedPrefsRepo(SharedPreferences sharedPreferences, Gson gson) {
        this.gson = gson;
        this.sharedPreferences = sharedPreferences;
        this.editor = sharedPreferences.edit();
    }


    public void saveCardToSharedPreference(List<SavedCard> cardsToSave, String phoneNumber, String publicKey) {

        savePhoneNumber(phoneNumber);

        Type type = new TypeToken<List<SavedCard>>() {}.getType();
        String savedCardsJson = gson.toJson(cardsToSave, type);

        editor.putString(SAVED_CARDS_PREFIX + phoneNumber + publicKey, savedCardsJson).apply();
    }


    public List<SavedCard> getSavedCards(String phoneNumber, String publicKey) {
        String savedCardsJson = sharedPreferences.getString(
                SAVED_CARDS_PREFIX + phoneNumber + publicKey, "[]");

        try {
            Type type = new TypeToken<List<SavedCard>>() {
            }.getType();
            return gson.fromJson(savedCardsJson, type);
        }
        catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public void saveFlwRef(String flwRef) {
        editor.putString(FLW_REF_KEY, flwRef).apply();
    }


    public String fetchFlwRef() {
        return sharedPreferences.getString(FLW_REF_KEY, "");
    }


    private void savePhoneNumber(String phoneNumber) {
        editor.putString(PHONE_NUMBER, phoneNumber).apply();
    }


    public String fetchPhoneNumber() {
        return sharedPreferences.getString(PHONE_NUMBER, "");
    }

    public void clear(){
        editor.clear().apply();
    }
}

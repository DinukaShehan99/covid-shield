package com.borntocode.covidshield.managers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    public static final String PREF_FILE = "COVID_SHIELD_PREF_FILE";
    public static final String IS_DONE_TUTORIAL = "COVID_SHIELD_PREF_FILE_IS_DONE_TUTORIAL";
    public static final String FIREBASE_USER_ID = "COVID_SHIELD_PREF_FILE_FIREBASE_USER_ID";

    public static SharedPreferences sharedPreferences;

    public SharedPreferencesManager (Context context){
        sharedPreferences = context.getSharedPreferences(SharedPreferencesManager.PREF_FILE, Context.MODE_PRIVATE);
    }

    public void savePreferences(String key, String value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.commit();
    }

    public String getPreferences(String key){
        return sharedPreferences.getString(key,"");
    }

    public void savePreferences(String key, Boolean value){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    public boolean getBooleanPreferences(String key){
        return sharedPreferences.getBoolean(key,false);
    }
}

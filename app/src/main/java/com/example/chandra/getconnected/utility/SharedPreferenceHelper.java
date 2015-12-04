package com.example.chandra.getconnected.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by chandra on 12/1/2015.
 */
public class SharedPreferenceHelper {


    public void saveInSharedPreference(Context context, String key, String value) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String loadFromSharedPreference(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getString(key, null);
        } else {
            return null;
        }
    }




}

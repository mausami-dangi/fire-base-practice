package com.example.mausami.firebasepractice;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    Context context;

    PreferenceManager(Context context) {
        this.context = context;
    }

    public void putPreference(String name, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }
}

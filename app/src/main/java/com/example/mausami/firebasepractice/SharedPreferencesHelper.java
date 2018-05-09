package com.example.mausami.firebasepractice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesHelper {
    public static String FIREBASE_TOKEN = "FIREBASE_TOKEN";
    private static SharedPreferences sharedPreferences = null;
    private static SharedPreferences.Editor editor = null;

    public static void initSharedPreference(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public static void putString(String key, String value, Context context){
        if (sharedPreferences == null){
            initSharedPreference(context);
        }
        editor.putString( key, value).commit();
    }

    public static void putBoolean(String key, boolean value, Context context){
        if (sharedPreferences == null){
            initSharedPreference(context);
        }
        editor.putBoolean( key, value).commit();
    }

    public static String getString(String key, Context context){
        if (sharedPreferences == null){
            initSharedPreference(context);
        }
        return sharedPreferences.getString(key,"");
    }

    public static boolean getBoolean(String key, Context context){
        if (sharedPreferences == null){
            initSharedPreference(context);
        }
        return sharedPreferences.getBoolean(key,false);
    }
}

package com.mattkula.brownsnews;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by matt on 3/30/14.
 */
public class Prefs {
    public final static String TAG = "browns_news";
    public final static String TAG_IS_FIRST_TIME = "is_first_time";

    public static boolean isFirstTime(Context c){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        boolean isFirstTime = preferences.getBoolean(TAG_IS_FIRST_TIME, true);
        return isFirstTime;
    }

    public static void setValueForKey(Context c, String key, String value){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void setValueForKey(Context c, String key, boolean value){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

}

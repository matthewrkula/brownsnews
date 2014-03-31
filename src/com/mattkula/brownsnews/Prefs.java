package com.mattkula.brownsnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.mattkula.brownsnews.sources.NewsSource;

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

    public static boolean isNewsSourceSelected(Context c, NewsSource source){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getBoolean(source.getName(), true);
    }

    public static void setValueForKey(Context c, String key, String value){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
        Log.v("ASDF", key + " set to " + value);
    }

    public static void setValueForKey(Context c, String key, boolean value){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
        Log.v("ASDF", key + " set to " + (value ? "true" : "false"));
    }

}

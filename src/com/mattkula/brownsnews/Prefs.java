package com.mattkula.brownsnews;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mattkula.brownsnews.sources.NewsSource;

/**
 * Created by matt on 3/30/14.
 */
public class Prefs {
    public final static String LOG_UPDATE = "logupdate";
    public final static String TAG = "browns_news";
    public final static String TAG_IS_FIRST_TIME = "is_first_time";
    public final static String TAG_LATEST_UPDATE = "latest_update";
    public final static String VERSION = "1.3.1";

    /** Updating constants **/
    public final static String TAG_NOTIFICATION_ENABLED = "notification_enabled";
    public final static String TAG_NOTIFICATION_INTERVAL = "notification_interval";
    public final static String TAG_UPDATE_STATUS = "update_status";
    public final static String TAG_UPDATE_LAST_LINK = "update_last_link";
    public final static String STATUS_UPDATING = "status_updating";
    public final static String STATUS_NOT_UPDATING = "status_not_updating";

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
        Log.v("prefs", key + " set to " + value);
    }

    public static void setValueForKey(Context c, String key, boolean value){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
        Log.v("prefs", key + " set to " + (value ? "true" : "false"));
    }

    public static void setValueForKey(Context c, String key, long value){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
        Log.v("prefs", key + " set to " + value);
    }

    public static String getValueForKey(Context c, String key, String defaultValue){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getString(key, defaultValue);
    }

    public static boolean getValueForKey(Context c, String key, boolean defaultValue){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getBoolean(key, defaultValue);
    }

    public static long getValueForKey(Context c, String key, long defaultValue){
        SharedPreferences preferences = c.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return preferences.getLong(key, defaultValue);
    }

}

package com.mattkula.brownsnews.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import com.mattkula.brownsnews.Prefs;

import java.util.Calendar;

/**
 * Created by matt on 4/2/14.
 */
public class UpdateManager {

    public static void rescheduleUpdates(Context context){
        Intent intent = new Intent("com.mattkula.intent.UPDATE_ARTICLES");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Calendar c = Calendar.getInstance();
        long duration = Prefs.getValueForKey(context, Prefs.TAG_NOTIFICATION_INTERVAL, 3600) * 1000;

        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);

        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() + duration, duration, pendingIntent);
        Log.v(Prefs.LOG_UPDATE, "Update in " + duration/60000 + " minutes.");
    }
}

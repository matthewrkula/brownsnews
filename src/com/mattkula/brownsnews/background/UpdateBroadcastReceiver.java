package com.mattkula.brownsnews.background;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.mattkula.brownsnews.Prefs;

/**
 * Created by matt on 4/1/14.
 */
public class UpdateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(Prefs.LOG_UPDATE, "UpdateBroadcastReceiver called.");
        Prefs.setValueForKey(context, Prefs.TAG_UPDATE_STATUS, Prefs.STATUS_UPDATING);

        Intent i = new Intent(context, UpdateService.class);
        context.startService(i);
    }

}

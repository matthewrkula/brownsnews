package com.mattkula.brownsnews.background;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.mattkula.brownsnews.Prefs;

/**
 * Created by Matt on 4/1/14.
 *
 * Meant to call the UpdateService that is responsible for downloading and checking if new data
 * is available.
 */
public class UpdateBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(Prefs.LOG_UPDATE, "UpdateBroadcastReceiver received call.");
        Prefs.setValueForKey(context, Prefs.TAG_UPDATE_STATUS, Prefs.STATUS_UPDATING);

        Intent i = new Intent(context, UpdateService.class);
        context.startService(i);
    }

}

package com.mattkula.brownsnews.background;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mattkula.brownsnews.MainActivity;
import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.data.Article;
import com.mattkula.brownsnews.sources.NewsSourceManager;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Matt on 4/2/14.
 *
 * Responsible for downloading articles and checking if new ones are available. If they are, it then
 * sends a notification to the user.
 */
public class UpdateService extends IntentService implements NewsSourceManager.OnArticlesDownloadedListener {

    public UpdateService(){
        super("UpdateService");
    }

    public UpdateService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(Prefs.LOG_UPDATE, "UpdateService starting update.");

        NewsSourceManager manager = new NewsSourceManager();
        manager.getAllArticles(this, getApplicationContext());
    }

    @Override
    public void onArticlesDownloaded(ArrayList<Article> articles) {
        if(Prefs.getValueForKey(getApplicationContext(), Prefs.TAG_UPDATE_LAST_LINK, "none").equals(articles.get(0).link)){
            Log.v(Prefs.LOG_UPDATE, "No new articles.");
            return;
        } else {
            Log.v(Prefs.LOG_UPDATE, "******** Newest Title ********");
            Log.v(Prefs.LOG_UPDATE, articles.get(0).title);
            Log.v(Prefs.LOG_UPDATE, "******************************");
        }

        Prefs.setValueForKey(getApplicationContext(), Prefs.TAG_UPDATE_LAST_LINK, articles.get(0).link);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                                            .setSmallIcon(R.drawable.logo)
                                            .setContentTitle("Browns News")
                                            .setContentText(articles.get(0).title)
                                            .setContentIntent(pendingIntent)
                                            .getNotification();
        notification.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;

        NotificationManager notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);

        Prefs.setValueForKey(getApplicationContext(), Prefs.TAG_UPDATE_STATUS, Prefs.STATUS_NOT_UPDATING);
    }
}

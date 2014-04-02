package com.mattkula.brownsnews.background;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.data.Article;
import com.mattkula.brownsnews.sources.NewsSourceManager;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matt on 4/2/14.
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
        Log.v(Prefs.LOG_UPDATE, "Update Service");

        NewsSourceManager manager = new NewsSourceManager();
        manager.getAllArticles(this, getApplicationContext());
    }

    @Override
    public void onArticlesDownloaded(ArrayList<Article> articles) {
        Log.v(Prefs.LOG_UPDATE, "****************");
        Log.v(Prefs.LOG_UPDATE, articles.get(0).title);

        Prefs.setValueForKey(getApplicationContext(), Prefs.TAG_UPDATE_STATUS, Prefs.STATUS_NOT_UPDATING);
    }
}

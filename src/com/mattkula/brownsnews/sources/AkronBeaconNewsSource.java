package com.mattkula.brownsnews.sources;

import android.text.format.DateUtils;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mattkula.brownsnews.MyApplication;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.data.Article;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matt on 3/31/14.
 */
public class AkronBeaconNewsSource implements NewsSource{

    @Override
    public String getName() {
        return "Akron Beacon Journal";
    }

    @Override
    public int getImageId() {
        return R.drawable.akron_beacon_journal;
    }

    @Override
    public void getLatestArticles(final NewsSourceManager manager) {
        final ArrayList<Article> articles = new ArrayList<Article>();

        Request request = new JsonObjectRequest(Request.Method.GET,
                "http://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://www.ohio.com/cmlink/1.215556&num=8",
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    JSONArray entries = jsonObject.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");

                    for(int i =0; i < entries.length(); i++){
                        JSONObject entry = entries.getJSONObject(i);
                        Article article = Article.createFromJsonObject(entry);
                        article.newsSource = getName();
                        articles.add(article);
                    }

                    manager.addToArticles(articles);

                } catch (JSONException e) {
                    Log.e("ASDF", "json error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ASDF", "error with Akron Beacon");
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }
}

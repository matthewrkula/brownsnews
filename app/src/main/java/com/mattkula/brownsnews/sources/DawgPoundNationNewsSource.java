package com.mattkula.brownsnews.sources;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mattkula.brownsnews.MyApplication;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by matt on 3/27/14.
 */
public class DawgPoundNationNewsSource implements NewsSource {

    @Override
    public String getName() {
        return "Dawg Pound Nation";
    }

    @Override
    public int getImageId() {
        return R.drawable.browns_dog;
    }

    @Override
    public String getURL() {
        return "https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://dawgpoundnation.com/feed/&num=20";
    }

    @Override
    public void getLatestArticles(final NewsSourceManager manager) {
        final ArrayList<Article> articles = new ArrayList<Article>();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getURL(),
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    JSONArray entries = jsonObject.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");

                    for(int i =0; i < entries.length(); i++){
                        JSONObject entry = entries.getJSONObject(i);
                        Article article = Article.createFromJsonObject(entry);
                        article.newsSource = getName();
                        article.imageUrl = "none";
                        article.content = article.content.replaceAll("width:\\d+px", "");
                        article.content = article.content.replaceAll("margin-left:\\d+px;", "");
                        articles.add(article);
                    }

                    manager.addToArticles(articles);

                } catch (JSONException e) {
                    Log.e("ASDF", "json error");
                    manager.addToArticles(articles);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                manager.onError(DawgPoundNationNewsSource.this);
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }
}

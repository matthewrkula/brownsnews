package com.mattkula.brownsnews.sources;

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
 * Created by matt on 3/28/14.
 */
public class WaitingForNextYearNewsSource implements NewsSource{

    private String[] allowedCategories = new String[]{
            "browns", "football", "nfl", "hoyer", "pettine", "cleveland browns"
    };

    @Override
    public String getName() {
        return "Waiting for Next Year";
    }

    @Override
    public int getImageId() {
        return R.drawable.waiting_for_next_year;
    }

    @Override
    public void getLatestArticles(final NewsSourceManager manager) {
        final ArrayList<Article> articles = new ArrayList<Article>();

        Request request = new JsonObjectRequest(Request.Method.GET,
                "https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://waitingfornextyear.com/feed/&num=20",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                try {

                    JSONArray entries = jsonObject.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");

                    for(int i =0; i < entries.length(); i++){
                        JSONObject entry = entries.getJSONObject(i);
                        Article article = Article.createFromJsonObject(entry);
                        article.newsSource = getName();
                        article.content = article.content.replaceAll("___________________________________________", "");

                        for(int j=0; j < allowedCategories.length; j++){
                            if(article.categories.contains(allowedCategories[j])){
                                articles.add(article);
                                break;
                            }
                        }
                    }

                    manager.addToArticles(articles);

                } catch (JSONException e) {
                    Log.e("ASDF", "json error");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ASDF", "error with dawgs by nature");
            }
        });

        MyApplication.getInstance().addToRequestQueue(request);
    }
}

package com.mattkula.brownsnews.sources;

import android.util.Log;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.mattkula.brownsnews.MyApplication;
import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by matt on 3/31/14.
 */
public class AkronBeaconNewsSource implements RxNewsSource {

    @Override
    public String getName() {
        return "Akron Beacon Journal";
    }

    @Override
    public int getImageId() {
        return R.drawable.akron_beacon_journal;
    }

    @Override
    public String getURL() {
        return "http://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://www.ohio.com/cmlink/1.215556&num=8";
    }

    @Override
    public Observable<Article> getLatestArticles() {
        final PublishSubject<Article> subject = PublishSubject.create();

        Request request = new JsonObjectRequest(Request.Method.GET, getURL(), null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray entries = jsonObject.getJSONObject("responseData").getJSONObject("feed").getJSONArray("entries");

                            for(int i =0; i < entries.length(); i++){
                                JSONObject entry = entries.getJSONObject(i);
                                Article article = Article.createFromJsonObject(entry);
                                article.newsSource = getName();
                                article.author = "ABJ";

                                subject.onNext(article);
                            }

                            subject.onCompleted();

                        } catch (JSONException e) {
                            subject.onError(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        subject.onError(volleyError);
                    }
                });

        MyApplication.getInstance().addToRequestQueue(request);
        return subject;
    }
}

package com.mattkula.brownsnews.sources;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.mattkula.brownsnews.MyApplication;
import com.mattkula.brownsnews.database.Article;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import rx.Observable;
import rx.subjects.PublishSubject;

public abstract class NewsSource {

    public abstract String getName();

    public abstract String getURL();

    public abstract int getImageId();

    public void makeModifications(Article article) {};

    public boolean shouldUseArticle(Article article) { return true; }

    public Observable<Article> getLatestArticles() {
        final PublishSubject<Article> subject = PublishSubject.create();

        Request request = new JsonObjectRequest(Request.Method.GET, getURL(), null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            JSONArray entries = jsonObject.getJSONObject("responseData")
                                                          .getJSONObject("feed")
                                                          .getJSONArray("entries");

                            for(int i =0; i < entries.length(); i++){
                                JSONObject entry = entries.getJSONObject(i);
                                Article article = Article.createFromJsonObject(entry);
                                article.newsSource = getName();
                                makeModifications(article);
                                if (shouldUseArticle(article)) {
                                    subject.onNext(article);
                                }
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

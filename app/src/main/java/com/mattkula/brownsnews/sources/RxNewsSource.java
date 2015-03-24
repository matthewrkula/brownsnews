package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.database.Article;

import java.util.List;

import rx.Observable;

/**
 * Created by matt on 3/27/14.
 */
public interface RxNewsSource {

    public Observable<Article> getLatestArticles();

    public String getName();

    public String getURL();

    public int getImageId();

}

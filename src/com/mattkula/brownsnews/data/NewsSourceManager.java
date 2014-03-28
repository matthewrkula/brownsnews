package com.mattkula.brownsnews.data;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Matt on 3/27/14.
 */
public class NewsSourceManager {
    DawgsByNatureNewsSource dbn = new DawgsByNatureNewsSource();
    DawgPoundNationNewsSource dpn = new DawgPoundNationNewsSource();

    ArrayList<Article> articles;

    int numOfSources = 2;
    int counter = 0;

    OnArticlesDownloadedListener listener;

    public void getAllArticles(OnArticlesDownloadedListener listener){
        this.listener = listener;
        articles = new ArrayList<Article>();
        counter = 0;

        dbn.getLatestArticles(this);
        dpn.getLatestArticles(this);
    }

    public void addToArticles(ArrayList<Article> newArticles){
        this.articles.addAll(newArticles);
        counter++;
        if(counter == numOfSources){
            Collections.sort(this.articles);
            listener.onArticlesDownloaded(this.articles);
        }
    }

    public interface OnArticlesDownloadedListener {
        public void onArticlesDownloaded(ArrayList<Article> articles);
    }
}

package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.data.Article;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Matt on 3/27/14.
 */
public class NewsSourceManager {
    NewsSource[] sources = new NewsSource[]{
            new DawgsByNatureNewsSource(),
            new DawgPoundNationNewsSource(),
            new WaitingForNextYearNewsSource()
    };

    ArrayList<Article> articles;

    int counter = 0;

    OnArticlesDownloadedListener listener;

    public void getAllArticles(OnArticlesDownloadedListener listener){
        this.listener = listener;
        articles = new ArrayList<Article>();
        counter = 0;

        for(int sourceCount=0; sourceCount < sources.length; sourceCount++){
            sources[sourceCount].getLatestArticles(this);
        }
    }

    public void addToArticles(ArrayList<Article> newArticles){
        this.articles.addAll(newArticles);
        counter++;
        if(counter == sources.length){
            Collections.sort(this.articles);
            listener.onArticlesDownloaded(this.articles);
        }
    }

    public interface OnArticlesDownloadedListener {
        public void onArticlesDownloaded(ArrayList<Article> articles);
    }
}

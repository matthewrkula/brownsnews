package com.mattkula.brownsnews.sources;

import android.content.Context;
import android.util.Log;
import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.data.Article;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Matt on 3/27/14.
 */
public class NewsSourceManager {
    public static NewsSource[] sources = new NewsSource[]{
            new DawgsByNatureNewsSource(),
            new DawgPoundNationNewsSource(),
            new WaitingForNextYearNewsSource(),
            new ESPNNewsSource(),
            new BrownsWebsiteNewsSource(),
    };

    ArrayList<Article> articles;

    int counter = 0;
    int numOfSources = 0;

    OnArticlesDownloadedListener listener;

    public void getAllArticles(OnArticlesDownloadedListener listener, Context c){
        this.listener = listener;
        articles = new ArrayList<Article>();
        counter = 0;
        numOfSources = 0;

        for(int sourceCount=0; sourceCount < sources.length; sourceCount++){
            if(Prefs.isNewsSourceSelected(c, sources[sourceCount])){
                numOfSources++;
                sources[sourceCount].getLatestArticles(this);
            }
        }

        if(numOfSources == 0) listener.onArticlesDownloaded(this.articles);
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

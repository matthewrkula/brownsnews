package com.mattkula.brownsnews.sources;

import android.content.Context;
import android.util.Log;
import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;

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
            new AkronBeaconNewsSource(),
            new PlainDealerNewsSource(),
    };

    ArrayList<Article> articles;

    int counter = 0;
    int numOfSources = 0;

    OnArticlesDownloadedListener listener;
    ArticleDataSource dataSource;

    public void getAllArticles(OnArticlesDownloadedListener listener, Context c){
        dataSource = new ArticleDataSource(c);
        dataSource.open();
        this.listener = listener;
        articles = new ArrayList<Article>();
        counter = 0;
        numOfSources = 0;

        for(int sourceCount=0; sourceCount < sources.length; sourceCount++){
            numOfSources++;
            sources[sourceCount].getLatestArticles(this);
        }

        if(numOfSources == 0) listener.onArticlesDownloaded();
    }

    public void addToArticles(ArrayList<Article> newArticles){
        for(Article article : newArticles){
            this.articles.add(dataSource.createOrGetArticle(article));
        }
        counter++;
        if(counter == numOfSources){
            Collections.sort(this.articles);
            listener.onArticlesDownloaded();
            dataSource.close();
        }
    }

    public void onError(NewsSource source){
        Log.e("BROWNSERROR", source.getName() + " could not be downloaded.");
        counter++;
    }

    public static ArrayList<NewsSource> getAllowedSources(Context context){
        ArrayList<NewsSource> allowedSources = new ArrayList<NewsSource>();
        for(int i=0; i < sources.length; i++){
            if(Prefs.isNewsSourceSelected(context, sources[i])){
                allowedSources.add(sources[i]);
            }
        }
        return allowedSources;
    }

    public interface OnArticlesDownloadedListener {
        public void onArticlesDownloaded();
    }
}

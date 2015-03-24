package com.mattkula.brownsnews.sources;

import android.content.Context;
import android.util.Log;
import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Matt on 3/27/14.
 */
public class NewsSourceManager {
//    public static NewsSource[] sources = new NewsSource[]{
//            new DawgsByNatureNewsSource(),
//            new DawgPoundNationNewsSource(),
//            new WaitingForNextYearNewsSource(),
//            new ESPNNewsSource(),
//            new BrownsWebsiteNewsSource(),
//            new AkronBeaconNewsSource(),
//            new PlainDealerNewsSource(),
//    };
    public static RxNewsSource[] sources = new RxNewsSource[]{
            new AkronBeaconNewsSource(),
    };

    ArrayList<Article> articles;

    int counter = 0;
    int numOfSources = 0;

    OnArticlesDownloadedListener listener;
    ArticleDataSource dataSource;

    public void getAllArticles(final OnArticlesDownloadedListener listener, final Context c){
        dataSource = new ArticleDataSource(c);
        dataSource.open();
        this.listener = listener;
        articles = new ArrayList<Article>();
        counter = 0;
        numOfSources = 0;

        Observable.from(sources)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<RxNewsSource, Observable<Article>>() {
                    @Override
                    public Observable<Article> call(RxNewsSource rxNewsSource) {
                        return rxNewsSource.getLatestArticles();
                    }
                })
                .doOnNext(new Action1<Article>() {
                    @Override
                    public void call(Article article) {
                        dataSource.createOrGetArticle(article);
                    }
                })
                .subscribe(new Subscriber<Article>() {
                    @Override
                    public void onCompleted() {
                        dataSource.close();
                        listener.onArticlesDownloaded();
                    }

                    @Override
                    public void onError(Throwable e) {
                        dataSource.close();
                        listener.onArticlesDownloaded();
                    }

                    @Override
                    public void onNext(Article article) {

                    }
                });
    }

    public static ArrayList<RxNewsSource> getAllowedSources(Context context){
        ArrayList<RxNewsSource> allowedSources = new ArrayList<RxNewsSource>();
        for(int i=0; i < sources.length; i++){
            if(Prefs.isNewsSourceSelected(context, sources[i])){
                allowedSources.add(sources[i]);
            }
        }
        return allowedSources;
    }

    public void addToArticles(List<Article> articles) {

    }

    public void onError(NewsSource source) {

    }

    public interface OnArticlesDownloadedListener {
        public void onArticlesDownloaded();
    }
}

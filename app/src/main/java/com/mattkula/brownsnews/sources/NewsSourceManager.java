package com.mattkula.brownsnews.sources;

import android.content.Context;

import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.database.Article;
import com.mattkula.brownsnews.database.ArticleDataSource;

import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

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

    ArticleDataSource dataSource;

    public void getAllArticles(final OnArticlesDownloadedListener listener, Context c){
        dataSource = new ArticleDataSource(c);
        dataSource.open();

        Observable.from(sources)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<NewsSource, Observable<Article>>() {
                    @Override
                    public Observable<Article> call(NewsSource newsSource) {
                        return newsSource.getLatestArticles();
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
                        dataSource.createOrGetArticle(article);
                    }
                });
    }

    public static ArrayList<NewsSource> getAllowedSources(final Context context){
        ArrayList<NewsSource> allowedSources = new ArrayList<>();
        for (NewsSource newsSource : sources) {
            if(Prefs.isNewsSourceSelected(context, newsSource)){
                allowedSources.add(newsSource);
            }
        }
        return allowedSources;
    }

    public interface OnArticlesDownloadedListener {
        void onArticlesDownloaded();
    }

}

package com.mattkula.brownsnews.sources;

/**
 * Created by matt on 3/27/14.
 */
public interface NewsSource {

    public void getLatestArticles(final NewsSourceManager manager);

}

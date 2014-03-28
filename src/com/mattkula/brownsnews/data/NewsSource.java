package com.mattkula.brownsnews.data;

/**
 * Created by matt on 3/27/14.
 */
public interface NewsSource {

    public void getLatestArticles(final NewsSourceManager manager);

}

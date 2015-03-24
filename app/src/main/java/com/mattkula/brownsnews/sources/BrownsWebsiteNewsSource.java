package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.R;

public class BrownsWebsiteNewsSource extends NewsSource {

    @Override
    public String getName() {
        return "Cleveland Browns Website";
    }

    @Override
    public int getImageId() {
        return R.drawable.clevelandbrowns;
    }

    @Override
    public String getURL() {
        return "http://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://www.clevelandbrowns.com/cda-web/rss-module.htm?tagName=News&num=20";
    }

}

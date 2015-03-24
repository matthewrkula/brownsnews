package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.R;

public class DawgsByNatureNewsSource extends NewsSource {

    @Override
    public String getName() {
        return "Dawgs By Nature";
    }

    @Override
    public int getImageId() {
        return R.drawable.dawgs_by_nature;
    }

    @Override
    public String getURL() {
        return "https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://www.dawgsbynature.com/rss/current&num=20";
    }

}

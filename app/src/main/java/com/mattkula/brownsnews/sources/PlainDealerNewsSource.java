package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.R;

public class PlainDealerNewsSource extends NewsSource {

    @Override
    public String getName() {
        return "Plain Dealer";
    }

    @Override
    public int getImageId() {
        return R.drawable.brownie;
    }

    @Override
    public String getURL() {
        return "https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://impact.cleveland.com/browns/atom.xml&num=8";
    }

}

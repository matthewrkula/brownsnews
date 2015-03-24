package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;

public class AkronBeaconNewsSource extends NewsSource {

    @Override
    public String getName() {
        return "Akron Beacon Journal";
    }

    @Override
    public int getImageId() {
        return R.drawable.akron_beacon_journal;
    }

    @Override
    public String getURL() {
        return "http://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://www.ohio.com/cmlink/1.215556&num=8";
    }

    @Override
    public void makeModifications(Article article) {
        article.author = "ABJ";
    }

}

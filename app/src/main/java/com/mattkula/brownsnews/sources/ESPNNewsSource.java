package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;

public class ESPNNewsSource extends NewsSource{

    @Override
    public String getName() {
        return "ESPN";
    }

    @Override
    public int getImageId() {
        return R.drawable.espn;
    }

    @Override
    public String getURL() {
        return "http://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://espn.go.com/blog/feed?blog=cleveland-browns&num=20";
    }

    @Override
    public void makeModifications(Article article) {
        article.content = article.content.replaceAll("</?a(|\\s+[^>]+)>", "");
        article.imageUrl = "none";
    }

}

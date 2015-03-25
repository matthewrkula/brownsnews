package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;

public class DawgPoundNationNewsSource extends NewsSource {

    @Override
    public String getName() {
        return "Dawg Pound Nation";
    }

    @Override
    public int getImageId() {
        return R.drawable.browns_dog;
    }

    @Override
    public String getURL() {
        return "https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://dawgpoundnation.com/feed/&num=20";
    }

    @Override
    public void makeModifications(Article article) {
        article.content = article.content.replaceAll("width:\\d+px", "");
        article.content = article.content.replaceAll("margin-left:\\d+px;", "");
    }

}

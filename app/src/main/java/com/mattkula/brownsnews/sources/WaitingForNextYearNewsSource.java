package com.mattkula.brownsnews.sources;

import com.mattkula.brownsnews.R;
import com.mattkula.brownsnews.database.Article;

public class WaitingForNextYearNewsSource extends NewsSource {

    private String[] allowedCategories = new String[]{
            "browns", "football", "nfl", "hoyer", "pettine", "cleveland browns"
    };

    @Override public String getName() {
        return "Waiting for Next Year";
    }

    @Override public int getImageId() {
        return R.drawable.waiting_for_next_year;
    }

    @Override public String getURL() {
        return "https://ajax.googleapis.com/ajax/services/feed/load?v=2.0&q=http://waitingfornextyear.com/feed/&num=20";
    }

    @Override public void makeModifications(Article article) {
        article.content = article.content.replaceAll("___________________________________________", "");
        article.content = article.content.replaceFirst("<a(|\\s+[^>]+)>", "");
    }

    @Override public boolean shouldUseArticle(Article article) {
        for (String category : allowedCategories) {
            if(article.categories.contains(category)){
                return true;
            }
        }
        return false;
    }

}

package com.mattkula.brownsnews.database;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by matt on 3/27/14.
 */
public class Article implements Serializable, Comparable<Article>{

    public long id;
    public String title;
    public String link;
    public String author;
    public Date publishedDate;
    public String contentSnippet;
    public String content;
    public String imageUrl;
    public String newsSource;
    public ArrayList<String> categories;
    public boolean isRead;
    public boolean isSaved;

    public static Article createFromJsonObject(JSONObject entry){
        Article article = new Article();

        try {
            article.title = entry.getString("title");
            article.author= entry.getString("author");
            article.link = entry.getString("link");
            article.publishedDate = getDate(entry.getString("publishedDate"));
            article.content = getCleanContent(entry.getString("content"));
            article.contentSnippet = getCleanContent(entry.getString("contentSnippet"));
            article.imageUrl = getImageUrl(entry.getString("content"));
            article.categories =  getCategories(entry.getJSONArray("categories"));
            article.isRead = false;
            article.isSaved = false;
        } catch (JSONException e){

        }

        return article;
    }

    public static ArrayList<String> getCategories(JSONArray array){
        ArrayList<String> cats = new ArrayList<String>();
        try {
            for(int i=0; i < array.length(); i++){
                cats.add(array.getString(i).toLowerCase());
            }
        } catch (Exception e){
           Log.e("ASDF", e.toString());
        }
        return cats;
    }

    public static String getImageUrl(String content){
        if(content.contains("<img")){
            int start = content.indexOf("src=") + 5;
            int end = content.indexOf("\"", start);
            return content.substring(start, end);
        } else {
            return "none";
        }
    }

    private static String getCleanContent(String content){
        content = content.replaceAll("<img.*>", "");
//        content = content.replaceAll("</?a(|\\s+[^>]+)>", "");
        return content;
    }

    private static Date getDate(String content){
        DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZZZ");
        try {
            Date date = formatter.parse(content);
            return date;
        } catch (ParseException e) {
            return new Date(1000, 10, 30);
        }
    }

    @Override
    public int compareTo(Article article) {
        return article.publishedDate.compareTo(this.publishedDate);
    }

    @Override
    public boolean equals(Object o) {
        return this.title.equals(((Article)o).title);
    }

    @Override
    public String toString() {
        return title + " " + author;
    }

    public static Article generateFakeArticle(){
        Article a = new Article();
        a.title = "Fake title";
        a.link = "http://example.com";
        a.author = "Matt Kula";
        a.imageUrl = "none";
        a.content = "Lorem ipsum yadda lfkjwl";
        a.publishedDate = new Date(System.currentTimeMillis());
        return a;
    }
}

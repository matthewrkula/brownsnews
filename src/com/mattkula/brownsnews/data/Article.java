package com.mattkula.brownsnews.data;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by matt on 3/27/14.
 */
public class Article implements Serializable, Comparable<Article>{

    public String title;
    public String link;
    public String author;
    public Date publishedDate;
    public String contentSnippet;
    public String content;
    public String imageUrl;

    public static Article createFromJsonObject(JSONObject entry){
        Article article = new Article();

        try {
            article.title = entry.getString("title");
            article.author= entry.getString("author");
            article.link = entry.getString("link");
            article.publishedDate = getDate(entry.getString("publishedDate"));
            article.content = getCleanContent(entry.getString("content"));
            article.contentSnippet = entry.getString("contentSnippet");
            article.imageUrl = getImageUrl(entry.getString("content"));
        } catch (JSONException e){

        }

        return article;
    }

    public static String getImageUrl(String content){
        if(content.startsWith("<img")){
            int start = content.indexOf("http");
            int end = content.indexOf(">") - 1;
            return content.substring(start, end);
        } else {
            return "none";
        }
    }

    private static String getCleanContent(String content){
        content = content.replaceAll("<img.*>", "");
        content = content.replaceAll("</?a(|\\s+[^>]+)>", "");
        content = content.replaceAll("<li>", "<p>");
        content = content.replaceAll("</li>", "</p>");
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
}

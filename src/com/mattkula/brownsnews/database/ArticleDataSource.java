package com.mattkula.brownsnews.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import com.mattkula.brownsnews.Prefs;
import com.mattkula.brownsnews.sources.NewsSource;
import com.mattkula.brownsnews.sources.NewsSourceManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by matt on 4/3/14.
 */
public class ArticleDataSource {

    ArticleSQLiteHelper helper;
    SQLiteDatabase database;
    Context context;

    public ArticleDataSource(Context context){
        helper = new ArticleSQLiteHelper(context);
        this.context = context;
    }

    public void open() throws SQLiteException {
        database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public Article createOrGetArticle(Article article){
        Article saved = getArticleForLink(article.link);
        if(saved != null){
//            Log.v("LOGDATABASE", "Getting from SQLite: " + article.title);
            return saved;
        }

        ContentValues values = new ContentValues();
        values.put(ArticleSQLiteHelper.COLUMN_TITLE, article.title);
        values.put(ArticleSQLiteHelper.COLUMN_LINK, article.link);
        values.put(ArticleSQLiteHelper.COLUMN_AUTHOR, article.author);
        values.put(ArticleSQLiteHelper.COLUMN_SNIPPET, article.contentSnippet);
        values.put(ArticleSQLiteHelper.COLUMN_CONTENT, article.content);
        values.put(ArticleSQLiteHelper.COLUMN_IMAGE_URL, article.imageUrl);
        values.put(ArticleSQLiteHelper.COLUMN_SOURCE, article.newsSource);
        values.put(ArticleSQLiteHelper.COLUMN_IS_SAVED, article.isSaved);
        values.put(ArticleSQLiteHelper.COLUMN_IS_READ, article.isRead);
        values.put(ArticleSQLiteHelper.COLUMN_PUBLISHED_DATE, article.publishedDate.getTime());
        long id = database.insert(ArticleSQLiteHelper.TABLE_ARTICLES, null, values);
        article = getArticleForId(id);
        Log.v("LOGDATABASE", "Now saved: " + article.title);
        return article;
    }

    public Article getArticleForLink(String link){
        Cursor cursor = database.query(ArticleSQLiteHelper.TABLE_ARTICLES, null,
                ArticleSQLiteHelper.COLUMN_LINK + " = ?", new String[]{link}, null, null, null);
        cursor.moveToFirst();

        if(cursor.isAfterLast()){
            cursor.close();
            return null;
        }

        Article article = getArticleFromCursor(cursor);
        cursor.close();
        return article;
    }

    public Article getArticleForId(long id){
        Cursor cursor = database.query(ArticleSQLiteHelper.TABLE_ARTICLES, null, "_id = " + id, null, null, null, null);
        cursor.moveToFirst();
        Article article = getArticleFromCursor(cursor);
        cursor.close();
        return article;
    }

    public void deleteArticle(Article article){
        database.delete(ArticleSQLiteHelper.TABLE_ARTICLES, "_id = " + article.id, null);
    }

    public ArrayList<Article> getAllArticles(int count){
        count = count == 0 ? 30 : count;
        ArrayList<Article> articles = new ArrayList<Article>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ArticleSQLiteHelper.TABLE_ARTICLES
                + " WHERE " + ArticleSQLiteHelper.COLUMN_SOURCE + " IN " + getAllowedSourcesString()
                + " ORDER BY " + ArticleSQLiteHelper.COLUMN_PUBLISHED_DATE + " DESC "
                + " LIMIT " + count, null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Article article = getArticleFromCursor(cursor);
            cursor.moveToNext();
            articles.add(article);
        }
        return articles;
    }

    public ArrayList<Article> getSavedArticles(){
        ArrayList<Article> articles = new ArrayList<Article>();
        Cursor cursor = database.rawQuery("SELECT * FROM " + ArticleSQLiteHelper.TABLE_ARTICLES
                + " WHERE " + ArticleSQLiteHelper.COLUMN_IS_SAVED + " = 1"
                + " ORDER BY " + ArticleSQLiteHelper.COLUMN_PUBLISHED_DATE + " DESC ", null);
        cursor.moveToFirst();

        while(!cursor.isAfterLast()){
            Article article = getArticleFromCursor(cursor);
            cursor.moveToNext();
            articles.add(article);
        }
        return articles;
    }

    private String getAllowedSourcesString(){
        StringBuilder builder = new StringBuilder();
        ArrayList<NewsSource> allowedSources = NewsSourceManager.getAllowedSources(context);
        builder.append("(");
        for(int i=0; i < allowedSources.size(); i++){
            builder.append("'" + allowedSources.get(i).getName() + (i == allowedSources.size() - 1 ? "'" : "',"));
        }
        builder.append(")");
        return builder.toString();
    }

    private Article getArticleFromCursor(Cursor cursor){
        Article article = new Article();
        article.id = cursor.getLong(0);
        article.title = cursor.getString(1);
        article.link = cursor.getString(2);
        article.author = cursor.getString(3);
        article.contentSnippet = cursor.getString(4);
        article.content = cursor.getString(5);
        article.imageUrl = cursor.getString(6);
        article.newsSource = cursor.getString(7);
        article.isSaved = cursor.getInt(8) > 0;
        article.isRead = cursor.getInt(9) > 0;
        article.publishedDate = new Date(cursor.getLong(10));
        return article;
    }

}

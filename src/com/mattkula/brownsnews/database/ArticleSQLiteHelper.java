package com.mattkula.brownsnews.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by matt on 4/3/14.
 */
public class ArticleSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_ARTICLES = "articles";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_LINK = "link";
    public static final String COLUMN_AUTHOR = "author";
    public static final String COLUMN_SNIPPET = "snippet";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_IMAGE_URL = "image_url";
    public static final String COLUMN_SOURCE = "source";
    public static final String COLUMN_IS_SAVED = "saved";
    public static final String COLUMN_IS_READ = "read";
    public static final String COLUMN_PUBLISHED_DATE = "publishedDate";

    private static final String DATABASE_NAME = "articles.db";
    private static final int DATABASE_VERSION = 1;

    private final static String DATABASE_CREATE = "CREATE TABLE " + TABLE_ARTICLES
            + " ("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text not null, "
            + COLUMN_LINK + " text not null, "
            + COLUMN_AUTHOR + " text not null, "
            + COLUMN_SNIPPET + " text, "
            + COLUMN_CONTENT + " text not null, "
            + COLUMN_IMAGE_URL + " text, "
            + COLUMN_SOURCE + " text, "
            + COLUMN_IS_SAVED + " integer default 0, "
            + COLUMN_IS_READ + " integer default 0, "
            + COLUMN_PUBLISHED_DATE + " integer not null);";

    public ArticleSQLiteHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_ARTICLES);
        onCreate(sqLiteDatabase);
    }
}

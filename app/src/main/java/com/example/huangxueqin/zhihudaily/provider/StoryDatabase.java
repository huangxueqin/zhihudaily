package com.example.huangxueqin.zhihudaily.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by huangxueqin on 16/8/21.
 */
public class StoryDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "news.db";

    private static final int VER_2016_RELEASE_A = 100;
    private static final int CUR_DATABASE_VERSION = VER_2016_RELEASE_A;

    interface Tables {
        String ReadNewses = "read_newses";
    }


    private final Context mContext;

    public StoryDatabase(Context context) {
        super(context, DB_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ReadNewses + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + StoryContact.ReadNewses.NEWS_ID + " INTEGER NOT NULL,"
                + StoryContact.ReadNewses.NEWS_DATE + " TEXT,"
                + StoryContact.ReadNewses.NEWS_TITLE + " TEXT,"
                + StoryContact.ReadNewses.NEWS_IMAGES + " TEXT,"
                + StoryContact.ReadNewses.NEWS_TYPE + " INTEGER,"
                + StoryContact.ReadNewses.NEWS_GA_PREFIX + " TEXT,"
                + "UNIQUE (" + StoryContact.ReadNewses.NEWS_ID + ") ON CONFLICT REPLACE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static void deleteDatabse(Context context) {
        context.deleteDatabase(DB_NAME);
    }

}

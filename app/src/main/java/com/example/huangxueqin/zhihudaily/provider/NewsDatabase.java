package com.example.huangxueqin.zhihudaily.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by huangxueqin on 16/8/21.
 */
public class NewsDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "news.db";

    private static final int VER_2016_RELEASE_A = 100;
    private static final int CUR_DATABASE_VERSION = VER_2016_RELEASE_A;

    interface Tables {
        String ReadNewses = "read_newses";
    }


    private final Context mContext;

    public NewsDatabase(Context context) {
        super(context, DB_NAME, null, CUR_DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + Tables.ReadNewses + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + NewsContact.ReadNewses.NEWS_ID + " TEXT NOT NULL,"
                + NewsContact.ReadNewses.NEWS_DATE + " TEXT,"
                + NewsContact.ReadNewses.NEWS_TITLE + " TEXT,"
                + NewsContact.ReadNewses.NEWS_IMAGES + " TEXT,"
                + NewsContact.ReadNewses.NEWS_TYPE + " INTEGER,"
                + NewsContact.ReadNewses.NEWS_GA_PREFIX + " TEXT,"
                + "UNIQUE (" + NewsContact.ReadNewses.NEWS_ID + ") ON CONFLICT REPLACE)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public static void deleteDatabse(Context context) {
        context.deleteDatabase(DB_NAME);
    }

}

package com.example.huangxueqin.zhihudaily.db;


import android.net.Uri;

/**
 * Created by huangxueqin on 16/8/21.
 */
public final class NewsContact {

    public static final String CONTENT_TYPE_APP_BASE = "zhihudaily.";
    public static final String CONTENT_TYPE_BASE = "vnd.android.cursor.dir/vnd." + CONTENT_TYPE_APP_BASE;
    public static final String CONTENT_ITEM_TYPE_BASE = "vnd.android.cursor.item/vnd." + CONTENT_TYPE_APP_BASE;

    static interface ReadNewsesColumns {
        String NEWS_ID = "news_id";
        String NEWS_DATE = "news_date";
        String NEWS_TITLE = "news_title";
        String NEWS_IMAGES = "news_images";
        String NEWS_GA_PREFIX = "news_ga_prefix";
        String NEWS_TYPE = "news_type";
    }

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + NewsProvider.AUTHORITY);
    public static final String PATH_READ_NEWSES = NewsDatabase.Tables.ReadNewses;

    static class ReadNewses implements ReadNewsesColumns {
        private static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_READ_NEWSES).build();

        public static Uri buildReadNewsesUri(String news_id) {
            return CONTENT_URI.buildUpon().appendPath(news_id).build();
        }
    }


    public static String makeContentType(String id) {
        if(id != null) {
            return CONTENT_TYPE_BASE + id;
        }
        else {
            return null;
        }
    }

    public static String makeContentItemType(String id) {
        if(id != null) {
            return CONTENT_ITEM_TYPE_BASE + id;
        }
        else {
            return null;
        }
    }
}

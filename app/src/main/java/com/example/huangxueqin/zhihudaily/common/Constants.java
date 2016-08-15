package com.example.huangxueqin.zhihudaily.common;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by huangxueqin on 16-7-22.
 */
public class Constants {
    public static final String BASE_URL = "http://news-at.zhihu.com/api/";

    public static class FragmentTags {
        public static final String LATEST_NEWS_FRAGMENT_TAGS = "latest_news_fragment";
    }

    public static class IntentKeys {
        public static final String KEY_NEWS_ID = "news_id";
    }

    public static Point getScreenSize(Context context) {
        Point size = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(size);
        return size;
    }
}

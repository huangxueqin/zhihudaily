package com.example.huangxueqin.zhihudaily.support;

import android.content.Context;
import android.graphics.Point;
import android.view.WindowManager;

/**
 * Created by huangxueqin on 16-7-22.
 */
public class Constants {
    public static final String BASE_URL = "http://news-at.zhihu.com/api/";
    private static final String URL_REQUEST_START_IMAGE = "http://news-at.zhihu.com/api/4/start-image";

    public static Point getScreenSize(Context context) {
        Point size = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(size);
        return size;
    }
}

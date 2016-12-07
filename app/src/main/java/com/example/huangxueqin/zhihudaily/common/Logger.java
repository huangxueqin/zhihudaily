package com.example.huangxueqin.zhihudaily.common;

import android.util.Log;

/**
 * Created by huangxueqin on 2016/12/7.
 */

public class Logger {
    private static final String TAG = "ZHIHU_DAILY";

    public static void D(String msg) {
        Log.d(TAG, msg);
    }

    public static void D(String tag, String msg) {
        Log.d(tag, msg);
    }
}

package com.example.huangxueqin.zhihudaily.common;

import android.content.Context;

/**
 * Created by huangxueqin on 16/9/10.
 */
public class ScreenUtils {
    public static int px2dp(Context context, int px) {
        return (int)(px / context.getResources().getDisplayMetrics().density + 0.5);
    }

    public static int dp2px(Context context, int dp) {
        return (int)(dp * context.getResources().getDisplayMetrics().density + 0.5);
    }
}

package com.example.huangxueqin.zhihudaily.common;

/**
 * Created by huangxueqin on 16/9/10.
 */
public class ColorUtils {
    public static int getGreyColor(int alpha) {
        int clampedAlpha = Math.min(255, Math.max(0, alpha));
        return 0xffffff | (alpha << 24);
    }

    public static int getTransparentColor(int color, int alpha) {
        int clampedAlpha = Math.min(255, Math.max(0, alpha));
        return ((color & 0xffffff) | (alpha << 24));
    }
}

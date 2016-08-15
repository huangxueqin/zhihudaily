package com.example.huangxueqin.zhihudaily.common;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huangxueqin on 16/8/15.
 */
public class Utilities {
    /**
     * get date string, eg: 2013.11.18 -> 20131118
     * @param index offset from today
     * @return
     */
    public static String getDateStr(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, index);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        return formatter.format(calendar.getTime());
    }
}

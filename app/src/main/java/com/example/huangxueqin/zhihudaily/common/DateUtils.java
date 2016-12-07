package com.example.huangxueqin.zhihudaily.common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by huangxueqin on 16/8/15.
 */
public class DateUtils {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyyMMdd");
    private static final SimpleDateFormat READABLE_FORMATTER = new SimpleDateFormat("MM月dd日");
    /**
     * get date string, eg: 2013.11.18 -> 20131118
     * @param index offset from today
     * @return
     */
    public static String getDateStr(int index) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, index);
        return FORMATTER.format(calendar.getTime());
    }

    public static String getDateStr(String startDate, int prev) {
        try {
            Calendar ca = Calendar.getInstance();
            ca.setTime(FORMATTER.parse(startDate));
            ca.add(Calendar.DAY_OF_MONTH, -prev);
            return FORMATTER.format(ca.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getReadableDateStr(String dateStr) {
        try {
            Calendar ca = Calendar.getInstance();
            ca.setTime(FORMATTER.parse(dateStr));
            return READABLE_FORMATTER.format(ca.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}

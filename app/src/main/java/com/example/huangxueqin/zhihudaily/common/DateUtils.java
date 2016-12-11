package com.example.huangxueqin.zhihudaily.common;

import android.content.Context;

import com.example.huangxueqin.zhihudaily.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huangxueqin on 16/8/15.
 */
public class DateUtils {
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyyMMdd");
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

    public static String getReadableDateStr(Context context, String dateStr) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat(context.getResources().getString(R.string.list_date_format));
            Calendar ca = Calendar.getInstance();
            ca.setTime(FORMATTER.parse(dateStr));
            return formatter.format(ca.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}

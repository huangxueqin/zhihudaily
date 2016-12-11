package com.example.huangxueqin.zhihudaily.common;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxueqin on 2016/12/11.
 */

public class ArrayUtils {
    public static <T> boolean isEmpty(T... array) {
        return array == null || array.length == 0;
    }

    public static <T> ArrayList<T>  asList(T... arrays) {
        if (arrays == null) { return new ArrayList<>(); }
        ArrayList<T> list = new ArrayList<>(arrays.length);
        for (T t : arrays) {
            list.add(t);
        }
        return list;
    }

    public static <T> T lastOf(List<T> list) {
        if (list.isEmpty()) { return null; }
        return list.get(list.size()-1);
    }

    public static <T> T firstOf(List<T> list) {
        if (list.isEmpty()) { return null; }
        return list.get(0);
    }
}

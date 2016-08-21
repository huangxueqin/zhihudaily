package com.example.huangxueqin.zhihudaily.models;

import java.util.ArrayList;

/**
 * Created by huangxueqin on 16/8/9.
 */
public class News {
    public String date;
    public String title;
    public String ga_prefix;
    public ArrayList<String> images;
    public Integer type;
    public String id;

    // extra data
    public boolean read;
}

package com.example.huangxueqin.zhihudaily.models;

import java.util.ArrayList;

/**
 * Created by huangxueqin on 16-7-26.
 */
public class NewsContent {
    public String body;
    public String image_source;
    public String title;
    public String image;
    public String share_url;
    public String[] js;
    public ArrayList<Recommender> recommenders;
    public String ga_prefix;
    public Section section;
    public int type;
    public String id;
    public String[] css;

    public static class Recommender {
        public String avatar;
    }

    public static class Section {
        public String thumbnail;
        public String id;
        public String name;
    }
}

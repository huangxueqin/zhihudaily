package com.example.huangxueqin.zhihudaily.models;

import java.util.ArrayList;

/**
 * Created by huangxueqin on 16-7-22.
 */
public class LatestNews {
    public String date;
    public ArrayList<News> stories;
    public ArrayList<TopStory> top_stories;

    public static class TopStory {
        public String title;
        public String ga_prefix;
        public String image;
        public Integer type;
        public String id;
    }
}

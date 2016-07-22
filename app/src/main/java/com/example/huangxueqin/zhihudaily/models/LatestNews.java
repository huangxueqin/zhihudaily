package com.example.huangxueqin.zhihudaily.models;

import java.util.ArrayList;

/**
 * Created by huangxueqin on 16-7-22.
 */
public class LatestNews {
    String date;
    ArrayList<Story> stories;
    ArrayList<TopStory> top_stories;

    public static class Story {
        String title;
        String ga_prefix;
        ArrayList<String> images;
        Integer type;
        String id;
    }

    public static class TopStory {
        String title;
        String ga_prefix;
        String image;
        Integer type;
        String id;
    }
}

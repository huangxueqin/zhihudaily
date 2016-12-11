package com.example.huangxueqin.zhihudaily.models.story;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangxueqin on 2016/12/10.
 */

public class FineStory extends SimpleStory {
    public String body;
    @SerializedName("image_source")
    public String imageSource;
    @SerializedName("share_url")
    public String shareURL;
    public String[] js;
    public String[] css;
    public Recommender[] recommenders;
    public Section section;

    public static class Recommender {
        public String avatar;
    }

    public static class Section {
        public String thumbnail;
        public long id;
        public String name;
    }
}

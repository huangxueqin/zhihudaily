package com.example.huangxueqin.zhihudaily.models.story;

import com.example.huangxueqin.zhihudaily.models.story.SimpleStory;
import com.google.gson.annotations.SerializedName;

/**
 * Created by huangxueqin on 2016/12/11.
 */

public class StoryResponse {
    public String date;
    public SimpleStory[] stories;
    @SerializedName("top_stories")
    public SimpleStory[] topStories;
}

package com.example.huangxueqin.zhihudaily.common;

import com.example.huangxueqin.zhihudaily.models.StartImageInfo;
import com.example.huangxueqin.zhihudaily.models.story.FineStory;
import com.example.huangxueqin.zhihudaily.models.story.StoryResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by huangxueqin on 16-7-22.
 */
public interface ZhihuAPI {
    @GET("4/start-image/320*432")
    Call<StartImageInfo> get320StartImage();

    @GET("4/start-image/480*728")
    Call<StartImageInfo> get480StartImage();

    @GET("4/start-image/720*1184")
    Call<StartImageInfo> get720StartImage();

    @GET("4/start-image/1080*1776")
    Call<StartImageInfo> get1080StartImage();

    @GET("4/news/latest")
    Call<StoryResponse> getLatestNews();

    @GET("4/news/{id}")
    Call<FineStory> getNewsContent(@Path("id") String id);

    @GET("4/news/before/{date}")
    Call<StoryResponse> getHistoryNews(@Path("date") String date);
}

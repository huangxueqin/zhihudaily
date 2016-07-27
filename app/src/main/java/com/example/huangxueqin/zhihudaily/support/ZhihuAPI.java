package com.example.huangxueqin.zhihudaily.support;

import com.example.huangxueqin.zhihudaily.models.LatestNews;
import com.example.huangxueqin.zhihudaily.models.NewsContent;
import com.example.huangxueqin.zhihudaily.models.StartImageInfo;

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
    Call<LatestNews> getLatestNews();

    @GET("4/news/{id}")
    Call<NewsContent> getNewsContent(@Path("id") String id);
}

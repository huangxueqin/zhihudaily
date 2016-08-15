package com.example.huangxueqin.zhihudaily.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.huangxueqin.zhihudaily.common.Constants;
import com.example.huangxueqin.zhihudaily.common.ZhihuAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huangxueqin on 16-7-26.
 */
public class BaseActivity extends AppCompatActivity {
    protected Retrofit mRetrofit;
    protected ZhihuAPI mAPI;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mAPI = mRetrofit.create(ZhihuAPI.class);
    }
}

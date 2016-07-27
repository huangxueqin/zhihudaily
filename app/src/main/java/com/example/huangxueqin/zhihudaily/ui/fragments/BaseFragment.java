package com.example.huangxueqin.zhihudaily.ui.fragments;

import android.support.v4.app.Fragment;

import com.example.huangxueqin.zhihudaily.interfaces.IFragmentCallback;
import com.example.huangxueqin.zhihudaily.support.Constants;
import com.example.huangxueqin.zhihudaily.support.ZhihuAPI;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huangxueqin on 16-7-22.
 */
public class BaseFragment extends Fragment implements IFragmentCallback {
    protected ZhihuAPI mAPI;

    @Override
    public void setAPI(ZhihuAPI api) {
        mAPI = api;
    }
}

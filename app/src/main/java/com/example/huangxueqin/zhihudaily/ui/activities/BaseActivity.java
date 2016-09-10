package com.example.huangxueqin.zhihudaily.ui.activities;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.common.Constants;
import com.example.huangxueqin.zhihudaily.common.ZhihuAPI;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by huangxueqin on 16-7-26.
 */
public class BaseActivity extends AppCompatActivity {
    protected Retrofit mRetrofit;
    protected ZhihuAPI mAPI;

    @Nullable
    @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mAPI = mRetrofit.create(ZhihuAPI.class);
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        bindView();
    }

    protected void bindView() {
        ButterKnife.bind(this);
        setupToolbar();
    }

    private void setupToolbar() {
        if(mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }
}

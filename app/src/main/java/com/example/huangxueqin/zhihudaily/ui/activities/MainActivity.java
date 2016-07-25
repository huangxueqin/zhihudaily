package com.example.huangxueqin.zhihudaily.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.support.Constants;
import com.example.huangxueqin.zhihudaily.ui.fragments.BaseFragment;
import com.example.huangxueqin.zhihudaily.ui.fragments.LatestNewsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangxueqin on 16-7-21.
 */
public class MainActivity extends AppCompatActivity {
    BaseFragment mLatestNewsFragment = new LatestNewsFragment();
    FragmentTransaction mTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupFragmentOnce();
    }

    @Override
    protected void onResume() {
        mTransaction.show(mLatestNewsFragment);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void setupFragmentOnce() {
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.container, mLatestNewsFragment, Constants.FragmentTags.LATEST_NEWS_FRAGMENT_TAGS);
        mTransaction.commit();
    }
}

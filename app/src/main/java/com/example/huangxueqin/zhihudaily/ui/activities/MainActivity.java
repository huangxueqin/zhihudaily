package com.example.huangxueqin.zhihudaily.ui.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.view.Menu;
import android.view.Window;

import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.common.Constants;
import com.example.huangxueqin.zhihudaily.ui.fragments.BaseFragment;
import com.example.huangxueqin.zhihudaily.ui.fragments.LatestNewsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangxueqin on 16-7-21.
 */
public class MainActivity extends BaseActivity {
    BaseFragment mLatestNewsFragment = new LatestNewsFragment();
    FragmentTransaction mTransaction;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar.setTitle(R.string.app_name);
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
        mLatestNewsFragment.setAPI(mAPI);
        mTransaction = getSupportFragmentManager().beginTransaction();
        mTransaction.add(R.id.container, mLatestNewsFragment, Constants.FragmentTags.LATEST_NEWS_FRAGMENT_TAGS);
        mTransaction.commit();
    }
}

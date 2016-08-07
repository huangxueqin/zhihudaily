package com.example.huangxueqin.zhihudaily.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.interfaces.INewsListItemClickListener;
import com.example.huangxueqin.zhihudaily.models.LatestNews;
import com.example.huangxueqin.zhihudaily.support.Constants;
import com.example.huangxueqin.zhihudaily.ui.activities.NewsPresentActivity;
import com.example.huangxueqin.zhihudaily.ui.adapters.NewsListAdapter;
import com.example.huangxueqin.zhihudaily.ui.widget.LineDecoration;
import com.example.huangxueqin.zhihudaily.ui.widget.PullRefreshLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huangxueqin on 16-7-22.
 */
public class LatestNewsFragment extends BaseFragment implements Callback<LatestNews>, INewsListItemClickListener, PullRefreshLayout.OnRefreshListener {
    private static final String TAG = "LatestNewsFragment TAG";

    @BindView(R.id.news_refresher) PullRefreshLayout mRefresher;
    @BindView(R.id.latest_news_list) RecyclerView mNewsList;
    private boolean mCancel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_latest_news, container, false);
        ButterKnife.bind(this, v);
        mRefresher.setOnRefreshListener(this);
        mNewsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mNewsList.addItemDecoration(new LineDecoration(getContext()));
        requestLatestNews();
        return v;
    }

    void requestLatestNews() {
        Call<LatestNews> call = mAPI.getLatestNews();
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<LatestNews> call, Response<LatestNews> response) {
        if(!mCancel) {
            mNewsList.setAdapter(new NewsListAdapter(response.body(), this));
            if (mRefresher.isRefreshing()) {
                mRefresher.setRefresh(false);
            }
        }
    }

    @Override
    public void onFailure(Call<LatestNews> call, Throwable t) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mCancel = true;
    }

    @Override
    public void onRequestNews(String id) {
        Intent i = new Intent(getActivity(), NewsPresentActivity.class);
        i.putExtra(Constants.IntentKeys.KEY_NEWS_ID, id);
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        requestLatestNews();
    }

    public static void D(String msg) {
        Log.d(TAG, msg);
    }
}

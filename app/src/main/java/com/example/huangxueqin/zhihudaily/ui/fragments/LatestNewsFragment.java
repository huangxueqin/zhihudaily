package com.example.huangxueqin.zhihudaily.ui.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.common.Utilities;
import com.example.huangxueqin.zhihudaily.interfaces.INewsListItemClickListener;
import com.example.huangxueqin.zhihudaily.models.HistoryNews;
import com.example.huangxueqin.zhihudaily.models.LatestNews;
import com.example.huangxueqin.zhihudaily.common.Constants;
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
public class LatestNewsFragment extends BaseFragment implements INewsListItemClickListener, PullRefreshLayout.OnRefreshListener {
    private static final String TAG = "LatestNewsFragment TAG";


    private enum RequestAction {ACTION_REQUEST_LATEST, ACTION_REQUEST_HISTORY};

    @BindView(R.id.news_refresher) PullRefreshLayout mRefresher;
    @BindView(R.id.latest_news_list) RecyclerView mNewsList;
    private boolean mCancel;
    private boolean mIsLoadHistoryNews;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_latest_news, container, false);
        ButterKnife.bind(this, v);
        mRefresher.setOnRefreshListener(this);
        mNewsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mNewsList.addItemDecoration(new LineDecoration(getContext()));
        mNewsList.addOnScrollListener(mNewsListScrollListener);
        requestNewsByAction(RequestAction.ACTION_REQUEST_LATEST);
        return v;
    }

    void requestNewsByAction(RequestAction action, String... params) {
        switch (action) {
            case ACTION_REQUEST_LATEST:
                Call<LatestNews> latestNewsCall = mAPI.getLatestNews();
                latestNewsCall.enqueue(mLatestNewsCallback);
                break;
            case ACTION_REQUEST_HISTORY:
                String date = params[0];
                D("request history news, Data: " + date);
                Call<HistoryNews> historyNewsCall = mAPI.getHistoryNews(date);
                historyNewsCall.enqueue(mHistoryNewsCallback);
                break;
        }
    }

    private Callback<LatestNews> mLatestNewsCallback = new Callback<LatestNews>() {
        @Override
        public void onResponse(Call<LatestNews> call, Response<LatestNews> response) {
            if(!mCancel) {
                mNewsList.setAdapter(new NewsListAdapter(response.body(), LatestNewsFragment.this));
            }
            mRefresher.setRefresh(false);
        }

        @Override
        public void onFailure(Call<LatestNews> call, Throwable t) {
            mRefresher.setRefresh(false);
        }
    };


    private Callback<HistoryNews> mHistoryNewsCallback = new Callback<HistoryNews>() {
        @Override
        public void onResponse(Call<HistoryNews> call, Response<HistoryNews> response) {
            if(!mCancel) {
                NewsListAdapter adapter = (NewsListAdapter) mNewsList.getAdapter();
                HistoryNews historyNews = response.body();
                adapter.addHistoryNews(response.body().stories);
            }
            mIsLoadHistoryNews = false;
        }

        @Override
        public void onFailure(Call<HistoryNews> call, Throwable t) {
            mIsLoadHistoryNews = false;
        }
    };

    @Override
    public void onRequestNews(String id) {
        Intent i = new Intent(getActivity(), NewsPresentActivity.class);
        i.putExtra(Constants.IntentKeys.KEY_NEWS_ID, id);
        startActivity(i);
    }

    @Override
    public void onRefresh() {
        requestNewsByAction(RequestAction.ACTION_REQUEST_LATEST);
    }

    private RecyclerView.OnScrollListener mNewsListScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if(dy > 0 && !canNewsListScrollDown() && !mIsLoadHistoryNews) {
                NewsListAdapter adapter = (NewsListAdapter) mNewsList.getAdapter();
                requestNewsByAction(RequestAction.ACTION_REQUEST_HISTORY, Utilities.getDateStr(-adapter.getNextHistoryIndex()));
                mIsLoadHistoryNews = true;
            }
        }
    };

    private boolean canNewsListScrollDown() {
        if(Build.VERSION.SDK_INT < 14) {
            LinearLayoutManager lm = (LinearLayoutManager) mNewsList.getLayoutManager();
            int visibleItemCount = mNewsList.getChildCount();
            int totalItemCount = lm.getItemCount();
            int firstItemCount = lm.findFirstVisibleItemPosition();
            if(firstItemCount + visibleItemCount >= totalItemCount) {
                View lastVisibleView = mNewsList.getChildAt(visibleItemCount-1);
                if(lastVisibleView.getBottom() >= mNewsList.getWidth() - mNewsList.getPaddingBottom()) {
                    return false;
                }
            }
            return true;
        } else {
            return mNewsList.canScrollVertically(1);
        }
    }

    public static void D(String msg) {
        Log.d(TAG, msg);
    }
}

package com.example.huangxueqin.zhihudaily.ui.activities;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.common.ColorUtils;
import com.example.huangxueqin.zhihudaily.common.ScreenUtils;
import com.example.huangxueqin.zhihudaily.common.Constants;
import com.example.huangxueqin.zhihudaily.models.story.FineStory;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huangxueqin on 16-7-26.
 */
public class NewsPresentActivity extends BaseActivity implements Callback<FineStory> {
    private static final int TOOLBAR_HIDE_SHOW_DIST = 50;

    @BindView(R.id.content_presenter)
    WebView mNewsPresenter;
    @BindView(R.id.news_container)
    NestedScrollView mNewsContainer;
    @BindView(R.id.news_image)
    ImageView mNewsImage;

    private String mNewsId;
    private int mToolbarHeight;
    private int mNewsImageHeight;
    private int mToolbarHideShowDist;
    private boolean mIsToolbarHide = false;
    private int mLastScrollY = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_present);
        ButterKnife.bind(this);
        mNewsPresenter.getSettings().setJavaScriptEnabled(true);
        mNewsPresenter.getSettings().setDefaultTextEncodingName("UTF-8");
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNewsId = getIntent().getStringExtra(Constants.IntentKeys.KEY_NEWS_ID);
        if(mNewsId != null) {
            loadNews(mNewsId);
        }

        mToolbarHeight = getActionBarHeight();
        getNewsImageHeight();
        mToolbarHideShowDist = ScreenUtils.dp2px(this, TOOLBAR_HIDE_SHOW_DIST);
        mLastScrollY = mNewsImageHeight;
        mNewsContainer.setOnScrollChangeListener(mNewsScrollChangeListener);
    }

    private int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
    }

    private void getNewsImageHeight() {
        mNewsImage.post(new Runnable() {
            @Override
            public void run() {
                mNewsImageHeight = mNewsImage.getHeight();
                D("mNewsImageHeight = " + mNewsImageHeight);
            }
        });
    }

    private void loadNews(String id) {
        Call<FineStory> call = mAPI.getNewsContent(id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<FineStory> call, Response<FineStory> response) {
        FineStory content = response.body();
        Glide.with(this).load(content.image).into(mNewsImage);
        mNewsPresenter.loadDataWithBaseURL("x-data://base", buildHtml(content), "text/html", "UTF-8", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFailure(Call<FineStory> call, Throwable t) {
    }

    private String buildHtml(FineStory content) {

        StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"img-wrap\">")
                .append("<h1 class=\"headline-title\">").append(content.title).append("</h1>")
                .append("<span class=\"img-source\">").append(content.imageSource).append("</span>")
                .append("<img src=\"").append(content.image).append("\" alt=\"\">")
                .append("<div class=\"img-mask\"></div>");
        String body = content.body.replace("<div class=\"img-place-holder\"></div>", "");

        String[] js = content.js;
        String[] css = content.css;

        StringBuffer newsHtml = new StringBuffer();
        newsHtml.append("<html><head>");
        for(int i = 0; i < css.length; i++) {
            newsHtml.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
                    .append(css[i])
                    .append("\">");
        }
        newsHtml.append("</head><body>");
        newsHtml.append(body);
        newsHtml.append("</body></html>");

        return newsHtml.toString();
    }

    private NestedScrollView.OnScrollChangeListener mNewsScrollChangeListener = new NestedScrollView.OnScrollChangeListener() {
        @Override
        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
            if(scrollY != oldScrollY) {
                int currentScrollY = mNewsContainer.getScrollY();
                if(currentScrollY <= mToolbarHeight + mNewsImageHeight) {
                    // change newsImage's top margin value
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mNewsImage.getLayoutParams();
                    params.topMargin = mToolbarHeight - currentScrollY / 2;
                    FrameLayout parent = (FrameLayout) mNewsImage.getParent();
                    parent.requestLayout();
                }

                // adjust toolbar color
                if(currentScrollY <= mNewsImageHeight) {
                    int curToolbarAlpha = (int) (255 - 255.0/mNewsImageHeight * currentScrollY);
                    setToolbarBackgroundAlpha(curToolbarAlpha);
                    if(mIsToolbarHide) {
                        mIsToolbarHide = false;
                        mToolbar.setVisibility(View.VISIBLE);
                    }
                }

                // adjust toolbar hide/show
                if(currentScrollY > mNewsImageHeight) {
                    if(scrollY > oldScrollY) {
                        if(!mIsToolbarHide) {
                            mIsToolbarHide = true;
                            mLastScrollY = -1;
                            mToolbar.setVisibility(View.GONE);
                        }
                    }
                    else if(scrollY < oldScrollY) {
                        if(mIsToolbarHide) {
                            if (mLastScrollY == -1) {
                                mLastScrollY = currentScrollY;
                            } else if(mLastScrollY - currentScrollY > mToolbarHideShowDist) {
                                mIsToolbarHide = false;
                                setToolbarBackgroundAlpha(255);
                                mToolbar.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        }
    };

    private void setToolbarBackgroundAlpha(int alpha) {
        int originColor = ((ColorDrawable)mToolbar.getBackground()).getColor();
        mToolbar.setBackground(new ColorDrawable(ColorUtils.getTransparentColor(originColor, alpha)));
    }

    private static void D(String msg) {
        Log.d(NewsPresentActivity.class.getSimpleName(), msg);
    }
}

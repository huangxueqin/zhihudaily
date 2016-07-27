package com.example.huangxueqin.zhihudaily.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.models.NewsContent;
import com.example.huangxueqin.zhihudaily.support.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by huangxueqin on 16-7-26.
 */
public class NewsPresentActivity extends BaseActivity implements Callback<NewsContent> {

    @BindView(R.id.content_presenter) WebView mNewsPresenter;
    String mNewsId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_present);
        ButterKnife.bind(this);
        mNewsPresenter.getSettings().setJavaScriptEnabled(true);
        mNewsPresenter.getSettings().setDefaultTextEncodingName("UTF-8");

        mNewsId = getIntent().getStringExtra(Constants.IntentKeys.KEY_NEWS_ID);
        if(mNewsId != null) {
            loadNews(mNewsId);
        }
    }

    private void loadNews(String id) {
        Call<NewsContent> call = mAPI.getNewsContent(id);
        call.enqueue(this);
    }

    @Override
    public void onResponse(Call<NewsContent> call, Response<NewsContent> response) {
        mNewsPresenter.loadDataWithBaseURL("", buildHtml(response.body()), "text/html", "UTF-8", "");
    }

    @Override
    public void onFailure(Call<NewsContent> call, Throwable t) {
    }

    private String buildHtml(NewsContent content) {
        String title = content.title;
        String body = content.body;
        String[] js = content.js;
        String[] css = content.css;
        StringBuffer sb = new StringBuffer();
        sb.append("<html><head><title>")
                .append(title)
                .append("</title>");
        for(int i = 0; i < css.length; i++) {
            sb.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
                    .append(css[i])
                    .append("\">");
        }
        sb.append("</head><body>");
        sb.append(body);
        for(int i = 0; i < js.length; i++) {
            sb.append("<script>");
            sb.append(js[i]);
            sb.append("</script>");
        }

        sb.append("</body></html>");
        return sb.toString();
    }
}

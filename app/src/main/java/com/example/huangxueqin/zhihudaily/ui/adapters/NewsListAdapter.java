package com.example.huangxueqin.zhihudaily.ui.adapters;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.interfaces.INewsListItemClickListener;
import com.example.huangxueqin.zhihudaily.models.LatestNews;
import com.example.huangxueqin.zhihudaily.ui.widget.CirclePageIndicator;

import java.util.List;

/**
 * Created by huangxueqin on 16-7-25.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> implements ViewPager.OnPageChangeListener {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_LIST_ITEM = 1;

    private LatestNews mLatestNews;
    private int mCurrentHeaderItem;
    private INewsListItemClickListener mListener;

    public NewsListAdapter(LatestNews latestNews) {
        this(latestNews, null, 0);
    }

    public NewsListAdapter(LatestNews latestNews, INewsListItemClickListener listener) {
        this(latestNews, listener, 0);
    }

    public NewsListAdapter(LatestNews latestNews, int currentHeaderItem) {
        this(latestNews, null, currentHeaderItem);
    }

    public NewsListAdapter(LatestNews latestNews, INewsListItemClickListener listener, int currentHeaderItem) {
        mLatestNews = latestNews;
        mCurrentHeaderItem = currentHeaderItem;
        mListener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && mLatestNews.top_stories != null && mLatestNews.top_stories.size() > 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_LIST_ITEM;
        }
    }

    public Object getItem(int position) {
        if(hasHeader()) {
            return position == 0 ? mLatestNews.top_stories : mLatestNews.stories.get(position-1);
        } else {
            return mLatestNews.stories.get(position);
        }
    }

    public boolean hasHeader() {
        return getItemViewType(0) == TYPE_HEADER;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_HEADER) {
            root = inflater.inflate(R.layout.view_top_stories, parent, false);
        } else {
            root = inflater.inflate(R.layout.view_latest_news_list_item, parent, false);
        }
        return new ViewHolder(root, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPosition(position);
        if(getItemViewType(position) == TYPE_HEADER) {
            if(holder.mTopStoriesGallery.getAdapter() == null) {
                holder.mTopStoriesGallery.setAdapter(new TopNewAdapter(mLatestNews.top_stories));
                holder.mPageIndicator.setViewPager(holder.mTopStoriesGallery);
            }
            holder.mTopStoriesGallery.addOnPageChangeListener(this);
            holder.mTopStoriesGallery.setCurrentItem(mCurrentHeaderItem);
        } else {
            LatestNews.Story story = (LatestNews.Story) getItem(position);
            holder.mNewsTitle.setText(story.title);
            Glide.with(holder.mNewsThumb.getContext())
                    .load(story.images.get(0))
                    .into(holder.mNewsThumb);
        }
    }

    @Override
    public int getItemCount() {
        if(mLatestNews != null) {
            return (mLatestNews.stories == null ? 0 : mLatestNews.stories.size()) +
                    (mLatestNews.top_stories == null ? 0 : 1);
        }
        return 0;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        mCurrentHeaderItem = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int mPosition;
        ViewPager mTopStoriesGallery;
        CirclePageIndicator mPageIndicator;
        TextView mNewsTitle;
        ImageView mNewsThumb;

        public ViewHolder(View root, int viewType) {
            super(root);
            if(viewType == TYPE_LIST_ITEM) {
                mNewsTitle = (TextView) root.findViewById(R.id.news_title);
                mNewsThumb = (ImageView) root.findViewById(R.id.news_thumb);
            } else {
                mTopStoriesGallery = (ViewPager) root.findViewById(R.id.top_stories_gallery);
                mPageIndicator = (CirclePageIndicator) root.findViewById(R.id.top_stories_indicator);
            }
            root.setOnClickListener(this);
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            String id = null;
            Object o = getItem(mPosition);
            if(o instanceof LatestNews.Story) {
                id = ((LatestNews.Story) o).id;
            } else {
                List<LatestNews.TopStory> topStories = (List<LatestNews.TopStory>) o;
                id = topStories.get(mTopStoriesGallery.getCurrentItem()).id;
            }

            if(mListener != null) {
                mListener.onRequestNews(id);
            }
        }
    }
}

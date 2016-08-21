package com.example.huangxueqin.zhihudaily.ui.adapters;

import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.common.DateTools;
import com.example.huangxueqin.zhihudaily.interfaces.INewsListItemClickListener;
import com.example.huangxueqin.zhihudaily.models.HistoryNews;
import com.example.huangxueqin.zhihudaily.models.LatestNews;
import com.example.huangxueqin.zhihudaily.models.News;
import com.example.huangxueqin.zhihudaily.ui.widget.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxueqin on 16-7-25.
 */
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> implements ViewPager.OnPageChangeListener {
    public static final int TYPE_GALLERY = 0;
    public static final int TYPE_LIST_ITEM_NEWS = 1;
    public static final int TYPE_LIST_ITEM_DATE = 2;

    private List<LatestNews.TopStory> mTopStories;
    private List<News> mStories;

    private String mFirstDate;
    private String mLastDate;

    private int mCurrentGalleryIndex;
    private boolean mHasGallery;
    private INewsListItemClickListener mListener;

    public NewsListAdapter(LatestNews latestNews) {
        this(latestNews, null, 0);
    }

    public NewsListAdapter(LatestNews latestNews, INewsListItemClickListener listener) {
        this(latestNews, listener, 0);
    }

    public NewsListAdapter(LatestNews latestNews, int galleryStartIndex) {
        this(latestNews, null, galleryStartIndex);
    }

    public NewsListAdapter(LatestNews latestNews, INewsListItemClickListener listener, int galleryStartIndex) {
        mTopStories = latestNews.top_stories;
        mStories = new ArrayList<>();
        mStories.add(createDateItem(latestNews.date));
        mStories.addAll(latestNews.stories);
        mFirstDate = mLastDate = latestNews.date;

        mCurrentGalleryIndex = galleryStartIndex;
        mHasGallery = mTopStories != null && mTopStories.size() != 0;
        mListener = listener;
    }

    private News createDateItem(String date) {
        News emptyStory = new News();
        emptyStory.date = date;
        return emptyStory;
    }

    public void addHistoryNews(HistoryNews historyNews) {
        mStories.add(createDateItem(historyNews.date));
        mStories.addAll(historyNews.stories);
        mLastDate = historyNews.date;
        notifyDataSetChanged();
    }

    public String getNextDateUnloaded() {
//        return mLastDate != null ? DateTools.getDateStr(mLastDate, 1) : null;
        return mLastDate;
    }

    @Override
    public int getItemViewType(int position) {
        if(mHasGallery && position == 0) {
            return TYPE_GALLERY;
        }
        else {
            News s = (News) getItem(position);
            return s.id == null ? TYPE_LIST_ITEM_DATE : TYPE_LIST_ITEM_NEWS;
        }
    }

    public Object getItem(int position) {
        if(mHasGallery) {
            return position == 0 ? mTopStories : mStories.get(position-1);
        }
        else {
            return mStories.get(position);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_GALLERY) {
            root = inflater.inflate(R.layout.view_top_stories, parent, false);
        }
        else if(viewType == TYPE_LIST_ITEM_NEWS){
            root = inflater.inflate(R.layout.view_latest_news_list_item, parent, false);
        }
        else if(viewType == TYPE_LIST_ITEM_DATE) {
            root = inflater.inflate(R.layout.view_latest_news_header, parent, false);
        }
        return new ViewHolder(root, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPosition(position);
        int itemType = holder.mViewType;
        if(itemType == TYPE_GALLERY) {
            if(holder.mTopStoriesGallery.getAdapter() == null) {
                TopNewAdapter tna = new TopNewAdapter(mTopStories);
                tna.setNewListClickListener(mListener);
                holder.mTopStoriesGallery.setAdapter(tna);
                holder.mPageIndicator.setViewPager(holder.mTopStoriesGallery);
            }
            holder.mTopStoriesGallery.addOnPageChangeListener(this);
            holder.mTopStoriesGallery.setCurrentItem(mCurrentGalleryIndex);
        }
        else if(itemType == TYPE_LIST_ITEM_NEWS){
            News story = (News) getItem(position);
            holder.mNewsTitle.setText(story.title);
            Glide.with(holder.mNewsThumb.getContext())
                    .load(story.images.get(0))
                    .into(holder.mNewsThumb);
        }
        else if(itemType == TYPE_LIST_ITEM_DATE) {
            if(position == 0 || position == 1) {
                holder.mNewsGroupHeaderText.setText(
                        holder.mRoot.getContext().getResources().getString(R.string.today_news_header));
            }
            else {
                News emptyStory = (News) getItem(position);
                holder.mNewsGroupHeaderText.setText(DateTools.getReadableDateStr(emptyStory.date));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mStories != null ? (mStories.size() + (mHasGallery ? 1 : 0)) : 0;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentGalleryIndex = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // for top gallery
        ViewPager mTopStoriesGallery;

        // for news list_item_news
        CirclePageIndicator mPageIndicator;
        TextView mNewsTitle;
        ImageView mNewsThumb;

        // for news list_item_date
        TextView mNewsGroupHeaderText;

        int mViewType;
        int mPosition;
        View mRoot;

        public ViewHolder(View root, int viewType) {
            super(root);
            mViewType = viewType;
            mRoot = root;
            if(viewType == TYPE_LIST_ITEM_NEWS) {
                mNewsTitle = (TextView) root.findViewById(R.id.news_title);
                mNewsThumb = (ImageView) root.findViewById(R.id.news_thumb);
                root.setOnClickListener(this);
            }
            else if(viewType == TYPE_GALLERY){
                mTopStoriesGallery = (ViewPager) root.findViewById(R.id.top_stories_gallery);
                mPageIndicator = (CirclePageIndicator) root.findViewById(R.id.top_stories_indicator);
            }
            else if(viewType == TYPE_LIST_ITEM_DATE) {
                mNewsGroupHeaderText = (TextView) root.findViewById(R.id.item_header);
            }
        }

        public void setPosition(int position) {
            mPosition = position;
        }

        @Override
        public void onClick(View view) {
            String id = null;
            Object o = getItem(mPosition);
            id = ((News) o).id;
            if(mListener != null) {
                mListener.onRequestNews(id);
            }
        }
    }



    private static void D(String msg) {
        Log.d("NewListAdapter", msg);
    }
}

package com.example.huangxueqin.zhihudaily.ui.adapters;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.huangxueqin.zhihudaily.interfaces.INewsListItemClickListener;
import com.example.huangxueqin.zhihudaily.models.LatestNews;

import java.util.List;

/**
 * Created by huangxueqin on 16-7-25.
 */
public class TopNewAdapter extends PagerAdapter implements View.OnClickListener {
    List<LatestNews.TopStory> mTopStories;
    INewsListItemClickListener mListener;

    public TopNewAdapter(List<LatestNews.TopStory> topStories) {
        mTopStories = topStories;
    }

    @Override
    public int getCount() {
        return mTopStories != null ? mTopStories.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String id = mTopStories.get(position).id;
        String url = mTopStories.get(position).image;
        ImageView image = new ImageView(container.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        container.addView(image, lp);
        Glide.with(container.getContext())
                .load(url)
                .into(image);
        image.setOnClickListener(this);
        image.setTag(id);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    public void setNewListClickListener(INewsListItemClickListener l) {
        mListener = l;
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            mListener.onRequestNews((String) view.getTag());
        }
    }
}

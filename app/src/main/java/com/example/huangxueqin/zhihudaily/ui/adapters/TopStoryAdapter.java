package com.example.huangxueqin.zhihudaily.ui.adapters;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.huangxueqin.zhihudaily.interfaces.IStoryListItemClickListener;
import com.example.huangxueqin.zhihudaily.models.story.SimpleStory;

/**
 * Created by huangxueqin on 16-7-25.
 */
public class TopStoryAdapter extends PagerAdapter implements View.OnClickListener {
    SimpleStory[] mTopStories;
    IStoryListItemClickListener mListener;

    public TopStoryAdapter(IStoryListItemClickListener listener, SimpleStory... topStories) {
        mListener = listener;
        mTopStories = topStories;
    }

    @Override
    public int getCount() {
        return mTopStories != null ? mTopStories.length : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        long id = mTopStories[position].id;
        String url = mTopStories[position].image;
        ImageView image = new ImageView(container.getContext());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setColorFilter(Color.rgb(180, 180, 180), android.graphics.PorterDuff.Mode.MULTIPLY);
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

    public void setNewListClickListener(IStoryListItemClickListener l) {
        mListener = l;
    }

    @Override
    public void onClick(View view) {
        if(mListener != null) {
            mListener.onRequestNews((long) view.getTag());
        }
    }
}

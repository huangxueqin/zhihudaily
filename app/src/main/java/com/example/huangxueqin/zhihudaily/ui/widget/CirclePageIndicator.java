package com.example.huangxueqin.zhihudaily.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.example.huangxueqin.zhihudaily.R;

/**
 * Created by huangxueqin on 16-7-25.
 */
public class CirclePageIndicator extends View {
    private static final int SELECTED_COLOR = 0xFFF0F0F0;
    private static final int UNSELECTED_COLOR = 0x80C0C0C0;
    private static final int DOT_SIZE_DP = 7;

    private int mSelectedColor;
    private int mUnSelectedColor;
    private int mDotSize;

    private ViewPager mViewPager;
    private int mNumPages;
    private int mCurrentPosition;
    private int mCurrentOffset;

    private Paint mSelectedPaint;
    private Paint mUnSelectedPaint;

    public CirclePageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        float density = getResources().getDisplayMetrics().density;
        int defaultDotSize = (int) (DOT_SIZE_DP * density);
        if(attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CirclePageIndicator, defStyle, 0);
            mDotSize = ta.getDimensionPixelSize(R.styleable.CirclePageIndicator_dot_size, defaultDotSize);
            mSelectedColor = ta.getColor(R.styleable.CirclePageIndicator_dot_color_selected, SELECTED_COLOR);
            mUnSelectedColor = ta.getColor(R.styleable.CirclePageIndicator_dot_color_unselected, UNSELECTED_COLOR);
            ta.recycle();
        } else {
            mDotSize = defaultDotSize;
            mSelectedColor = SELECTED_COLOR;
            mUnSelectedColor = UNSELECTED_COLOR;
        }
        mSelectedPaint = new Paint();
        mSelectedPaint.setColor(mSelectedColor);
        mSelectedPaint.setAntiAlias(true);
        mSelectedPaint.setStyle(Paint.Style.FILL);
        mUnSelectedPaint = new Paint();
        mUnSelectedPaint.setColor(mUnSelectedColor);
        mUnSelectedPaint.setAntiAlias(true);
        mUnSelectedPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if(mNumPages <= 0) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int paddingHorizontal = getPaddingLeft() + getPaddingRight();
        int paddingVertical = getPaddingTop() + getPaddingBottom();
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode == MeasureSpec.EXACTLY) {
            mDotSize = Math.min(mDotSize, (width-paddingHorizontal) / (2*mNumPages-1));
        } else if(widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(width, mDotSize * (2*mNumPages-1) + paddingHorizontal);
            mDotSize = (width-paddingHorizontal) / (2*mNumPages-1);
        } else if(widthMode == MeasureSpec.UNSPECIFIED) {
            width = mDotSize * (2*mNumPages-1) + paddingHorizontal;
        }

        if(heightMode == MeasureSpec.EXACTLY) {
            mDotSize = Math.min(mDotSize, height - paddingVertical);
        } else if(heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(height, mDotSize + paddingVertical);
            mDotSize = height - paddingVertical;
        } else if(heightMode == MeasureSpec.UNSPECIFIED) {
            height = mDotSize + paddingVertical;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if(mNumPages > 0) {
            int paddingVertical = getPaddingTop() + getPaddingBottom();
            int paddingHorizontal = getPaddingLeft() + getPaddingRight();
            mDotSize = Math.min((w - paddingHorizontal) / (mNumPages * 2 - 1), h-paddingVertical);
        }
    }



    public void setViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(mViewPagerScrollListener);
        mViewPager.addOnAdapterChangeListener(mViewPagerAdapterChangeListener);
        if(mViewPager != null && mViewPager.getAdapter() != null) {
            mNumPages = mViewPager.getAdapter().getCount();
        } else {
            mNumPages = 0;
        }
        mCurrentPosition = 0;
        mCurrentOffset = 0;
        if(mNumPages > 0) {
            requestLayout();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mNumPages > 0) {
            int w = getWidth();
            int h = getHeight();
            int x = w/2 - (mNumPages-1) * mDotSize;
            int y = h / 2;

            for(int i = 0; i < mNumPages; i++) {
                canvas.drawCircle(x, y, mDotSize/2, mUnSelectedPaint);
                x += 2 * mDotSize;
            }
            x -= 2 * mDotSize;
            int selectX = x - (mNumPages-1-mCurrentPosition) * 2 * mDotSize;
            int selectY = y;
            canvas.drawCircle(selectX+mCurrentOffset, selectY, mDotSize/2, mSelectedPaint);
        }
    }

    private ViewPager.OnPageChangeListener mViewPagerScrollListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            D("position = " + position + ", positionOffset = " + positionOffset + ", positionOffsetPixels = " + positionOffsetPixels);
            mCurrentPosition = position;
            mCurrentOffset = (int) (2 * mDotSize * positionOffset);
            invalidate();
        }

        @Override
        public void onPageSelected(int position) {
            mCurrentOffset = 0;
            mCurrentPosition = position;
            invalidate();
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private ViewPager.OnAdapterChangeListener mViewPagerAdapterChangeListener = new ViewPager.OnAdapterChangeListener() {
        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager, @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            setViewPager(viewPager);
        }
    };

    private static final void D(String msg) {
        Log.d("CirclePageIndicator", msg);
    }
}

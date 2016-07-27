package com.example.huangxueqin.zhihudaily.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.example.huangxueqin.zhihudaily.R;

/**
 * Created by huangxueqin on 16-7-27.
 */
public class PullRefreshLayout extends ViewGroup {
    private static final int BG_LIGHT = 0xFFFAFAFA;
    private static final int BG_DARK = 0xFF888888;
    private static final float DRAG_RATE = .35f;

    private View mTarget;
    private View mHeader;

    private int mCurrentTargetOffsetTop = -1;
    private int mHeaderIndex = -1;

    private int mTouchSlop;
    private boolean mIsBeingDragged = false;
    private int mActivePointId;
    private int mInitDownY;
    private int mLastDownY;

    private int mRefreshThreshold;
    private OnRefreshListener mOnRefreshListener;
    private boolean mIsRefreshing;
    private boolean mIsReturnToStart;
    private boolean mNotify;
    private boolean mStopAnimation;

    public static interface OnRefreshListener {
        void onRefresh();
    }

    public PullRefreshLayout(Context context) {
        this(context, null, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        int headerViewId = -1;
        if(attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshLayout);
            headerViewId = ta.getResourceId(R.styleable.PullRefreshLayout_header_view, -1);
        }
        createHeaderView(headerViewId);
        setChildrenDrawingOrderEnabled(true);
    }

    private void createHeaderView(int resId) {
        if(resId == -1) {
            float density = getResources().getDisplayMetrics().density;
            TextView tv = new TextView(getContext());
            tv.setBackgroundColor(Color.TRANSPARENT);
            tv.setText("下拉刷新");
            tv.setPadding((int) (10 * density), (int) (20 * density), (int) (10 * density), (int) (20 * density));
            mHeader = tv;
        } else {
            mHeader = LayoutInflater.from(getContext()).inflate(resId, this, false);
        }
        mHeader.setVisibility(GONE);
        addView(mHeader);
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void setRefreshing(boolean refreshing) {
        setRefreshing(refreshing, refreshing ? true : false);
    }

    public void setRefreshing(boolean refreshing, boolean notify) {
        if(mIsRefreshing == refreshing) {
            return;
        }
        mIsRefreshing = refreshing;
        mNotify = notify;
        mStopAnimation = false;
        int startValue = mCurrentTargetOffsetTop;
        int endValue = 0;
        if(mIsRefreshing) {
            endValue = 0;
        } else {
            endValue = mRefreshThreshold;
        }
        ValueAnimator scrollToStartAnimator = ValueAnimator.ofInt(startValue, endValue);
        if(mIsRefreshing) {
            scrollToStartAnimator.addListener(scrollerAnimatorListener);
        }
        scrollToStartAnimator.setDuration(500);
        scrollToStartAnimator.addUpdateListener(mScrollAnimationUpdateListener);
        scrollToStartAnimator.start();
    }

    private ValueAnimator.AnimatorUpdateListener mScrollAnimationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if(!mStopAnimation) {
                int value = (int) valueAnimator.getAnimatedValue();
                mCurrentTargetOffsetTop = value;
                requestLayout();
            } else {
                valueAnimator.cancel();
            }
        }
    };

    private ValueAnimator.AnimatorListener scrollerAnimatorListener = new ValueAnimator.AnimatorListener() {

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mIsRefreshing = false;
            if(mNotify) {
                if(mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private void ensureTarget() {
        if(mTarget == null) {
            for(int i = 0; i < getChildCount(); i++) {
                View v = getChildAt(i);
                if(v != mHeader) {
                    mTarget = v;
                    break;
                }
            }
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        if(mHeaderIndex < 0) {
            return i;
        }
        if(i == childCount - 1) {
            return mHeaderIndex;
        } else if(i >= mHeaderIndex) {
            return i+1;
        } else {
            return i;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(mTarget == null) {
            ensureTarget();
        }
        if(mTarget == null) {
            return;
        }
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        mHeader.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

        mTarget.measure(MeasureSpec.makeMeasureSpec(width - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height-getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));

        for(int i = 0; i < getChildCount(); i++) {
            if(getChildAt(i) == mHeader) {
                mHeaderIndex = i;
                break;
            }
        }

        if(mCurrentTargetOffsetTop == -1) {
            mCurrentTargetOffsetTop = 0;
        }
        mRefreshThreshold = mHeader.getMeasuredHeight();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if(getChildCount() == 0) {
            return;
        }
        if(mTarget == null) {
            ensureTarget();
        }
        if(mTarget == null) {
            return;
        }

        int offset = Math.max(0, mCurrentTargetOffsetTop);
        int targetLeft = getPaddingTop();
        int targetTop = offset + getPaddingTop();
        int targetRight = targetLeft + mTarget.getMeasuredWidth();
        int targetBottom = targetTop + mTarget.getMeasuredHeight();
        mTarget.layout(targetLeft, targetTop, targetRight, targetBottom);
        mHeader.layout(width/2-mHeader.getMeasuredWidth()/2, -mHeader.getMeasuredHeight(),
                width/2+mHeader.getMeasuredWidth()/2, 0);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if(mTarget == null || mIsReturnToStart || canChildScrollUp()) {
            return false;
        }

        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointId = ev.getPointerId(0);
                mInitDownY = (int) ev.getY();
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                int currentY = (int) ev.getY();
                if(currentY - mInitDownY > mTouchSlop) {
                    mLastDownY = mInitDownY + mTouchSlop;
                    mIsBeingDragged = true;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointUp(ev);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointId = -1;
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mTarget == null|| mIsReturnToStart || canChildScrollUp()) {
            return false;
        }
        int action = event.getActionMasked();
        int currentY = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointId = event.getPointerId(event.getActionIndex());
                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if(mIsBeingDragged) {
                    int offset = (int) ((currentY - mLastDownY) * DRAG_RATE);
                    if(offset != 0) {
                        mCurrentTargetOffsetTop += offset;
                        if(mCurrentTargetOffsetTop >= 0) {
                            requestLayout();
                        }
                    }
                    mLastDownY = currentY;
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondaryPointUp(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointId = -1;
                releaseDrag();
                break;
        }
        return true;
    }

    public void onSecondaryPointUp(MotionEvent ev) {
        int pointIndex = ev.getActionIndex();
        int pointId = ev.getPointerId(pointIndex);
        if(pointId == mActivePointId) {
            int activeIndex = pointIndex == 0 ? 1 : 0;
            mActivePointId = ev.getPointerId(activeIndex);
        }

    }

    public boolean canChildScrollUp() {
        if(Build.VERSION.SDK_INT < 14) {
            if(mTarget instanceof AbsListView) {
                AbsListView list = (AbsListView) mTarget;
                return list.getChildCount() > 0 && list.getChildAt(0).getTop() < list.getPaddingTop();
            } else {
                // customize view should override this method
                return mTarget.canScrollVertically(-1);
            }
        } else {
            return mTarget.canScrollVertically(-1);
        }
    }

    private void releaseDrag() {
        if(mCurrentTargetOffsetTop >= mRefreshThreshold) {
            setRefreshing(true, true);
        }
        else {

        }
    }

    private static void D(String msg) {
        Log.d(PullRefreshLayout.class.getSimpleName(), msg);
    }
}

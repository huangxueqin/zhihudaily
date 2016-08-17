package com.example.huangxueqin.zhihudaily.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.TextView;

import com.example.huangxueqin.zhihudaily.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangxueqin on 16-7-27.
 */
public class PullRefreshLayout extends ViewGroup {
    private static final int BG_LIGHT = 0xFFFAFAFA;
    private static final int BG_DARK = 0xFF888888;

    private static final int ANIMATION_DURATION = 100;
    private static final int ANIMATION_FORCE_REFRESH_DURATION = 100;
    private static final int ANIMATION_FLOATING_TIME = 1000;

    private View mTarget;
    private View mHeader;

    private int mCurrentTargetOffsetTop = -1;
    private int mHeaderIndex = -1;

    private int mTouchSlop;
    private boolean mIsBeingDragged = false;
    private int mActivePointerId = -1;
    private int mLastDownY = -1;
    private int mSecondPointerId = -1;
    private SparseArray<Integer> mOtherPointerDownY = new SparseArray<>();
    private List<Integer> mOtherPointerIds = new ArrayList<>();

    private OnRefreshListener mOnRefreshListener;
    private int mRefreshOffsetTop;
    private boolean mIsRefreshing;
    private boolean mWillReleaseToRefresh;
    private boolean mForceRefresh;
    private boolean mForceReset;

    private ValueAnimator mDefaultForceRefreshAnimator;
    private ValueAnimator mDefaultForceResetAnimator;
    private ValueAnimator mDefaultCancellableResetAnimator;

    private Handler mMainHandler;

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
        setWillNotDraw(false);
        setChildrenDrawingOrderEnabled(true);
        mMainHandler = new Handler(Looper.getMainLooper());
        mWillReleaseToRefresh = true;
    }

    private void createHeaderView(int resId) {
        if(resId == -1) {
            float density = getResources().getDisplayMetrics().density;
            TextView tv = new TextView(getContext());
            tv.setBackgroundColor(Color.TRANSPARENT);
            tv.setText("下拉刷新");
            tv.setTextColor(BG_DARK);
            tv.setGravity(Gravity.CENTER);
            tv.setPadding((int) (10 * density), (int) (30 * density), (int) (10 * density), (int) (30 * density));
            mHeader = tv;
        } else {
            mHeader = LayoutInflater.from(getContext()).inflate(resId, this, false);
        }
        addView(mHeader);
    }

    private void setCurrentTargetOffsetTop(int offsetTop) {
        if(mCurrentTargetOffsetTop != offsetTop) {
            mCurrentTargetOffsetTop = offsetTop;
            requestLayout();
            if(mWillReleaseToRefresh) {
                setHeaderForCurrentOffsetTop();
            }
        }
    }

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    public boolean isRefreshing() {
        return mIsRefreshing;
    }

    public void setRefresh(boolean refreshing) {
        if(mIsRefreshing == refreshing) {
            return;
        }
        if(refreshing) {
            mForceRefresh = true;
            onForceRefresh(mCurrentTargetOffsetTop);
        }
        else {
            mIsRefreshing = false;
            if(mCurrentTargetOffsetTop > 0) {
                setHeaderViewForRefreshComplete();
            }
            else {
                mWillReleaseToRefresh = true;
                setHeaderForCurrentOffsetTop();
            }
        }
    }

    private void setHeaderViewForRefreshComplete() {
        TextView tv = (TextView) mHeader;
        tv.setText(R.string.refresh_complete);
    }

    private void setHeaderForCurrentOffsetTop() {
        TextView tv = (TextView) mHeader;
        if(mCurrentTargetOffsetTop >= mRefreshOffsetTop) {
            tv.setText(R.string.refresh_prompt_release);
        } else {
            tv.setText(R.string.refresh_prompt_pull_down);
        }
    }

    private void setHeaderForRefreshOnGoing() {
        TextView tv = (TextView) mHeader;
        tv.setText(R.string.refresh_ongoing);
    }

    private void resetHeaderView() {
        TextView tv = (TextView) mHeader;
        tv.setText(R.string.refresh_prompt_pull_down);
    }

    private void onRefresh() {
        mWillReleaseToRefresh = false;
        setHeaderForRefreshOnGoing();
        if(mOnRefreshListener != null) {
            mOnRefreshListener.onRefresh();
        }
    }

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

        if(mCurrentTargetOffsetTop == -1) {
            mCurrentTargetOffsetTop = 0;
        }

        mHeader.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST));

        mTarget.measure(MeasureSpec.makeMeasureSpec(width - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(height-getPaddingTop() - getPaddingBottom() - Math.min(0, mCurrentTargetOffsetTop), MeasureSpec.EXACTLY));

        for(int i = 0; i < getChildCount(); i++) {
            if(getChildAt(i) == mHeader) {
                mHeaderIndex = i;
                break;
            }
        }

        mRefreshOffsetTop = mHeader.getMeasuredHeight();
        initAnimators();
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        int width = getMeasuredWidth();
        if(getChildCount() == 0) {
            return;
        }
        if(mTarget == null) {
            ensureTarget();
        }
        if(mTarget == null) {
            return;
        }

        int offset = mCurrentTargetOffsetTop;
        int targetLeft = getPaddingTop();
        int targetTop = offset + getPaddingTop();
        int targetRight = targetLeft + mTarget.getMeasuredWidth();
        int targetBottom = targetTop + mTarget.getMeasuredHeight();
        mTarget.layout(targetLeft, targetTop, targetRight, targetBottom);
        mHeader.layout(width/2-mHeader.getMeasuredWidth()/2, offset-mHeader.getMeasuredHeight(),
                width/2+mHeader.getMeasuredWidth()/2, offset);

    }

    private float computeDragRate(float offset) {
        return (1 - Math.abs(offset)/getHeight()) * (1 - Math.abs(offset)/getHeight()) * (1 - Math.abs(offset)/getHeight()) ;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();
        if(mTarget == null || mForceRefresh || mForceReset || canChildScrollUp()) {
            return false;
        }

        final int action = ev.getActionMasked();
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
//        D("onInterceptTouchEvent: ", ev);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = pointerId;
                mLastDownY = (int) ev.getY();
                mIsBeingDragged = mCurrentTargetOffsetTop != 0;
                mSecondPointerId = -1;
                mOtherPointerDownY.clear();
                mOtherPointerIds.clear();
                mMainHandler.removeCallbacks(mForceResetRunnable);
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointerId == mActivePointerId) {
                    int currentY = (int) ev.getY();
                    if(currentY - mLastDownY >= mTouchSlop) {
                        mLastDownY = mLastDownY + mTouchSlop;
                        mIsBeingDragged = true;
                    }
                } else {
                    mOtherPointerDownY.put(pointerId, (int) ev.getY(pointerIndex));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onSecondPointerDown(ev, pointerIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondPointerUp(ev, pointerIndex);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = -1;
                mLastDownY = -1;
                mSecondPointerId = -1;
                mOtherPointerIds.clear();
                mOtherPointerDownY.clear();
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mTarget == null || mForceRefresh || mForceReset || canChildScrollUp()) {
            return false;
        }
        final int action = event.getActionMasked();
        final int pointerIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(pointerIndex);
//        D("onTouchEvent: ", event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointerId == mActivePointerId) {
                    final int currentY = (int) event.getY();
                    if (mIsBeingDragged) {
                        int offset = (int) ((currentY - mLastDownY) * computeDragRate(mCurrentTargetOffsetTop));
                        if (offset != 0) {
                            setCurrentTargetOffsetTop(mCurrentTargetOffsetTop + offset);
                        }
                        mLastDownY = currentY;
                    }
                } else {
                    mOtherPointerDownY.put(pointerId, (int)event.getY(pointerIndex));
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                onSecondPointerDown(event, pointerIndex);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                onSecondPointerUp(event, pointerIndex);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
                mActivePointerId = -1;
                mLastDownY = -1;
                mSecondPointerId = -1;
                mOtherPointerDownY.clear();
                mOtherPointerIds.clear();
                releaseDrag();
                break;
        }
        return true;
    }

    private void updateSecondPointerId(int secondPointId) {
        mSecondPointerId = -1;
        for(int i = 0; i < mOtherPointerIds.size(); i++) {
            if(mOtherPointerIds.get(i) != secondPointId) {
                mSecondPointerId = mOtherPointerIds.get(i);
                break;
            }
        }
    }

    private void onSecondPointerDown(MotionEvent event, int pointerIndex) {
        int pointerId = event.getPointerId(pointerIndex);
//        D("onSecondPointerDown: activeId = " + mActivePointerId + ", pointerId = " + pointerId + ", pointerIndex = " + pointerIndex);
        if(mSecondPointerId == -1) {
            mSecondPointerId = pointerId;
        }
        mOtherPointerIds.add(new Integer(pointerId));
        mOtherPointerDownY.put(pointerId, (int) event.getY(pointerIndex));
//        D("put (" + pointerId + ", " + mOtherPointerDownY.get(pointerId) + ")");
    }

    private void onSecondPointerUp(MotionEvent event, int pointerIndex) {
        int pointerId = event.getPointerId(pointerIndex);
//        D("onSecondPointerUp: activeId = " + mActivePointerId + ", pointerId = " + pointerId + ", pointerIndex = " + pointerIndex);
        int idToRemove = pointerId;
        if(pointerId == mActivePointerId || pointerId == mSecondPointerId) {
            idToRemove = mSecondPointerId;
            if(pointerId == mActivePointerId) {
                mActivePointerId = mSecondPointerId;
                mLastDownY = mOtherPointerDownY.get(mSecondPointerId);
//                D("get (" + mSecondPointerId + ", " + mOtherPointerDownY.get(mSecondPointerId) + ")");
            }
            updateSecondPointerId(mSecondPointerId);
        }
        mOtherPointerDownY.remove(idToRemove);
        mOtherPointerIds.remove(new Integer(idToRemove));
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
        if(mWillReleaseToRefresh && !mIsRefreshing && mCurrentTargetOffsetTop >= mRefreshOffsetTop) {
            mIsRefreshing = true;
            onRefresh();
            onReleaseToRefresh(mCurrentTargetOffsetTop);
        }
        else {
            onReleaseToReset(mCurrentTargetOffsetTop);
        }
    }

    private void onForceRefresh(int startPos) {
        if(startPos == 0) {
            mDefaultForceRefreshAnimator.start();
        }
        else {
            ValueAnimator animator = initTransitAnimation(startPos, mRefreshOffsetTop, ANIMATION_FORCE_REFRESH_DURATION);
            animator.addListener(mForceRefreshListener);
            animator.start();
        }
    }

    private void onReleaseToRefresh(int startPos) {
        ValueAnimator animator = initTransitAnimation(startPos, mRefreshOffsetTop, ANIMATION_FORCE_REFRESH_DURATION);
        animator.addUpdateListener(mCancellableUpdateListener);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(!mIsBeingDragged) {
                    long delay = mIsRefreshing ? ANIMATION_FLOATING_TIME : ANIMATION_FLOATING_TIME / 2;
                    mMainHandler.postDelayed(mForceResetRunnable, delay);
                }
            }
        });
        animator.start();
    }

    private void onReleaseToReset(int startPos) {
        ValueAnimator animator = null;
        if(startPos == mRefreshOffsetTop) {
            animator = mDefaultCancellableResetAnimator;
        }
        else {
            animator = initTransitAnimation(startPos, 0, ANIMATION_DURATION);
            animator.addUpdateListener(mCancellableUpdateListener);
            animator.addListener(mCancellableResetListener);
        }
        animator.start();
    }

    private ValueAnimator initTransitAnimation(int start, int end, int duration) {
        ObjectAnimator transitAnimator = ObjectAnimator.ofInt(this, "CurrentTargetOffsetTop", start, end);
        transitAnimator.setInterpolator(new DecelerateInterpolator());
        transitAnimator.setDuration(duration);
        return transitAnimator;
    }

    private AnimatorListenerAdapter mForceRefreshListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mIsRefreshing = true;
            mForceRefresh = false;
            onRefresh();
            mMainHandler.postDelayed(mForceResetRunnable, ANIMATION_FLOATING_TIME);
        }
    };

    private AnimatorListenerAdapter mForceResetListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            mForceReset = false;
            if(!mIsRefreshing) {
                mWillReleaseToRefresh = true;
                setHeaderForCurrentOffsetTop();
            }
        }
    };

    private AnimatorListenerAdapter mCancellableResetListener = new AnimatorListenerAdapter() {
        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            if(!mIsRefreshing) {
                mWillReleaseToRefresh = true;
            }
            setHeaderForCurrentOffsetTop();
        }
    };

    private ValueAnimator.AnimatorUpdateListener mCancellableUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if(mIsBeingDragged) {
                valueAnimator.cancel();
            }
        }
    };

    private Runnable mForceResetRunnable = new Runnable() {
        @Override
        public void run() {
            D("mForceResetRunnable running");
            mForceReset = true;
            ValueAnimator animator = null;
            if(mCurrentTargetOffsetTop == mRefreshOffsetTop) {
                D("use default animator");
                animator = mDefaultForceResetAnimator;
            }
            else {
                animator = initTransitAnimation(mCurrentTargetOffsetTop, 0, ANIMATION_FORCE_REFRESH_DURATION);
                animator.addListener(mForceResetListener);
            }
            animator.start();
        }
    };

    private void initAnimators() {
        // init mDefaultForceRefreshAnimator
        mDefaultForceRefreshAnimator = initTransitAnimation(0, mRefreshOffsetTop, ANIMATION_FORCE_REFRESH_DURATION);
        mDefaultForceRefreshAnimator.addListener(mForceRefreshListener);

        // init mDefaultForceResetAnimator
        mDefaultForceResetAnimator = initTransitAnimation(mRefreshOffsetTop, 0, ANIMATION_DURATION);
        mDefaultForceResetAnimator.addListener(mForceResetListener);

        // init mDefaultCancellableResetAnimator
        mDefaultCancellableResetAnimator = initTransitAnimation(mRefreshOffsetTop, 0, ANIMATION_DURATION);
        mDefaultCancellableResetAnimator.addUpdateListener(mCancellableUpdateListener);
        mDefaultCancellableResetAnimator.addListener(mCancellableResetListener);
    }

    private static void D(String msg) {
        Log.d(PullRefreshLayout.class.getSimpleName(), msg);
    }

    private static void D(String str, MotionEvent ev) {
        String msg = "";
        switch(ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                msg = "action_down";
                break;
            case MotionEvent.ACTION_MOVE:
                msg = "action_move";
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                msg = "action_pointer_down";
                break;
            case MotionEvent.ACTION_POINTER_UP:
                msg = "action_pointer_up";
                break;
            case MotionEvent.ACTION_UP:
                msg = "action_up";
                break;
            case MotionEvent.ACTION_CANCEL:
                msg = "action_cancel";
                break;
        }
        D(str + msg);
    }
}

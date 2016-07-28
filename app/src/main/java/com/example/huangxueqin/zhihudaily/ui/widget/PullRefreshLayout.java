package com.example.huangxueqin.zhihudaily.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
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

    private static final int ANIMATION_TO_START_DURATION = 200;
    private static final int ANIMATION_TO_TRIGGER_DURATION  = 200;

    private View mTarget;
    private View mHeader;

    private int mCurrentTargetOffsetTop = -1;
    private int mHeaderIndex = -1;

    private int mTouchSlop;
    private boolean mIsBeingDragged = false;
    private int mActivePointId = -1;
    private int mLastDownY = -1;
    private int mSecondPointId = -1;
    private SparseArray<Integer> mOtherPointerDownY = new SparseArray<>();
    private List<Integer> mOtherPointerIds = new ArrayList<>();

    private int mRefreshThreshold;
    private OnRefreshListener mOnRefreshListener;
    private boolean mIsRefreshing;
    private boolean mNotify;
    private boolean mStopAnimationOnTouchDown;

    ValueAnimator mTransitAnimator;

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

    public void setCurrentTargetOffsetTop(int offsetTop) {
        if(mCurrentTargetOffsetTop != offsetTop) {
            mCurrentTargetOffsetTop = offsetTop;
            requestLayout();
        }
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
        if(mIsRefreshing) {
            mNotify = notify;
            animateToPostion(mCurrentTargetOffsetTop, 0, ANIMATION_TO_START_DURATION, mNotify);
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

        mRefreshThreshold = mHeader.getMeasuredHeight();
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

//        int offset = Math.max(0, mCurrentTargetOffsetTop);
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
        if(mTarget == null || canChildScrollUp()) {
            return false;
        }

        final int action = ev.getActionMasked();
        final int pointerIndex = ev.getActionIndex();
        final int pointerId = ev.getPointerId(pointerIndex);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointId = ev.getPointerId(0);
                mLastDownY = (int) ev.getY();
                mIsBeingDragged = mCurrentTargetOffsetTop != 0;
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointerId == mActivePointId) {
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
                mActivePointId = -1;
                mSecondPointId = -1;
                mOtherPointerIds.clear();
                mOtherPointerDownY.clear();
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mTarget == null || canChildScrollUp()) {
            return false;
        }
        final int action = event.getActionMasked();
        final int pointerIndex = event.getActionIndex();
        final int pointerId = event.getPointerId(pointerIndex);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mStopAnimationOnTouchDown = true;
                mActivePointId = event.getPointerId(event.getActionIndex());
                break;
            case MotionEvent.ACTION_MOVE:
                if(pointerId == mActivePointId) {
                    final int currentY = (int) event.getY();
                    if (mIsBeingDragged) {
                        int offset = (int) ((currentY - mLastDownY) * computeDragRate(mCurrentTargetOffsetTop));
                        if (offset != 0) {
                            int totalOffset = mCurrentTargetOffsetTop + offset;
                            if (mCurrentTargetOffsetTop != totalOffset) {
                                mCurrentTargetOffsetTop = totalOffset;
                                if (mCurrentTargetOffsetTop >= mRefreshThreshold) {
                                    ((TextView) mHeader).setText("释放立即刷新");
                                } else {
                                    ((TextView) mHeader).setText("下拉刷新");
                                }
                                requestLayout();
                            }
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
                mActivePointId = -1;
                mSecondPointId = -1;
                mOtherPointerDownY.clear();
                mOtherPointerIds.clear();
                releaseDrag();
                break;
        }
        return true;
    }

    private void updateSecondPointerId(int secondPointId) {
        mSecondPointId = -1;
        for(int i = 0; i < mOtherPointerIds.size(); i++) {
            if(mOtherPointerIds.get(i) != secondPointId) {
                mSecondPointId = mOtherPointerIds.get(i);
                break;
            }
        }
    }

    private void onSecondPointerDown(MotionEvent event, int pointerIndex) {
        int pointerId = event.getPointerId(pointerIndex);
        if(mSecondPointId == -1) {
            mSecondPointId = pointerId;
        }
        mOtherPointerIds.add(new Integer(pointerId));
        mOtherPointerDownY.put(pointerId, (int) event.getY(pointerIndex));
        D("put (" + pointerId + ", " + mOtherPointerDownY.get(pointerId) + ")");
    }

    private void onSecondPointerUp(MotionEvent event, int pointerIndex) {
        int pointerId = event.getPointerId(pointerIndex);
        int idToRemove = pointerId;
        if(pointerId == mActivePointId || pointerId == mSecondPointId) {
            idToRemove = mSecondPointId;
            if(pointerId == mActivePointId) {
                mActivePointId =  mSecondPointId;
                mLastDownY = mOtherPointerDownY.get(mSecondPointId);
                D("get (" + mSecondPointId + ", " + mOtherPointerDownY.get(mSecondPointId) + ")");
            }
            updateSecondPointerId(mSecondPointId);
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
        if(mCurrentTargetOffsetTop >= mRefreshThreshold) {
            setRefreshing(true, true);
        }
        else {
            animateToPostion(mCurrentTargetOffsetTop, 0, ANIMATION_TO_START_DURATION, false);
        }
    }

    private void initTransitAnimation(int start, int end, int duration) {
        mTransitAnimator = ObjectAnimator.ofInt(this, "CurrentTargetOffsetTop", start, end);
        mTransitAnimator.setInterpolator(new DecelerateInterpolator());
        mTransitAnimator.setDuration(duration);
        mTransitAnimator.addUpdateListener(mTransitAnimatorUpdateListener);
    }

    private ValueAnimator.AnimatorListener mTransitAnimatorListener = new ValueAnimator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if(mNotify) {
                if(mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
                mNotify = false;
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {
        }
    };

    private ValueAnimator.AnimatorUpdateListener mTransitAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            if(mStopAnimationOnTouchDown) {
                valueAnimator.cancel();
                mStopAnimationOnTouchDown = false;
            }
        }
    };

    private void animateToPostion(int start, int end, int duration, boolean notify) {
        initTransitAnimation(start, end, duration);
        mTransitAnimator.addUpdateListener(mTransitAnimatorUpdateListener);
        mTransitAnimator.addListener(mTransitAnimatorListener);
        mTransitAnimator.start();
    }

    private static void D(String msg) {
        Log.d(PullRefreshLayout.class.getSimpleName(), msg);
    }
}

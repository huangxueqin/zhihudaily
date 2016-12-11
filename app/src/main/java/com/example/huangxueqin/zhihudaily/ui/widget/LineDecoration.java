package com.example.huangxueqin.zhihudaily.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.ui.adapters.StoryListAdapter;

/**
 * Created by huangxueqin on 16-7-25.
 */
public class LineDecoration extends RecyclerView.ItemDecoration {
    private static final int LINE_PADDING_DP = 10;

    Paint mPaint;
    int mLinePadding;

    public LineDecoration(Context context) {
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.item_line_sep));
        mLinePadding = (int) context.getResources().getDisplayMetrics().density * LINE_PADDING_DP;
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int count = parent.getLayoutManager().getChildCount();
        StoryListAdapter adapter = (StoryListAdapter) parent.getAdapter();
        if(adapter != null) {
            for (int i = 0; i < count - 1; i++) {
                View child = parent.getChildAt(i);
                int alpha = (int) (child.getAlpha() * 255);
                mPaint.setAlpha(alpha);
                int position = parent.getLayoutManager().getPosition(child);
                if (adapter.getItemViewType(position) == StoryListAdapter.TYPE_LIST_STORY) {
                    c.drawLine(mLinePadding, child.getBottom(), parent.getWidth() - mLinePadding, child.getBottom(), mPaint);
                }
            }
        }
    }
}

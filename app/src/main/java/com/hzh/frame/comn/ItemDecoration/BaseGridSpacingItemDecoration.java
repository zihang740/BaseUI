package com.hzh.frame.comn.ItemDecoration;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.hzh.frame.util.Util;

/**
 * 自适应任意列的 GridLayoutManager 的分割块 ItemDecoration
 */
public class BaseGridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount;
    private int dividerWidth;
    private int dividerWidthTop;
    private int dividerWidthBot;

    private Paint dividerPaint;

    /**
     * @param spanCount gridLayoutManager 列数
     * @param dividerWidthDp 分割块宽高,单位:dp
     */
    public BaseGridSpacingItemDecoration(Context context, int spanCount, int dividerWidthDp) {
        this.spanCount = spanCount;

        this.dividerPaint = new Paint();
        this.dividerPaint.setColor(Color.BLUE);

        this.dividerWidth = Util.dip2px(context, dividerWidthDp);
        this.dividerWidthTop = dividerWidth / 2;
        this.dividerWidthBot = dividerWidth - dividerWidthTop;
    }

    @Override
    public void getItemOffsets(Rect outRect, View child, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, child, parent, state);

        int pos = parent.getChildAdapterPosition(child);
        int column = (pos) % spanCount;// 计算这个child 处于第几列


        outRect.top = dividerWidthTop;
        outRect.bottom = dividerWidthBot;

        outRect.left = (column * dividerWidth / spanCount);
        outRect.right = dividerWidth - (column + 1) * dividerWidth / spanCount;
    }

}

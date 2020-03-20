package com.hzh.frame.comn.ItemDecoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hzh.frame.R;
import com.hzh.frame.widget.xrecyclerview.BaseRecyclerAdapter;

/**
 * @author hzh
 * @version 1.0
 * @date 2017/8/21 
 */
public class BaseItemDecoration extends RecyclerView.ItemDecoration {

    private Context mContext;
    private Paint mPaint;
    private int lineHeight;

    public BaseItemDecoration(Context context) {
        this(context, R.color.base_bg);
    }
    
    public BaseItemDecoration(Context context,int lineColor) {
        this(context,lineColor,2);
    }

    public BaseItemDecoration(Context context,int lineColor,int lineHeight) {
        mContext=context;
        mPaint=new Paint();
        mPaint.setColor(ContextCompat.getColor(mContext, lineColor));
        this.lineHeight=lineHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int itemPosition = parent.getChildLayoutPosition(view);
        switch (parent.getAdapter().getItemViewType(itemPosition)){
            case BaseRecyclerAdapter.TYPE_HEADER:
                outRect.bottom= lineHeight;
                break;
            case BaseRecyclerAdapter.TYPE_FOOTER:
                break;
            case BaseRecyclerAdapter.TYPE_NORMAL:
                if(itemPosition!=parent.getAdapter().getItemCount()-1){
                    outRect.bottom= lineHeight;
                }
                break;
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        int left = parent.getPaddingLeft();
        int right = parent.getWidth() - parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = parent.getChildAt(i);
            int itemPosition = parent.getChildLayoutPosition(view);
            switch (parent.getAdapter().getItemViewType(itemPosition)){
                case BaseRecyclerAdapter.TYPE_HEADER:
                    c.drawRect(left, view.getBottom(), right, view.getBottom()+lineHeight, mPaint);//绘制矩形
                    break;
                case BaseRecyclerAdapter.TYPE_FOOTER:
                    break;
                case BaseRecyclerAdapter.TYPE_NORMAL:
                    if(itemPosition!=parent.getAdapter().getItemCount()-1){
                        c.drawRect(left, view.getBottom(), right, view.getBottom()+lineHeight, mPaint);//绘制矩形
                    }
                    break;
            }
        }
    }
}

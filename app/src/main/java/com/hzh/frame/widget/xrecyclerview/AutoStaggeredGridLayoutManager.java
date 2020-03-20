package com.hzh.frame.widget.xrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * 自适应内容的高度
 * */
public class AutoStaggeredGridLayoutManager extends StaggeredGridLayoutManager{

	public AutoStaggeredGridLayoutManager(int spanCount, int orientation) {
		super(spanCount, orientation);
	}
	@Override  
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {  
        View view = recycler.getViewForPosition(0);  
        if(view != null){  
            measureChild(view, widthSpec, heightSpec);  
            int measuredWidth = MeasureSpec.getSize(widthSpec);  
            int measuredHeight = view.getMeasuredHeight();  
            setMeasuredDimension(measuredWidth, measuredHeight);  
        }  
    }  
}

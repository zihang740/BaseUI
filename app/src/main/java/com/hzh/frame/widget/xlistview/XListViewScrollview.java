package com.hzh.frame.widget.xlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 兼容Scrollview的ListView
 * */
public class XListViewScrollview extends ListView{

	public XListViewScrollview(Context context) {
		super(context);
	}
	
	public XListViewScrollview(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
	
	public XListViewScrollview(Context context, AttributeSet attrs,int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	@Override
    /**
     * 重写该方法，达到使ListView适应ScrollView的效果
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}

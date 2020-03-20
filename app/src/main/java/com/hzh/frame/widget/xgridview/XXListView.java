package com.hzh.frame.widget.xgridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 解决的问题：ListView显示不全，只显示了一行的数据，比较奇怪，尝试重写ListView来解决
 * 
 * @author hzh
 * @since 2016-07-06 16:41
 * 
 */
public class XXListView extends ListView {

  public XXListView(Context context) {
    super(context);
  }

  public XXListView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public XXListView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }

}

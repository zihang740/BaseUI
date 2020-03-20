package com.hzh.frame.widget.xgridview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 解决的问题：GridView显示不全，只显示了一行的图片，比较奇怪，尝试重写GridView来解决
 * 
 * @author hzh
 * @since 2016-07-06 16:41
 * 
 */
public class XGridView extends GridView {

  public XGridView(Context context) {
    super(context);
  }

  public XGridView(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public XGridView(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
    super.onMeasure(widthMeasureSpec, expandSpec);
  }

}

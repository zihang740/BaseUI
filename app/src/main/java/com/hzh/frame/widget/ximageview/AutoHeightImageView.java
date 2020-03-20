package com.hzh.frame.widget.ximageview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 根据图片宽度和图片比例自适应图片高度ImageView
 * @author hzh
 * @version 1.0
 * @date 2017/8/25
 */

public class AutoHeightImageView extends android.support.v7.widget.AppCompatImageView {

    public AutoHeightImageView(Context context) {
        super(context);
    }

    public AutoHeightImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AutoHeightImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Drawable drawable = getDrawable();
        if (drawable != null) {
            int width = View.MeasureSpec.getSize(widthMeasureSpec);
            int height = (int) Math.ceil((float) width * (float) drawable.getIntrinsicHeight() / (float) drawable.getIntrinsicWidth());
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}

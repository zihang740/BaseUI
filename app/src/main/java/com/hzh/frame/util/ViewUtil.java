package com.hzh.frame.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;

/**
 * @author
 * @version 1.0
 * @date 2018/10/14
 */
public class ViewUtil {
    
    //获取View的Bitmap图片
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }
}

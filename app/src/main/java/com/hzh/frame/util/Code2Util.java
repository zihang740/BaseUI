package com.hzh.frame.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 二维码生成类
 * */
public class Code2Util {

	/**
	 * 生成二维码
	 * @param content 二维码内容
	 */
	public static Bitmap create(String content,int with,int height) {
        return createQRcode(content,with,height);
	}

    /**
     * 使用Bitmap代替二维码
     * @paraogo Logo
     * @return Bitmap 最终合成的图片
     */
    public static Bitmap createMyBitmap(String content,Bitmap logo,int with,int height) {
        return createMyBitmap(createQRcode(content,with,height),logo);
    }
	
    /**
     * 生成二维码(带Logo版本)
     * @param content 二维码内容
     * @param logo 二维码logo
     */
    public static Bitmap create(String content,Bitmap logo,int with,int height) {
        return createQRcodeAndLogo(createQRcode(content,with,height),logo);
    }

    /**
     * 生成二维码(带边框版本)
     * @param content 二维码内容
     * @param border 二维码边框
     */
    public static Bitmap createBorder(String content,Bitmap border,int with,int height) {
        return createQRcodeAndBorder(createQRcode(content,with,height),border);
    }

    /**
     * 生成二维码图片
     * @param content 二维码链接
     */
    public static Bitmap createQRcode(String content,int with,int height) {
        try {
            // 生成二维码
            BitMatrix matrix = new MultiFormatWriter().encode(content,BarcodeFormat.QR_CODE, with, height);
            matrix = deleteWhite(matrix);//删除白边
            int mwidth = matrix.getWidth();
            int mheight = matrix.getHeight();
            // 二维矩阵转为一维像素数组,也就是一直横着排了
            int[] pixels = new int[mwidth * mheight];
            for (int y = 0; y < mheight; y++) {
                for (int x = 0; x < mwidth; x++) {
                    if (matrix.get(x, y)) {
                        //二维码颜色
                        pixels[y * mwidth + x] = 0xff000000;
                    }
                    else{
                        //二维码背景颜色
                        pixels[y * mwidth + x] = 0xffffffff;
                    }
                }
            }
            //二维码图片
            Bitmap topBitmap = Bitmap.createBitmap(mwidth, mheight,Bitmap.Config.ARGB_4444);
            // 通过像素数组生成bitmap,具体参考api
            topBitmap.setPixels(pixels, 0, mwidth, 0, 0, mwidth, mheight);
            return topBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }
	
    /**
     * 因为目前我们只有一套资源文件，全都放在hdpi下面，这样如果是遇到高密度手机， 系统会按照
     * scale = (float) targetDensity / density 把图片放到几倍，这样会使得在高密度手机上经常会发生OOM。
     * 这个方法用来解决在如果密度大于hdpi（240）的手机上，decode资源文件被放大scale，内容浪费的问题。
     * @param resources
     * @param id
     */
    public static Bitmap decodeResource(Resources resources, int id) {
        int densityDpi = resources.getDisplayMetrics().densityDpi;
        Bitmap bitmap;
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Bitmap.Config.ALPHA_8;
        if (densityDpi > DisplayMetrics.DENSITY_HIGH) {
            opts.inTargetDensity = value.density;
            bitmap = BitmapFactory.decodeResource(resources, id, opts);
        }else{
            bitmap = BitmapFactory.decodeResource(resources, id);
        }
        return bitmap;
    }
    
	/**
	 * 合成DIY二维码
	 * @param topBitmap 上图
	 * @param bottomBitmap 底图
	 * @return Bitmap 最终合成的图片
	 */
	private static Bitmap mergeBitmap(Bitmap topBitmap, Bitmap bottomBitmap) {
		Bitmap bitmap = Bitmap.createBitmap(bottomBitmap.getWidth(), bottomBitmap.getHeight(),bottomBitmap.getConfig());
		Canvas canvas = new Canvas(bitmap);
		//src 这个是表示绘画图片的大小
		Rect src = new Rect();// 图片
        src.left = 0;   
        src.top = 0;
        src.right = 0 + bottomBitmap.getWidth();
        src.bottom = 0 + bottomBitmap.getHeight();
        //dst 是表示 绘画这个图片的位置
        Rect dst = new Rect();// 屏幕位置及尺寸
        dst.left = 0;    
        dst.top = 0;    
        dst.right = 0 + bottomBitmap.getWidth();    
        dst.bottom = 0 + bottomBitmap.getHeight();    
		canvas.drawBitmap(bottomBitmap, src, dst, null);
		canvas.drawBitmap(topBitmap, 0, 0, null);
		return bitmap;
    }

    /**
     * 使用Bitmap代替二维码
     * @paraogo Logo
     * @return Bitmap 最终合成的图片
     */
    private static Bitmap createMyBitmap(Bitmap code,Bitmap myBitmap) {
        Bitmap bitmap = Bitmap.createBitmap(code.getWidth(), code.getHeight(),code.getConfig());
        Canvas canvas = new Canvas(bitmap);
        //Size:图片大小
        Rect codeS = new Rect();
        codeS.left = 0;
        codeS.top = 0;
        codeS.right = codeS.left + code.getWidth();
        codeS.bottom = codeS.top + code.getHeight();
        Rect logoS = new Rect();
        logoS.left = 0;
        logoS.top = 0;
        logoS.right = logoS.left + myBitmap.getWidth();
        logoS.bottom = logoS.top + myBitmap.getHeight();
        //Position:图片位置
        Rect codeP = new Rect();
        codeP.left = 0;
        codeP.top = 0;
        codeP.right = codeP.left + code.getWidth();
        codeP.bottom = codeP.top + code.getHeight();

//        canvas.drawBitmap(code, codeS, codeP, null);
        canvas.drawBitmap(myBitmap, logoS, codeP, null);
        return bitmap;
    }

    /**
     * 合成LOGO二维码
     * @param code 二维码
     * @paraogo Logo
     * @return Bitmap 最终合成的图片
     */
    private static Bitmap createQRcodeAndLogo(Bitmap code, Bitmap logo) {
        Bitmap bitmap = Bitmap.createBitmap(code.getWidth(), code.getHeight(),code.getConfig());
        Canvas canvas = new Canvas(bitmap);
        //Size:图片大小
        Rect codeS = new Rect();
        codeS.left = 0; 
        codeS.top = 0;
        codeS.right = codeS.left + code.getWidth();
        codeS.bottom = codeS.top + code.getHeight();
        Rect logoS = new Rect();
        logoS.left = 0;
        logoS.top = 0;
        logoS.right = logoS.left + logo.getWidth();
        logoS.bottom = logoS.top + logo.getHeight();
        //Position:图片位置
        Rect codeP = new Rect();
        codeP.left = 0;
        codeP.top = 0;
        codeP.right = codeP.left + code.getWidth();
        codeP.bottom = codeP.top + code.getHeight();
        Rect logoP = new Rect();
        logoP.left = codeP.left+code.getWidth()/2-code.getWidth()/15;
        logoP.top = codeP.top+code.getHeight()/2-code.getHeight()/15;
        logoP.right = codeP.left+code.getWidth()/2+code.getWidth()/15;
        logoP.bottom = codeP.top+code.getHeight()/2+code.getHeight()/15;

        canvas.drawBitmap(code, codeS, codeP, null);
        canvas.drawBitmap(logo, logoS, logoP, null);
        return bitmap;
    }

    /**
     * 合成边框二维码
     * @param code 二维码
     * @paraogo Logo
     * @return Bitmap 最终合成的图片
     */
    private static Bitmap createQRcodeAndBorder(Bitmap code, Bitmap border) {
        //这个值为2微码的截取值,因为生成的码有一些白边,这里用这个值截取掉白边的部分
        int InterceptValue=(int)(code.getWidth()/100.0f*12);
        
        Bitmap bitmap = Bitmap.createBitmap(code.getWidth()-InterceptValue, code.getHeight()-InterceptValue,code.getConfig());
        Canvas canvas = new Canvas(bitmap);
        //Size:图片大小
        Rect codeS = new Rect();
        codeS.left = InterceptValue;
        codeS.top = InterceptValue;
        codeS.right = code.getWidth()-InterceptValue;
        codeS.bottom = code.getHeight()-InterceptValue;
        Rect borderS = new Rect();
        borderS.left = 0;
        borderS.top = 0;
        borderS.right = borderS.left + border.getWidth();
        borderS.bottom = borderS.top + border.getHeight();
        //Position:图片位置
        Rect codeP = new Rect();
        codeP.left = 0;
        codeP.top = 0;
        codeP.right = codeP.left + codeS.right;
        codeP.bottom = codeP.top + codeS.bottom;
        Rect borderP = new Rect();
        borderP.left = codeP.left;
        borderP.top = codeP.top;
        borderP.right = codeP.right;
        borderP.bottom = codeP.bottom;

        canvas.drawBitmap(code, codeS, codeP, null);
        canvas.drawBitmap(border, borderS, borderP, null);
        return bitmap;
    }

    private static BitMatrix deleteWhite(BitMatrix matrix) {
        int[] rec = matrix.getEnclosingRectangle();
        int resWidth = rec[2] + 1;
        int resHeight = rec[3] + 1;

        BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
        resMatrix.clear();
        for (int i = 0; i < resWidth; i++) {
            for (int j = 0; j < resHeight; j++) {
                if (matrix.get(i + rec[0], j + rec[1]))
                    resMatrix.set(i, j);
            }
        }
        return resMatrix;
    }

}

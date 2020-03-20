package com.hzh.frame.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.hzh.frame.BaseInitData;
import com.hzh.frame.R;
import com.hzh.frame.util.AndroidUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtil {


    //放大缩小图片  
    public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidht = ((float)w / width);
        float scaleHeight = ((float)h / height);
        matrix.postScale(scaleWidht, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbmp;
    }

    //将Drawable转化为Bitmap  
    public static Bitmap drawableToBitmap(Drawable drawable){
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height,drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0,0,width,height);
        drawable.draw(canvas);
        return bitmap;

    }

    //获得圆角图片的方法  
    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    //获得带倒影的图片方法  
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap){
        final int reflectionGap = 4;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap,
                0, height/2, width, height/2, matrix, false);

        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height,width,height + reflectionGap,
                deafalutPaint);

        canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0,bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in  
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient  
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()+ reflectionGap, paint);

        return bitmapWithReflection;
    }

    /**
     * 图片大小压缩
     * @param bitMap 图片
     * @param maxSize 图片允许最大空间   单位：KB
     * */
    public static byte[] zoom(Bitmap bitMap,int maxSize) {
        return zoom(bitMap,maxSize,10);
    }

    /**
     * 图片大小压缩
     * @param bitMap 图片
     * @param maxSize 图片允许最大空间   单位：KB
     * @param step 每次压缩的步长,及每次压缩的范围(如:step=10,每次压缩就会按照100%,90%,80%压缩)  单位：百分比,范围(1,100)   
     * */
    public static byte[] zoom(Bitmap bitMap,int maxSize,int step) {
        //将bitmap放至数组中，意在bitmap的大小（与实际读取的原文件要大）  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //options区间(0,100)
        for(int options = 100-step;options>=1;options-=step){
            if(options<100 && options>0){
                baos.reset(); // 重置baos即清空baos  
                bitMap.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中  
                if(baos.toByteArray().length / 1024 <= maxSize){// 循环判断如果压缩后图片是否大于100kb,大于继续压缩  
                    break;
                }
            }
        }
        return baos.toByteArray();
    }

    /***
     * 图片的缩放方法
     * @param bgimage：源图片资源
     * @param newWidth：缩放后宽度
     * @param newHeight：缩放后高度
     * @return
     */
    public static Bitmap zoomImage(Bitmap bgimage, double newWidth,double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,(int) height, matrix, true);
        return bitmap;
    }


    //需要模糊就增大这个值把图片缩放增大再进行模糊
    public static int scaleRatio = 30;
    //这个参数不建议修改 会出现OOM
    public static int blurRadius = 8;
    /**
     * 图片毛玻璃效果
     * */
    public static Bitmap doBlur(Bitmap sentBitmap, boolean canReuseInBitmap) {
    	
    	/*我们可以利用这个function来进行bitmap的缩放。
                              其中前三个参数很明显，其中宽高我们可以选择为原图尺寸的1/10；
                             第四个filter是指缩放的效果，filter为true则会得到一个边缘平滑的bitmap，反之，则会得到边缘锯齿、pixelrelated的bitmap。
                            这里我们要对缩放的图片进行虚化，所以无所谓边缘效果文*/
        sentBitmap = Bitmap.createScaledBitmap(sentBitmap,sentBitmap.getWidth()/scaleRatio,sentBitmap.getHeight() /scaleRatio, false);

        Bitmap bitmap;
        if (canReuseInBitmap) {
            bitmap = sentBitmap;
        } else {
            bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
        }

        if (blurRadius < 1) {
            return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = blurRadius + blurRadius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = blurRadius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -blurRadius; i <= blurRadius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + blurRadius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = blurRadius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - blurRadius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + blurRadius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -blurRadius * w;
            for (i = -blurRadius; i <= blurRadius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + blurRadius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = blurRadius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - blurRadius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        bitmap.setPixels(pix, 0, w, 0, 0, w, h);
        return (bitmap);
    }

    /**
     * 根据URL生成Bitmap
     * @param path 本地SD卡目录路径
     * */
    public static Bitmap sdUrl2bitmap(String path) {
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 2.为位图设置100K的缓存
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTempStorage = new byte[100 * 1024];
        // 3.设置位图颜色显示优化方式
        // ALPHA_8：每个像素占用1byte内存（8位）
        // ARGB_4444:每个像素占用2byte内存（16位）
        // ARGB_8888:每个像素占用4byte内存（32位）
        // RGB_565:每个像素占用2byte内存（16位）
        // Android默认的颜色模式为ARGB_8888，这个颜色模式色彩最细腻，显示质量最高。但同样的，占用的内存//也最大。也就意味着一个像素点占用4个字节的内存。我们来做一个简单的计算题：3200*2400*4
        // bytes //=30M。如此惊人的数字！哪怕生命周期超不过10s，Android也不会答应的。
        opts.inPreferredConfig = Config.RGB_565;
        // 4.设置图片可以被回收，创建Bitmap用于存储Pixel的内存空间在系统内存不足时可以被回收
        opts.inPurgeable = true;
        // 5.设置位图缩放比例
        // width，hight设为原来的四分一（该参数请使用2的整数倍）,这也减小了位图占用的内存大小；例如，一张//分辨率为2048*1536px的图像使用inSampleSize值为4的设置来解码，产生的Bitmap大小约为//512*384px。相较于完整图片占用12M的内存，这种方式只需0.75M内存(假设Bitmap配置为//ARGB_8888)。
        //opts.inSampleSize = 4;
        // 6.设置解码位图的尺寸信息
        opts.inInputShareable = true;
        // 7.解码位图
        Bitmap btp = BitmapFactory.decodeStream(is, null, opts);
        return btp;
    }

    /**
     * 生成二维码
     * @param url 二维码链接
     * @return
     */
    public static Bitmap create2DCodePure(String url,int widthT,int heightT) {
        try {
            // 生成二维码
            BitMatrix matrix = matrix = new MultiFormatWriter().encode(url,BarcodeFormat.QR_CODE, widthT, heightT);
            int width = matrix.getWidth();
            int height = matrix.getHeight();
            // 二维矩阵转为一维像素数组,也就是一直横着排了
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = 0xff000000;
                    }
                    else{
                        //白色填充背景
                        //	                    pixels[y * width + x] = 0xffffffff;  
                    }
                }
            }
            //二维码图片
            Bitmap topBitmap = Bitmap.createBitmap(width, height, Config.ARGB_4444);
            // 通过像素数组生成bitmap,具体参考api
            topBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            //最终合成二维码图片
            return topBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存Bitmap到本地,并获取到保存后的本地文件路径
     * */
    public static String saveBitmap(String name,Bitmap mBitmap){
        String path="";//保存后的本地文件路径
        try {
            File file=new File(Environment.getExternalStorageDirectory(), "/"+ BaseInitData.ImageFrameCacheDir+"/"+name+".png");
            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            path=file.getPath();
            FileOutputStream fOut = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            Log.e("保存图片出错", "在保存图片时出错：" + e.toString());
        }
        return path;
    }

    /**
     * 保存图片到本地相册
     * */
    public static String saveBitmap2Camera(Context context,Bitmap bitmap, String bitName){
        String fileName;
        File file;
        bitName=bitName + ".JPEG";
        if (Build.BRAND.equals("Xiaomi")) { // 小米手机
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera/" + bitName;
        } else { // Meizu 、Oppo
            fileName = Environment.getExternalStorageDirectory().getPath() + "/DCIM/" + bitName;
        }
        file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            // 格式为 JPEG，照相机拍出的图片为JPEG格式的，PNG格式的不能显示在相册中
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                out.flush();
                out.close();
                // 插入图库
                MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), bitName, null);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 发送广播，通知刷新图库的显示
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + fileName)));
        return file.getPath();
    }
    
    /**
     * 因为目前我们只有一套资源文件，全都放在hdpi下面，这样如果是遇到高密度手机， 系统会按照
     * scale = (float) targetDensity / density 把图片放到几倍，这样会使得在高密度手机上经常会发生OOM。
     *
     * 这个方法用来解决在如果密度大于hdpi（240）的手机上，decode资源文件被放大scale，内容浪费的问题。
     * @param resources
     * @param id
     * @return
     */
    public static Bitmap decodeResource(Resources resources, int id) {
        int densityDpi = resources.getDisplayMetrics().densityDpi;
        Bitmap bitmap;
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inPreferredConfig = Config.ALPHA_8;
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
    public static Bitmap mergeBitmap(Bitmap topBitmap, Bitmap bottomBitmap,String userName, String phone,boolean isAder) {
        Bitmap bitmap = Bitmap.createBitmap(bottomBitmap.getWidth(), bottomBitmap.getHeight(),bottomBitmap.getConfig());
        Canvas canvas = new Canvas(bitmap);
        Rect src = new Rect();// 图片
        Rect dst = new Rect();// 屏幕位置及尺寸
        //src 这个是表示绘画图片的大小
        src.left = 0;   //0,0  
        src.top = 0;
        src.right = 0 + bottomBitmap.getWidth();// mBitDestTop.getWidth();,这个是桌面图的宽度，
        src.bottom = 0 + bottomBitmap.getHeight();//mBitDestTop.getHeight()/2;// 这个是桌面图的高度的一半
        // 下面的 dst 是表示 绘画这个图片的位置
        dst.left = 0;    //miDTX,//这个是可以改变的，也就是绘图的起点X位置
        dst.top = 0;    //mBitQQ.getHeight();//这个是QQ图片的高度。 也就相当于 桌面图片绘画起点的Y坐标
        dst.right = 0 + bottomBitmap.getWidth();    //miDTX + mBitDestTop.getWidth();// 表示需绘画的图片的右上角
        dst.bottom = 0 + bottomBitmap.getHeight();    // mBitQQ.getHeight() + mBitDestTop.getHeight();//表示需绘画的图片的右下角
        canvas.drawBitmap(bottomBitmap, src, dst, null);
        if(isAder){
            Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.parseColor("#885b24"));
            p.setTextSize(bottomBitmap.getHeight()/40);

            canvas.drawBitmap(topBitmap, bottomBitmap.getWidth()*11/80, bottomBitmap.getHeight()*55/80, null);
            String content="兹授权("+userName+")手机号: "+phone+" 取得我公司个人广告商特权在此权限内,可享受在7商城广告平台发布精准广告服务.此授权不得转让.";
            int hangNum=21;//没行显示长度
            int startIndex=0;//每行起始下标
            for(int i=0;startIndex<content.length();i++){
                String showLangNum="";
                for(int j=startIndex;j<startIndex+hangNum;j++){
                    if(j+1>content.length()){
                        break;
                    }
                    if(Util.isChinese(content.substring(j, j+1))){
                        showLangNum=showLangNum+content.substring(j, j+1);
                    }else{
                        showLangNum=showLangNum+content.substring(j, j+1)+" ";
                    }
                }
                startIndex=startIndex+hangNum;
                if(i==0){
                    //第一行
                    canvas.drawText(showLangNum, (int)(bottomBitmap.getWidth()/4.79), (int)(bottomBitmap.getHeight()/2.15), p);
                }else{
                    //其它行
                    canvas.drawText(showLangNum, (int)(bottomBitmap.getWidth()/6.4), (int)(bottomBitmap.getHeight()/2.15+i*25), p);
                }
            }
        }else{
            canvas.drawBitmap(topBitmap, 0, 0, null);
        }
        return bitmap;

    }



    // 得到byte[]
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    // 得到Bitmap
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }


}
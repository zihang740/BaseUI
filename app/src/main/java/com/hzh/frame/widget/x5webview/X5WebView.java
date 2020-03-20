package com.hzh.frame.widget.x5webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;

import com.tencent.smtt.export.external.interfaces.SslError;
import com.tencent.smtt.export.external.interfaces.SslErrorHandler;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class X5WebView extends WebView {
    public static final String TAG = "X5WebView";
    
    private WebViewClient client = new WebViewClient() {
        /**
         * 防止加载网页时调起系统浏览器
         */
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        /**
         * ssl忽略证书
         */
        @Override
        public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
            sslErrorHandler.proceed();//接受证书
        }
    };

    @SuppressLint("SetJavaScriptEnabled")
    public X5WebView(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
        this.setWebViewClient(client);
        // this.setWebChromeClient(chromeClient);
        // WebStorage webStorage = WebStorage.getInstance();
        initWebViewSettings();
        this.getView().setClickable(true);
    }

    private void initWebViewSettings() {
        WebSettings webSetting = this.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        // 1、LayoutAlgorithm.NARROW_COLUMNS ： 适应内容大小
        // 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
        webSetting.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setSupportMultipleWindows(true);
         webSetting.setLoadWithOverviewMode(true);////缩放至屏幕的大小
        webSetting.setAppCacheEnabled(true);
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setGeolocationEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        // webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
        webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
        // webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        // this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
        // settings 的设计
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret = super.drawChild(canvas, child, drawingTime);
        canvas.save();
        Paint paint = new Paint();
        paint.setColor(0x7fff0000);
        paint.setTextSize(24.f);
        paint.setAntiAlias(true);
        if (getX5WebViewExtension() != null) {
            //			canvas.drawText(this.getContext().getPackageName() + "-pid:"+ android.os.Process.myPid(), 10, 50, paint);
            //			canvas.drawText("X5  Core:" + QbSdk.getTbsVersion(this.getContext()), 10,100, paint);
        } else {
            //			canvas.drawText(this.getContext().getPackageName() + "-pid:"+ android.os.Process.myPid(), 10, 50, paint);
            //			canvas.drawText("Sys Core", 10, 100, paint);
        }
        //		canvas.drawText(Build.MANUFACTURER, 10, 150, paint);
        //		canvas.drawText(Build.MODEL, 10, 200, paint);
        canvas.restore();
        return ret;
    }

    public X5WebView(Context arg0) {
        super(arg0);
        setBackgroundColor(85621);
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
    }

    public void setWebViewClient(android.webkit.WebViewClient webViewClient) {
    }

    
    public boolean isScrollX=false;
    
    /**
     * 当webview滚动到边界时执行(草 ... 无效,这里没被调用)
     * @param scrollX 第一个参数为距离远点的X轴的距离
     * @param scrollY 第二个参数为距离远点的Y轴的距离
     * @param clampedX 第三个参数当ScrollView滑动到左侧边界的时候值为true
     * @param clampedY 第四个参数当ScrollView滑动到下边界的时候值为true
     * */
    @Override
    public void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        isScrollX = clampedX;
        if(isScrollX){
            Log.w(TAG,"true ---> 已滑动到边界");
        }else{
            Log.w(TAG,"false ---> 未滑动到边界");
        }
    }
}

package com.hzh.frame.widget.xwebview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.alibaba.android.arouter.facade.annotation.Route;

/**
 * 嵌入Acitvity式WebView
 */
public class XEmbedWebView extends WebView {

    LoadStateCallback callback;

    public XEmbedWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void init(){
        getSettings().setDefaultTextEncodingName("UTF-8") ;
        //这个属性可以让webview只显示一列，也就是自适应页面大小 不能左右滑动，但在使用中发现，只针对4.4以下有效，因为4.4的webview内核改了，Google也在api中说了，要么改html样式，要么改变WebView；
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        // 设置显示缩放按钮
        getSettings().setBuiltInZoomControls(false);
        // 支持缩放
        getSettings().setSupportZoom(false); 
        //设置WebView属性，每次及时加载 不要缓存
        getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置WebView属性，能够执行Javascript脚本    
        getSettings().setJavaScriptEnabled(true);
        //设置可以访问文件  
        getSettings().setAllowFileAccess(true);
        setScrollContainer(false);
        setScrollbarFadingEnabled(false);
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        //设置Web视图    
        setWebViewClient(new webViewClient());
        setHorizontalScrollBarEnabled(false);//水平不显示  滚动条
        setVerticalScrollBarEnabled(false); //垂直不显示 滚动条
    }

    //Web视图    
    private class webViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // 网页加载完毕
            if(callback!=null){
                callback.loadEnd();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            loadUrl("file:///android_asset/errorpage/error.html");
        }
    }
    
    
    public void setUrlLoadStateListener(LoadStateCallback callback){
        this.callback=callback;
    }

    //网址加载状态接口
    public interface LoadStateCallback{
        void loadError();
       void loadEnd();
    }
   
}

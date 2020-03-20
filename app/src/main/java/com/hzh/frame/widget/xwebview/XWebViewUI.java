package com.hzh.frame.widget.xwebview;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.hzh.frame.R;
import com.hzh.frame.ui.activity.BaseUI;
import com.hzh.frame.util.Util;

public class XWebViewUI extends BaseUI {
    private XWebView webview;
    private ProgressBar progressBar;
    private RelativeLayout progressBarLayout;
    private String titleName = "", url = "http://www.baidu.com";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreateBase() {
        if (getIntent().getExtras() != null) {
            if (null != getIntent().getStringExtra("title")) {
                titleName = getIntent().getStringExtra("title");
            }
            if (null != getIntent().getStringExtra("url")) {
                url = getIntent().getStringExtra("url");
            }
        }
        setContentView(R.layout.base_xwebview);
        getTitleView().setContent(titleName);
        progressBarLayout = findViewById(R.id.progressBarLayout);
        progressBar = findViewById(R.id.progressBar);
        initWebView();
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void initWebView(){
        webview = findViewById(R.id.webView);
        //设置可以访问文件  
        webview.getSettings().setAllowFileAccess(true);
        
        webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);//设置js可以直接打开窗口，如window.open()，默认为false
        webview.getSettings().setJavaScriptEnabled(true);//是否允许JavaScript脚本运行，默认为false。设置true时，会提醒可能造成XSS漏洞
        webview.getSettings().setSupportZoom(true);//是否可以缩放，默认true
        webview.getSettings().setBuiltInZoomControls(true);//是否显示缩放按钮，默认false
        webview.getSettings().setUseWideViewPort(true);//设置此属性，可任意比例缩放。大视图模式
        webview.getSettings().setLoadWithOverviewMode(true);//和setUseWideViewPort(true)一起解决网页自适应问题
        webview.getSettings().setAppCacheEnabled(true);//是否使用缓存
        webview.getSettings().setDomStorageEnabled(true);//开启本地DOM存储
        webview.getSettings().setLoadsImagesAutomatically(true); // 加载图片
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);//播放音频，多媒体需要用户手动？设置为false为可自动播放
        // 加载需要显示的网页
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // 网页加载完毕
                progressBar.setVisibility(View.GONE);
                progressBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                webview.loadUrl("file:///android_asset/errorpage/error.html");
            }

            @Override //设置不用系统浏览器打开,直接显示在当前Webview
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webview.loadUrl(url);
                return true;
            }

            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                handler.proceed();//这里校验失败的时候放过
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //获取网页标题
                if (!Util.isEmpty(title) && "".equals(titleName)) {
                    getTitleView().setContent(title);
                }
            }
        });
        webview.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}

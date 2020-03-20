package com.hzh.frame.widget.x5webview;


import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import com.hzh.frame.R;
import com.hzh.frame.ui.activity.BaseUI;
import com.hzh.frame.util.Util;

public class X5WebViewUI extends BaseUI {
    private X5WebView webview;
    private String titleName = "", url = "http://www.baidu.com";

    @Override
    public boolean setTitleIsShow() {
        return getIntent().getBooleanExtra("showTitle",true);
    }

    @Override
	protected void onCreateBase() {
        setContentView(R.layout.base_ui_x5webview);
        if (getIntent().getExtras() != null) {
            if (null != getIntent().getStringExtra("title")) {
                titleName = getIntent().getStringExtra("title");
            }
            if (null != getIntent().getStringExtra("url")) {
                url = getIntent().getStringExtra("url");
            }
            if(getIntent().getBooleanExtra("showTitle",true)){
                getTitleView().setContent(titleName);
            }
            initWebView();
        }
    }

    public void initWebView(){
        webview = findViewById(R.id.x5WebView);
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //获取网页标题
                if (!Util.isEmpty(title) && "".equals(titleName) && getIntent().getBooleanExtra("showTitle",true)) {
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
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
    
}

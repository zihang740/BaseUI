package com.hzh.frame.ui.activity;

import android.app.ActivityGroup;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hzh.frame.R;
import com.hzh.frame.comn.annotation.InjectUtils;
import com.hzh.frame.core.BaseSP;
import com.hzh.frame.tools.LanguageTools;
import com.hzh.frame.util.CloseAppUtil;
import com.hzh.frame.util.Util;
import com.hzh.frame.widget.toast.BaseToast;
import com.hzh.frame.widget.xrefresh.XSwipeRefreshLayout;
import com.hzh.frame.widget.xtitleview.TitleView;

@SuppressWarnings("deprecation")
public abstract class BaseGroupUI extends ActivityGroup implements SwipeRefreshLayout.OnRefreshListener {

    public LayoutInflater inflater;
    public FrameLayout rootView;//页面根布局
    public XSwipeRefreshLayout refreshBar;//下拉刷新
    public FrameLayout rootTitleBar;
    public FrameLayout rootTitle;//根标题布局
    public FrameLayout rootContent;//根主页面布局
    public TitleView titleView;//标题View
    public LinearLayout loadingView;//加载中页面

    @Override
    protected void attachBaseContext(Context newBase) {
        //兼容 8.0 以上版本多语言切换
        String language= BaseSP.getInstance().getString("language");
        if(!Util.isEmpty(language)){
            super.attachBaseContext(LanguageTools.setAppLanguage(newBase, language));
        }else{
            super.attachBaseContext(newBase);
        }
    }
    
    /**
     * onCreate之前初始化配置页面
     * */
    protected void onCreateBefore(){
        CloseAppUtil.activityList.add(this);
        //软键盘弹出不顶走Activity页面
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        //设置屏幕方向
        setRequestedOrientation(setScreenDirection());
        //获取Layout布局
        inflater = getLayoutInflater();
        //创建父容器
        rootView=(FrameLayout) inflater.inflate(R.layout.base_view_rootview, null);
        refreshBar=(XSwipeRefreshLayout) rootView.findViewById(R.id.rootViewSwipeRefresh);
        refreshBar.setEnabled(false);
        rootTitleBar=(FrameLayout) rootView.findViewById(R.id.rootTitleBar);
        rootTitle=(FrameLayout) rootView.findViewById(R.id.rootViewTitle);
        rootContent=(FrameLayout) rootView.findViewById(R.id.rootViewContent);
    }

    /**
     * onCreate之后初始化配置页面
     * */
    protected void onCreateAfter(){
        //下拉刷新初始化
        refreshBar.setColorSchemeResources(R.color.base_refresh);
        refreshBar.setOnRefreshListener(this);
        //初始化状态栏
        initTitleBar();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        onCreateBefore();
        InjectUtils.injectLayout(this);
        onCreateBase();
        onCreateAfter();
    }

    @Override
    public void setContentView(int layoutResID) {
        //清空所有容器
        rootTitle.removeAllViews();
        rootContent.removeAllViews();
        //根页面 | 显示  | 标题View
        if(setTitleIsShow()){
            rootTitle.setVisibility(View.VISIBLE);
            rootTitle.addView(initTitleView());
        }else{
            rootTitle.setVisibility(View.GONE);
        }
        //根页面 | 主页面 | 添加  | 布局View
        rootContent.addView(inflater.inflate(layoutResID, null));
        //根页面 | 主页面 | 添加  | 页面加载中View
        rootContent.addView(initLoadingView());
        //完成布局
        setContentView(rootView);
        InjectUtils.injectView(this);
        InjectUtils.injectListener(this);
    }
    /*--------------状态栏 Start-------------*/
    /**
     * 初始化状态栏
     * */
    protected void initTitleBar(){
    };
    
    /*--------------状态栏 End-------------*/
    
    /*--------------标题 Start-------------*/
    private LinearLayout initTitleView() {
        titleView = (TitleView) inflater.inflate(setTitleLayout(), null);
        titleView.init(this);
        return titleView;
    }
    /**
     * 设置标题布局文件
     * id必须为:comn_title_left,comn_title,comn_title_right
     * */
    public int setTitleLayout(){ return R.layout.base_view_title;}
    /**
     * 是否显示标题
     * */
    public boolean setTitleIsShow(){ return true;}
    /**
     * 获取标题View 必须在setContentView之后
     * */
    public TitleView getTitleView(){ return titleView;}
    /*--------------标题 End-------------*/


    /*--------------页面加载中View Start-------------*/
    public LinearLayout initLoadingView(){
        loadingView = (LinearLayout) inflater.inflate(R.layout.base_view_loading, null);
        //禁止点击空白地方响应到下一层主页面点击事件
        loadingView.findViewById(R.id.load).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {}
        });
        loadingView.setVisibility(View.GONE);
        return loadingView;
    }
    /*--------------页面加载中View End-------------*/


    /**
     * 提示信息
     * @param msg 提示信息
     * */
    protected void alert(String msg) {
        BaseToast.getInstance().setMsg(R.id.content,msg).show();
    }

    /**
     * 显示加载框 加载中 (在setContentView之后添加)
     * */
    protected void showLoding() {
        loadingView.setVisibility(View.VISIBLE);
        ((LinearLayout)loadingView.findViewById(R.id.loading)).setVisibility(View.VISIBLE);
        ((LinearLayout)loadingView.findViewById(R.id.loadingFail)).setVisibility(View.GONE);
    }

    /**
     * 显示加载框->加载失败->点击重载
     * */
    protected void showLodingFail() {
        loadingView.setVisibility(View.VISIBLE);
        ((LinearLayout)loadingView.findViewById(R.id.loading)).setVisibility(View.GONE);
        ((LinearLayout)loadingView.findViewById(R.id.loadingFail)).setVisibility(View.VISIBLE);
        ((LinearLayout)loadingView.findViewById(R.id.loadingFail)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击重载
                onCreateBase();
            }
        });
    }

    /**
     * 显示加载框->加载失败->点击重载
     * */
    protected void showLodingFailCall() {
        loadingView.setVisibility(View.VISIBLE);
        ((LinearLayout)loadingView.findViewById(R.id.loading)).setVisibility(View.GONE);
        ((LinearLayout)loadingView.findViewById(R.id.loadingFail)).setVisibility(View.VISIBLE);
        ((LinearLayout)loadingView.findViewById(R.id.loadingFail)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击重载
                showLodingFailCallMethod();
            }
        });
    }
    /**
     * 显示加载框->加载失败->点击重载
     * */
    protected void showLodingFailCallMethod() {}

    /**
     * 关闭加载框
     * */
    protected void dismissLoding() {
        loadingView.setVisibility(View.GONE);
    }

    public XSwipeRefreshLayout getRefreshBar(){
        return this.refreshBar;
    }

    @Override
    public void onRefresh() {
        refreshBar.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        //去除所有页面跳转动画
        overridePendingTransition(0,0);
        super.onPause();
    }

    /**
     * Fragment中调用getActivity为null的问题  注释:super.onSaveInstanceState(outState);
     * */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
    }

    /**
     * 设置页面背景
     * */
    protected void setActivityBg(int drawableID){
        rootView.setBackgroundResource(drawableID);
    }

    /**
     * 设置屏幕方向
     * */
    protected int setScreenDirection(){
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }
    protected abstract void onCreateBase();

}

package com.hzh.frame.ui.fragment;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.hzh.frame.R;
import com.hzh.frame.widget.toast.BaseToast;
import com.hzh.frame.widget.xrefresh.XSwipeRefreshLayout;
import com.hzh.frame.widget.xtitleview.TitleView;
import com.trello.rxlifecycle2.components.support.RxFragment;

public abstract class BaseFM extends RxFragment implements OnRefreshListener{

    public View rootLayout;
    public LayoutInflater inflater;
    public FrameLayout rootView;//页面根布局
	private XSwipeRefreshLayout refreshBar;//下拉刷新
    public FrameLayout rootTitleBar;
    public FrameLayout rootTitle;//根标题布局
    public FrameLayout rootContent;//根主页面布局
    public TitleView titleView;//标题View
    public LinearLayout loadingView;//加载中页面

    /**
	 * onCreate之前初始化配置页面
	 * */
	protected void onCreateBefore(LayoutInflater inflater){
		//软键盘弹出不顶走Activity页面
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		//禁止横屏显示
		getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//获取Layout布局
		this.inflater = inflater;
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
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		onCreateBefore(inflater);
		onCreateBase();
		onCreateAfter();
		return rootView;
	}
	
	public View setContentView(int layoutResID) {
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
		 rootLayout=inflater.inflate(layoutResID, null);
		 rootContent.addView(rootLayout);
		 //根页面 | 主页面 | 添加  | 页面加载中View
		 rootContent.addView(initLoadingView());
		 return rootLayout;
	}
	/*--------------状态栏 Start-------------*/
    /**
     * 初始化状态栏
     * */
    public void initTitleBar(){
    };
    /*--------------状态栏 End-------------*/

	/*--------------标题 Start-------------*/
    private LinearLayout initTitleView() {
    	 titleView = (TitleView) inflater.inflate(setTitleLayout(), null);
 	     titleView.init(getActivity());
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
	public void onStop() {
		super.onStop();
	}

	/**
	 * 设置页面背景
	 * */
	protected void setActivityBg(int drawableID){
		rootView.setBackgroundResource(drawableID);
	}
	protected abstract void onCreateBase();

	
    public <T extends View> T findViewById(int id) {
        return rootLayout.findViewById(id);
    }
}

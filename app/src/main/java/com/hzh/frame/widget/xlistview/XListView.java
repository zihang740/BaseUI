package com.hzh.frame.widget.xlistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.hzh.frame.R;

public class XListView extends ListView implements OnScrollListener {

	private Context context;
	private float mLastY = -1; 
	private Scroller mScroller; //实现View平滑滚动的一个Helper类
	private OnScrollListener mScrollListener; 
	private IXListViewListener mListViewListener;
	private XListViewHeader mHeaderView;
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; 
	private boolean mEnablePullRefresh = true;//是否显示头布局
	private boolean mPullRefreshing = false; //下拉刷新:是否正在加载中
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad = true;//是否显示脚布局
	private boolean mPullLoading = false;//加载更多:是否正在加载中
	
	private int mTotalItemCount;
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;
	private final static int SCROLL_DURATION = 400; 
	private final static int PULL_LOAD_MORE_DELTA = 50; //加载更多:下边距>PULL_LOAD_MORE_DELTA则开始加载数据
	private final static float OFFSET_RADIO = 1.8f;
	
	private int startPage=0;//起始页码
	private int page=0;//当前页码
	private int limit=20;//默认每页显示数

	public XListView(Context context) {
		super(context);
		this.context=context;
		initWithContext();
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context=context;
		initWithContext();
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context=context;
		initWithContext();
	}

	/**
	 * 初始化ListView
	 * @author hzh 2015-06-15 16:30
	 * */
	private void initWithContext() {
		/*
		 * mScroller用来回弹下拉刷新/上拉更多
		 * 配合computerScroll来使用
		 */
		mScroller = new Scroller(context, new DecelerateInterpolator());
		super.setOnScrollListener(this);
		createHeaderView();
		createFooterView();
	}

	/**
	 * 创建头布局
	 * @author hzh 2015-06-24 17:47
	 * */
	public void createHeaderView(){
		//实例化头布局
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);
		//监听布局文件加载到视图中(第一时间获取mHeaderViewContent的高)
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver().removeGlobalOnLayoutListener(this);
					}
				}
		);
	}
	
	/**
	 * 创建脚布局
	 * @author hzh 2015-06-24 17:47
	 * */
	public void createFooterView(){
		//实例化脚布局
		mFooterView = new XListViewFooter(context);
		mFooterView.setState(XListViewFooter.STATE_NORMAL);//设置加载更多状态
		mFooterView.setOnClickListener(new OnClickListener() {//绑定脚监听
			@Override
			public void onClick(View v) {
				if(mEnablePullLoad)startLoadMore();
			}
		});
		addFooterView(mFooterView);
	}
	
	/**
	 * 是否需要下拉刷新(头布局)
	 * */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
//		if (!mEnablePullRefresh) {
//			//不需要
//			removeHeaderView(mHeaderView);
//		} else {
//			//需要
//			removeHeaderView(mHeaderView);
//			createHeaderView();
//		}
	}
	
	
	/**
	 * 是否需要加载更多(脚布局)
	 * */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			//不需要
			removeFooterView(mFooterView);
		} else {
			//需要
			removeFooterView(mFooterView);
			createFooterView();
		}
	}
	
	/**
	 * 初始化适配器
	 * @author hzh 2015-06-18 16:30
	 * */
	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
	}

	/**
	 * 设置分页信息
	 * @param startPage 起始页码
	 * @param page 当前页码
	 * @param limit 每页显示数
	 * */
	public void setPageInfo(int startPage,int page,int limit) {
		this.startPage=startPage;
		this.page=page;
		this.limit=limit;
	}
	
	/**
	 * 获取当前页码
	 * */
	public int getPage() {
		return page;
	}

	/**
	 * 设置当前页码
	 * @param page 页码
	 * */
	public void setPage(int page) {
		this.page = page;
	}

	//停止下拉刷新中状态
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	//停止加载更多中状态
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	//设置最近更新
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText("最近更新:"+time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	//修改头布局高度
	private void updateHeaderHeight(float delta) {
		mHeaderView.setHeaderViewHeight((int) delta
				+ mHeaderView.getHeaderViewHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getHeaderViewHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0);
	}

	private void resetHeaderHeight() {
		int height = mHeaderView.getHeaderViewHeight();
		if (height == 0)return;
		if (mPullRefreshing && height <= mHeaderViewHeight)return;
		int finalHeight = 0; 
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,SCROLL_DURATION);
		invalidate();
	}

	//修改脚布局高度
	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) {
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height);
	}

	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,SCROLL_DURATION);
			invalidate();
		}
	}

	//下拉刷新
	public void startRefresh() {
		mPullRefreshing = true;
		mHeaderView.setState(XListViewHeader.STATE_REFRESHING);
		if (mListViewListener != null) {
			page=startPage;
			mListViewListener.loadNetworkData(page,limit,true);
		}
	}
	
	//加载更多
	public void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			page++;
			mListViewListener.loadNetworkData(page,limit,false);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastY = ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			final float deltaY = ev.getRawY() - mLastY;
			mLastY = ev.getRawY();
			if (getFirstVisiblePosition() == 0 && (mHeaderView.getHeaderViewHeight() > 0 || deltaY > 0)) {
				if(mEnablePullRefresh){
					updateHeaderHeight(deltaY / OFFSET_RADIO);
				}
				invokeOnScrolling();
			} else 
			if (getLastVisiblePosition() == mTotalItemCount - 1 && (mFooterView.getBottomMargin() > 0 || deltaY < 0)) {
				if(mEnablePullLoad){
					updateFooterHeight(-deltaY / OFFSET_RADIO);
				}
			}
			break;
		default:
			mLastY = -1; 
			if (getFirstVisiblePosition() == 0) {
				if (mEnablePullRefresh && !mPullRefreshing && mHeaderView.getHeaderViewHeight() > mHeaderViewHeight) {
					startRefresh();
				}
				resetHeaderHeight();
			} else 
			if (getLastVisiblePosition() == mTotalItemCount - 1) {
				if (mEnablePullLoad && !mPullLoading && mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) {
					startLoadMore();
				}
				resetFooterHeight();
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setHeaderViewHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	/**
	 * @description 正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调
	 * @author 贺子航  2014-06-24 11:54
	 * @param scrollState 0:当屏幕停止滚动时 <br />
	 *                    1:当屏幕滚动且用户使用的触碰或手指还在屏幕上时 <br />
	 *                    2:由于用户的操作，屏幕产生惯性滑动时 <br />
	 */
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	/**
	 * @description 滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次
	 * @author 贺子航  2014-06-24 11:54
	 * @param  firstVisibleItem 当前能看见的第一个列表项ID（从0开始）
	 * @param  visibleItemCount 当前能看见的列表项个数（小半个也算）
	 * @param  totalItemCount 列表项共数
	 */
	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,totalItemCount);
		}
	}

	//设置下拉刷新、加载更多监听
	public void setMListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	//下拉刷新、加载更多 | 回调接口
	public interface IXListViewListener {
		/**
		 * 下拉刷新、加载更多 | 数据加载
		 * @param page 当前页码
		 * @param limit 每页显示数
		 * @param isRefresh 是否下拉刷新
		 * */
		public void loadNetworkData(int page, int limit, boolean isRefresh);
	}

}

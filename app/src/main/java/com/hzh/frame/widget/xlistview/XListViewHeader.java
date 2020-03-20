package com.hzh.frame.widget.xlistview;


import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hzh.frame.R;

public class XListViewHeader extends LinearLayout {
	private LinearLayout mHeaderView;
	//箭头
	private ImageView mArrowImageView;
	//加载圈
	private ProgressBar mProgressBar;
	//下拉刷新"
	private TextView mHintTextView;
	private int mState = STATE_NORMAL;

	private Animation mRotateUpAnim;
	private Animation mRotateDownAnim;
	
	private final int ROTATE_ANIM_DURATION = 180;
	//下拉刷新
	public final static int STATE_NORMAL = 0;
	//松开刷新
	public final static int STATE_READY = 1;
	//正在加载
	public final static int STATE_REFRESHING = 2;

	public XListViewHeader(Context context) {
		super(context);
		initView(context);
	}

	public XListViewHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	/**
	 * 初始化头布局
	 * @author hzh 2015-06-15 16:35
	 * */
	private void initView(Context context) {
		// 初始情况，设置下拉刷新view高度为0
		LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, 0);
		mHeaderView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.base_xlistview_head, null);
		addView(mHeaderView, lp);
		setGravity(Gravity.BOTTOM);

		mArrowImageView = (ImageView)findViewById(R.id.xlistview_header_arrow);
		mHintTextView = (TextView)findViewById(R.id.xlistview_header_hint_textview);
		mProgressBar = (ProgressBar)findViewById(R.id.xlistview_header_progressbar);
		
		//箭头向上动画
		mRotateUpAnim = new RotateAnimation(0.0f, -180.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		mRotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateUpAnim.setFillAfter(true);
		//箭头向下动画
		mRotateDownAnim = new RotateAnimation(-180.0f, 0.0f,Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		mRotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
		mRotateDownAnim.setFillAfter(true);
	}

	public void setState(int state) {
		if (state == mState) return ;//下拉刷新状态直接返回
		if (state == STATE_REFRESHING) {//正在加载状态	
			// 显示进度
			mArrowImageView.clearAnimation();
			mArrowImageView.setVisibility(View.GONE);
			mProgressBar.setVisibility(View.VISIBLE);
		} else {//松开刷新状态	
			// 显示箭头图片
			mArrowImageView.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}
		
		switch(state){
		case STATE_NORMAL:
			//下拉刷新
			if (mState == STATE_READY) {
				//松开刷新
				mArrowImageView.startAnimation(mRotateDownAnim);
			}
			if (mState == STATE_REFRESHING) {
				//正在加载
				mArrowImageView.clearAnimation();
			}
			mHintTextView.setText("下拉刷新");
			break;
		case STATE_READY:
			//松开刷新
			if (mState != STATE_READY) {
				mArrowImageView.clearAnimation();
				mArrowImageView.startAnimation(mRotateUpAnim);
				mHintTextView.setText("松开刷新");
			}
			break;
		case STATE_REFRESHING:
			//正在加载
			mHintTextView.setText("正在加载...");
			break;
			default:
		}
		mState = state;
	}
	
	//设置HeaderView高度
	public void setHeaderViewHeight(int height) {
		if (height < 0)height = 0;
		LayoutParams lp = (LayoutParams) mHeaderView.getLayoutParams();
		lp.height = height;
		mHeaderView.setLayoutParams(lp);
	}
	//获取HeaderView高度
	public int getHeaderViewHeight() {
		return mHeaderView.getHeight();
	}

}

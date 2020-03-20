package com.hzh.frame.widget.xtitleview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzh.frame.BaseInitData;
import com.hzh.frame.R;
import com.hzh.frame.util.StatusBarUtil;

/**
 * 一个最基础的标题样式
 * */
public class TitleView extends LinearLayout{

	private Activity activity;
	private ImageView left;
    private TextView content;
    private LinearLayout right;
    private TextView rightContent;
    private TextView titleLine;
    private ImageView rightIcon;
	

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@SuppressLint("NewApi")
	public TitleView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TitleView(Context context) {
		super(context);
	}



	public void init(Activity activity){
		this.activity=activity;
		left=(ImageView)findViewById(R.id.comn_title_left);
		content=(TextView) findViewById(R.id.comn_title);
		right=(LinearLayout) findViewById(R.id.comn_title_right);
		rightContent=(TextView) findViewById(R.id.comn_title_right_text);
		rightIcon=(ImageView) findViewById(R.id.comn_title_right_icon);
        titleLine=(TextView) findViewById(R.id.titleLine);
		//初始默认配置
		left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				TitleView.this.activity.finish();
			}
		});
		right.setVisibility(View.INVISIBLE);
		
        //主题 | 1:深色 2:浅色
        if(BaseInitData.base_theme_Tone){
            left.setImageResource(R.mipmap.base_back_white);
            rightIcon.setImageResource(R.mipmap.base_title_right_white_icon2);
        }else{
            left.setImageResource(R.mipmap.base_back_black);
            rightIcon.setImageResource(R.mipmap.base_title_right_black_icon2);
        }
        addStatusBar();
	}

    /**
     * 添加系统标题栏
     * */
    public void addStatusBar(){
        addView(StatusBarUtil.createStatusBarView(activity, ContextCompat.getColor(activity, R.color.base_statusbar)), 0);
    }
	
    /**
     * 添加系统标题栏
     * */
    public void addStatusBar(int colorId){
        addView(StatusBarUtil.createStatusBarView(activity, ContextCompat.getColor(activity, colorId)), 0);
    }

    /**
     * 删除系统标题栏
     * */
    public void removeStatusBar(){
        removeViewAt(0);
    }
	
	/**
	 * 背景 | 设置背景透明度 
	 * */
	@SuppressLint("NewApi")
	public void setBgAlpha(int alpha){
		findViewById(R.id.title).setAlpha(alpha);
	}
	
	/**
	 * 内容 | 添加 
	 * */
	public void setContent(String text){
		content.setText(text);
	}

    /**
     * 内容 | 颜色 
     * */
    public void setContentColor(@ColorInt int colorRes){
        content.setTextColor(colorRes);
    }
	
	/**
	 * 左 | 是否显示
	 * */
	public void setLeftIsShow(boolean flag){
		if(flag){
			left.setVisibility(View.VISIBLE);
		}else{
			left.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 右 | 右侧点击按钮只显示图标
	 * @param resource 本地图标资源图片
	 * */
	public void setRightIcon(int resource) {
		right.setVisibility(View.VISIBLE);
		rightContent.setTextSize(12);
		 //右侧点击按钮图标
		rightIcon.setImageResource(resource);
		rightIcon.setVisibility(View.VISIBLE);
        //右侧点击按钮文字不显示
		rightContent.setText("");
		rightContent.setVisibility(View.GONE);
	}
	
	/**
	 * 右 | 右侧点击按钮只显示文字
	 * @param text 显示文字内容
	 * */
	public void setRightContent(String text) {
		right.setVisibility(View.VISIBLE);
		rightContent.setTextSize(12);
		//右侧点击按钮文字显示
		rightContent.setText(text);
		rightContent.setVisibility(View.VISIBLE);
		//右侧点击按钮图标
		rightIcon.setImageResource(0);
		rightIcon.setVisibility(View.GONE);
	}

    /**
     * 右 | 右侧点击按钮只显示文字
     * @param text 显示文字内容
     * @param size 字体大小
     * */
    public void setRightContent(String text,int size) {
        right.setVisibility(View.VISIBLE);
        rightContent.setTextSize(size);
        //右侧点击按钮文字显示
        rightContent.setText(text);
        rightContent.setVisibility(View.VISIBLE);
        //右侧点击按钮图标
        rightIcon.setImageResource(0);
        rightIcon.setVisibility(View.GONE);
    }
	
	/**
	 * 右 | 右侧点击按钮既显示图标又显示文字
	 * */
	public void setRightIconAndContent(int resource,String text) {
		right.setVisibility(View.VISIBLE);
		rightContent.setTextSize(12);
		//标题右侧又有图标又有文本
		rightIcon.setVisibility(View.VISIBLE);
		rightIcon.setImageResource(resource);
		rightContent.setVisibility(View.VISIBLE);
		rightContent.setText(text);
		rightContent.setTextSize(10);
	}
	
	
	/**
	 * 左 | Onclick
	 * */
	public void setLeftOnclickListener(OnClickListener l){
		left.setOnClickListener(l);
	}
	
	/**
	 * 右 | Onclick
	 * */
	public void setRightOnclickListener(OnClickListener l){
		right.setOnClickListener(l);
		rightIcon.setOnClickListener(l);
		rightContent.setOnClickListener(l);
	}

    public void setLineIsShow(boolean flag){
	    if(flag){
	        titleLine.setVisibility(View.VISIBLE);
        }else{
            titleLine.setVisibility(View.GONE);
        }
    }
	
    public TextView getLine(){
        return titleLine;
    }
	
	public TextView getRightContent(){
        return rightContent;
    }

}

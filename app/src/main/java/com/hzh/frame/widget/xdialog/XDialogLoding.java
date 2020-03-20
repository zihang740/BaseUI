package com.hzh.frame.widget.xdialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzh.frame.R;

/**
 * 加载中
 * */
public class XDialogLoding extends Dialog {

	private TextView mTextView;
	private XloadingCallBack xCallBack;//单击重载接口
	private boolean flag;//是否允许单击重载
	private LinearLayout restart;//单击重载

	public XDialogLoding(Activity activity, String msg,XloadingCallBack xCallBack,boolean flag) {
		super(activity, R.style.XLodingDialog);
		this.xCallBack=xCallBack;
		LayoutInflater in = LayoutInflater.from(activity);
		View viewDialog = in.inflate(R.layout.base_xdialog_loding, null);
		//绑定单击重载按钮单击
		restart=(LinearLayout) viewDialog.findViewById(R.id.restart);
		restart.setOnClickListener(new restartOnClick());
		//设置显示内容
		mTextView = (TextView) viewDialog.findViewById(R.id.textView);
		mTextView.setText(msg);
		setContentView(viewDialog);
		//设置弹出框效果
		getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		//动画
//		getWindow().setWindowAnimations(R.style.XLodingDialogAnim);
		// 点击对话框外部取消对话框显示
		setCanceledOnTouchOutside(false);
	}
	
	/**
	 * 点击重载单击事件
	 * */
	class restartOnClick implements View.OnClickListener{
		@Override
		public void onClick(View v) {
			if(flag){
				xCallBack.reStart();
			}
		}
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}


	public void setText(String msg) {
		mTextView.setText(msg);
	}
	
	
	public interface XloadingCallBack{
		void reStart();
	}
}

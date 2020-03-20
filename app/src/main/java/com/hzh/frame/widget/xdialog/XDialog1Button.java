package com.hzh.frame.widget.xdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hzh.frame.R;
import com.hzh.frame.comn.callback.CallBack;

/**
 * 1个按钮的选择框Dialog
 * */
public class XDialog1Button extends Dialog {
	
	private TextView content,confirm,line;
	private CallBack callbackThis;

	public XDialog1Button(Context context) {
		super(context, R.style.XSubmitDialog);
		LayoutInflater in = LayoutInflater.from(context);
		View viewDialog = in.inflate(R.layout.base_xdialog_1button, null);
		content= viewDialog.findViewById(R.id.content);
		confirm= viewDialog.findViewById(R.id.confirm);
		line= viewDialog.findViewById(R.id.line);
		confirm.setVisibility(View.GONE);
		line.setVisibility(View.GONE);
		setContentView(viewDialog);
		// 点击对话框外部取消对话框显示
		setCanceledOnTouchOutside(true);
	}

	/**
	 * @param context
	 * @param msg 提示内容
	 * @param callback 选择结果回调
	 * */
	public XDialog1Button(Context context, String msg, CallBack callback) {
		super(context, R.style.XSubmitDialog);
		callbackThis=callback;
		LayoutInflater in = LayoutInflater.from(context);
		View viewDialog = in.inflate(R.layout.base_xdialog_1button, null);
		content= viewDialog.findViewById(R.id.content);
		confirm= viewDialog.findViewById(R.id.confirm);
		confirm.setOnClickListener(view -> {
            callbackThis.onSuccess(view);
            dismiss();
        });
		content.setText(msg);
		setContentView(viewDialog);
		// 点击对话框外部取消对话框显示
		setCanceledOnTouchOutside(true);
	}
	
	public XDialog1Button setButtonName(String name){
        confirm.setText(name);
        return this;
    }
}

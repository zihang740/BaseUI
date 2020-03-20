package com.hzh.frame.widget.xdialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hzh.frame.R;
import com.hzh.frame.comn.callback.CallBack;

/**
 * 2个按钮的选择框Dialog
 * */
public class XDialog2Button extends Dialog {

    public XDialog2Button(Activity activity) {
        super(activity, R.style.XSubmitDialog);
        LayoutInflater in = LayoutInflater.from(activity);
        View viewDialog = in.inflate(R.layout.base_xdialog_2button, null);
        setContentView(viewDialog);
        // 点击对话框外部取消对话框显示
        setCanceledOnTouchOutside(true);
    }
    
    /**
     * @param msg 提示内容
     */
    public XDialog2Button setMsg(String msg){
        ((TextView)findViewById(R.id.content)).setText(msg);
        return this;
    }

    /**
     * @param okName 确定按钮名称
     * @param noName 取消按钮名称
     */
    public XDialog2Button setConfirmName(String okName,String noName){
        ((TextView) findViewById(R.id.confirm)).setText(okName);
        ((TextView) findViewById(R.id.cancel)).setText(noName);
        return this;
    }

    /**
     * @param callback 提示内容
     */
    public XDialog2Button setCallback(CallBack callback){
        findViewById(R.id.confirm).setOnClickListener(view -> {
            callback.onSuccess(null);
            dismiss();
        });
        findViewById(R.id.cancel).setOnClickListener(view -> {
            dismiss();
        });
        return this;
    }

    @Override
    public void show() {
        if (!isShowing()) {
            super.show();
        }else{
            super.dismiss();
            super.show();
        }
    }
	
}

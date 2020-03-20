package com.hzh.frame.widget.xdialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.hzh.frame.R;
import com.hzh.frame.util.Util;

public class XDialogSubmit extends Dialog {
    
    public Activity activity;

    public XDialogSubmit setMsg(String msg){
        if(!Util.isEmpty(msg)){
            ((TextView)findViewById(R.id.textView)).setText(msg);
            ((TextView)findViewById(R.id.textView)).setVisibility(View.VISIBLE);
        }else{
            ((TextView)findViewById(R.id.textView)).setVisibility(View.GONE);
        }
        return this;
    }

    public XDialogSubmit(Activity activity) {
        super(activity, R.style.XSubmitDialog);
        this.activity=activity;
		LayoutInflater in = LayoutInflater.from(activity);
		View viewDialog = in.inflate(R.layout.base_xdialog_submit, null);
		// 这里可以设置dialog的大小，当然也可以设置dialog title等
        //LayoutParams layoutParams = new LayoutParams(Util.getWindowWith(context) * 20 / 30,Util.getWindowWith(context) * 10 / 30);
        //mDialog.setContentView(viewDialog, layoutParams);
		setContentView(viewDialog);
		// 点击对话框外部取消对话框显示
		setCanceledOnTouchOutside(false);
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
    
    public XDialogSubmit alert(){
        show();
        return this;
    }

	
}

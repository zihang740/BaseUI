package com.hzh.frame.widget.xdialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.hzh.frame.R;
import com.hzh.frame.comn.callback.CallBack;

/**
 * 登录提示框Dialog
 * */
public class XDialogLogin extends Dialog {
	
    private static XDialogLogin _instance;

    public static XDialogLogin getInstance(Context context){
        synchronized(XDialogLogin.class){
            if(_instance==null){
                _instance=new XDialogLogin(context);
            } else {
                if(!_instance.getContext().getClass().equals(context.getClass())){
                    //不同活动页面
                    if (_instance.isShowing()) {
                        _instance.dismiss();
                    }
                    _instance=new XDialogLogin(context);
                }
            }
            return _instance;
        }
    }

    public XDialogLogin(Context context) {
        super(context, R.style.XSubmitDialog);
        setContentView(LayoutInflater.from(context).inflate(R.layout.base_xdialog_2button, null));
        // 点击对话框外部取消对话框显示
        setCanceledOnTouchOutside(true);
    }

    /**
     * @param msg 提示内容
     */
    public XDialogLogin setMsg(String msg){
        ((TextView) findViewById(R.id.content)).setText(msg);
        return this;
    }

    /**
     * @param okName 确定按钮名称
     * @param noName 取消按钮名称
     */
    public XDialogLogin setConfirmName(String okName,String noName){
        ((TextView) findViewById(R.id.confirm)).setText(okName);
        ((TextView) findViewById(R.id.cancel)).setText(noName);
        return this;
    }

    /**
     * @param callback 提示内容
     */
    public XDialogLogin setCallback(CallBack callback){
        findViewById(R.id.confirm).setOnClickListener(view -> {
            callback.onSuccess(view);
            dismiss();
        });
        findViewById(R.id.cancel).setOnClickListener(view -> dismiss());
        return this;
    }
}

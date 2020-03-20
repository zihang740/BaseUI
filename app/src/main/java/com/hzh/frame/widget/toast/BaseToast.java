package com.hzh.frame.widget.toast;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzh.frame.BaseInitData;
import com.hzh.frame.R;
import com.hzh.frame.util.Util;

public class BaseToast extends Toast{
    public static final String MSG_DEFAULT="服务器偷懒了";
    public static final String MSG_RE_LOGIN="RE_LOGIN";//重新登录的提示标识

    private String msg="";
    private static BaseToast _instance; 
    
    public static BaseToast getInstance(){
        if (null==_instance){
            _instance=new BaseToast();
        }
        //重置初始样式(因为是单例的Toast,所以当用户自定义setView后,此Toast就永远是用户自定义setView的样式了)
        _instance.setView(R.layout.base_view_toast);
        return _instance;
    }
    
    public BaseToast(){
        super(BaseInitData.applicationContext);
        setView(R.layout.base_view_toast);
        setDuration(Toast.LENGTH_SHORT);
        setGravity(Gravity.CENTER, 0, 0);
    }

    //需要使用新的布局可以 setView(新布局资源ID).setMsg(新布局中控件ID,显示内容)
    public BaseToast setView(@LayoutRes int layoutResID) {
        LayoutInflater inflate = (LayoutInflater) BaseInitData.applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflate.inflate(layoutResID,null);
        setView(view);
        return this;
    }
    
    public BaseToast setMsg(String msg) {
        return setMsg(R.id.content,msg);
    }

    public BaseToast setMsg(@IdRes int resID, String msg) {
        this.msg=msg;
        if(Util.isEmpty(msg)){
            msg=MSG_DEFAULT;
        }
        View view=getView().findViewById(resID);
        if (view instanceof TextView){
            ((TextView)view).setText(msg);
        }else{
            setText(msg);
        }
        return this;
    }

    public BaseToast setIcon(@IdRes int resID, @DrawableRes int drawableId) {
        View view=getView().findViewById(resID);
        if (view instanceof ImageView){
            ((ImageView)view).setImageResource(drawableId);
        }
        return this;
    }

    @Override
    public void show() {
        if(Util.isEmpty(msg)){
            //屏蔽掉空弹窗
            return;
        }
        if(MSG_RE_LOGIN.equals(msg)){
            //屏蔽掉重新登录的提示
            return;
        }
        super.show();
    }
}

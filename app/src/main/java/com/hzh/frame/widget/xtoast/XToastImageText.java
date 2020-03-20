package com.hzh.frame.widget.xtoast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hzh.frame.R;
import com.hzh.frame.util.Util;

public class XToastImageText extends Toast {
	
	public XToastImageText(Context context) {
		super(context);
	}
	
	/**
	 * @param context 当前窗口
	 * @param iconResId 显示图标资源ID
	 * @param msg 显示内容
	 * */
	public static XToastImageText makeContent (Context context,int iconResId, String msg) {
		XToastImageText toast=new XToastImageText(context);
		LayoutInflater in = LayoutInflater.from(context);
		View view = in.inflate(R.layout.base_view_toast_icon, null);
		ImageView icon=((ImageView)view.findViewById(R.id.icon));
		TextView content=((TextView)view.findViewById(R.id.content));
		if(iconResId==0){
			icon.setVisibility(View.GONE);
		}else{
			icon.setVisibility(View.VISIBLE);
			icon.setImageResource(iconResId);
		}
		if(Util.isEmpty(msg)){
			content.setVisibility(View.GONE);
		}else{
			content.setVisibility(View.VISIBLE);
			content.setText(msg);
		}
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		return toast;
	}

    /**
     * @param context 当前窗口
     * @param msg 显示内容
     * */
    public static XToastImageText makeContent (Context context, String msg) {
        XToastImageText toast=new XToastImageText(context);
        LayoutInflater in = LayoutInflater.from(context);
        View view = in.inflate(R.layout.base_view_toast_icon, null);
        TextView content=((TextView)view.findViewById(R.id.content));
        if(Util.isEmpty(msg)){
            content.setVisibility(View.GONE);
        }else{
            content.setVisibility(View.VISIBLE);
            content.setText(msg);
        }
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }

    /**
     * @param context 当前窗口
     * */
    public static XToastImageText makeContent (Context context) {
        XToastImageText toast=new XToastImageText(context);
        LayoutInflater in = LayoutInflater.from(context);
        toast.setView(in.inflate(R.layout.base_view_toast_icon, null));
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        return toast;
    }
}

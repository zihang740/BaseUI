package com.hzh.frame.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.activeandroid.Model;
import com.activeandroid.query.Delete;
import com.alibaba.android.arouter.launcher.ARouter;
import com.hzh.frame.core.BaseSP;

import java.util.ArrayList;
import java.util.List;

//关闭Activity的类
public class CloseAppUtil {

	public static List<Activity> activityList = new ArrayList<Activity>();


    /**
     * 退出APP
     * @param activity 当前Activity
     * */
    public static void closeApp(Activity activity) {
        closeApp(activity,null);
    }
    /**
     * 退出APP
     * @param activity 当前Activity
     * */
	public static void closeApp(Activity activity,Callback callback) {
		// 关闭所有Activity
		for (int i = 0; i < activityList.size(); i++) {
			if (null != activityList.get(i)) {
				activityList.get(i).finish();
			}
		}
        if(callback!=null){
            callback.complete();
        }
		//完全退出APP
		ActivityManager activityMgr = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		activityMgr.killBackgroundProcesses(activity.getPackageName());
		System.exit(0);
	}

    /**
     * 退出登录重新登录
     * @param fromActivity 当前Activity
     * @param toRouter 目标窗口路由
     * */
    public static void restartLogin(Activity fromActivity, String toRouter){
        restartLogin(fromActivity,toRouter,null);
    }

    /**
     * 退出登录重新登录
     * @param fromActivity 当前Activity
     * @param toRouter 目标窗口路由
     * @param clearUserClass  需要擦除用户数据所在的用户表Class       
     * */
    public static void restartLogin(Activity fromActivity, String toRouter, Class<? extends Model> clearUserClass){
        restartLogin(fromActivity,toRouter,clearUserClass,null);
    }
	/**
     * 退出登录重新登录
     * @param fromActivity 当前Activity
     * @param toRouter 目标窗口路由
     * @param clearUserClass  需要擦除用户数据所在的用户表Class       
     * */
	public static void restartLogin(Activity fromActivity, String toRouter, Class<? extends Model> clearUserClass,Callback callback){
        // 关闭所有Activity
        for (int i = 0; i < activityList.size(); i++) {
            if (null != activityList.get(i) && !activityList.get(i).getLocalClassName().equals(fromActivity.getLocalClassName())) {
                activityList.get(i).finish();
            }
        }
        if(clearUserClass!=null){
            new Delete().from(clearUserClass).execute();
        }
        if(toRouter!=null){
            ARouter.getInstance().build(toRouter).navigation(fromActivity);
        }
        if(callback!=null){
            callback.complete();
        }
        fromActivity.finish();
    }

    public interface Callback {
        void complete();
    }
}

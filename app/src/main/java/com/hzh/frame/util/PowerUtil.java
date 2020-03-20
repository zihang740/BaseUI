package com.hzh.frame.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

/**
 * APP权限申请
 */
public class PowerUtil {
    

    /**
     * 逐个申请未授权的APP所需的权限(申请后系统会回调一个传入Activity的onRequestPermissionsResult)
     * 获取错略位置 android.permission.ACCESS_COARSE_LOCATION，通过WiFi或移动基站的方式获取用户错略的经纬度信息，定位精度大概误差在30~1500米    
     * 获取精确位置 android.permission.ACCESS_FINE_LOCATION，通过GPS芯片接收卫星的定位信息，定位精度达10米以内   
     * */
    public static void apply(Activity activity, int requestCode,String... permissions){
        if (Build.VERSION.SDK_INT>=23) {
            if(permissions.length==0){
                permissions=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE} ;
            }
            for (int i = 0; i < permissions.length; i++) {
                if (!selectApply(activity, permissions[i])) {
                    // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
                    ActivityCompat.requestPermissions(activity, permissions, requestCode);
                    break;
                }
            }
        }
    }

    /**
     * 查询权限是否已经获取到
     * @return true 已经获取到
     * */
    public static boolean selectApply(Activity activity,String permission){
        if (ActivityCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /**
     * 查询权限是否已经全部获取到
     * @return true 已经全部获取到
     * */
    public static boolean selectApply(Activity activity,String[] permissions){
        boolean powerAllState=true;
        //判断每个权限的申请情况
        for(int i=0;i<permissions.length;i++){
            if(!selectApply(activity,permissions[i])){
                Log.i(AndroidUtil.getPackageName()+"--->授权状态查询:","授权失败 | " + permissions[i]);
                powerAllState=false;break;
            }
        }
        return powerAllState;
    }
}

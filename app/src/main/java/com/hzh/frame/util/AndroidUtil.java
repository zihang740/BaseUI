package com.hzh.frame.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.hzh.frame.BaseInitData;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Android系统相关工具类
 * @date 2017/12/14
 */

public class AndroidUtil {
    // 当前屏幕的宽
    private static int mWindowWidth = 0;
    // 当前屏幕的高
    private static int mWindowHeight = 0;
    
    
    /**
     * 判断是否是Flyme系统
     * */
    public static boolean isFlyme() {
        try {
            final Method method = Build.class.getMethod("hasSmartBar");
            return method != null;
        } catch (final Exception e) {
            return false;
        }
    }
    
    private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
    private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
    private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";
    /**
     * 判断是否是Miui系统
     * */
    public static boolean isMIUI() {
        try {
            final AndroidUtilQuote prop = AndroidUtilQuote.newInstance();
            return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
                    || prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
                    || prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
        } catch (final IOException e) {
            return false;
        }
    }


    /**
     * 获取当前屏幕的宽
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowWith(Context context) {
        if (mWindowWidth == 0) {
            DisplayMetrics dm = getWindowInfo(context);
            mWindowWidth = dm.widthPixels;
        }
        return mWindowWidth;
    }
    
    /**
     * 获取当前屏幕的信息
     * @author hzh 2015-06-07 20:40
     * */
    public static DisplayMetrics getWindowInfo(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    /**
     * 获取当前屏幕的宽
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowWith() {
        if (mWindowWidth == 0) {
            DisplayMetrics dm = getWindowInfo(BaseInitData.applicationContext);
            mWindowWidth = dm.widthPixels;
        }
        return mWindowWidth;
    }

    /**
     * 获取当前屏幕的宽
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowWith(Context context, boolean isRefresh) {
        if (mWindowWidth == 0) {
            DisplayMetrics dm = getWindowInfo(context);
            mWindowWidth = dm.widthPixels;
        } else {
            if (isRefresh) {
                DisplayMetrics dm = getWindowInfo(context);
                mWindowWidth = dm.widthPixels;
            }
        }
        return mWindowWidth;
    }

    /**
     * 获取当前屏幕的宽
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowWith(boolean isRefresh) {
        if (mWindowWidth == 0) {
            DisplayMetrics dm = getWindowInfo(BaseInitData.applicationContext);
            mWindowWidth = dm.widthPixels;
        } else {
            if (isRefresh) {
                DisplayMetrics dm = getWindowInfo(BaseInitData.applicationContext);
                mWindowWidth = dm.widthPixels;
            }
        }
        return mWindowWidth;
    }

    /**
     * 获取当前屏幕的高
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowHeight(Context context) {
        if (mWindowHeight == 0) {
            DisplayMetrics dm = getWindowInfo(context);
            mWindowHeight = dm.heightPixels;
        }
        return mWindowHeight;
    }

    /**
     * 获取当前屏幕的高
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowHeight() {
        if (mWindowHeight == 0) {
            DisplayMetrics dm = getWindowInfo(BaseInitData.applicationContext);
            mWindowHeight = dm.heightPixels;
        }
        return mWindowHeight;
    }

    /**
     * 获取当前屏幕的高
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowHeight(Context context, boolean isRefresh) {
        if (mWindowHeight == 0) {
            DisplayMetrics dm = getWindowInfo(context);
            mWindowHeight = dm.heightPixels;
        } else {
            if (isRefresh) {
                DisplayMetrics dm = getWindowInfo(context);
                mWindowHeight = dm.heightPixels;
            }
        }
        return mWindowHeight;
    }

    /**
     * 获取当前屏幕的高
     * @author hzh 2015-06-07 20:40
     * */
    public static int getWindowHeight(boolean isRefresh) {
        if (mWindowHeight == 0) {
            DisplayMetrics dm = getWindowInfo(BaseInitData.applicationContext);
            mWindowHeight = dm.heightPixels;
        } else {
            if (isRefresh) {
                DisplayMetrics dm = getWindowInfo(BaseInitData.applicationContext);
                mWindowHeight = dm.heightPixels;
            }
        }
        return mWindowHeight;
    }

    /**
     * 获取版本号(内部识别号)
     * */
    public static int getVersionCode(Context context){
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 版本名
     */
    public static String getVersionName(Context context) {
        return getPackageInfo(context).versionName;
    }
    private static PackageInfo getPackageInfo(Context context) {
        PackageInfo pi = null;

        try {
            PackageManager pm = context.getPackageManager();
            pi = pm.getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);

            return pi;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return pi;
    }

    /**
     * 获取屏幕状态栏开高度
     */
    public static int getStatusBarHeight() {
        int result = 0;
        int resourceId = BaseInitData.applicationContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = BaseInitData.applicationContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 获取AndroidManifest中设置的meta-data值
     */
    public static String getMetaData(Activity activity, String name){
        ActivityInfo info= null;
        try {
            info = activity.getPackageManager().getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            if(info != null){
                return info.metaData.getString(name);
            }else{
                return "";
            }
        }
    }

    /**
     * 判断当前应用是否是debug状态
     */
    public static boolean isApkInDebug() {
        try {
            ApplicationInfo info = BaseInitData.applicationContext.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getPackageName(){
        return BaseInitData.applicationContext.getPackageName();
    }
}

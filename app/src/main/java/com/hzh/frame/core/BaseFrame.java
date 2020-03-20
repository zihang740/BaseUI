package com.hzh.frame.core;

import android.app.Application;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.mobstat.StatService;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.hzh.frame.BaseInitData;
import com.hzh.frame.core.HttpFrame.BaseHttp;
import com.hzh.frame.core.HttpFrame.config.BaseHttpConfig;
import com.hzh.frame.util.AndroidUtil;
import com.tencent.smtt.sdk.QbSdk;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author
 * @version 1.0
 * @date 2018/5/9
 */
public class BaseFrame {

    // 程序创建的时候执行
    public static void init(Application application) {
        BaseInitData.applicationContext=application;
        //数据库框架
        ActiveAndroid.initialize(application);
        //Facebook图片请求框架
        Fresco.initialize(application);
        //请求框架
        initHttp();
        //百度移动统计可视化
        initBaiduStatistics(application);
        //SharedPreferences
        initSP(application);
        //腾讯X5
        initPreInitX5Core(application);
        //RxJava
        initRxJava();
        //Arouter
        initArouter(application);
    }

    // 程序终止的时候执行
    public static void stop() {
        ActiveAndroid.dispose();
    }
    
    public static void initHttp(){
        BaseHttpConfig config = new BaseHttpConfig.Builder()
                .baseUrl(BaseInitData.http_client_url)
                .baseWsUrl(BaseInitData.ws_client_url)
                .timeOut(20)
                .queryPath("query.do")
                .writePath("write.do")
                .build();
        BaseHttp.getInstance().init(config);
    }
    
    
    public static void initBaiduStatistics(Application application){
        // 打开调试开关，可以查看logcat日志。版本发布前，为避免影响性能，移除此代码
        // 查看方法：adb logcat -s sdkstat
//        StatService.setDebugOn(true);

        // 开启自动埋点统计，为保证所有页面都能准确统计，建议在Application中调用。
        // 第三个参数：autoTrackWebview：
        // 如果设置为true，则自动track所有webview；如果设置为false，则不自动track webview，
        // 如需对webview进行统计，需要对特定webview调用trackWebView() 即可。
        // 重要：如果有对webview设置过webchromeclient，则需要调用trackWebView() 接口将WebChromeClient对象传入，
        // 否则开发者自定义的回调无法收到。
        StatService.autoTrace(application, true, false);

        // 根据需求使用
        // StatService.autoTrace(this, true, false);
    }

    public static void initSP(Application application){
        BaseSP.getInstance().init(application);
    }

    
    public static void initPreInitX5Core(Application application) {
        //搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。

        QbSdk.PreInitCallback callback = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean arg0) {
                // TODO Auto-generated method stub
                //x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.d("app", " onViewInitFinished is " + arg0);
            }

            @Override
            public void onCoreInitFinished() {
                // TODO Auto-generated method stub
            }
        };
        //x5内核初始化接口
        QbSdk.initX5Environment(application,  callback);
    }
    
    
    public static void initRxJava(){
        //防止异常处理:RxJava OnErrorNotImplementedException
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) {
                //异常处理
            }
        });
    }
    
    public static void initArouter(Application application){
        if (AndroidUtil.isApkInDebug()) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(application);
    }
 
}

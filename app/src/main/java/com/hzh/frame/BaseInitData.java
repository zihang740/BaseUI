package com.hzh.frame;

import android.content.Context;

/**
 * 初始化数据文件(在APP的Application中重写)
 * @author hzh 2015-06-08 10:39
 */
public class BaseInitData {
  public static Context applicationContext;
    
  public static String http_client_url = "请求接口地址";
  public static String http_client_key = "请求接口地址Key";
  public static int http_client_version = 416;//请求接口号

  public static String ws_client_url = "WebSoket请求地址";
  
  public static String DownLoadUrl ="Http接口请求回参中验证接口版本,弹出的强制升级弹窗的下载链接";


  //index==0:设置图片在下载期间显示的图片
  //index==1:设置图片Uri为空或是错误的时候显示的图片
  //index==2:设置图片加载/解码过程中错误时候显示的图片
  public static int[] ImageFrameBgImage=new int[]{R.drawable.base_image_default,R.drawable.base_image_default,R.drawable.base_image_default};
  public static String ImageFrameCacheDir ="BaseUI";//用户自己重写,指在内部、外部存储设备上建立的一个文件夹,用于存储相关文件

  public static String SharedPreferencesFileName="BaseUI_SP";//BaseUI的默认SharedPreferences名称
  public static String WX_KEY="申请的微信Key";

  //主题色调 | true:深色 false:浅色
  public static boolean base_theme_Tone=true;
  //是否强制登录
  public static boolean force_login= false;
}
  

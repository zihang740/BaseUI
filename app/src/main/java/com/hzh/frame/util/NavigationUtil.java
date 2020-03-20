package com.hzh.frame.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 调用第3方导航软件
 * @author hzh
 * @version 1.0
 * @date 2017/9/6
 */

public class NavigationUtil {

    /**
     * 调用已安装的APP导航
     * @param context
     * @param lat 默认百度坐标系bd09ll
     * @param lng 默认百度坐标系bd09ll
     */
    public static void play(Context context,String lat,String lng){
        if(NavigationUtil.isAvilible(context,"com.baidu.BaiduMap")){  
            //百度地图
            try {
                Intent intent = Intent.getIntent("intent://map/direction?origin=我的位置&destination=latlng:"+lat+","+lng+"|name:我的目的地&mode=driving&src=yourCompanyName|yourAppName#Intent;scheme=bdapp;" + "package=com.baidu.BaiduMap;end");
                context.startActivity(intent); //启动调用  
            } catch (URISyntaxException e) {
                Log.e("intent", e.getMessage());
            }
        }else
        if(NavigationUtil.isAvilible(context,"com.autonavi.minimap")){
            //高德地图
            try {
                double[] gd_lng_lat=bdToGaoDe(Double.parseDouble(lat),Double.parseDouble(lng));
                lng=gd_lng_lat[0]+"";
                lat=gd_lng_lat[1]+"";
                Intent intent2 = Intent.getIntent("androidamap://route?sourceApplication=softname&sname=我的位置&dlat="+lat+"&dlon="+lng+"&dname=我的目的地&dev=0&m=0&t=1");
                context.startActivity(intent2); //启动调用  
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }else{
            //没有安装地图,调用网页版本百度地图
            Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
            context.startActivity(new Intent(Intent.ACTION_VIEW, uri));
        }
    }

    /**
     * 检查手机上是否安装了指定的软件 
     * @param context
     * @param packageName：应用包名 
     * @return
     */
    public static boolean isAvilible(Context context, String packageName){
        //获取packagemanager   
        final PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息   
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名   
        List<String> packageNames = new ArrayList<String>();
        //从pinfo中将包名字逐一取出，压入pName list中   
        if(packageInfos != null){
            for(int i = 0; i < packageInfos.size(); i++){
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE   
        return packageNames.contains(packageName);
    }

    /**
     * 高德转百度（火星坐标gcj02ll–>百度坐标bd09ll） 
     */
    private static double[] gaoDeToBaidu(double gd_lon, double gd_lat) {
        double[] bd_lat_lon = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = gd_lon, y = gd_lat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * PI);
        bd_lat_lon[0] = z * Math.cos(theta) + 0.0065;
        bd_lat_lon[1] = z * Math.sin(theta) + 0.006;
        return bd_lat_lon;
    }

    /**
     * 百度转高德（百度坐标bd09ll–>火星坐标gcj02ll）
     */
    private static double[] bdToGaoDe(double bd_lat, double bd_lon) {
        double[] gd_lng_lat = new double[2];
        double PI = 3.14159265358979324 * 3000.0 / 180.0;
        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        gd_lng_lat[0] = z * Math.cos(theta);
        gd_lng_lat[1] = z * Math.sin(theta);
        return gd_lng_lat;
    }
}

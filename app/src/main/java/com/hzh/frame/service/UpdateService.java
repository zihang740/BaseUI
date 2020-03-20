package com.hzh.frame.service;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.hzh.frame.R;
import com.hzh.frame.core.BaseSP;
import com.hzh.frame.util.FileUtil;
import com.hzh.frame.widget.toast.BaseToast;

import java.io.File;


//APK下载安装
public class UpdateService extends Service {


    /**
     * 安卓系统下载类
     **/
    private DownloadManager manager;
    /**
     * 接收下载完的广播
     **/
    private DownloadCompleteReceiver receiver;
    private String fileNetWorkUrl;//下载文件的网络地址
    private String fileSaveLocalPath;//下载后文件保存到本地的路径
    private String fileName;//文件名称
    private long downEnqueueId;//下载队列任务ID

    /**
     * 初始化下载器
     **/
    private void initDownManager() {
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //设置下载地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(fileNetWorkUrl));
        // 设置允许使用的网络类型，这里是移动网络和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        down.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(fileNetWorkUrl));
        down.setMimeType(mimeString);
        // 下载时，通知栏显示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 显示下载界面
        down.setVisibleInDownloadsUi(true);
        // 设置下载后文件存放的位置
        down.setDestinationInExternalPublicDir(fileSaveLocalPath, fileName);
        down.setTitle(this.getResources().getString(R.string.base_app_name));
        // 将下载请求放入队列
        downEnqueueId=manager.enqueue(down);
        BaseSP.getInstance().put("donwAPKEnqueueId",downEnqueueId);
        //注册下载监听广播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fileNetWorkUrl = intent.getStringExtra("fileNetWorkUrl");
        fileSaveLocalPath = intent.getStringExtra("fileSaveLocalPath");
        fileName = intent.getStringExtra("fileName");
        File file = new File(Environment.getExternalStoragePublicDirectory(fileSaveLocalPath) , fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            // 调用下载
            initDownManager();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "下载失败 "+fileNetWorkUrl, Toast.LENGTH_SHORT).show();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (receiver != null)
            // 注销下载广播
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    // 接受下载完成后的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //判断是否下载完成的广播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //获取下载的文件id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if ( downEnqueueId == downId ) {
                    //自动安装apk(注:这里暂时不使用自动安装,把安装动作放到用户点击安装)
                    //                    install(context);
                } else {
                    Toast.makeText(context, "安装失败", Toast.LENGTH_SHORT).show();
                }
                //停止服务并关闭广播
                UpdateService.this.stopSelf();
            }
        }



        /**
         * 通过隐式意图调用系统安装程序安装APK
         */
        public  void install(Context context) {
            File file = new File(Environment.getExternalStoragePublicDirectory(fileSaveLocalPath) , fileName);
            if(file.exists()){
                //不使用ACTION_VIEW会导致:安装apk时弹出“选择打开方式”让用户选择而不是直接跳转到APP安装界面
                Intent intent = new Intent(Intent.ACTION_VIEW);
                // 由于没有在Activity环境下启动Activity,设置下面的标签
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.N) { //判读版本是否在7.0以上
                    Uri apkUri = FileUtil.createFileProviderUri(file.getPath());
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                }else{
                    intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
                }
                context.startActivity(intent);
            }else {
                BaseToast.getInstance().setMsg(getApplication().getString(R.string.base_no_download_apk)).show();
            }
        }
    }

}
package com.hzh.frame.widget.xdialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.hzh.frame.R;
import com.hzh.frame.core.BaseSP;
import com.hzh.frame.service.UpdateService;
import com.hzh.frame.util.AndroidUtil;
import com.hzh.frame.util.FileUtil;
import com.hzh.frame.widget.toast.BaseToast;
import com.hzh.frame.widget.xProgressButton.ProgressButton;

import java.io.File;

/**
 * APP升级弹窗
 * 1.基础权限申请:
 *  文件读取选权限:Manifest.permission.READ_EXTERNAL_STORAGE
 *  文件写入权限:Manifest.permission.WRITE_EXTERNAL_STORAGE
 * 2.安装未知应用来源的权限
 *  必须重写所在Activity的onActivityResult,以兼容Android 8.0以上系统安装未知应用来源的权限申请回调
 *  例:
 *  @Override
 *  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 *    super.onActivityResult(requestCode, resultCode, data);
 *    if (resultCode == RESULT_OK && requestCode == XDialogUpdateAPP.INSTALL_PERMISS_CODE) {
 *       //安装未知应用来源的权限通过,安装APK
 *       if(updateAPPDialog!=null) updateAPPDialog.openApk();
 *    }
 *  }
 * 3.需要在AndroidManifest注册android.support.v4.content.FileProvider
 * */
public class XDialogUpdateAPP extends Dialog {
    
    public static final int INSTALL_PERMISS_CODE = 0xff01;

    private String fileSaveLocalPath=Environment.DIRECTORY_DOWNLOADS;//保存到本地的路径
    private String fileName="Shop7.apk";//文件名称
    
	private Activity activity;
	private String appDownUrl;//APK下载地址
    private ProgressButton progressButton;//提交进度条按钮
    private TextView content;//显示内容
    private TextView updataVersionCode;//新版本版本号


    public XDialogUpdateAPP setAppDownUrl(String appDownUrl) {
        this.appDownUrl = appDownUrl;
        return this;
    }

    public XDialogUpdateAPP setUpdataVersionCode(String updataVersionCode) {
        this.updataVersionCode.setText(updataVersionCode);
        return this;
    }
    
    public XDialogUpdateAPP setFileName(String fileName){
        this.fileName=fileName;
        return this;
    }

    /**
	 * @param activity
	 * */
	public XDialogUpdateAPP(Activity activity) {
		super(activity, R.style.XSubmitDialog);
		this.activity=activity;
        View layout = LayoutInflater.from(activity).inflate(R.layout.base_xdialog_update_app, null);
        content=layout.findViewById(R.id.content);
        updataVersionCode=layout.findViewById(R.id.updataVersionCode);
        progressButton=layout.findViewById(R.id.confirm);
        progressButton.setOnClickListener(new ConfirmOnClick());
		setContentView(layout);
        setDialogWidth();
		// 点击对话框外部取消对话框显示
		setCanceledOnTouchOutside(false);
	}

    public XDialogUpdateAPP setMsg(String msg) {
        content.setText(msg);
        return this;
    }
	
	class ConfirmOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            progressButton.setClickable(false);
            //APK下载安装
            Intent intent = new Intent(activity, UpdateService.class);
            intent.putExtra("fileNetWorkUrl",appDownUrl);//下载文件的网络地址
            intent.putExtra("fileSaveLocalPath", fileSaveLocalPath);//保存到本地的路径
            intent.putExtra("fileName", fileName);//文件名称
            activity.startService(intent);
            //注册内容观察者，实时显示进度
            DownloadChangeContentObserver downloadChangeObserver = new DownloadChangeContentObserver(mHandler);
            activity.getContentResolver().registerContentObserver(Uri.parse("content://downloads/my_downloads"), true, downloadChangeObserver);
            progressButton.setProgress(0);
            progressButton.setText("0%");
        }
    }
	
	//设置弹窗宽度
	public void setDialogWidth(){
        // 宽度全屏
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
    }
    

    @Override
    public void show() {
        if (!isShowing()) {
            super.show();
        }else{
            super.dismiss();
            super.show();
        }
    }

    //下载进度回调更新UI
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            if(msg.what == DownloadChangeContentObserver.PROGRESS){
                int progress=(int) Float.parseFloat(msg.obj.toString());
                //更新进度条进度
                progressButton.setProgress(progress);
                progressButton.setText(progress+"%");
                if(progress>=100){//加载完成
                    progressButton.setClickable(true);
                    progressButton.setButtonColor(Color.parseColor("#80cc33"));
                    progressButton.setText("点击安装");
                    progressButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            install();//安装
                        }
                    });
                }
            }
        }
    };

    //下载进度内容观察者
    class DownloadChangeContentObserver extends ContentObserver {
        public static final int PROGRESS=1;
        private Handler mHandler ;  //此Handler用来更新UI线程
        
        public DownloadChangeContentObserver(Handler handler) {
            super(handler);
            mHandler=handler;
        }

        @Override
        public void onChange(boolean selfChange) {
            DownloadManager dManager = (DownloadManager) activity.getSystemService(Context.DOWNLOAD_SERVICE);
            long donwAPKEnqueueId= BaseSP.getInstance().getLong("donwAPKEnqueueId");//下载队列任务ID
            
            Cursor cursor = dManager.query(new DownloadManager.Query().setFilterById(donwAPKEnqueueId));
            if (cursor != null && cursor.moveToFirst()) {
                String[] names=cursor.getColumnNames();
                int totalColumn = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                int currentColumn = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                Double totalSize = cursor.getDouble(totalColumn);
                Double currentSize = cursor.getDouble(currentColumn);
                Double percent =  currentSize /  totalSize;
                Double progress =  Math.floor(percent * 100);
                Message msg= new Message();
                msg.obj=progress;
                msg.what=PROGRESS;
                mHandler.sendMessage(msg);
            }
        }
    }

    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    public  void install() {
        /**
         * Andoird 8.0+申请APP所需的权限(REQUEST_INSTALL_PACKAGES:请求安装未知应用来源的权限)
         * */
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            boolean hasInstallPermission = activity.getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {//判断是否通过安装未知来源权限
                toInstallPermissionSettingIntent();
            }else{
                openApk();
            }
        }else{
            openApk();
        }
    }

    /**
     * 开启安装未知来源权限
     */
    private void toInstallPermissionSettingIntent() {
        Uri packageURI = Uri.parse("package:"+ AndroidUtil.getPackageName());
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,packageURI);
        activity.startActivityForResult(intent, INSTALL_PERMISS_CODE);
    }
    
    //打开APK
    public void openApk(){
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
            activity.startActivity(intent);
        }else {
            BaseToast.getInstance().setMsg(activity.getString(R.string.base_no_download_apk)).show();
        }
    }
	
	
}

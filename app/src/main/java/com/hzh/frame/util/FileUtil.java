package com.hzh.frame.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;

import com.hzh.frame.BaseInitData;
import com.hzh.frame.R;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 操作本地文件工具类
 * */
public class FileUtil {
	
	
	/**
	 * 保存文件到本地(默认保存到系统目录 MyConfig.ImageFrameCacheDir下,下级目录自己建立,如:fileName=head/UserHead.png则保存图片到'系统目录/head/UserHead.png')
	 * @param byteArray 数据源
	 * @param fileName 文件名称(格式xxx或者xxx.x)
	 * @return returnPath 文件保存路径
	 * */
	public static String saveLocal2ByteArray(byte[] byteArray, String fileName) {
		return saveLocal2ByteArray(byteArray,"",fileName);
	}
	
	
	/**
	 * 保存文件到本地(默认保存到系统目录 MyConfig.ImageFrameCacheDir下,下级目录自己建立,如:fileName=head/UserHead.png则保存图片到'系统目录/head/UserHead.png')
	 * @param byteArray 数据源
	 * @param subdirectories 子目录(格式:/xxx或者/xxx/xxx)
	 * @param fileName 文件名称(格式xxx或者xxx.x)
	 * @return returnPath 文件保存路径
	 * */
	public static String saveLocal2ByteArray(byte[] byteArray,String subdirectories, String fileName) {
		String returnPath;
		//项目目录地址
		File filePath = new File(Environment.getExternalStorageDirectory(), BaseInitData.ImageFrameCacheDir+subdirectories);
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			if (!filePath.exists()) {
				filePath.mkdirs();
			}
			file = new File(filePath.getPath() +file.separator+ fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
			returnPath=filePath.getPath() +file.separator+ fileName;
		} catch (Exception e) {
			e.printStackTrace();
			returnPath=null;
		} finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnPath;
	}
	
	
	/**
	 * 保存文件到本地(默认保存到系统目录 MyConfig.ImageFrameCacheDir下,下级目录自己建立,如:fileName=head/UserHead.png则保存图片到'系统目录/head/UserHead.png')
	 * @param bitmap 图片(默认图片保存到本地格式png)
	 * @param fileName 文件名称(格式xxx或者xxx.png)
	 * */
	public static String saveLocal2Bitmap(Bitmap bitmap, String fileName){
		return saveLocal2Bitmap(bitmap,"",fileName);
	}
	
	
	/**
	 * 保存文件到本地(默认保存到系统目录 MyConfig.ImageFrameCacheDir下,下级目录自己建立,如:fileName=head/UserHead.png则保存图片到'系统目录/head/UserHead.png')
	 * @param bitmap 图片(默认图片保存到本地格式png)
	 * @param subdirectories 子目录(格式:/xxx或者/xxx/xxx)
	 * @param fileName 文件名称(格式xxx或者xxx.png)
	 * */
	public static String saveLocal2Bitmap(Bitmap bitmap,String subdirectories, String fileName){
		String returnPath;
		//项目目录地址
		File filePath = new File(Environment.getExternalStorageDirectory(), BaseInitData.ImageFrameCacheDir+subdirectories);
		File file = null;
		FileOutputStream fos = null;
		try {
			if (!filePath.exists() && filePath.isDirectory()) {
				filePath.mkdirs();
			}
			file = new File(filePath.getPath() +file.separator+ fileName);
			fos = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
		    returnPath=filePath.getPath() +file.separator+ fileName;
		} catch (IOException e) {
			e.printStackTrace();
			returnPath=null;
		} finally{
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returnPath;
   }

    /**
     * 按行读取文本文件
     * @param context 上下文
     * @param fileName 文件名称
     * @return 文件内容
     */
    public static String readTextFromFile(Context context,String fileName){
        try {
            InputStreamReader reader = new InputStreamReader(context.getAssets().open(fileName));
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer("");
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
                buffer.append("\n");
            }
            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建FileProvider方式的Uri
     * 注意你的xxx.xxx.xxx.fileProvider跟AndroidManifest.xml配置的provider.authorities一致
     * @param filePath 文件路径 格式:'/storage/emulated/0/Tencent/mta/mid.txt'
     */
    public static Uri createFileProviderUri(String filePath){
        return FileProvider.getUriForFile(BaseInitData.applicationContext, AndroidUtil.getPackageName() + ".fileProvider", new File(filePath));
    }


    /**
     * 读取文件
     * @param file 待读取的文件
     */
    public static byte[] readFile(File file) {
        // 需要读取的文件，参数是文件的路径名加文件名
        if (file.isFile()) {
            // 以字节流方法读取文件

            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                // 设置一个，每次 装载信息的容器
                byte[] buffer = new byte[1024];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                // 开始读取数据
                int len = 0;// 每次读取到的数据的长度
                while ((len = fis.read(buffer)) != -1) {// len值为-1时，表示没有数据了
                    // append方法往sb对象里面添加数据
                    outputStream.write(buffer, 0, len);
                }
                // 输出字符串
                return outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(BaseInitData.applicationContext.getString(R.string.base_no_file));
        }
        return null;
    }

    /**
     * 删除指定路径文件
     * @param path 待删除的文件
     */
    public static void deleteFile(String path){
        if(path == null || "".equals(path))return;
        File file = new File(path);
        if(file.exists()){
            file.delete();
        }
    }

    /**
     * 删除指定file的文件
     * @param file 待删除的文件
     */
    public static void deleteFile(File file) {
        if (file == null || !file.exists())return;
        file.delete();
    }

    /**
     * 删除指定根目录下所有文件
     * @param root 待删除的根目录
     */
    public static void deleteAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) {
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * 检查或者创建文件夹
     * @param path 需检查的文件夹路径
     */
    public static void checkOrCreateFolder(String path){
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }



    /**
     * 获取Android文件管理器中的文件真实路径(如content://media/extenral/images/media/17766转 file://)
     * @param uri 需检查的文件夹路径
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getFilePathFromContentUri(Context context, Uri uri) {
        boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                if (id != null && id.startsWith("raw:/")) {
                    return id.replaceFirst("raw:/", "/");
                }
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = "_id=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = "_data";
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) { return "com.android.externalstorage.documents".equals(uri.getAuthority());}
    private static boolean isDownloadsDocument(Uri uri) { return "com.android.providers.downloads.documents".equals(uri.getAuthority());}
    private static boolean isMediaDocument(Uri uri) { return "com.android.providers.media.documents".equals(uri.getAuthority());}
}

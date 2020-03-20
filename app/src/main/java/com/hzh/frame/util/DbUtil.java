package com.hzh.frame.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @version 1.0
 * @date 2018/11/15
 */
public class DbUtil {

    /**
     * 打开数据库<br />
     * @param dbPath 数据库存放文件夹+数据库名
     */
    public static SQLiteDatabase openDatabase(Context context,String dbPath) {
        return SQLiteDatabase.openOrCreateDatabase(dbPath,null);
    }

    /**
     * 加载Assets数据库文件到本地数据中,根据targetVersion判断是否覆盖加载,targetVersion大于当前数据库版本才加载,否则不会加载<br />
     * 调用例子:loadDB(this,"/data/data/"+AndroidUtil.getPackageName() +"/databases/","baidu_area.db","baidu_area.db",1);
     * @param context 上下文
     * @param dbPath 被替换的数据库存放文件夹
     * @param dbName 被替换的数据库名
     * @param assetsName 替换数据库文件名
     * @param targetVersion 需要判断的目标数据库版本
     */
    public static void loadDB(Context context,String dbPath,String dbName,String assetsName,int targetVersion){
        try {
            //检查文件夹是否存在
            FileUtil.checkOrCreateFolder(dbPath);
            File dbf = new File(dbPath + dbName);
            if (dbf.exists()) {
                dbf.delete();//删除老版本的数据库
            }
            // 打开目标数据库 或者 创建一个新的空壳数据库
            SQLiteDatabase db=SQLiteDatabase.openOrCreateDatabase(dbf, null);
            if(!isLastDB(db,targetVersion)){//判断数据库是否最新版本
                // 把替换的数据库文件(Asseets中的db文件)复制到替换路径下
                copyFileByAssets(context,dbPath,dbName,assetsName);
            }
        } catch (IOException e) {
            throw new Error("加载Assets本地数据库文件("+assetsName+")到-->"+dbPath+dbName+"失败");
        }
    }


    /**
     * 判断数据库是否最新版本
     * @param db 需要进行判断的数据库
     * @param targetVersion 需要判断的目标数据库版本
     */
    public static boolean isLastDB(SQLiteDatabase db,int targetVersion){
        return db.getVersion() <= targetVersion ? false : true;
    }

    /**
     * 从本地资产文件夹(Assets)复制文件到指定文件夹中
     * (注:如果是db数据库文件,复制前需先创建一个空壳数据库(使用openOrCreateDatabase(file)即可)
     * @param context 上下文
     * @param assetsName 需要复制的Assets文件名
     * @param filePath 复制到的文件夹路径
     * @param fileName 复制后的文件名 
     */
    public static void copyFileByAssets(Context context,String filePath,String fileName,String assetsName) throws IOException {
        FileUtil.checkOrCreateFolder(filePath);
        // 打开Assets本地数据库文件作为输入流
        InputStream inputStream = context.getAssets().open(assetsName);
        // 刚刚创建的空数据库的路径
        String outFileName = filePath + fileName;
        // 打开空DB作为输出流
        OutputStream outputStream = new FileOutputStream(outFileName);
        // 将字节从输入文件传输到输出文件
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        // 关闭流文件
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    /**
     * 表数据集合转实体类数据集合:entity是用来表示T类的结构<br />
     */
    public static <T> List<T> getEntityArrayByCursor(Cursor cursor, Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        while(cursor.moveToNext()){
            try {
                T item= entityClass.newInstance();
                //获取到实体内的所有成员变量作为表字段
                for (Field field:entityClass.getDeclaredFields()){
                    //如果取得的Field是private的，那么就要调用setAccessible(true)，否则会报IllegalAccessException
                    //true:指示反射的对象在使用时应该取消 Java 语言访问检查
                    //false:指示反射的对象应该实施 Java 语言访问检查。
                    //实际上setAccessible是启用和禁用访问安全检查的开关,并不是为true就能访问为false就不能访问
                    field.setAccessible(true);
                    //字段名(有注解用注解,没注解用变量名)
                    String fieldName= field.getName();
                    //然后以列名拿到列名在游标中的位置
                    Integer columnIndex=cursor.getColumnIndex(fieldName);

                    if(columnIndex!=-1){
                        if(field.getType()==String.class){
                            //向var1对象的这个Field设置新值var2
                            field.set(item,cursor.getString(columnIndex));
                        }else
                        if(field.getType()==Double.class){
                            field.set(item,cursor.getDouble(columnIndex));
                        }else
                        if(field.getType()==double.class){
                            field.set(item,cursor.getDouble(columnIndex));
                        }else
                        if(field.getType()==Float.class){
                            field.set(item,cursor.getFloat(columnIndex));
                        }else
                        if(field.getType()==float.class){
                            field.set(item,cursor.getFloat(columnIndex));
                        }else
                        if(field.getType()==Integer.class){
                            field.set(item,cursor.getInt(columnIndex));
                        }else
                        if(field.getType()==int.class){
                            field.set(item,cursor.getInt(columnIndex));
                        }else
                        if(field.getType()==Long.class){
                            field.set(item,cursor.getLong(columnIndex));
                        }else
                        if(field.getType()==long.class){
                            field.set(item,cursor.getLong(columnIndex));
                        }else
                        if(field.getType()==Byte[].class){
                            field.set(item,cursor.getBlob(columnIndex));
                        }else
                        if(field.getType()==byte[].class){
                            field.set(item,cursor.getBlob(columnIndex));
                        }else{
                            continue;
                        }
                    }
                }
                list.add(item);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    /**
     * 判断数据库表是否存在 
     * @param dbName 库名 (例:shop7.db)
     * @param tableName 表名
     */
    public static boolean haveTable(Context context, String dbName, String tableName) {
        SQLiteDatabase db=DbUtil.openDatabase(context,"/data/data/"+ AndroidUtil.getPackageName() +"/databases/"+dbName);
        String sql = "select count(*) as c from sqlite_master where type ='table' and name ='"+tableName+"';";
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                return true;
            }
        }
        db.close();
        return false;
    }

    /**
     * 读取Assets数据库文件（.sql），并执行sql语句,一条语句一行
     * @param dbName 库名 (例:shop7.db)
     * @param assetsName assets下的*.sql文件路径，比如 acupoints.sql
     */
    public static void executeAssetsSQL(Context context,String dbName, String assetsName) {
        SQLiteDatabase db=DbUtil.openDatabase(context,"/data/data/"+ AndroidUtil.getPackageName() +"/databases/"+dbName);
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(context.getAssets().open(assetsName)));
            String line;
            String buffer = "";
            //开启事务
            db.beginTransaction();
            while ((line = in.readLine()) != null) {
                buffer += line;
                if (line.trim().endsWith(";")) {
                    db.execSQL(buffer.replace(";", ""));
                    buffer = "";
                }
            }
            //设置事务标志为成功，当结束事务时就会提交事务
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("执行Assets数据库"+assetsName+"文件异常", e.toString());
        } finally {
            //事务结束
            db.endTransaction();
            db.close();
            try {
                if (in != null)
                    in.close();
            } catch (Exception e) {
                Log.e("执行Assets数据库"+assetsName+"文件异常", e.toString());
            }
        }
    }
}

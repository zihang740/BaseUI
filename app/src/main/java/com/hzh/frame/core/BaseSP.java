package com.hzh.frame.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.hzh.frame.BaseInitData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * SharedPreferences工具类
 * 文件存储在/data/data/APP包名/shared_prefs 下的XXX.XML
 * @author hzh  2015-06-04 13:55
 * */
public class BaseSP {
	/**
	 * 保存在手机里面的文件名
	 */
    private static BaseSP _instance;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private SharedPreferencesCompat sharedPreferencesCompat;

    public static BaseSP getInstance(){
        if(_instance==null){
            _instance=new BaseSP();
        }
        return _instance;
    }
    
    public void init(Application application){
        sharedPreferences = application.getSharedPreferences(BaseInitData.SharedPreferencesFileName,Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        sharedPreferencesCompat=new SharedPreferencesCompat();
    }
	
	/**
	 * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
	 * @param key
	 * @param object
	 */
	public void put(String key, Object object) {
		if (object instanceof String) {
            editor.putString(key, (String) object);
		} else 
		if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
		} else 
		if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
		} else 
		if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
		} else 
		if (object instanceof Long) {
            editor.putLong(key, (Long) object);
		} else {
            editor.putString(key, object.toString());
		}
        sharedPreferencesCompat.apply(editor);
	}

	
	public int getInt(String key){
		return getInt(key,0);
	}
	public String getString(String key){
		return getString(key,"");
	}
	public boolean getBoolean(String key){
		return getBoolean(key,false);
	}
	public float getFloat(String key){
		return getFloat(key,0.0f);
	}
	public long getLong(String key){
		return getLong(key,0);
	}
	
	public String getString(String key,String defaultValue){
		return sharedPreferences.getString(key,defaultValue);
	}
	public int getInt(String key,int defaultValue){
		return sharedPreferences.getInt(key,defaultValue);
	}
	public boolean getBoolean(String key,boolean defaultValue){
		return sharedPreferences.getBoolean(key,defaultValue);
	}
	public float getFloat(String key,float defaultValue){
		return sharedPreferences.getFloat(key,defaultValue);
	}
	public long getLong(String key,long defaultValue){
		return sharedPreferences.getLong(key,defaultValue);
	}
	
	
	/**
	 * 移除某个key值已经对应的值
	 * @param key
	 */
	public void remove(String key) {
        editor.remove(key);
        sharedPreferencesCompat.apply(editor);
	}

	/**
	 * 清除所有数据
	 */
	public void clear() {
        editor.clear();
        sharedPreferencesCompat.apply(editor);
	}

	/**
	 * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
	 * @author hzh
	 */
	private class SharedPreferencesCompat {
		private final Method sApplyMethod = findApplyMethod();

		/**
		 * 反射查找apply的方法
		 * @return
		 */
		@SuppressWarnings({ "unchecked", "rawtypes" })
		private Method findApplyMethod() {
			try {
				Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
			} catch (NoSuchMethodException e) {
			}
			return null;
		}

		/**
		 * 如果找到则使用apply执行，否则使用commit
		 */
		public void apply(SharedPreferences.Editor editor) {
			try {
				if (sApplyMethod != null) {
					sApplyMethod.invoke(editor);
				}
			} catch (IllegalArgumentException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
            editor.commit();
		}
	}
}

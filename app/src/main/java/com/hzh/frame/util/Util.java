package com.hzh.frame.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.google.gson.Gson;
import com.hzh.frame.BaseInitData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Util {

	/**
	 * 判空
	 * 
	 * @author hzh 2015-06-07 20:40
	 * */
	public static boolean isEmpty(Object value) {
		if (value == null) {
			return true;
		}
		if ("".equals(value.toString()) || "null".equals(value.toString())) {
			return true;
		}
		return false;
	}
	

	/**
	 * 获得系统的最新时间
	 */
	public static String getNewTime(String format) {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(System.currentTimeMillis());
	}
	
	/**
	 * date1>date2
	 * @param format yyyy-MM-dd HH:mm:ss
	 */
	public static boolean dateCompare(String date1,String date2,String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		java.util.Date left = null;
		java.util.Date right = null;
		try {
			left=sdf.parse(date1);
			right=sdf.parse(date2);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return left.before(right);
	}
	
	/**
	 * 获得系统的最新时间 延后一个月
	 */
	public static String getNewTimeNextMonth(String format) {
		Calendar calendar = Calendar.getInstance();
		java.util.Date data=new java.util.Date(System.currentTimeMillis());
        calendar.setTime(data);
        calendar.add(Calendar.MONTH, 1);
        data = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.format(data);
	}


	/**
	 * @param strTime 时间毫秒数
	 * @param format 需要返回的时间字符串格式
	 * */
	public static String long2DateString(String strTime, String format) {
		return new SimpleDateFormat(format).format(new Date(Long
				.parseLong(strTime)));
	}

	/**
	 * @param strTime 时间毫秒数
	 * */
	public static Date long2Date(long strTime) {
		return new Date(strTime);
	}
	
	/**
	 * @param strTime 时间毫秒数
	 * @param format 需要返回的时间格式(yyyy-MM-dd HH:mm:ss)
	 * */
	public static String long2Date(String strTime,String format) {
		return new SimpleDateFormat(format).format(new Date(Long.parseLong(strTime)));
	}
	
	
	/**
	 * 判断是否是数字[0-9]
	 * @param content 需要判断的内容
	 * */
	public static boolean isNumber(String content) {
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是数字[0-9]并且能被100整除
	 * @param content 需要判断的内容
	 * */
	public static boolean isNumber100(String content) {
		Pattern p = Pattern.compile("[0-9]*");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			if (Integer.parseInt(content) % 100 == 0) {
				return true;
			}
		}
		return false;

	}

	/**
	 * 判断是否是字母[a-zA-Z]
	 * @param content 需要判断的内容
	 * */
	public static boolean isChar(String content) {
		Pattern p = Pattern.compile("[a-zA-Z]");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断是否是中文[\u4e00-\u9fa5]
	 * @param content 需要判断的内容
	 * */
	public static boolean isChinese(String content) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(content);
		if (m.matches()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保留前五位显示数字的数值进位
	 * */
	public static String[] numberCarry(long number) {
		String[] result = new String[2];
		if (number / 100000000 > 0) {
			result[0] = (number / 10000) + "";
			result[1] = "万人";
		} else if (number / 10000000 > 0) {
			result[0] = (number / 1000) + "";
			result[1] = "千人";
		} else if (number / 1000000 > 0) {
			result[0] = (number / 100) + "";
			result[1] = "百人";
		} else if (number / 100000 > 0) {
			result[0] = (number / 10) + "";
			result[1] = "十人";
		} else {
			result[0] = number + "";
			result[1] = "人";
		}

		return result;
	}

	public static String getChannel(Context context) {
        ApplicationInfo appinfo = context.getApplicationInfo();
        String sourceDir = appinfo.sourceDir;
        String ret = "";
        ZipFile zipfile = null;
        try {
            zipfile = new ZipFile(sourceDir);
            Enumeration<?> entries = zipfile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = ((ZipEntry) entries.nextElement());
                String entryName = entry.getName();
                if (entryName.startsWith("META-INF/mtchannel")) {
                    ret = entryName;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipfile != null) {
                try {
                    zipfile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] split = ret.split("_");
        if (split != null && split.length >= 2) {
            return ret.substring(split[0].length() + 1,ret.length()-1);
        } else {
            return "";
        }
    }
	
	/**
	 * @param date  yyyy-mm-dd
	 * */
	public static String getWeek(java.util.Date date) {
        String Week = "周\u0020\u0020";   
        Calendar c = Calendar.getInstance();
        try {
          c.setTime(date);
        } catch (Exception e) {
          e.printStackTrace();
        }
        switch(c.get(Calendar.DAY_OF_WEEK)){
        case 1:
            Week += "末";
            break;
        case 2:
            Week += "一";
            break;
        case 3:
            Week += "二";
            break;
        case 4:
            Week += "三";
            break;
        case 5:
            Week += "四";
            break;
        case 6:
            Week += "五";
            break;
        case 7:
            Week += "六";
            break;
        default:
            break;          
        }           
        return Week;
	}
	
	public static String getWeekDay(java.util.Date date) {
        String Week = "星期";   
        Calendar c = Calendar.getInstance();
        try {
          c.setTime(date);
        } catch (Exception e) {
          e.printStackTrace();
        }
        switch(c.get(Calendar.DAY_OF_WEEK)){
        case 1:
            Week += "日";
            break;
        case 2:
            Week += "一";
            break;
        case 3:
            Week += "二";
            break;
        case 4:
            Week += "三";
            break;
        case 5:
            Week += "四";
            break;
        case 6:
            Week += "五";
            break;
        case 7:
            Week += "六";
            break;
        default:
            break;          
        }           
        return Week;
	}
	
	
	/** 
	 * 计算地球上任意两点(经纬度)距离 
	 * @param long1Str 第一点经度 
	 * @param lat1Str 第一点纬度 
	 * @param long2Str 第二点经度 
	 * @param lat2Str 第二点纬度 
	 * @return 返回距离 单位：米 
	 */  
	public static long Distance(String long1Str, String lat1Str, String long2Str, String lat2Str) {  
	    double a, b, R,long1, lat1, long2, lat2; 
	    long1=Double.parseDouble(long1Str);
	    lat1=Double.parseDouble(lat1Str);
	    long2=Double.parseDouble(long2Str);
	    lat2=Double.parseDouble(lat2Str);
	    R = 6378137; // 地球半径  
	    lat1 = lat1 * Math.PI / 180.0;  
	    lat2 = lat2 * Math.PI / 180.0;  
	    a = lat1 - lat2;  
	    b = (long1 - long2) * Math.PI / 180.0;  
	    double d;  
	    double sa2, sb2;  
	    sa2 = Math.sin(a / 2.0);  
	    sb2 = Math.sin(b / 2.0);  
	    d = 2 * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));  
	    return Long.parseLong((d+"").substring(0,(d+"").indexOf(".")));  
	}
	
	/**
	 * 自适应 转 km或m
	 * */
	public static String mTokm(long m){
		if(m<1000){
			return m+"m";
		}else{
			if(m%1000==0){
				return (m/1000)+"km";
			}else{
				return (m/1000)+"."+(m%1000/100)+"km";
			}
		}
	}
	
	/**
	 * 接口加密
	 * */
	public static String createHttpKey(String value) {
		return MD5.md5((MD5.md5(value) + BaseInitData.http_client_key));
	}

    /**
     * 接口加密
     * */
    public static String urlEncode(HashMap<String,Object> param) {
        String encode=null;
        try {
            encode=URLEncoder.encode(new Gson().toJson(param),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encode;
    }

    /**
     * 接口加密
     * */
    public static JSONObject createV2HttpKey(JSONObject paramsMap,JSONObject params) {
        try {
            String key="";
            String sign="";
            Iterator<String> it = params.keys();
            while(it.hasNext()){
                String name = it.next();
                String value = params.getString(name);
                key = key + "," + name;
                sign = sign + "+" + value;
            }
            key=key.replaceFirst(",","");
            sign=sign.replaceFirst("\\+","")+"+"+BaseInitData.http_client_key;
            sign=MD5.encode(sign,"UTF-8").toUpperCase();
            sign=Sha1Util.encryptToSHA(sign).toUpperCase();
            paramsMap.put("key",key);
            paramsMap.put("sign",sign);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return paramsMap;
        }
    }

    /**
     * 接口加密
     * */
    public static String createV2Sign(String value) {
        return MD5.md5((MD5.md5(value) + BaseInitData.http_client_key));
    }
	
	
    /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }

    public static int spToPx(Context context,float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    
    /** 
     * @param time 时间毫秒数
     */  
    public static String long2HHmmss(long time){
    	long HH=time/1000/3600;
    	long mm=time/1000%3600/60;
    	long ss=time/1000%3600%60;
    	return HH+":"+mm+":"+ss;
    } 
    
    
	/**
	 * 判断是否是电子邮箱格式
	 * */
	public static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

    /**
     * 浮点数显示格式(默认保留小数点后面1位)
     * */
    public static String doubleFormat1(String value) {
        if(Util.isEmpty(value)){
            value="0.0";
        }
        return doubleFormat(Double.parseDouble(value.trim()),"#0.0");
    }
	
	/**
	 * 浮点数显示格式(默认保留小数点后面2位)
	 * */
	public static String doubleFormat(String value) {
        if(Util.isEmpty(value)){
            value="0.00";
        }
		return doubleFormat(Double.parseDouble(value.trim()),"#0.00");
	}
	/**
	 * 浮点数显示格式(默认保留小数点后面2位)
	 * */
	public static String doubleFormat(String value,String format) {
        if(Util.isEmpty(value)){
            value="0.00";
        }
		return doubleFormat(Double.parseDouble(value.trim()),format);
	}
	
	/**
	 * 浮点数显示格式(默认保留小数点后面2位)
	 * */
	public static String doubleFormat(Double value) {
		return doubleFormat(value,"#0.00");
	}
	
	/**
	 * 浮点数显示格式(默认保留小数点后面2位)
	 * */
	public static String doubleFormat(Double value,String format) {
		return new DecimalFormat(format).format(value);
	}

 
}

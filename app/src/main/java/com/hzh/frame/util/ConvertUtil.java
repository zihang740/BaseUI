package com.hzh.frame.util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * @author
 * @version 1.0
 */

public class ConvertUtil extends Util{

    /**
     * jsonArray集合转实体类数据集合:entity是用来表示T类的结构<br />
     */
    public static <T> List<T> getEntityArrayByJsonArray(JSONArray jsonArray, Class<T> entityClass) {
        List<T> list = new ArrayList<>();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.optJSONObject(i);
                try {
                    T model = entityClass.newInstance();
                    //获取到实体内的所有成员变量作为表字段
                    for (Field field : entityClass.getDeclaredFields()) {
                        //如果取得的Field是private的，那么就要调用setAccessible(true)，否则会报IllegalAccessException
                        //true:指示反射的对象在使用时应该取消 Java 语言访问检查
                        //false:指示反射的对象应该实施 Java 语言访问检查。
                        //实际上setAccessible是启用和禁用访问安全检查的开关,并不是为true就能访问为false就不能访问
                        field.setAccessible(true);
                        //字段名(有注解用注解,没注解用变量名)
                        String fieldName = field.getName();
                        if (field.getType() == String.class) {
                            //向var1对象的这个Field设置新值var2
                            field.set(model, jsonObject.optString(fieldName));
                        } else if (field.getType() == Double.class) {
                            field.set(model, jsonObject.optDouble(fieldName));
                        } else if (field.getType() == double.class) {
                            field.set(model, jsonObject.optDouble(fieldName));
                        } else if (field.getType() == Float.class) {
                            field.set(model, jsonObject.optDouble(fieldName));
                        } else if (field.getType() == float.class) {
                            field.set(model, jsonObject.optDouble(fieldName));
                        } else if (field.getType() == Integer.class) {
                            field.set(model, jsonObject.optInt(fieldName));
                        } else if (field.getType() == int.class) {
                            field.set(model, jsonObject.optInt(fieldName));
                        } else if (field.getType() == Long.class) {
                            field.set(model, jsonObject.optLong(fieldName));
                        } else if (field.getType() == long.class) {
                            field.set(model, jsonObject.optLong(fieldName));
                        } else if (field.getType() == Boolean.class) {
                            field.set(model, jsonObject.optBoolean(fieldName));
                        } else if (field.getType() == boolean.class) {
                            field.set(model, jsonObject.opt(fieldName));
                        } else {
                            continue;
                        }
                    }
                    list.add(model);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }
    
}
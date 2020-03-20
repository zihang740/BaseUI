package com.hzh.frame.comn.annotation;

import com.activeandroid.Model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查询数据库表信息
 * */
@Target(ElementType.FIELD)//用于描述域
@Retention(RetentionPolicy.RUNTIME)//在运行时有效（即运行时保留)
public @interface SelectTable {
    Class<? extends Model> table();
    //true:只查询第一条 false:查询所有
    boolean isSingle() default true;
    String sqlWhere() default "1=1";
}

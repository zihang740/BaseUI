package com.hzh.frame.comn.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 定在注解之上
 * @version 1.0
 * @date 2018/6/4
 */
@Retention(RetentionPolicy.RUNTIME)
//该注解在另一个注解上使用
@Target(ElementType.ANNOTATION_TYPE)
public @interface EventBase {
    //事件3要素
    //1:事件订阅setOnClickListener
    String ListenerSetter();
    
    //2:事件源OnClickListener
    Class<?> ListenerType();

    //2:事件
    String CallbackMethod();
}

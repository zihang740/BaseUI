package com.hzh.frame.comn.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 事件IOC注入的动态代理
 * */
public class ListenerInvocationHandler implements InvocationHandler {
    
    //Activity
    private Object activity;
    //OnClickListener
    private Method activityMethod;

    public ListenerInvocationHandler(Object activity, Method activityMethod) {
        this.activity = activity;
        this.activityMethod = activityMethod;
    }

    /**
     * @param proxy:　指代我们所代理的那个真实对象
     * @param method:　指代的是我们所要调用真实对象的某个方法的Method对象
     * @param args:　指代的是调用真实对象某个方法时接受的参数
     * */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return activityMethod.invoke(activity,args);
    }
}

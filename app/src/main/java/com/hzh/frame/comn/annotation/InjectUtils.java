package com.hzh.frame.comn.annotation;

import android.view.View;

import com.activeandroid.query.Select;
import com.hzh.frame.comn.proxy.ListenerInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 自写注解
 * */
public class InjectUtils {

    /**
     * 注入布局
     * */
    public static void injectLayout(Object context){
        Class<?> clazz=context.getClass();
        ContentView setContentView= clazz.getAnnotation(ContentView.class);
        if(setContentView!=null){
            int layoutId=setContentView.value();
            if(layoutId>0){
                try {
                    Method method=clazz.getMethod("setContentView",int.class);
                    try {
                        method.invoke(context,layoutId);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 注入控件
     * */
    public static void injectView(Object context){
        try{
            Class<?> clazz=context.getClass();
            Field[] fields=clazz.getDeclaredFields();
            for(Field field :fields){
                if(field.isAnnotationPresent(ViewInject.class)){
                    //findViewById标注
                    ViewInject findId=field.getAnnotation(ViewInject.class);
                    int viewId=findId.value();
                    if(viewId>0){
                        Method method=clazz.getMethod("findViewById",int.class);
                        //反射访问私有成员，必须加上这句
                        field.setAccessible(true);
                        //然后对这个属性复制
                        field.set(context, method.invoke(context,viewId));
                    }
                } else
                if(field.isAnnotationPresent(SelectTable.class)){
                    //inflate标注
                    SelectTable selectTable=field.getAnnotation(SelectTable.class);
                    Class tableClass=selectTable.table();
                    String sqlWhere=selectTable.sqlWhere();
                    boolean isSingle=selectTable.isSingle();
                    //反射访问私有成员，必须加上这句
                    field.setAccessible(true);
                    //然后对这个属性复制
                    if(isSingle){
                        field.set(context, new Select().from(tableClass).where(sqlWhere).executeSingle());
                    }else{
                        field.set(context, new Select().from(tableClass).where(sqlWhere).execute());
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 注入事件
     * */
    public static void injectListener(Object context){
        Class<?> clazz=context.getClass();
        //获取类中所有方法
        Method[] methods=clazz.getDeclaredMethods();
        for (Method method:methods){
            //获取方法中所有注解
            Annotation[] annotations=method.getAnnotations();
            for(Annotation annotation:annotations){
                //获取注解上的所有注解
                Class<?> annotationType=annotation.annotationType();
                //获取注解上的EventBase注解
                EventBase eventBase=annotationType.getAnnotation(EventBase.class);
                if(eventBase==null){
                    //为找到EventBase元注解,跳过本次循环
                    continue;//跳过本次,进入下一个注解
                }
                //找到EventBase注解并获取事件3要素
                String listenerSetter = eventBase.ListenerSetter();
                Class<?> listenerType=eventBase.ListenerType();
                String listenerCallback=eventBase.CallbackMethod();
                try {
                    //这里无法用annotation.value,因为这是一个通用事件注入,无法具体确认annotation的类型,所以这里只能通过反射去获取当前注解的value方法
                    Method valueMethod=annotationType.getDeclaredMethod("value");
                    //通过反射调用获取到的value方法
                    int[] valueId= (int[]) valueMethod.invoke(annotation);
                    for(int id:valueId){
                        //通过反射获取到当前Activity的findViewById方法
                        Method findViewById=clazz.getMethod("findViewById",int.class);
                        //调用findViewById获取到对应ID的View
                        View view= (View) findViewById.invoke(context,id);
                        if(view==null){
                            //为找到id对应的View,跳过本次循环
                            continue;
                        }
                        //获取到当前View的事件订阅方法
                        Method subscribeMethod = view.getClass().getMethod(listenerSetter, listenerType);

                        //创建一个动态代理类(根据当前Activity和当前注解的作用方法)
                        ListenerInvocationHandler listenerInvocationHandler = new ListenerInvocationHandler(context, method);
                        //loader:　　一个ClassLoader对象，定义了由哪个ClassLoader对象来对生成的代理对象进行加载
                        //interfaces:　　一个Interface对象的数组，表示的是我将要给我需要代理的对象提供一组什么接口，如果我提供了一组接口给它，那么这个代理对象就宣称实现了该接口(多态)，这样我就能调用这组接口中的方法了
                        //h:　　一个InvocationHandler对象，表示的是当我这个动态代理对象在调用方法的时候，会关联到哪一个InvocationHandler对象上
                        //创建一个动态代理对象:需要代理的事件源(如:View.OnClickListener.class这个事件源),代理出来的代理对象就是实现了listenerType这个接口的实现类,可强转为listenerType
                        Object proxy= Proxy.newProxyInstance(listenerType.getClassLoader(), new Class[]{listenerType}, listenerInvocationHandler);
                        //调用当前View的事件订阅方法完成订阅过程,而订阅的对象则用了一个proxy动态代理替换,所有执行的listenerType都先调用到listenerInvocationHandler的invoke方法
                        subscribeMethod.invoke(view, proxy);
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
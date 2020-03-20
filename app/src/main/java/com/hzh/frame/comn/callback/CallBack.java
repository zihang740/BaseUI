package com.hzh.frame.comn.callback;


/**
 * 回调接口
 */
public abstract class CallBack<T> {
    public abstract void onSuccess(T t);
    public void onFail(T t){};
}

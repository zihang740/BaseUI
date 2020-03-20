package com.hzh.frame.widget.rxbus;

import com.google.gson.Gson;

/**
 * @author
 * @version 1.0
 * @date 2019/2/24
 */
public class MsgEvent<T> {
    
    private T msg;
    private String tag;

    public MsgEvent(T msg) {
        this.msg = msg;
        this.tag = "mainLine";
    }

    public MsgEvent(String tag,T msg) {
        this.msg = msg;
        this.tag = tag;
    }

    public T getMsg() {
        return msg;
    }

    public MsgEvent setMsg(T msg) {
        this.msg = msg;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public MsgEvent setTag(String tag) {
        this.tag = tag;
        return this;
    }
    
    public String toString(){
        return new Gson().toJson(this);
    }
}

package com.hzh.frame.core.HttpFrame.api;

import com.google.gson.annotations.SerializedName;

/**
 * @author
 * @version 1.0
 * @date 2018/5/22
 */

public class ApiResult<T> {
    private int result;
    private String msg;
    @SerializedName("data")
    private Body<T> body;

    public static class Body<T> {
        public T data;
        public int code;
        public String msg;
    }

    public boolean isSuccess(){
        return result >= 1;
    }

    public int getResult() {
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return body.data;
    }

}


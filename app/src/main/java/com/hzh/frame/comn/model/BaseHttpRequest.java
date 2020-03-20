package com.hzh.frame.comn.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Http请求参数
 * */
@Table(name = "BaseHttpRequest")
public class BaseHttpRequest extends Model {
    
	@Column(name="port")//接口编号
	private String port;
    
	@Column(name="state")//状态 1:请求完成 2:正在请求中
	private int state;

    @Column(name="type")//类型 1:读请求 2:写请求
    private int type;
    
    @Column(name="requestParams")//请求参数
    private String requestParams;

    @Column(name="responseParams")//返回参数
    private String responseParams;

    public BaseHttpRequest setPort(String port) {
        this.port = port;
        return this;
    }

    public BaseHttpRequest setState(int state) {
        this.state = state;
        return this;
    }

    public BaseHttpRequest setType(int type) {
        this.type = type;
        return this;
    }

    public BaseHttpRequest setRequestParams(String requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public BaseHttpRequest setResponseParams(String responseParams) {
        this.responseParams = responseParams;
        return this;
    }

    public int getState() {
        return state;
    }
}

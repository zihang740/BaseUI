package com.hzh.frame.comn.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Http请求
 * */
@Table(name = "BaseHttpCache")
public class BaseHttpCache extends Model {
    
	@Column(name="port")//接口编号
	private String port;

    @Column(name="type")//类型 0:读请求 1:写请求
    private int type;

    @Column(name = "page")//页码
    private int page;

    @Column(name="requestParams")//请求参数
    private String requestParams;

    @Column(name="responseParams")//返回参数
    private String responseParams;

    @Column(name="lastTime")//最后一次更新时间
    private long lastTime;
    

    public BaseHttpCache setPort(String port) {
        this.port = port;
        return this;
    }

    public BaseHttpCache setType(int type) {
        this.type = type;
        return this;
    }

    public BaseHttpCache setPage(int page) {
        this.page = page;
        return this;
    }

    public BaseHttpCache setRequestParams(String requestParams) {
        this.requestParams = requestParams;
        return this;
    }

    public BaseHttpCache setResponseParams(String responseParams) {
        this.responseParams = responseParams;
        return this;
    }

    public BaseHttpCache setLastTime(long lastTime) {
        this.lastTime = lastTime;
        return this;
    }

    public String getResponseParams() {
        return responseParams;
    }
}

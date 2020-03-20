package com.hzh.frame.comn.callback;

import com.hzh.frame.core.WsFrame.WsStatus;

import okhttp3.Response;
import okhttp3.WebSocket;

/**
 * @author
 * @version 1.0
 * @date 2020/2/2
 */
public abstract class WsCallBack{

    public static final String TAG="WsCallBack";

    //返回数据是否是Gzip格式
    private boolean isGzip = false;

    //是否断线重连
    private boolean isReConnect = true;

    //断线重连步长是否叠加(达到重连时间间隔越来越长的效果,按reConnectStepSecond累加增长)
    private boolean isReConnectSuperpositionStep = true;

    //断线重连步长,多就重连一次(单位:秒)
    private int reConnectStepSecond = 5;

    //当前连接状态
    private String state = WsStatus.CONNECTING;


    public void onOpen(WebSocket webSocket, Response response) {
    }

    public abstract void onMessage(WebSocket webSocket, String message);

    /**
     * 重新连接
     * @param newWebSocket 新的WebSocket连接
     * */
    public void onReConnect(WebSocket newWebSocket) {}


    public boolean getIsGzip() {
        return isGzip;
    }

    public WsCallBack setIsGzip(boolean gzip) {
        isGzip = gzip;
        return this;
    }

    public boolean getIsReConnect() {
        return isReConnect;
    }

    public WsCallBack setIsReConnect(boolean reConnect) {
        isReConnect = reConnect;
        return this;
    }

    public String getState() {
        return state;
    }

    public WsCallBack setState(String state) {
        this.state = state;
        return this;
    }

    public boolean getIsReConnectSuperpositionStep() {
        return isReConnectSuperpositionStep;
    }

    public WsCallBack setIsReConnectSuperpositionStep(boolean reConnectSuperpositionStep) {
        isReConnectSuperpositionStep = reConnectSuperpositionStep;
        return this;
    }

    public int getReConnectStepSecond() {
        return reConnectStepSecond;
    }

    public WsCallBack setReConnectStepSecond(int reConnectStepSecond) {
        this.reConnectStepSecond = reConnectStepSecond;
        return this;
    }
}


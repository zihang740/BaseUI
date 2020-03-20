package com.hzh.frame.core.WsFrame;

import com.hzh.frame.BaseInitData;
import com.hzh.frame.comn.callback.WsCallBack;
import com.hzh.frame.core.HttpFrame.BaseHttp;

import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;

/**
 * @author
 * @version 1.0
 * @date 2020/2/26
 */
public class BaseWs {
    WebSocket webSocket;
    WsCallBack wsCallBack;

    //创建连接
    public static BaseWs connect(WsCallBack callback){
        return connect(BaseInitData.ws_client_url,callback);
    }
    
    //创建连接
    public static BaseWs connect(String url, WsCallBack callback){
        WebSocket webSocket= BaseHttp.getInstance().getConfig().getClient().newWebSocket(new Request.Builder().url(url).build(), new WsListener(callback.setState(WsStatus.CONNECTING)));
        if(webSocket==null || callback==null){
            return null;
        }else{
            return new BaseWs(webSocket,callback);
        }
    }

    //不允许外部new
    private BaseWs(){}
    //不允许外部new
    private BaseWs(WebSocket webSocket, WsCallBack wsCallBack){
        this.webSocket=webSocket;
        this.wsCallBack=wsCallBack;
    }

    //更换新的WebSocket,主要用于断线重连业务,获取新的socket连接
    public BaseWs setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
        return this;
    }

    //获取连接状态
    public String getState(){
        return wsCallBack.getState();
    }
    
    //发送消息
    public void send(String msg){
        webSocket.send(msg);
    }

    //发送消息
    public void send(ByteString msg){
        webSocket.send(msg);
    }

    //关闭连接
    public void close(){
        close(WsStatus.CLOSE, WsStatus.DISCONNECTED_ACTIVE);
    }

    //关闭连接
    public void close(int code, String reason){
        if(WsStatus.DISCONNECTED_PASSIVE.equals(reason)){
            wsCallBack.setIsReConnect(true);//关闭断线重连机制
            wsCallBack.setState(WsStatus.DISCONNECTED_PASSIVE);
        }else
        if(WsStatus.DISCONNECTED_ACTIVE.equals(reason)){
            wsCallBack.setIsReConnect(false);//关闭断线重连机制
            wsCallBack.setState(WsStatus.DISCONNECTED_ACTIVE);
        }
        webSocket.close(code,reason);//关闭连接
    }
}

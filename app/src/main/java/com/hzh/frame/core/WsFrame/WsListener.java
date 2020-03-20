package com.hzh.frame.core.WsFrame;

import android.util.Log;

import com.hzh.frame.comn.callback.WsCallBack;
import com.hzh.frame.core.HttpFrame.BaseHttp;
import com.hzh.frame.tools.GZIPTools;
import com.hzh.frame.util.AndroidUtil;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * 默认带心跳处理的listener
 */
public class WsListener extends WebSocketListener {

    public final static String TAG = "WsListener";
    public final static String MESSAGE_TYPE_PING = "WsListener_message_type_ping";
    public long reConnectCount = 0 ; //一次重连周期内,已完成的断线重连计数

    public WsCallBack callBack;

    public WsListener(){}
    public WsListener(WsCallBack callBack){
        this.callBack=callBack;
    }

    //当服务器建立好了连接之后，会调用此方法，一般就是在这里做登录/鉴权操作
    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        reConnectCount= 0 ;
        if(AndroidUtil.isApkInDebug()) Log.i(TAG,"连接成功 ---> "+webSocket.request().url());
        if(callBack!=null){
            callBack.setState(WsStatus.CONNECTED);
            callBack.onOpen(webSocket,response);
        }
    }

    //收到了服务器发送的消息，text就是收到的消息
    @Override
    public void onMessage(WebSocket webSocket, String text) {
        if(callBack!=null){
            String message=heartbeatHandle(webSocket,text);
            if(!message.equals(MESSAGE_TYPE_PING)){
                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"接收到消息 ---> "+message);
                Flowable.just(message)
                        .onBackpressureBuffer()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> callBack.onMessage(webSocket,response));
            }
        }
    }

    //收到了服务器发送的消息，bytes就是收到的消息
    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        if(callBack!=null){
            String message=heartbeatHandle(webSocket,bytes);
            if(!message.equals(MESSAGE_TYPE_PING)) {
                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"接收到消息 ---> "+message);
                Flowable.just(message)
                        .onBackpressureBuffer()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(response -> callBack.onMessage(webSocket,response));
            }
        }
    }

    //远程端 | 当远程端暗示没有数据交互时回调（即此时准备关闭，但连接还没有关闭）
    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        if(AndroidUtil.isApkInDebug()) Log.i(TAG,"关闭中 ---> "+webSocket.request().url());
        //服务端断开链接是不会去调用onClosed()的,这里我们需要本地再手动调用下
        webSocket.close(code, reason);
    }

    //本地 | 关闭完成后会调用此方法，一般是主动关闭后会调用
    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        if(callBack!=null){
            if(WsStatus.DISCONNECTED_ACTIVE.equals(reason)){
                callBack.setState(WsStatus.DISCONNECTED_ACTIVE);
                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"已关闭 ---> [state:主动关闭]"+webSocket.request().url());
            }else{
                callBack.setState(WsStatus.DISCONNECTED_PASSIVE);
                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"已关闭 ---> [state:被动关闭]"+webSocket.request().url());
                onReConnect(webSocket.request());//被动关闭,启动断线重新连接处理
            }
        }
    }

    //连接报错的时候会调用，如果需要重连的话一般就是在这里进行重连逻辑
    //出错原因有很多可能，比如本地网络因素，服务器报错之类，连接失败，发送失败，都会走这里
    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        if(callBack!=null){
            callBack.setState(WsStatus.DISCONNECTED_PASSIVE);
        }
        if(AndroidUtil.isApkInDebug()) Log.i(TAG,"连接异常 ---> [state:被动关闭]"+webSocket.request().url());
        onReConnect(webSocket.request());//被动关闭,启动断线重新连接处理
    }

    //心跳处理
    public String heartbeatHandle(WebSocket webSocket, ByteString message){
        String messageStr="";
        try {
            if(callBack.getIsGzip()){//GZIP解压
                messageStr=new String(GZIPTools.uncompress(message.toByteArray()),"utf-8");
            }else{
                messageStr=new String(message.toByteArray(),"utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return heartbeatHandle(webSocket,messageStr);
    }

    //心跳处理
    public String heartbeatHandle(WebSocket webSocket, String message){
        if(message.startsWith("{\"ping\":")){
            if (AndroidUtil.isApkInDebug()) Log.i(TAG,"ping：" + message);
            String pong=message.replace("ping","pong");
            webSocket.send(pong);
            if (AndroidUtil.isApkInDebug()) Log.i(TAG,"pong：" + pong);
            return MESSAGE_TYPE_PING;
        } else {
            return message;
        }
    }

    //断线重新连接处理
    public void onReConnect(Request request){
        if(callBack != null){
            if(callBack.getIsReConnect() && callBack.getState() != WsStatus.DISCONNECTED_ACTIVE){
                //需要重新连接 && 不是主动关闭
                callBack.setState(WsStatus.RECONNECT);//重新连接
                long reConnectSecond= ++reConnectCount * callBack.getReConnectStepSecond();
                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"断线重连 ---> ["+reConnectSecond+"秒后开始断线重连]"+request.url());

                Flowable.timer(reConnectSecond, TimeUnit.SECONDS)
                        .subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io())
                        .subscribe(aLong -> {
                            if(WsStatus.DISCONNECTED_ACTIVE.equals(callBack.getState())){
                                //等待期间用户已经主动关闭,不需要再断线重连
                                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"断线重连 ---> [等待期间用户已经主动关闭,不需要再断线重连]"+request.url());
                            }else
                            if(!callBack.getIsReConnect()){
                                //等待期间用户已经主动关闭,不需要再断线重连
                                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"断线重连 ---> [等待期间用户已经主动关闭,不需要再断线重连]"+request.url());
                                callBack.setState(WsStatus.DISCONNECTED_ACTIVE);
                            }else{
                                //创建新的WebSocket连接
                                if(AndroidUtil.isApkInDebug()) Log.i(TAG,"断线重连 ---> [开始断线重连]"+request.url());
                                WebSocket newWebSocket= BaseHttp.getInstance().getConfig().getClient().newWebSocket(request,this);
                                Flowable.just(newWebSocket)
                                        .onBackpressureBuffer()
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(webSocket ->{
                                            if(callBack!=null){
                                                callBack.onReConnect(webSocket);
                                            }
                                        });
                            }
                        });
            }
        }
    }

}
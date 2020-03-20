package com.hzh.frame.core.WsFrame;

/**
 * @author zjm
 */

public class WsStatus {
    public static final int CLOSE= 1000;//关闭

    public final static String CONNECTED = "CONNECTED"; //连接成功
    public final static String CONNECTING = "CONNECTING"; //连接中
    public final static String RECONNECT = "RECONNECT"; //重新连接
    public final static String DISCONNECTED_ACTIVE = "DISCONNECTED_ACTIVE"; //主动关闭
    public final static String DISCONNECTED_PASSIVE = "DISCONNECTED_PASSIVE"; //被动关闭
}

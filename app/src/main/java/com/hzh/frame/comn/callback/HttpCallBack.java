package com.hzh.frame.comn.callback;

import android.app.Activity;

import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.hzh.frame.BaseInitData;
import com.hzh.frame.R;
import com.hzh.frame.comn.model.BaseHttpCache;
import com.hzh.frame.comn.model.BaseHttpRequest;
import com.hzh.frame.core.BaseSP;
import com.hzh.frame.util.Util;
import com.hzh.frame.widget.rxbus.MsgEvent;
import com.hzh.frame.widget.rxbus.RxBus;
import com.hzh.frame.widget.toast.BaseToast;
import com.hzh.frame.widget.xdialog.XDialogSubmit;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;

public abstract class HttpCallBack{

    public static final String TAG="HttpCallBack";
    
	public static final int RESPONSE_JSONOBJECT= 0;//返回类型为JSONObject
    public static final int RESPONSE_BYTEARRAY= 1;//返回类型为byte[]
    public static final int REQUEST_QUERY= 0;//请求类型 | 读取类请求
    public static final int REQUEST_WRITE= 1;//请求类型 | 写入类请求
    public static final int START_PAGE= 1;//保存缓存数据起始页码
    public static final String LOGIN_NOT= "LOGIN_NOT";//未登录
    
    private XDialogSubmit submit;
    private String port;//接口编号
    private Integer page;//页码
    private Integer requestType;//请求类型 REQUEST_QUERY:读请求 REQUEST_WRITE:写请求
    private Integer responseType;//默认返回类型为JSONObject
    private Boolean cache;//是否缓存
    private Activity activity;//当前活动窗口
    private MediaType mediaType;//文件上传类型 参考:okhttp的MediaType.parse属性(https://www.jianshu.com/p/4721d7b5e780)内含MIME 参考手册所有对照表

    public HttpCallBack(){
        this(RESPONSE_JSONOBJECT);
    }

    public HttpCallBack(MediaType mediaType){
        this.cache=false;
        this.responseType=RESPONSE_JSONOBJECT;
        this.mediaType=mediaType;
    }

    public HttpCallBack(Integer responseType){
        this.cache=false;
        this.responseType=responseType;
        this.mediaType=MediaType.parse("image/jpeg");//默认上传图片jpeg类型
    }

    public HttpCallBack setMediaType(String mediaType) {
        this.mediaType = MediaType.parse(mediaType);
        return this;
    }

    public HttpCallBack setSubmit(XDialogSubmit submit) {
        this.submit = submit;
        return this;
    }

    public HttpCallBack setPort(String port) {
        this.port = port;
        return this;
    }

    public HttpCallBack setPage(Integer page) {
        this.page = page;
        return this;
    }

    public HttpCallBack setRequestType(Integer requestType) {
        this.requestType = requestType;
        return this;
    }

    public HttpCallBack setResponseType(Integer responseType) {
        this.responseType = responseType;
        return  this;
    }

    public HttpCallBack setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    //响应缓存JSON | 默认无分页缓存
    public HttpCallBack cache(){
        this.cache=true;
        this.page=START_PAGE;
        return this;
    }

    public Integer getPage() {
        return page;
    }

    public int getResponseType() {
        return responseType;
    }

    public Boolean getCache() { return cache; }

    public MediaType getMediaType() {
        return mediaType;
    }

    public Integer getRequestType() {
        return requestType;
    }

    public Activity getActivity() { return activity; }

    /**---------------------------------------------------------------------------------------------**/

    public void onResponse(String response){
        JSONObject jsonObject = null;
        try {
            jsonObject=new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(Util.isEmpty(response) || Util.isEmpty(jsonObject)){
            onFailure();
        }else{
            //加载过滤器
            loginFilter(jsonObject);
            submitFilter();
            httpFilter();
            cacheFilter(response);
            onSuccess(jsonObject);
        }
    }

    public void onResponse(byte[] byteArray){
        onSuccess(byteArray);
    }
    
    public void onFailure() {
        //加载过滤器
        submitFilter();
        httpFilter();
        onFail();
    }

    //重新登录过滤
    public void loginFilter(JSONObject response){
        Integer code = response.optInt("code");
        if(Util.isEmpty(code)){
           return; 
        }
        if(code==401 || code==300){//需要登录过滤
            if(Util.isEmpty(BaseSP.getInstance().getString("token"))){
                RxBus.getInstance().post(new MsgEvent(LOGIN_NOT,BaseInitData.applicationContext.getString(R.string.base_login_not_success)));//用户未登录
            }else{
                RxBus.getInstance().post(new MsgEvent(LOGIN_NOT,BaseInitData.applicationContext.getString(R.string.base_login_again_success)));//用户登录过期,需要重新登录
            }
            try {
                response.put("message", BaseToast.MSG_RE_LOGIN);
                response.put("msg", BaseToast.MSG_RE_LOGIN);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //关闭请求动画
    public void submitFilter(){
        if(submit!=null && !submit.activity.isFinishing() /*&& requestType==REQUEST_WRITE*/){
            submit.dismiss();
        }
    }

    //解开写请求的多次调用限制
    public void httpFilter(){
        if(!Util.isEmpty(port) && requestType==REQUEST_WRITE){
            new Update(BaseHttpRequest.class).set("state = 1").where("port = ?",port).execute();
        }
    }

    //缓存response
    public void cacheFilter(String response){
        if(cache){
            if(!Util.isEmpty(port)){
                if(page==START_PAGE){
                    //暂时只缓存缓存接口的第一页数据
                    BaseHttpCache cacheModel=new Select().from(BaseHttpCache.class).where("port = ? and page = ?",port,page).executeSingle();
                    if(cacheModel!=null){
                        //更新缓存
                        cacheModel.setType(REQUEST_QUERY).setResponseParams(response).setLastTime(System.currentTimeMillis()).save();
                    }else{
                        //创建缓存
                        new BaseHttpCache().setPort(port).setType(REQUEST_QUERY).setPage(page).setResponseParams(response).setLastTime(System.currentTimeMillis()).save();
                    }
                }else{
                    //缓存接口的其他页数据不与缓存
                }
            }else{
                //缓存的Response接口编号port不能为空
            }
        }
    }

	public void onSuccess(JSONObject response){};

    public void onSuccess(byte[] byteArray){};

    public void onFail(){};
    

}

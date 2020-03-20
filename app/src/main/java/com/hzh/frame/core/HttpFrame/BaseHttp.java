package com.hzh.frame.core.HttpFrame;

import android.app.Activity;

import com.activeandroid.query.Select;
import com.google.gson.Gson;
import com.hzh.frame.BaseInitData;
import com.hzh.frame.R;
import com.hzh.frame.comn.callback.HttpCallBack;
import com.hzh.frame.comn.model.BaseHttpCache;
import com.hzh.frame.comn.model.BaseHttpRequest;
import com.hzh.frame.core.BaseSP;
import com.hzh.frame.core.HttpFrame.api.ApiRequest;
import com.hzh.frame.core.HttpFrame.config.BaseHttpConfig;
import com.hzh.frame.util.AndroidUtil;
import com.hzh.frame.util.Util;
import com.hzh.frame.widget.toast.BaseToast;
import com.hzh.frame.widget.xdialog.XDialogSubmit;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BaseHttp {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");


    private BaseHttpConfig config;
    private static BaseHttp _instance;

    public static BaseHttp getInstance(){
        synchronized(BaseHttp.class){
            if(_instance==null){
                _instance=new BaseHttp();
            }
            return _instance;
        }
    }

    public void init(BaseHttpConfig config){
        this.config=config;
    }

    public BaseHttpConfig getConfig() {
        return config;
    }

    /**----------------------------start--- 使用Retrofit2发送网络请求 ---start----------------------------**/
    private static Map<Class, ApiRequest> requestPool = new HashMap<>();
    public <T extends ApiRequest> T getRequest(Class<T> clazz){
        ApiRequest request=requestPool.get(clazz);
        if(request==null){
            try {
                request= (ApiRequest) clazz.newInstance();
                request.setServer(config.getRetrofit().create(ApiRequest.Server.class));
                requestPool.put(clazz,request);
            } catch (Exception e) {
                throw new Error("instance api error:" + e.getMessage());
            }
        }
        return (T) request;
    }
    /**----------------------------end--- 使用Retrofit2发送网络请求 ---end----------------------------**/

    /**----------------------------start--- 直接调用OKhttp3发送网络请求 ---start----------------------------**/


    /**
     * 查询接口(post)
     * @param path 接口编号
     * @param callBack 回调方法
     * **/
    public void query(String path, HttpCallBack callBack) {
        query(path,null,callBack);
    }

    /**
     * 查询接口(post)
     * @param path 接口编号
     * @param params 查询参数
     * @param callBack 回调方法
     * **/
    public void query(String path, JSONObject params, HttpCallBack callBack) {
        callBack.setPort(path);//接口编号
        callBack.setRequestType(HttpCallBack.REQUEST_QUERY);
        post(config.getBaseUrl()+path,createRequestBody(path,params,callBack),callBack);
    }


    /**
     * 写入接口(post) | 默认拦截重复请求并弹出请求窗口(这里拦截只判断port,有需求可以把requestParams加进判断中)
     * @param path 接口编号
     * @param params 写入参数
     * @param callBack 回调方法
     * **/
    public <T extends Activity> void write(String path, JSONObject params, HttpCallBack callBack) {
        write(null,path,params,callBack);
    }

    /**
     * 写入接口(post) | 默认拦截重复请求并弹出请求窗口(这里拦截只判断port,有需求可以把requestParams加进判断中)
     * @param activity 当前活动(主要作用于弹出请求加载框,传null表示不需要请求加载框)
     * @param path 接口编号
     * @param params 写入参数
     * @param callBack 回调方法
     * **/
    public <T extends Activity> void write(T activity,String path, JSONObject params, HttpCallBack callBack) {
        write(activity,path,params,null,callBack);
    }

    /**
     * 写入接口(post) | 默认拦截重复请求并弹出请求窗口(这里拦截只判断port,有需求可以把requestParams加进判断中)
     * @param path 接口编号
     * @param params 写入参数
     * @param fileList 待上传的文件列表集合(注:集合Map数据格式 <name,上传供后台取的文件名>,<file,上传文件>)
     * @param callBack 回调方法
     * **/
    public <T extends Activity> void write(String path, JSONObject params, List<HashMap<String,Object>> fileList, HttpCallBack callBack) {
        write(null,path,params,fileList,callBack);
    }

    /**
     * 写入接口(post) | 默认拦截重复请求并弹出请求窗口(这里拦截只判断port,有需求可以把requestParams加进判断中)
     * @param activity 当前活动(主要作用于弹出请求加载框,传null表示不需要请求加载框)
     * @param path 接口编号
     * @param params 写入参数
     * @param fileList 待上传的文件列表集合(注:集合Map数据格式 <name,上传供后台取的文件名>,<file,上传文件>)
     * @param callBack 回调方法
     * **/
    public <T extends Activity> void write(T activity,String path, JSONObject params,List<HashMap<String,Object>> fileList, HttpCallBack callBack) {
        if(!repeatHttpInterceptor(path,activity,callBack)){
            callBack.setPort(path);//接口编号
            callBack.setRequestType(HttpCallBack.REQUEST_WRITE);
            if(fileList == null || fileList.size()==0){//非文件上传
                post(config.getBaseUrl()+path,createRequestBody(path,params,callBack),callBack);
            }else{//文件上传
                post(config.getBaseUrl()+path,createMultipartBody(path,params,fileList,callBack),callBack);
            }
        }
    }



    /**
     * 写入接口(post) | 绿色通道 | 不拦截重复请求不弹出请求窗口
     * @param path 接口编号
     * @param params 写入参数
     * @param callBack 回调方法
     * **/
    public void writeGreen(String path, JSONObject params, HttpCallBack callBack) {
        post(config.getBaseUrl()+config.getWritePath(),createRequestBody(path,params,callBack),callBack);
    }



    /**
     * 文件上传 | 默认拦截重复请求并弹出请求窗口(这里拦截只判断port,有需求可以把requestParams加进判断中)
     * @param path 接口编号
     * @param fileList 待上传的文件列表集合(注:集合Map数据格式 <name,上传供后台取的文件名>,<file,上传文件>)
     * @param callBack 回调方法
     * **/
    public void uploadFile(String path,List<HashMap<String,Object>> fileList, HttpCallBack callBack){
        uploadFile(path,false,fileList,callBack);
    }

    /**
     * 文件上传 | 默认拦截重复请求并弹出请求窗口(这里拦截只判断port,有需求可以把requestParams加进判断中)
     * @param path 接口编号
     * @param pathFull path是否是全路径
     * @param fileList 待上传的文件列表集合(注:集合Map数据格式 <name,上传供后台取的文件名>,<file,上传文件>)
     * @param callBack 回调方法
     * **/
    public void uploadFile(String path,boolean pathFull,List<HashMap<String,Object>> fileList, HttpCallBack callBack){
        uploadFile(null,path,pathFull,fileList,callBack);
    }
    /**
     * 文件 | 默认拦截重复请求并弹出请求窗口(这里拦截只判断port,有需求可以把requestParams加进判断中)
     * @param activity 当前活动(主要作用于弹出请求加载框,传null表示不需要请求加载框)
     * @param path 接口编号
     * @param pathFull path是否是全路径
     * @param fileList 待上传的文件列表集合(注:集合Map数据格式 <name,上传供后台取的文件名>,<file,上传文件>)
     * @param callBack 回调方法
     * **/
    public <T extends Activity> void uploadFile(T activity,String path,boolean pathFull,List<HashMap<String,Object>> fileList, HttpCallBack callBack){
        if(!repeatHttpInterceptor(path,activity,callBack)) {
            callBack.setPort(path);//接口编号
            callBack.setRequestType(HttpCallBack.REQUEST_WRITE);
            if(pathFull){//是全路径,直接调用
                post(path, createMultipartBody(path, new JSONObject(), fileList, callBack), callBack);
            }else{//不是全路径,自己拼接前缀
                post(config.getBaseUrl() + path, createMultipartBody(path, new JSONObject(), fileList, callBack), callBack);
            }
        }
    }

    /**
     * 发送Get请求
     * @param url 请求路径
     * @param callback 本地封装的继承至Okhttp3的Callback
     * **/
    public void get(String url, HttpCallBack callback){
        Request.Builder requestBuider = new Request.Builder()
                .header("user-agent", "android")
                .addHeader("content-type", "application/json;charset:utf-8")
                .addHeader("language", BaseSP.getInstance().getString("language"))
                .addHeader("token", BaseSP.getInstance().getString("token"))
                .addHeader("version-code", ""+AndroidUtil.getVersionCode(BaseInitData.applicationContext))
                .get()
                .url(url);
        config.getClient().newCall(requestBuider.build()).enqueue(new OkhttpCallback(callback));
    }

    /**
     * 发送Post请求
     * @param url 请求路径
     * @param body 请求体
     * @param callback 自己写的一个回调抽象类HttpCallBack
     * **/
    private void post(String url, RequestBody body, HttpCallBack callback){
        Request.Builder requestBuider=new Request.Builder()
                .header("user-agent", "android")
                .addHeader("content-type", "application/json;charset:utf-8")
                .addHeader("language", BaseSP.getInstance().getString("language"))
                .addHeader("token", BaseSP.getInstance().getString("token"))
                .addHeader("version-code", ""+AndroidUtil.getVersionCode(BaseInitData.applicationContext))
                .post(body)
                .url(url);
        config.getClient().newCall(requestBuider.build()).enqueue(new OkhttpCallback(callback));
    }


    /**
     * 重复发送http拦截器
     * @param port 接口编号
     * @param activity 当前活动窗口
     * @param callBack 继承至HttpCallBack
     * @return true 拦截 false 不拦截
     * **/
    public boolean repeatHttpInterceptor(String port, Activity activity, HttpCallBack callBack){
        BaseHttpRequest model=new Select().from(BaseHttpRequest.class).where("port = ?",port).executeSingle();
        if(model!=null){
            //当前port接口有请求记录
            if(model.getState()==2){//拦截
                BaseToast.getInstance().setView(R.layout.base_view_toast_yllow).setMsg(R.id.content,"别点了,请求正在途中...").show();
                return true;
            }else{//正常发送
                model.setState(2).save();
                if(activity!=null){
                    callBack.setSubmit(new XDialogSubmit(activity).alert());
                }
            }
        }else{
            //当前port接口无请求记录
            new BaseHttpRequest().setPort(port).setState(2).setType(HttpCallBack.REQUEST_WRITE).save();
            if(activity!=null){
                callBack.setSubmit(new XDialogSubmit(activity).alert());
            }
        }
        return false;
    }

    /**
     * 缓存调用拦截器
     * @param port 接口编号
     * @param params 传参
     * @param callBack 继承至HttpCallBack
     * **/
    public void cacheInterceptor(String port, JSONObject params, HttpCallBack callBack){
        if(callBack.getCache()){
            if(!Util.isEmpty(port)){
                if(params!=null && HttpCallBack.START_PAGE!=params.optInt("page")){
                    //有页码且非初始页码
                    callBack.setPage(params.optInt("page"));
                }else{
                    //无页码或是初始页码
                    callBack.setPage(HttpCallBack.START_PAGE);
                }
                if(callBack.getPage()== HttpCallBack.START_PAGE){
                    //第一页缓存数据
                    BaseHttpCache cache=new Select().from(BaseHttpCache.class).where("port = ? and page = ?",port,callBack.getPage()).executeSingle();
                    if(cache!=null){
                        try {
                            //先拿出缓存数据回调一次,稍后再回调一次服务器数据
                            callBack.onSuccess(new JSONObject(cache.getResponseParams()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 转换Params
     * @param port 接口编号
     * @param params 传参
     * @param callBack 继承至HttpCallBack
     * **/
    public RequestBody createRequestBody(String port, JSONObject params, HttpCallBack callBack){
        try {
            if(params==null){
                params=new JSONObject();
            }
            params.put("timestamp",System.currentTimeMillis()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        cacheInterceptor(port,params,callBack);
        HashMap<String,Object> paramsMap=new HashMap<>();
        paramsMap.put("num", port+"");
        paramsMap=Util.createV2HttpKey(paramsMap,params);
        paramsMap=setRequestBodyParams(paramsMap,params);
        return RequestBody.create(JSON,new Gson().toJson(paramsMap));
    }

    /**
     * 转换Params
     * @param port 接口编号
     * @param params 传参
     * @param fileList 待上传的文件列表集合(注:集合Map数据格式 <name,上传供后台取的文件名>,<file,上传文件>)
     * @param callBack 继承至HttpCallBack
     * **/
    public MultipartBody createMultipartBody(String port, JSONObject params, List<HashMap<String,Object>> fileList, HttpCallBack callBack){
        cacheInterceptor(port,params,callBack);

        MultipartBody.Builder body=new MultipartBody.Builder();
        body.setType(MultipartBody.FORM);//传输类型
        if(fileList!=null && fileList.size()>0){
            for(HashMap<String,Object> map:fileList){
                File file=(File) map.get("file");
                //okhttp的MediaType.parse属性(https://www.jianshu.com/p/4721d7b5e780)内含MIME 参考手册所有对照表
                RequestBody fileBody = RequestBody.create(callBack.getMediaType(), file);
                //循环添加file文件
                body.addFormDataPart(map.get("name").toString(),file.getName(),fileBody);
            }
        }
        body.addPart(createRequestBody(port,params,callBack));
        return body.build();
    }

    public HashMap<String,Object> setRequestBodyParams(HashMap<String,Object> paramsMap,JSONObject params){
        try {
            Iterator<String> it = params.keys();
            while(it.hasNext()){
                String key = it.next();
                String value = params.getString(key);
                paramsMap.put(key,value);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return paramsMap;
        }
    }

    //这里的OKhttpCallback是一个子线程,不能在这里面直接调用UI线程View做修改(可改成Rxjava实现更简洁)
    class OkhttpCallback implements Callback{
        private HttpCallBack httpCallBack;

        OkhttpCallback(HttpCallBack httpCallBack){
            this.httpCallBack=httpCallBack;
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if (response.isSuccessful()) {
                if(HttpCallBack.RESPONSE_JSONOBJECT==httpCallBack.getResponseType()){
                    Flowable.just(response.body().string())
                            .onBackpressureBuffer()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String response) throws IOException {
                                    httpCallBack.onResponse(response);
                                }
                            });
                } else
                if(HttpCallBack.RESPONSE_BYTEARRAY==httpCallBack.getResponseType()){
                    Flowable.just(response.body().bytes())
                            .onBackpressureBuffer()
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<byte[]>() {
                                @Override
                                public void accept(byte[] bytes) throws Exception {
                                    httpCallBack.onResponse(bytes);
                                }
                            });
                }
            } else {
                Flowable.just("")
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String s) throws IOException {
                                httpCallBack.onFailure();
                            }
                        });
            }
        }

        @Override
        public void onFailure(Call call, IOException e) {
            Flowable.just("")
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws IOException {
                            httpCallBack.onFailure();
                        }
                    });
        }
    }

    /**----------------------------end--- 直接调用OKhttp3发送网络请求 ---end----------------------------**/
}

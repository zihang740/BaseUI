package com.hzh.frame.core.HttpFrame.api;

import com.hzh.frame.BaseInitData;
import com.hzh.frame.util.Util;

import org.json.JSONObject;

import java.util.HashMap;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author
 * @version 1.0
 * @date 2018/5/22
 */
public abstract class ApiRequest{
  private Server server;

//  public Class getTClass() throws ClassNotFoundException {
//      //从一个泛型类型中获取第一个泛型参数的类型类(就是上面这个T)
//    return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//  }

  //这里的set方法会在BaseHttp.getRequest创建Api时注入
  public void setServer(Server server) {
      this.server = server;
  }

  public Server getServer() {
      return server;
  }

  protected Flowable<JSONObject> queryServer(int num, HashMap<String,Object> params){
      params.put("datetoken", Util.getNewTime("yyyy-MM-dd HH:mm:ss"));
      String paramJson= Util.urlEncode(params);
      return server
              .query(num, BaseInitData.http_client_version,Util.createHttpKey(paramJson),paramJson)
              .map(new Function<ResponseBody, JSONObject>() {
                  @Override
                  public JSONObject apply(ResponseBody response) throws Exception {
                      return new JSONObject(response.string());
                  }
              })
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread());
  }

  protected Flowable<JSONObject> writeServer(int num,HashMap<String,Object> params){
      params.put("datetoken", Util.getNewTime("yyyy-MM-dd HH:mm:ss"));
      String paramJson= Util.urlEncode(params);
      return server
              .write(num, BaseInitData.http_client_version,Util.createHttpKey(paramJson),paramJson)
              .map(new Function<ResponseBody, JSONObject>() {
                  @Override
                  public JSONObject apply(ResponseBody response) throws Exception {
                      return new JSONObject(response.string());
                  }
              })
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread());
  }

  public static interface Server{
      @FormUrlEncoded
      @POST("/appKu/query.do")
      Flowable<ResponseBody> query(@Field("num") Integer port, @Field("version") Integer version, @Field("key") String key, @Field("data") String param);

      @FormUrlEncoded
      @POST("/appKu/write.do")
      Flowable<ResponseBody> write(@Field("num") Integer port, @Field("version") Integer version, @Field("key") String key, @Field("data") String param);
  }

}

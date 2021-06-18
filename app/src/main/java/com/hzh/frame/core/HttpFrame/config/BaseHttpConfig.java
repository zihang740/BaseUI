package com.hzh.frame.core.HttpFrame.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hzh.frame.BaseInitData;
import com.hzh.frame.core.HttpFrame.interceptor.BaseHttpLogging;
import com.hzh.frame.core.HttpFrame.interceptor.IHttpInterceptor;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 请求框架配置参数
 * @author hzh
 * @version 1.0
 * @date 2018/5/9
 */

public class BaseHttpConfig {
    private String baseUrl;//请求链接
    private String baseWsUrl;//WebSoket请求链接
    private String queryPath;//查询路径
    private String writePath;//写入路径
    private int timeOut;//请求超时时长
    private List<IHttpInterceptor> interceptors;//请求拦截器
    private OkHttpClient client;//请求源
    private Retrofit retrofit;
    
    public BaseHttpConfig(){
        this(new Builder());
    }
    
    public BaseHttpConfig(Builder builder){
        this.baseUrl=builder.baseUrl;
        this.baseWsUrl=builder.baseWsUrl;
        this.queryPath=builder.queryPath;
        this.writePath=builder.writePath;
        this.timeOut=builder.timeOut;
        this.interceptors=builder.interceptors;
        this.client=builder.clientBuilder.build();
        this.retrofit=builder.retrofitBuilder.client(this.client).build();
    }
    
    public static class Builder{
        private String baseUrl;
        private String baseWsUrl;
        private String queryPath;
        private String writePath; 
        private int timeOut;
        private List<IHttpInterceptor> interceptors;//请求拦截器
        private OkHttpClient.Builder clientBuilder;
        private Retrofit.Builder retrofitBuilder;
        
        public Builder(){
            this.baseUrl= BaseInitData.http_client_url;
            this.baseWsUrl= BaseInitData.ws_client_url;
            this.queryPath="appKu/query.do";
            this.writePath="appKu/write.do";
            this.timeOut=20;
            this.interceptors=new ArrayList<IHttpInterceptor>();//请求拦截器
            this.clientBuilder=new OkHttpClient.Builder()
                    .connectTimeout(this.timeOut, TimeUnit.SECONDS)
                    .readTimeout(this.timeOut, TimeUnit.SECONDS)
                    .writeTimeout(this.timeOut, TimeUnit.SECONDS)
                    .addNetworkInterceptor(new HttpLoggingInterceptor(new BaseHttpLogging()).setLevel(HttpLoggingInterceptor.Level.BODY));
            GsonBuilder builder = new GsonBuilder();
            builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT, Modifier.STATIC);
            Gson gson = builder.create();
            this.retrofitBuilder=new Retrofit.Builder()
                    .baseUrl(this.baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
        }
        
        public Builder baseUrl(String baseUrl){
            this.baseUrl=baseUrl;
            this.retrofitBuilder.baseUrl(baseUrl);
            return this;
        }

        public Builder baseWsUrl(String baseWsUrl){
            this.baseWsUrl=baseWsUrl;
            return this;
        }

        public Builder queryPath(String queryPath){
            this.queryPath=queryPath;
            return this;
        }

        public Builder writePath(String writePath){
            this.writePath=writePath;
            return this;
        }

        public Builder timeOut(int timeOut){
            this.timeOut=timeOut;
            this.clientBuilder.connectTimeout(this.timeOut, TimeUnit.SECONDS);
            this.clientBuilder.readTimeout(this.timeOut, TimeUnit.SECONDS);
            this.clientBuilder.writeTimeout(this.timeOut, TimeUnit.SECONDS);
            return this;
        }

        public Builder addInterceptor(IHttpInterceptor interceptor){
            this.interceptors.add(interceptor);
            return this;
        }

        public BaseHttpConfig build(){
            return new BaseHttpConfig(this);
        }
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBaseWsUrl() {
        return baseWsUrl;
    }

    public String getQueryPath() {
        return queryPath;
    }

    public String getWritePath() {
        return writePath;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public OkHttpClient getClient() {
        return client;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}

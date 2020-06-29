package com.anber.multimediaexample.http.retrofit;

import android.content.Context;

import com.anber.multimediaexample.http.ApiService;


/**
 * Http接口管理类
 *
 * @author lj
 * @version 1.0
 * @date 2019-4-2
 * @fileName com.yjsm.host.cloudvideo.common.glide.http
 */
public class RetrofitServiceManager {

    private ApiService api;

    private static class Singleton {
        private static final RetrofitServiceManager INSTANCE = new RetrofitServiceManager();
    }

    private RetrofitServiceManager() {
    }

    public static RetrofitServiceManager get() {
        return Singleton.INSTANCE;
    }

    public void init(Context context) {
        //微秀
        api = HttpServiceFactory.createHttpService(context, ApiUrlInterface.baseUrl, ApiService.class);
        //工具包
//        api = HttpServiceFactory.createHttpService(context, "http://139.9.73.252:32517/", ApiService.class);

    }

    public ApiService getApi() {
        return api;
    }
}

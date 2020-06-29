package com.anber.multimediaexample.http.retrofit;

import android.content.Context;
import android.util.Log;

import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author:YuanChao
 * Date:2018/10/22
 */
public class HttpServiceFactory {
    private static final String TAG = "HttpServiceFactory";

    protected HttpServiceFactory() {
        Log.d(TAG,"[HttpServiceFactory]");
    }

    /*超时设置 s*/
    private static final int DEFAULT_TIME_OUT = 20;
    private static Retrofit innerInstance;

    private static OkHttpClient createClient(Context context) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(DEFAULT_TIME_OUT, TimeUnit.SECONDS);
        //网络请求拦截器
        builder.addInterceptor(HttpIntercepterLog.get());

        /**
         * 新Https认证
         */
        HttpsUtil.SSLParams sslParams = HttpsUtil.getSslSocketFactory(context, null,
                0, null);
        builder.sslSocketFactory(sslParams.getsSLSocketFactory(), sslParams.getTrustManager());
        builder.hostnameVerifier(HttpsUtil.getHostnameVerifier());
        return builder.build();
    }

    private static Retrofit createRetrofit(String baseUrl, OkHttpClient okHttpClient,
                                           CallAdapter.Factory callFactory, Converter.Factory factory) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addCallAdapterFactory(callFactory)
                .addConverterFactory(factory).build();
    }

    /**
     * 创建HttpService
     *
     * @param context
     * @param baseUrl
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T createHttpService(Context context, String baseUrl, Class<T> tClass) {
        GsonBuilder builder = new GsonBuilder();
        builder.excludeFieldsWithModifiers(Modifier.FINAL, Modifier.TRANSIENT);
        return createHttpService(context, baseUrl, tClass, RxJava2CallAdapterFactory.create(),
                GsonConverterFactory.create(builder.create()));
    }

    public static <T> T createHttpService(Context context, String baseUrl, Class<T> tClass,
                                          CallAdapter.Factory callFactory, Converter.Factory factory) {
        if (innerInstance == null) {
            innerInstance = createRetrofit(baseUrl, createClient(context), callFactory, factory);
        }
        return innerInstance.create(tClass);
    }
}

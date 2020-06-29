package com.goldze.base.http;

import com.trello.rxlifecycle2.LifecycleTransformer;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author:YuanChao
 * Date:2018/10/25
 */
public class HttpHelper {

    /**
     * 处理网络返回的observable
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applyResult(final LifecycleTransformer<T> transformer) {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream /*生命周期管理*/
                        .compose(transformer)
                        /*http请求线程*/
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        /*回调线程*/
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    public static <T> ObservableTransformer<T, T> applyNoLifecycle() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream /*生命周期管理*/
                        /*http请求线程*/
                        .subscribeOn(Schedulers.io())
                        .unsubscribeOn(Schedulers.io())
                        /*回调线程*/
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 异步线程调度
     *
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> applySchedulers() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}

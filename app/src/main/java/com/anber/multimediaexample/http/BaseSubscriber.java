package com.anber.multimediaexample.http;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.anber.multimediaexample.widget.PullToRefreshLayout;
import com.goldze.base.http.NetUtil;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class BaseSubscriber<T> implements Observer<T> {

    private static final String TAG = "BaseSubscriber";

    /**
     * 空布局的容器。
     */
    @Nullable
    private ViewGroup contentView;

    /**
     * 展示布局
     */
    private TipView mTipView;
    protected int mTipViewType = TipView.NONE;

    /**
     * 表示不需要展示空布局。
     */
    public static final int HIDE_TIP_VIEW = -2;

    private Disposable mDisposable;

    private Context context;

    public BaseSubscriber() {

    }

    public BaseSubscriber(Context context) {
        this.context = context;
    }

    public BaseSubscriber(@Nullable ViewGroup contentView, @TipView.TipType int tipViewType) {
        this.contentView = contentView;
        mTipViewType = tipViewType;
        if (contentView != null) {
            context = contentView.getContext();
            mTipView = new TipView(contentView.getContext());
            mTipView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
        }
    }

    @Override
    public void onSubscribe(Disposable disposable) {
        this.mDisposable = disposable;
    }

    @Override
    public void onNext(T t) {
        if (mDisposable != null && mDisposable.isDisposed()) {
            return;
        }
//        int tipType = getTipViewType(t);
//        if (tipType != HIDE_TIP_VIEW) {
//            showTipView(tipType);
//        } else {
            removeTipView();
            onDoNext(t);
//        }
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onError(Throwable throwable) {
        Log.d(TAG, throwable.getMessage());
        if (context != null) {
            onRefreshComplete();
            if (!NetUtil.checkNetWorkStatus(context)) {
                showToast("当前网络不可用，请检查你的网络设置");
                showTipView();
                return;
            }

            if (throwable instanceof SocketTimeoutException || throwable instanceof TimeoutException) {
                showToast("当前网络连接超时");
            } else if (throwable instanceof JSONException || throwable instanceof JsonParseException) {
                showToast("数据解析异常");
            } else {
                showToast(throwable.toString());
            }
            showTipView(mTipViewType);
        }
    }

    /**
     * 弹出错误提示
     *
     * @param message 错误信息
     */
    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    protected abstract void onDoNext(T t);

    public void showTipView() {
        showTipView(mTipViewType);
    }

    /**
     * 展示指定布局
     *
     * @param tipViewType 展示的布局类型
     */
    public void showTipView(int tipViewType) {
        int filterType = filterTipTypes(tipViewType);
        if (filterType == HIDE_TIP_VIEW) {
            return;
        }
        if (contentView != null) {
            TipView tipView;
            View topView = contentView.getChildAt(contentView.getChildCount() - 1);
            if (!(topView instanceof TipView)) {
                tipView = mTipView;
                contentView.addView(tipView);
            } else {
                tipView = (TipView) topView;
            }
            if (contentView instanceof PullToRefreshLayout) {
                ((PullToRefreshLayout) contentView).ensureTargetView(contentView.getChildCount() - 1);
            }
            tipView.showTip(filterType);
        }
    }

    /**
     * 下拉刷新结束
     */
    protected void onRefreshComplete() {
        if (contentView instanceof PullToRefreshLayout) {
            ((PullToRefreshLayout) contentView).setRefreshing(false);
        }
    }

    /**
     * 移除空布局
     */
    public void removeTipView() {
        if (contentView != null && mTipView.getParent() == null && !checkTipView()) {
            contentView.removeView(mTipView);
        }
    }

    private boolean checkTipView() {
        for (int i = 0; i < contentView.getChildCount(); i++) {
            View view = contentView.getChildAt(i);
            if (view instanceof TipView) {
                contentView.removeView(view);
                return true;
            }
        }
        return false;
    }

    /**
     * 你可以重写此方法做数据正确性校验。
     * 根据结果判断应该展示的空布局的类型
     *
     * @param t onNext传入的结果，可以根据结果来判断展示哪种类型的空布局。默认返回{@link #HIDE_TIP_VIEW}，表示不展示布局。
     * @return 如果你重写此方法并返回值不为HIDE_TIP_VIEW，则会加载空布局，则{@link #onDoNext(Object)}方法不会调用 。
     */
    public int getTipViewType(T t) {
        return HIDE_TIP_VIEW;
    }

    /**
     * 变换tipType
     *
     * @param tipType
     * @return
     */
    protected int filterTipTypes(int tipType) {
        return tipType;
    }
}

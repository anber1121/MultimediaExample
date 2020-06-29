package com.anber.multimediaexample.http;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.anber.multimediaexample.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author LJ
 * 展示所有错误数据的布局
 */
public class TipView extends FrameLayout {

    /**
     * 直播间列表无数据
     */
    public static final int LIVE_ROOM_LIST_NO_DATA = 1;
    public static final int NONE = -1;

    private SparseIntArray mSparseIntArray = new SparseIntArray();
    private LayoutInflater mInflater;
    private int mType = -1;

    public TipView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public TipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mInflater = LayoutInflater.from(context);
        setLayoutTransition(new LayoutTransition());
        mSparseIntArray.put(LIVE_ROOM_LIST_NO_DATA, R.layout.live_room_list_no_data);
    }


    /**
     * 展示内容。
     *
     * @param type 显示某个布局
     */
    public void showTip(@TipType int type) {
        if (mType == type) {
            return;
        }
        mType = type;
        removeAllViews();
        int layoutRes = mSparseIntArray.get(type);
        if (layoutRes > 0) {
            mInflater.inflate(layoutRes, this, true);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NONE, LIVE_ROOM_LIST_NO_DATA})
    public @interface TipType {

    }

    public int getmType() {
        return mType;
    }
}

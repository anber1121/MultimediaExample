package com.anber.multimediaexample.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anber.multimediaexample.R;


/**
 * @author YnanChao
 * @date 2017/8/30
 * 下拉刷新布局，使用方法类似SwipeRefreshLayout
 * <p>
 * <br/>
 * 添加headView的2种方法：<ol><li>使用自定义属性： app:ptr_head_layout="@layout/pull_down_header",此时布局中第一个子View将是内容。</li>
 * <li> 直接将下拉头部View写在布局中，和FrameLayout一样的布局方式。注意，布局中第一个子View为下拉头，第二个为内容</li>
 * </ol>
 * <li>设置下拉刷新监听：</li>
 * <li>展示、隐藏头部刷新布局{@link #setRefreshing(boolean)}</li>
 * <li>禁用下拉{@link #setEnabled(boolean)}</li>
 * </p>
 */

public class PullToRefreshLayout extends ViewGroup {

    private View mTargetView;
    protected View mHeadView;
    private float mInitialMotionY;
    private static final float DRAG_RATE = .5f;
    private boolean mIsBeingDragged;
    private boolean mIsChildDraggedWhenRefreshing;
    private boolean mReturningToStart;
    /**
     * 回弹到正在刷新状态的距离
     */
    protected float mToRefreshingOffset = getResources().getDisplayMetrics().heightPixels;
    private OnChildScrollDownCallBack mScrollDownCallBack;
    private int mState = STATE_NORMAL;
    private static final int ANIMATION_DURATION = 300;
    private int mInitialPointId;
    private int mTotalConsumed;
    private boolean mIsAnimationEnd = true;
    private CheckEventListener mCheckEventListener;
    private final int mTouchedSlop = ViewConfiguration.getWindowTouchSlop();
    private static final String TAG = "PullToRefreshLayout";

    public PullToRefreshLayout(Context context) {
        super(context);
    }

    public PullToRefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PullToRefreshLayout);
            int headViewLayout = typedArray.getResourceId(R.styleable.PullToRefreshLayout_ptr_head_layout, -1);
            if (headViewLayout != -1) {
                mHeadView = LayoutInflater.from(context).inflate(headViewLayout, this, false);
                addView(mHeadView, 0);
            }
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ensureTargetView();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (null != mCheckEventListener) {
            mCheckEventListener.getEvent(event);
        }
        //当不可以下拉时，不拦截事件。
        if (!isEnabled() || mReturningToStart || mState == STATE_REFRESHING || !canPullDown()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionY = event.getY();
                mInitialPointId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                int index = event.findPointerIndex(mInitialPointId);
                if (index < 0) {
                    return false;
                }
                mIsBeingDragged = event.getY(index) - mInitialMotionY > mTouchedSlop;
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged && mIsAnimationEnd) {
                    onActionUp();
                }
                mInitialMotionY = 0;
                mIsBeingDragged = false;
                break;
            default:
                break;
        }
        return mIsBeingDragged;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled() || mReturningToStart || mState == STATE_REFRESHING || !canPullDown()) {
            return false;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialMotionY = event.getY();
                mInitialPointId = event.getPointerId(0);
                break;
            case MotionEvent.ACTION_MOVE:
                int index = event.findPointerIndex(mInitialPointId);
                if (index < 0) {
                    return false;
                }
                float dy = event.getY(index) - mInitialMotionY;
                if (dy >= mTouchedSlop) {
                    float overScrollTop = (dy - mTouchedSlop) * DRAG_RATE;
                    startPullDown(overScrollTop < 0 ? 0 : overScrollTop);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsBeingDragged && mIsAnimationEnd) {
                    onActionUp();
                }
                if (null != mCheckEventListener) {
                    mCheckEventListener.getEvent(event);
                }
                mIsBeingDragged = false;
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 执行下拉动作。
     *
     * @param overScrollTop
     */
    private void startPullDown(float overScrollTop) {
        if (mIsAnimationEnd && overScrollTop >= 0 && !mIsChildDraggedWhenRefreshing) {
            mIsBeingDragged = true;

            int state = STATE_NORMAL;
            if (overScrollTop >= getReadyToRefreshOffset()) {
                state = STATE_READY_TO_REFRESHING;
            }
            if (mHeadView != null && mHeadView instanceof OnPullStateChangedListener && state != mState) {
                ((OnPullStateChangedListener) mHeadView).onPullStateChanged(state);
            }
            if (state != mState) {
                mState = state;
            }
            setTranslationY(overScrollTop);
        }
    }

    private float getReadyToRefreshOffset() {
        if (mHeadView != null && mHeadView instanceof OnPullStateChangedListener) {
            return ((OnPullStateChangedListener) mHeadView).getReadyToRefreshOffset();
        }
        return mToRefreshingOffset;
    }

    @Override
    public void setTranslationY(float translationY) {
        if (translationY > mToRefreshingOffset) {
            translationY = mToRefreshingOffset;
        }
        if (mHeadView != null && mTargetView != null) {
            mHeadView.setVisibility(VISIBLE);
            for (int i = 0; i < getChildCount(); i++) {
                getChildAt(i).setTranslationY(translationY);
            }
            if (mHeadView instanceof OnPullStateChangedListener) {
                ((OnPullStateChangedListener) mHeadView).onPullDown(translationY);
            }
            return;
        }
        super.setTranslationY(translationY);
    }

    @Override
    public float getTranslationY() {
        if (mTargetView != null) {
            return mTargetView.getTranslationY();
        }
        return super.getTranslationY();
    }

    public boolean isRefreshing() {
        return mState == STATE_REFRESHING;
    }

    private void onActionUp() {
        if (mOnRefreshListener != null && !isRefreshing()) {
            if (mState == STATE_READY_TO_REFRESHING) {
                mState = STATE_REFRESHING;
                if (mHeadView instanceof OnPullStateChangedListener) {
                    ((OnPullStateChangedListener) mHeadView).onPullStateChanged(mState);
                }
                if (mOnRefreshListener != null) {
                    mHeadView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mOnRefreshListener.onRefresh();
                        }
                    }, ANIMATION_DURATION);
                }
            }
        }
        mReturningToStart = !isRefreshing();
        if (isRefreshing()) {
            dealAnimation(getTranslationY(), getReadyToRefreshOffset());
        } else {
            dealAnimation(getTranslationY(), 0);
        }
    }

    public void ensureTargetView(int childIndex) {
        mTargetView = getChildAt(childIndex);
    }

    private void dealAnimation(float... value) {
        ValueAnimator animator = ValueAnimator.ofFloat(value);
        animator.setDuration(ANIMATION_DURATION);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                setTranslationY(value);
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (mAnimatorListener != null) {
                    mAnimatorListener.onAnimationStart(animator);
                }
                mIsAnimationEnd = false;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (mReturningToStart) {
                    mReturningToStart = false;
                }
                mIsAnimationEnd = true;
                if (getTranslationY() == 0) {
                    if (mHeadView != null) {
                        mHeadView.setVisibility(INVISIBLE);
                    }
                    if (mAnimatorListener != null) {
                        mAnimatorListener.onAnimationEnd(animator);
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        animator.start();
    }

    /**
     * 展示、隐藏头部刷新布局
     *
     * @param isRefreshing
     */
    public void setRefreshing(boolean isRefreshing) {
        if (!isEnabled()) {
            return;
        }
        int state = isRefreshing ? STATE_REFRESHING : STATE_NORMAL;
        if (state == mState) {
            return;
        } else {
            mState = state;
        }
        if (isRefreshing) {
            dealAnimation(0, getReadyToRefreshOffset());
        } else {
            dealAnimation(getReadyToRefreshOffset(), 0);
        }

        if (mHeadView instanceof OnPullStateChangedListener) {
            ((OnPullStateChangedListener) mHeadView).onPullStateChanged(mState);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (mHeadView != null) {
            mToRefreshingOffset = mHeadView.getMeasuredHeight();
            mHeadView.layout(0, -mHeadView.getMeasuredHeight(), mHeadView.getMeasuredWidth(), 0);
        }
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view == mHeadView) {
                continue;
            }
            try {
                view.layout(0, 0, getMeasuredWidth(), getMeasuredHeight());
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private void ensureTargetView() {
        if (getChildCount() > 1) {
            mHeadView = getChildAt(0);
            mTargetView = getChildAt(1);
        } else {
            mTargetView = getChildAt(0);
            setEnabled(false);
        }
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        mTotalConsumed = 0;
        if (isRefreshing() && !mIsAnimationEnd) {
            mIsChildDraggedWhenRefreshing = true;
        }
        return true;
    }

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
        if (!isEnabled()) {
            return;
        }
        if (canPullDown() && dy < 0 || (mIsBeingDragged && dy >= 0)) {

            mTotalConsumed += dy;
            float overScrollTop = -mTotalConsumed;
            if (overScrollTop >= 0) {
                consumed[1] = dy;
            } else {
                overScrollTop = 0;
            }
            if (!isRefreshing()) {
                startPullDown(overScrollTop * DRAG_RATE);
            }
        }

        if (dy > 0) {
            if (getTranslationY() == getReadyToRefreshOffset()) {
                mState = STATE_REFRESHING;
                setRefreshing(false);
            }
        }
    }

    boolean mIsBeingNestedScrolling;

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if (isEnabled()) {
            mIsBeingNestedScrolling = true;
        }
    }

    @Override
    public void onStopNestedScroll(View child) {
        super.onStopNestedScroll(child);
        if (isEnabled()) {
            if (mIsBeingNestedScrolling && Math.abs(mTotalConsumed) > 0) {
                onActionUp();
                mIsBeingDragged = false;
            }
            if (!isRefreshing() && mIsAnimationEnd) {
                mIsChildDraggedWhenRefreshing = false;
            }
            mIsBeingNestedScrolling = false;
            mTotalConsumed = 0;
        }
    }

    /**
     * 判断是否可以下滑
     *
     * @return
     */
    public boolean canPullDown() {
        if (mScrollDownCallBack != null) {
            return mScrollDownCallBack.canPullDown(this, mTargetView);
        }
        return mTargetView != null && mTargetView.getMeasuredHeight() != 0 && !mTargetView.canScrollVertically(-1);
    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
        this.mOnRefreshListener = onRefreshListener;
    }

    public interface OnChildScrollDownCallBack {
        /**
         * 自定义触发下拉条件
         *
         * @param viewGroup
         * @param targetView
         * @return
         */
        boolean canPullDown(ViewGroup viewGroup, View targetView);
    }

    public void setScrollDownCallBack(OnChildScrollDownCallBack scrollDownCallBack) {
        this.mScrollDownCallBack = scrollDownCallBack;
    }

    /**
     * 正常状态
     */
    public static final int STATE_NORMAL = 0;

    /**
     * 松开即可刷新
     */
    public static final int STATE_READY_TO_REFRESHING = 1;

    /**
     * 正在刷新
     */
    public static final int STATE_REFRESHING = 2;

    public interface OnPullStateChangedListener {
        /**
         * 下拉状态改变
         * <li>{@link #STATE_NORMAL}</li>
         * <li>{@link #STATE_READY_TO_REFRESHING}</li>
         * <li>{@link #STATE_REFRESHING}</li>
         *
         * @param state
         */
        void onPullStateChanged(int state);

        /**
         * 设置松开刷新需要的距离
         *
         * @return
         */
        float getReadyToRefreshOffset();

        /**
         * 滑动的距离
         *
         * @param overScrollTop
         * @return
         */
        void onPullDown(float overScrollTop);
    }

    /**
     * 回调首页轮播监听
     */
    public interface CheckEventListener {
        void getEvent(MotionEvent event);
    }

    public void setCheckEventListener(CheckEventListener checkEventListener) {
        mCheckEventListener = checkEventListener;
    }

    private Animator.AnimatorListener mAnimatorListener;

    public void setAnimatorListener(Animator.AnimatorListener animatorListener) {
        this.mAnimatorListener = animatorListener;
    }

    public View getHeadView() {
        return mHeadView;
    }
}

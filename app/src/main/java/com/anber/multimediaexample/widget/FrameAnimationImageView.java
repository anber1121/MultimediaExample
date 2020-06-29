package com.anber.multimediaexample.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

/**
 * 帧动画ImageView
 */
public class FrameAnimationImageView extends AppCompatImageView {
    private static final String TAG = "FrameAnimationImageView";

    private ObjectAnimator rotateAnimation;

    public FrameAnimationImageView(Context context) {
        super(context);
        init();
    }

    private void init() {
        rotateAnimation = ObjectAnimator.ofFloat(this, "rotation", 0F, 359F);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(-1);
    }

    public FrameAnimationImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameAnimationImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (rotateAnimation != null) {
            rotateAnimation.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (rotateAnimation != null) {
            rotateAnimation.pause();
        }
    }
}



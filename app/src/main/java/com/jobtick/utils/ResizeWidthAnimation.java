package com.jobtick.utils;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeWidthAnimation extends Animation implements Animation.AnimationListener {
    private int mWidth;
    private int mStartWidth;
    private View mView;

    private OnFinish onFinish;

    public ResizeWidthAnimation(View view, int width) {
        mView = view;
        mWidth = width;
        mStartWidth = view.getWidth();
        this.setAnimationListener(this);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newWidth = mStartWidth + (int) ((mWidth - mStartWidth) * interpolatedTime);

        mView.getLayoutParams().width = newWidth;
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        if(onFinish != null)
            onFinish.finished();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public OnFinish getOnFinish() {
        return onFinish;
    }

    public void setOnFinish(OnFinish onFinish) {
        this.onFinish = onFinish;
    }

    public interface OnFinish{
        void finished();
    }
}

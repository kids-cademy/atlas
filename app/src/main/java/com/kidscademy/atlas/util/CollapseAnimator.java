package com.kidscademy.atlas.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.View;

public class CollapseAnimator extends ValueAnimator implements ValueAnimator.AnimatorUpdateListener, View.OnClickListener {
    private static final int ANIMATION_DURATION = 300;

    private final View view;
    private final View.OnClickListener clickListener;

    public CollapseAnimator(final View view, View.OnClickListener clickListener) {
        this.view = view;
        this.clickListener = clickListener;

        setFloatValues(1, 0);
        setDuration(ANIMATION_DURATION);
        addUpdateListener(this);

        addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setScaleX(1.0F);
                view.setScaleY(1.0F);
                CollapseAnimator.this.clickListener.onClick(view);
            }
        });

        view.setClickable(true);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        float value = (float) animator.getAnimatedValue();
        view.setScaleX(value);
        view.setScaleY(value);
        view.invalidate();
    }

    @Override
    public void onClick(View v) {
        start();
    }
}

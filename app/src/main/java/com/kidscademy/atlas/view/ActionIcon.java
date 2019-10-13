package com.kidscademy.atlas.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.kidscademy.atlas.util.CollapseAnimator;

public class ActionIcon extends AppCompatImageView {
    private CollapseAnimator collapseAnimator;

    public ActionIcon(Context context, @Nullable AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener clickListener) {
        assert collapseAnimator == null;
        // keep it simple; we are sure this setter is called only once
        collapseAnimator = new CollapseAnimator(this, clickListener);
        super.setOnClickListener(collapseAnimator);
    }

    public void setColor(@ColorRes int color) {
        ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(getContext(), color)));
    }
}
